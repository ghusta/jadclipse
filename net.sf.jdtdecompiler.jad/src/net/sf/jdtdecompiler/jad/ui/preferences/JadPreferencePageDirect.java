package net.sf.jdtdecompiler.jad.ui.preferences;

import net.sf.jdtdecompiler.jad.IJadOptions;
import net.sf.jdtdecompiler.jad.JadPlugin;
import net.sf.jdtdecompiler.ui.fieldeditors.NoWSFieldEditor;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Directives preference page
 * 
 * @author Valdimir Grishchenko
 */
public class JadPreferencePageDirect extends FieldEditorPreferencePage
        implements IWorkbenchPreferencePage {

    public JadPreferencePageDirect() {
        super(FieldEditorPreferencePage.GRID);
        setPreferenceStore(JadPlugin.getDefault().getPreferenceStore());
    }

    /**
     * @see PreferencePage#createControl(Composite)
     */
    public void createControl(Composite parent) {
        super.createControl(parent);
        // WorkbenchHelp.setHelp(getControl(), new
        // DialogPageContextComputer(this,
        // IJavaHelpContextIds.JAVA_EDITOR_PREFERENCE_PAGE));
    }

    /**
     * @see FieldEditorPreferencePage#createFieldEditors()
     */
    protected void createFieldEditors() {
        addField(new BooleanFieldEditor(IJadOptions.OPTION_DEAD,
                "Try to decompile dead parts of code", getFieldEditorParent()));

        addField(new BooleanFieldEditor(IJadOptions.OPTION_DISASSEMBLER,
                "Disassembler only", getFieldEditorParent()));

        addField(new BooleanFieldEditor(IJadOptions.OPTION_NOCONV,
                "Don't convert Java identifiers into valid ones",
                getFieldEditorParent()));

        addField(new BooleanFieldEditor(IJadOptions.OPTION_NOCAST,
                "Don't generate auxiliary casts", getFieldEditorParent()));

        addField(new BooleanFieldEditor(IJadOptions.OPTION_NOCLASS,
                "Don't convert .class operators", getFieldEditorParent()));

        addField(new BooleanFieldEditor(IJadOptions.OPTION_NOCODE,
                "Don't generate the source code for methods",
                getFieldEditorParent()));

        addField(new BooleanFieldEditor(IJadOptions.OPTION_NOCTOR,
                "Suppress the empty constructors", getFieldEditorParent()));

        addField(new BooleanFieldEditor(IJadOptions.OPTION_NODOS,
                "Turn off check for class files written in DOS mode",
                getFieldEditorParent()));

        addField(new BooleanFieldEditor(IJadOptions.OPTION_NOFLDIS,
                "Don't disambiguate fields with the same names",
                getFieldEditorParent()));

        addField(new BooleanFieldEditor(IJadOptions.OPTION_NOINNER,
                "Turn off the support of inner classes", getFieldEditorParent()));

        addField(new BooleanFieldEditor(IJadOptions.OPTION_NOLVT,
                "Ignore Local Variable Table entries", getFieldEditorParent()));

        addField(new BooleanFieldEditor(IJadOptions.OPTION_SAFE,
                "Generate additional casts to disambiguate methods/fields",
                getFieldEditorParent()));

        NoWSFieldEditor packpref = new NoWSFieldEditor(IJadOptions.OPTION_PA,
                "Prefix for all packages in generated source files",
                getFieldEditorParent());
        addField(packpref);

        NoWSFieldEditor numclpref = new NoWSFieldEditor(IJadOptions.OPTION_PC,
                "Prefix for classes with numerical names",
                getFieldEditorParent());
        addField(numclpref);

        NoWSFieldEditor excpref = new NoWSFieldEditor(IJadOptions.OPTION_PE,
                "Prefix for unused exception names", getFieldEditorParent());
        addField(excpref);

        NoWSFieldEditor numfipref = new NoWSFieldEditor(IJadOptions.OPTION_PF,
                "Prefix for fields with numerical names",
                getFieldEditorParent());
        addField(numfipref);

        NoWSFieldEditor numlopref = new NoWSFieldEditor(IJadOptions.OPTION_PL,
                "Prefix for locals with numerical names",
                getFieldEditorParent());
        addField(numlopref);

        NoWSFieldEditor nummepref = new NoWSFieldEditor(IJadOptions.OPTION_PM,
                "Prefix for methods with numerical names",
                getFieldEditorParent());
        addField(nummepref);

        NoWSFieldEditor numpapref = new NoWSFieldEditor(IJadOptions.OPTION_PP,
                "Prefix for method parms with numerical names",
                getFieldEditorParent());
        addField(numpapref);
    }

    /**
     * @see IWorkbenchPreferencePage#init(IWorkbench)
     */
    public void init(IWorkbench arg0) {
    }

}

// OPTION_DEAD = "-dead"; //directives
// OPTION_DISASSEMBLER = "-dis"; //directives
// OPTION_NOCONV = "-noconv"; //directives
// OPTION_NOCAST = "-nocast"; //directives
// OPTION_NOCLASS = "-noclass"; //directives
// OPTION_NOCODE = "-nocode"; //directives
// OPTION_NOCTOR = "-noctor"; //directives
// OPTION_NODOS = "-nodos"; //directives
// OPTION_NOFLDIS = "-nofd"; //directives
// OPTION_NOINNER = "-noinner"; //directives
// OPTION_NOLVT = "-nolvt"; //directives
// OPTION_PA = "-pa"; //directives
// OPTION_PC = "-pc"; //directives
// OPTION_PE = "-pe"; //directives
// OPTION_PF = "-pf"; //directives
// OPTION_PL = "-pl"; //directives
// OPTION_PM = "-pm"; //directives
// OPTION_PP = "-pp"; //directives
// OPTION_SAFE = "-safe"; //directives
