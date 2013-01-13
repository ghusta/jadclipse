package net.sf.jdtdecompiler.jad;

public interface IJadOptions {
    public static final String OPTION_ANNOTATE = "-a"; // format
    public static final String OPTION_ANNOTATE_FQ = "-af"; // format
    public static final String OPTION_BRACES = "-b"; // format
    public static final String OPTION_CLEAR = "-clear"; // format
    public static final String OPTION_DIR = "-d"; // ?
    public static final String OPTION_DEAD = "-dead"; // directives
    public static final String OPTION_DISASSEMBLER = "-dis"; // directives
    public static final String OPTION_FULLNAMES = "-f"; // format
    public static final String OPTION_FIELDSFIRST = "-ff"; // format
    public static final String OPTION_DEFINITS = "-i"; // format
    public static final String OPTION_SPLITSTR_MAX = "-l"; // format
    public static final String OPTION_LNC = "-lnc"; // debug
    public static final String OPTION_LRADIX = "-lradix"; // format
    public static final String OPTION_SPLITSTR_NL = "-nl"; // format
    public static final String OPTION_NOCONV = "-noconv"; // directives
    public static final String OPTION_NOCAST = "-nocast"; // directives
    public static final String OPTION_NOCLASS = "-noclass"; // directives
    public static final String OPTION_NOCODE = "-nocode"; // directives
    public static final String OPTION_NOCTOR = "-noctor"; // directives
    public static final String OPTION_NODOS = "-nodos"; // directives
    public static final String OPTION_NOFLDIS = "-nofd"; // directives
    public static final String OPTION_NOINNER = "-noinner"; // directives
    public static final String OPTION_NOLVT = "-nolvt"; // directives
    public static final String OPTION_NONLB = "-nonlb"; // format
    public static final String OPTION_OVERWRITE = "-o"; // ?
    public static final String OPTION_SENDSTDOUT = "-p"; // ?
    public static final String OPTION_PA = "-pa"; // directives
    public static final String OPTION_PC = "-pc"; // directives
    public static final String OPTION_PE = "-pe"; // directives
    public static final String OPTION_PF = "-pf"; // directives
    public static final String OPTION_PI = "-pi"; // format
    public static final String OPTION_PL = "-pl"; // directives
    public static final String OPTION_PM = "-pm"; // directives
    public static final String OPTION_PP = "-pp"; // directives
    public static final String OPTION_PV = "-pv"; // format
    public static final String OPTION_RESTORE = "-r"; // ?
    public static final String OPTION_IRADIX = "-radix"; // format
    public static final String OPTION_EXT = "-s"; // ?
    public static final String OPTION_SAFE = "-safe"; // directives
    public static final String OPTION_SPACE = "-space"; // format
    public static final String OPTION_STAT = "-stat"; // misc
    public static final String OPTION_INDENT_SPACE = "-t"; // format
    public static final String OPTION_INDENT_TAB = "-t"; // ?
    public static final String OPTION_VERBOSE = "-v"; // misc
    public static final String OPTION_ANSI = "-8"; // misc
    public static final String OPTION_REDSTDERR = "-&"; // ?

    public static final String USE_TAB = "use tab";

    public static final String[] TOGGLE_OPTION = { OPTION_ANNOTATE,
            OPTION_ANNOTATE_FQ, OPTION_BRACES, OPTION_CLEAR, OPTION_DEAD,
            OPTION_DISASSEMBLER, OPTION_FULLNAMES, OPTION_FIELDSFIRST,
            OPTION_DEFINITS, OPTION_LNC, OPTION_SPLITSTR_NL, OPTION_NOCONV,
            OPTION_NOCAST, OPTION_NOCLASS, OPTION_NOCODE, OPTION_NOCTOR,
            OPTION_NODOS, OPTION_NOFLDIS, OPTION_NOINNER, OPTION_NOLVT,
            OPTION_NONLB,
            /* OPTION_OVERWRITE, */
            /* OPTION_SENDSTDOUT, */
            /* OPTION_RESTORE, */
            OPTION_SAFE, OPTION_SPACE, OPTION_STAT, OPTION_INDENT_TAB,
            OPTION_VERBOSE, OPTION_ANSI,
    /* OPTION_REDSTDERR */
    };

    public static final String[] VALUE_OPTION_STRING = {
    /* OPTION_DIR, */
    OPTION_PA, OPTION_PC, OPTION_PE, OPTION_PF, OPTION_PL, OPTION_PM,
            OPTION_PP,
    /* OPTION_EXT, */
    };

    public static final String[] VALUE_OPTION_INT = {
    /* OPTION_INDENT_SPACE, */
    OPTION_SPLITSTR_MAX, OPTION_LRADIX, OPTION_PI, OPTION_PV, OPTION_IRADIX, };

}
