use strict;

my (@tstack, @vstack);

my $incindent = 4;

my $instr_addr;
my (%instr, %next_instr, %prev_instr);

@tstack = ();
@vstack = ();

sub print_stack {
    my ($type, $value);
    while (@tstack and @vstack) {
	$type = shift @tstack;
	$value = shift @vstack;
	print STDERR "($type) $value, ";
    }
    if (@tstack) {
	print STDERR "TSTACK to big : @tstack ";
    } elsif (@vstack) { 
	print STDERR "VSTACK to big : @vstack ";
    }
    @tstack = ();
    @vstack = ();
}

sub print_code {
    my ($indent, $code, $addr) = @_;
    #print " "x$indent, $code, (defined addr)?"/* $addr */":"", "\n";
    print " "x$indent, $code, "\n";
}

sub dump_program {
    my $addr;
    foreach $addr (sort { $a <=> $b } keys %instr) {
	print_code (0, "$addr  $instr{$addr}");
    }
    return 1
}

sub convert_value($$$) {
    my ($value, $oldtype, $newtype) = @_;
    return "$value" if ($oldtype eq $newtype);
    return "$value" if ($oldtype =~ /\*/ or $newtype =~ /\*/);    
    return "$value" if ($oldtype eq "boolean" && $newtype eq "int");
    if ($oldtype eq "int" && $newtype eq "boolean") {
	$value =~ s/1/true/g;
	$value =~ s/0/false/g;
	$value =~ s/\&/\&\&/g;
	$value =~ s/\|/\|\|/g;
	return $value;
    }
    return $value; # "/*warn: conv: $oldtype => $newtype*/ $value";
}

sub get_type ($) {
    my $type;
    $_ = $_[0];
    SWITCH: {
	/^b$/ && ($type = "byte",last);
	/^c$/ && ($type = "char",last);
	/^s$/ && ($type = "short",last);
	/^i$/ && ($type = "int",last);
	/^l$/ && ($type = "long",last);
	/^f$/ && ($type = "float",last);
	/^d$/ && ($type = "double",last);
	/^a$/ && ($type = "*",last);
	die "internal error in get_type";
    }
    return $type;
}

sub pop_value_type ($) {
    if (not @tstack || not @vstack) {
	die "Stack is empty??";
    }
    my $result = "";
    my $want_type = $_[0];
    my $act_type  = pop @tstack;
    my $value = pop @vstack;
    warn "want_type not defined"
	if (not defined $want_type);
    warn "act_type not defined"
	if (not defined $act_type);
    $result = convert_value($value, $act_type, $want_type);
    return ($result, $act_type);
}

sub pop_value($) {
    @_ = pop_value_type($_[0]);
    return $_[0];
}

sub parse_type($) {
    $_[0] =~ /^\#\d+\s+ <Class
	\s+(\S+)      # type
	    >\s*$/x or die "Wrong field parameter `$_[0]'";
    return $1;
}

