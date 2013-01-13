package net.sf.jdtdecompiler.jad.ui.preferences;

import net.sf.jdtdecompiler.jad.IJadOptions;
import net.sf.jdtdecompiler.jad.JadPlugin;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Formatting preference page
 * 
 * @author Valdimir Grishchenko
 */
public class JadPreferencePageFormat extends FieldEditorPreferencePage
        implements IWorkbenchPreferencePage {
    public JadPreferencePageFormat() {
        super(FieldEditorPreferencePage.GRID);
        setPreferenceStore(JadPlugin.getDefault().getPreferenceStore());
    }

    /**
     * @see PreferencePage#createControl(Composite)
     */
    public void createControl(Composite parent) {
        super.createControl(parent);
    }

    /**
     * @see FieldEditorPreferencePage#createFieldEditors()
     */
    protected void createFieldEditors() {
        addField(new BooleanFieldEditor(IJadOptions.OPTION_ANNOTATE,
                "Generate JVM instructions as comments", getFieldEditorParent()));

        addField(new BooleanFieldEditor(IJadOptions.OPTION_ANNOTATE_FQ,
                "Output fully qualified names when annotating",
                getFieldEditorParent()));

        addField(new BooleanFieldEditor(IJadOptions.OPTION_BRACES,
                "Generate redundant braces", getFieldEditorParent()));

        addField(new BooleanFieldEditor(IJadOptions.OPTION_CLEAR,
                "Clear all prefixes", getFieldEditorParent()));

        addField(new BooleanFieldEditor(IJadOptions.OPTION_FULLNAMES,
                "Generate fully qualified names", getFieldEditorParent()));

        addField(new BooleanFieldEditor(IJadOptions.OPTION_FIELDSFIRST,
                "Output fields before methods", getFieldEditorParent()));

        addField(new BooleanFieldEditor(IJadOptions.OPTION_DEFINITS,
                "Print default initializers for fields", getFieldEditorParent()));

        addField(new BooleanFieldEditor(IJadOptions.OPTION_NONLB,
                "Don't insert a newline before opening brace",
                getFieldEditorParent()));

        addField(new BooleanFieldEditor(IJadOptions.OPTION_SPACE,
                "Output space between keyword (if, while, etc) and expression",
                getFieldEditorParent()));

        addField(new BooleanFieldEditor(IJadOptions.OPTION_SPLITSTR_NL,
                "Split strings on newline characters", getFieldEditorParent()));

        IntegerFieldEditor splitstr = new IntegerFieldEditor(
                IJadOptions.OPTION_SPLITSTR_MAX,
                "Split strings into pieces of max chars (0=dis)",
                getFieldEditorParent());
        addField(splitstr);

        ComboFieldEditor iradix = new ComboFieldEditor(
                IJadOptions.OPTION_IRADIX,
                "Display integers using the specified radix",
                new String[][] {{"8", "8"}, {"10", "10"}, {"16", "16"}},
                getFieldEditorParent());
        addField(iradix);

        ComboFieldEditor lradix = new ComboFieldEditor(
                IJadOptions.OPTION_LRADIX,
                "Display long integers using the specified radix",
                new String[][] {{"8", "8"}, {"10", "10"}, {"16", "16"}},
                getFieldEditorParent());
        addField(lradix);

        addField(new IntegerFieldEditor(IJadOptions.OPTION_PI,
                "Pack imports into one line using .* (0=dis)",
                getFieldEditorParent()));

        addField(new IntegerFieldEditor(IJadOptions.OPTION_PV,
                "Pack fields with the same types into one line (0=dis)",
                getFieldEditorParent()));

        ComboFieldEditor indent = new ComboFieldEditor(
                IJadOptions.OPTION_INDENT_SPACE,
                "Number of spaces for indentation: ", 
                new String[][] {
                        { "tab", IJadOptions.USE_TAB }, { "0", "0" },
                        { "1", "1" }, { "2", "2" }, { "3", "3" }, { "4", "4" },
                        { "5", "5" }, { "6", "6" }, { "7", "7" }, { "8", "8" },
                        { "9", "9" }, { "10", "10" }, { "11", "11" },
                        { "12", "12" }, { "13", "13" }, { "14", "14" },
                        { "15", "15" } },
                getFieldEditorParent());
        addField(indent);
    }

    /**
     * @see IWorkbenchPreferencePage#init(IWorkbench)
     */
    public void init(IWorkbench arg0) {
    }
}