sub parse_field($) {
    $_[0] =~ /^\#\d+\s+ <Field
	\s+(\S+)      # type
	\s+([^\[>]+)  # name
        ((?:\[\])*)   # [][]...  belongs to type
	    >\s*$/x or die "Wrong field parameter `$_[0]'";
    return $1.$3, $2;
}

sub parse_special($) {
    $_[0] =~ /\#\d+\s+<Method
	\s+([^\(\s]+)      # method
	\s*\(([^\)]*)\)    # params
        >\s*$/x or die "Wrong method parameter `$_[0]'";
    my ($method, $params) = ($1,$2);
    my @params = split /,\s*/, $params;
    return $method, @params;
}

sub parse_method($) {
    $_[0] =~ /\#\d+\s+<Method
	\s+(\S+)           # type
	\s+([^\(\s]+)      # method
	\s*\(([^\)]*)\)    # params
        >\s*$/x or die "Wrong method parameter `$_[0]'";
    my ($type, $method, $params) = ($1, $2,$3);
    my @params = split /,\s*/, $params;
    return $type, $method, @params;
}

sub classify($$) {
    my $class = $_[0] . ".";
    $class = "" if $class eq "this.";
    return $class.$_[1];
}

sub new_instr($) {
    if (defined ($instr{$instr_addr})) {
	$instr{$instr_addr} .= "\n/*warn: multiple*/\n".$_[0];
    } else {
	$instr{$instr_addr} = $_[0];
    }
    # print STDERR "$instr_addr: $instr{$instr_addr}\n";
}
			
sub new_assign($$) {
    my ($var, $value) = @_;
    if (@vstack) {
	if (("$value" eq "($var + 1)") &&
	    ($vstack[-1] eq "$var")) {
	    $vstack[-1] = "$var++";
	} else {
	    warn("`$var = $value' in expression, while vstack");
	}
    } else {
	new_instr("$var = $value;");
    }
}

sub combine_if_block {
    my ($addr, $end) = @_;
    
    $instr{$addr} =~ /if (\(.*\)) goto (\d+);/ or return;
    my ($cond, $dest) = ($1, $2);
    
  COMBINE:
    while (1) {
	my $if;


	# First combine ifs with the same dest addr, that is ors.
	my @conds = ($cond);
	for ($if = $next_instr{$addr}; $if < $end; $if = $next_instr{$addr}) {
	    
	    $instr{$if} =~ /if (\(.*\)) goto ($dest);/ or last;

    	    push @conds, $1;
	    #remove unnecessary ifs  (hope there are no goto's)
	    $next_instr{$addr} = $next_instr{$if};
	    $prev_instr{$next_instr{$if}}= $addr;
	    delete $instr{$if};
	    delete $prev_instr{$if};
	    delete $next_instr{$if};
	}
	if (@conds > 1) {
	    # combine conditions with or and reset the list
	    $cond = join " || ", @conds;
	    $cond = "($cond)";
	}

	last COMBINE if ($if >= $end || $instr{$if} !~ /^if/);

	# Now try if we can combine all further ifs until the destination (that is and)
	combine_if_block($if, $dest)
	    unless ($next_instr{$if} == $dest);

	last COMBINE if ($next_instr{$if} != $dest);
	
	# This is an and
	$instr{$if} =~ /if (\(.*\)) goto (\d+);/;
	$cond = "(!$cond && $1)";
	$dest = $2;
	$next_instr{$addr} = $next_instr{$if};
	$prev_instr{$next_instr{$if}}= $addr;
	delete $instr{$if};
	delete $prev_instr{$if};
	delete $next_instr{$if};
    }
	    
    # Okay, we are stuck here, build the combined if.
    $instr{$addr} = "if $cond goto $dest;";
}

sub simplify_instructions {
    my ($addr, $end) = @_;
  ADDR:
    for (; $addr < $end; $addr = $next_instr{$addr}) {
	combine_if_block($addr, $end) 
	    if $instr{$addr} =~ /^if /;

	while ( $instr{$addr} =~ 
	       /^(.*)new (java\.lang\.)?StringBuffer\((\).append\(.*)+\).toString\(\)(.*)$/ ) {
	    my ($first, $middle, $last) = ($1,$3,$4);
	    $middle =~ s/\).append\(/\+/g;
	    $middle =~ s/^\+//;
	    $instr{$addr} = $first.$middle.$last;
	}

	while ( $instr{$addr} =~ s/([A-Za-z_\$][A-Za-z_\$0-9]*) = \(\1 \+ 1\)/$1++/) {
	}
    }
}

# The parameters:
#  start  first instruction to decode
#  end    last instruction to decode + 1
#  next   instruction where control flows after this block 
#         (usually end but may be bigger)
#  break  instruction where a break would bring us to
#  indent The indentation of this block

sub print_stmtlist ($$$$$) {
    my ($start, $end, $next, $break, $indent) = @_;
    my $addr;
    $addr = $start;
  ADDR:
    while ($addr < $end) {
	(dump_program && die "Addresses out of range: $addr") if (not defined $next_instr{$addr});
      
	$_ = $instr{$addr};
	/^goto (\d+);$/ && do {
	    my $dest = $1;
	    if ($dest == $break) {
		print_code($indent, "break $dest;", $addr);
		$addr = $next_instr{$addr};
		next ADDR;
	    }
	    my $begin = $next_instr{$addr};
	    if ($instr{$dest} =~ /^if\s\((.*)\)\sgoto\s$begin/) {
		# This is a while-loop
		print_code($indent, "while ($1) {", $addr); 
		print_stmtlist($begin, $dest, $dest, $dest, $indent+$incindent);
		print_code($indent, "}");
		$addr = $next_instr{$dest};
		next ADDR;
	    }
	};
	/^if \((.*)\) goto (\d+);/ && do {
	    my $cond = $1;
	    my $next_after_if = $2;
	    if ($next_after_if > $addr && 
		($next_after_if <= $end || $next_after_if == $next)) {
		# This seems to be an if.
		print_code($indent, "if (!($cond)) {", $addr);

		# endthen is the last instruction in then block + 1
		my $endthen = ($next_after_if > $end) ? $end : $next_after_if;
		my $prev = $prev_instr{$endthen};
		if ($instr{$prev} =~ /^goto\s(.*);/ &&
		    $1 > $endthen && ($1 <= $end || $1 == $next)) {
		    $next_after_if = $1;
		    my $endelse = $1;
		    if ($endelse > $end) {
			$endelse = $end;
		    }
		    # there is an else part
		    print_stmtlist ($next_instr{$addr}, $prev, 
				    $next_after_if, $break, $indent+$incindent);
		    print_code($indent, "} else {");
		    print_stmtlist ($endthen, $endelse, 
				    $next_after_if, $break, $indent+$incindent);
		    $addr = $endelse;
		} else {
		    # no else-part
		    print_stmtlist ($next_instr{$addr}, $endthen, 
				    $next_after_if, $break, $indent+$incindent);
		    $addr = $endthen;
		}
		print_code($indent, "}");
		next ADDR;
	    }
	    if ($next_after_if == $break) {
		# This is an if () break;
		print_code($indent, "if ($cond) break;", $addr);
		$addr = $next_instr{$addr};
		next ADDR;		
	    }
	};
	/^case ((.|\n)*)$/ && do {
	    my $default;
	    my $cond = "NONE";
	    my @lines = split "\n", $1;
	    $_ = shift @lines;
	    /^\((.*)\)$/ and $cond = $1;
	    (shift @lines) =~ /^default: goto (\d+);/ and $default = $1;
	    my $next_after_switch = $default;
	    if ($instr{$prev_instr{$default}} =~ /^goto\s(\d+);/ and
		$1 > $default) {
		$next_after_switch = $1;
	    }
	    print_code ($indent, "switch ($cond) {", $addr);
	    my %cases = ($default => "default");
	    foreach (@lines) {
		(/^(\d+): goto (\d+);$/ and $cases{$2} = "case $1")
		    or warn ("ILLEGAL case : `$_'");
		my $casepos = $1;
		if ($casepos > $next_after_switch) {
		    if ($instr{$prev_instr{$1}} =~ /^goto\s(\d+);/ and
			$1 > $casepos) {
			$next_after_switch = $1;
		    } else {
			$next_after_switch = $casepos;
		    }
		}
	    }
	    $next_after_switch = $end 
		if ($next_after_switch > $end && $next_after_switch != $next);
	    my $endswitch = ($next_after_switch > $end) ? $end : $next_after_switch;
	    #print STDERR "Addr: $addr, labels: `",
	    #   (join ":", keys %cases ), "', default: $default, end: $next_after_switch\n";
	    $addr = $next_instr{$addr};
	    foreach $_ (sort { $a <=> $b } keys %cases) {
		my $next_case = $_;
		if ($instr{$prev_instr{$next_case}} eq 
		    "goto $next_after_switch;") {
		    print_stmtlist($addr, $prev_instr{$next_case},
				   $next_after_switch, $next_after_switch, 
				   $indent+$incindent);
		    print_code($indent+$incindent, "break;");
		} else {
		    print_stmtlist($addr, $next_case,
				   $next_case, $next_after_switch, 
				   $indent+$incindent);
		}
		print_code($indent, $cases{$next_case}.":");
		$addr = $next_case;
	    }
	    print_stmtlist($addr, $endswitch, 
			   $endswitch, $next_after_switch, 
			   $indent+$incindent);
	    print_code($indent, "}");
	    $addr = $endswitch;
	    next ADDR;
	};
	print_code($indent, $_, $addr);
	$addr = $next_instr{$addr};
    }
}



my %locals = ();
my $addr;

LINE: while (<>) {
     
    chomp;
    (/^\s*(\d+)\s+(.*)$/ and $addr = $1, $_ = $2) or do {
	warn "Line `$_' ist not formatted correctly\n";
	next LINE;
    };

    if (not @vstack) { 
	if (defined ($instr_addr)) {
	    new_instr("/*warn: missing instruction!*/")
		if (not defined $instr{$instr_addr});
	    $next_instr{$instr_addr} = $addr;
	    $prev_instr{$addr} = $instr_addr;
	} else {
	    $prev_instr{$addr} = -1;
	}
	$instr_addr = $addr;
    }

  INSTR: 
    {
	/^([ilfda])load[\s_]+(\d+)\s*$/ && do {
	    push @tstack, get_type($1);
	    my $local;
	    if ($2 == 0) {
		$local = "this";
	    } else {
		$local = "local_$2";
	    }
	    push @vstack, $local;
	    last INSTR;
	};
	/^([bcsilfda])aload\s*$/ && do {
	    my $warn = "";
	    my $index = pop_value("int");
	    my ($array, $atype) = pop_value_type(get_type($1)."[]");
	    my $type = $atype;
	    ($atype =~ /(.*)\[\]/ and $type = $1) or
		$warn = "/*warn: `$atype' not an array*/ ";
	    push @tstack, $type;
	    push @vstack, "$warn$array"."[$index]";
	    last INSTR;
	};
	(/^[bs](i)push\s+(-?\d+)\s*$/ ||
	 /^([ilfda])const[\s_]+([m\-]?[\d.Ee\+\-]+|null)\s*$/) && do {
	     push @tstack, get_type($1);
	     push @vstack, ($2 eq "m1") ? -1 : $2;
	     last INSTR;
	 };
	/ldc[12]?_?w?\s+\#\d+\s+\<(\S+)\s+([^\>]+)\>/ && do {
	    push @tstack, $1;
	    push @vstack, $2;
	    last INSTR;
        };
        /^([ilfda])store[\s_]+(\d+)\s*$/ && do { 
	    my $local;
	    my ($value, $type) = pop_value_type(get_type($1));
	    if ($2 == 0) {
		$local = "this";
	    } else {
		$local = "local_$2";
		if (not defined $locals{$2}) {
		    $locals{$2} = $type;
		    $local = "$type $local";
		} else {
    		    $local = convert_value($local, $type, $locals{$2});
		}
	    }
	    new_assign($local, $value);
	    last INSTR;
	};
	/^([bcsilfda])astore\s*$/ && do {
	    my ($value, $type)  = pop_value_type(get_type($1));
	    my $index = pop_value("int");
	    my ($array, $atype) = pop_value_type(get_type($1)."[]");
	    ($atype =~ /(.*)\[\]/ and $atype = $1) or
		$atype = "`$atype' not an array*/\n\t";
	    new_assign("$array"."[$index]", 
		       convert_value("$value", $type, $atype));
		      
	    last INSTR;
	};
	/^new\s+(.*)\s*/ && do {
	    my ($type) = parse_type($1);
	    push @tstack, $type;
	    push @vstack, "new $type";
	    last INSTR;
	};
        /^newarray\s+(\S+)\s*$/ && do {
	    my $arrtype = $1;
	    my $value = pop_value("int");
	    push @tstack, $arrtype."[]";
	    push @vstack, "new ".$arrtype."[$value]";
	    last INSTR;
        };
	/^getfield\s+(.*)$/ && do {
	    my ($type, $field) = parse_field($1);
	    my $class = pop_value("*") . ".";
	    $class = "" if $class eq "this.";
	    push @tstack, $type;
	    push @vstack, "$class$field";
	    last INSTR;
	};
	/^getstatic\s+(.*)$/ && do {
	    my ($type, $field) = parse_field($1);
	    push @tstack, $type;
	    my $class="FIXME.";
	    push @vstack, "$class$field";
	    last INSTR;
	};
        /^putfield\s+(.*)$/ && do {
	    my ($dtype, $field) = parse_field($1);
	    my $value = pop_value($dtype);
	    $field = classify(pop_value("*"), $field);
	    new_assign($field, $value);
	    last INSTR;
        };
	/^goto\s+(\d+)\s*$/ && do {
	    new_instr("goto $1;");
	    last INSTR;
	};
	/^tableswitch\s+(\d+)\s+to\s+(\d+): default=(\d+)\s*$/ && do {
	    my $from = $1;
	    my $to = $2;
	    my $default = $3;
	    my $num;
	    my $casestmt = "case (" . pop_value("int") . ")\n";
	    $casestmt .= "default: goto $default;\n";
	    for $num ($from .. $to) {
		$_ = <>;
		if ( $_ =~ /\s+$num:\s*(\d+)/ ) {
		    $casestmt .= "$num: goto $1;\n";
		} else {
		    warn "unknown case: `$_' at $.";
		}
	    }
	    new_instr($casestmt);
	    last INSTR;
	};
	/^lookupswitch\s+(\d+):\s+default=(\d+)\s*$/ && do {
	    my $anz = $1;
	    my $default = $2;
	    my $num;
	    my $casestmt = "case (" . pop_value("int") . ")\n";
	    $casestmt .= "default: goto $default;\n";
	    for $num (1 .. $anz) {
		$_ = <>;
		if ( $_ =~ /\s+(\d+):\s*(\d+)/ ) {
		    $casestmt .= "$1: goto $2;\n";
		} else {
		    $casestmt .= "error in case";
		}
	    }
	    new_instr($casestmt);
	    last INSTR;
	};

	/^invokespecial\s+(.*)$/ && do {
	    my ($method, @paramtypes) = parse_special ($1);
	    my @params=();
	    # Constructoraufruf!  Wenn alles glatt laeuft...
	    while (@paramtypes) {
		my $ptype = pop @paramtypes;
		my $value = pop_value($ptype);
		unshift @params, $value;
	    }
	    my ($new_class, $class_type) = pop_value_type ("*");
	    $method = $new_class;
	    my $call = "$method(" . join (", ", @params) . ")";
	    if ($vstack[-1] eq $new_class) {
		$vstack[-1] = "$call";
	    } else {
		new_instr("$call;");
	    }
	    last INSTR;
	};
	/^invoke(virtual|static)\s+(.*)$/ && do {
	    my ($type, $method, @paramtypes) = parse_method ($2);
	    my @params=();
	    while (@paramtypes) {
		my $ptype = pop @paramtypes;
		my $value = pop_value($ptype);
		unshift @params, $value;
	    }
	    my ($class, $class_type) = ($1 eq "virtual")? pop_value_type ("*") : "FIXME";
	    $method = classify($class, $method);
	    my $call = "$method(" . join (", ", @params) . ")";
	    if ($type eq "void") {
		new_instr("$call;");
	    } else {
		push @tstack, $type;
		push @vstack, $call;
	    }
	    last INSTR;
	};
	/^return\s*$/ && do {
	    new_instr("return;");
	    last INSTR;
	};
	/^pop\s*$/ && do {
	    unless (@vstack) {
		print STDERR "pop: Stack is empty at $addr";
	    }
	    new_instr(pop(@vstack).";");
	    pop @tstack;
	    last INSTR;
	};
	/^dup\s*$/ && do {
	    push @tstack, $tstack[-1];
	    push @vstack, $vstack[-1];
	    last INSTR;
	};
	/^dup2\s*$/ && do {
	    push @tstack, $tstack[-2];
	    push @vstack, $vstack[-2];
	    push @tstack, $tstack[-2];
	    push @vstack, $vstack[-2];
	    last INSTR;
	};
	/^dup_x([12])\s*$/ && do {
	    splice @tstack, -1-$1, 0, $tstack[-1];
	    splice @vstack, -1-$1, 0, $vstack[-1];
	    last INSTR;
	};
	/^([ilfd])neg\s*$/ && do {
	    my $type = get_type($1);
	    my $op1 = pop_value($type);	    
	    push @tstack, $type;
	    push @vstack, "-$op1";
	    last INSTR;
	};
	/^([ilfd])(add|sub|mul|div|rem|and|or|xor|shl|shr)\s*$/ && do {
	    my $type = get_type($1);
	    my $op2 = pop_value($type);
	    my $op1 = pop_value($type);
	    my $op;
	    for ($2) {
		/add/ && ($op="+", last);
		/sub/ && ($op="-", last);
		/mul/ && ($op="*", last);
		/div/ && ($op="/", last);
		/rem/ && ($op="%", last);
		/and/ && ($op="&", last);
		/or/  && ($op="|", last);
		/xor/ && ($op="^", last);
		/shl/ && ($op="<<", last);
		/shr/ && ($op=">>", last);
	    }
	    push @tstack, $type;
	    push @vstack, "($op1 $op $op2)";
	    last INSTR;
	};
	/^iinc\s+(\d+)\s+(-?\d+)\s*$/ && do {
	    my $value = $2;
	    my $local;
	    if ($1 == 0) {
		$local = "this";
	    } else {
		$local = "local_$1";
	    }
    	    new_instr(convert_value("$local", "int", $locals{$1}). 
		      (($2 == 1)? "++;" : " += $2;"));
	    last INSTR;
	};
	/^([bcifld])2([bcifld])\s*$/ && do {
	    my $value = pop_value(get_type($1));
	    my $type = get_type($2);
	    push @tstack, $type;
	    push @vstack, "($type) $value";
	    last INSTR;
	};
	/^([lfd])cmp([lg]?)\s*$/ && do {
	    my $type = get_type($1);
	    my $op2 = pop_value($type);
	    my $op1 = pop_value($type);
	    push @tstack, "int";
	    push @vstack, "($op1 <=>$2 $op2)";
	    last INSTR;
	};
	/^if(eq|lt|le|ne|gt|ge)\s+(\d+)\s*$/ && do {
	    my $op;
	    my $dest = $2;
	    for ($1) {
		/eq/ && ($op="==", last);
		/lt/ && ($op="<", last);
		/le/ && ($op="<=", last);
		/ne/ && ($op="!=", last);
		/gt/ && ($op=">", last);
		/ge/ && ($op=">=", last);
	    }
	    my $op1 = pop_value("int");
	    new_instr("if ($op1 $op 0) goto $dest;");
	    last INSTR;
	};
	/^if_icmp(eq|lt|le|ne|gt|ge)\s+(\d+)\s*$/ && do {
	    my $op;
	    my $dest = $2;
	    for ($1) {
		/eq/ && ($op="==", last);
		/lt/ && ($op="<", last);
		/le/ && ($op="<=", last);
		/ne/ && ($op="!=", last);
		/gt/ && ($op=">", last);
		/ge/ && ($op=">=", last);
	    }
	    my $op2 = pop_value("int");
	    my $op1 = pop_value("int");
	    new_instr("if ($op1 $op $op2) goto $dest;");
	    last INSTR;
	};
	/^if(null|nonnull)\s+(\d+)\s*$/ && do {
	    my $dest = $2;
	    my $op;
	    for ($1) {
		/notnull/ && ($op="!=", last);
		/null/ && ($op="==", last);
	    } 
	    my $op1 = pop_value("*");
	    new_instr("if ($op1 $op null) goto $dest;");
	    last INSTR;
	};
	
        do {
	    print STDERR "Stack: ";
	    &print_stack;
	    print STDERR "\nUnknown Instruction: `$_'\n\t";
	};
    }
}
$addr++;
$next_instr{$instr_addr} = $addr;

simplify_instructions (0, $addr);
print_stmtlist(0, $addr, $addr, $addr, 2*$incindent);
