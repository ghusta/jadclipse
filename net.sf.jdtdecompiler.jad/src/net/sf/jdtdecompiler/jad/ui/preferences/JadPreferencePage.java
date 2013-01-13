package net.sf.jdtdecompiler.jad.ui.preferences;

import net.sf.jdtdecompiler.jad.JadPlugin;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Main preference page
 * 
 * @author Valdimir Grishchenko
 */
public class JadPreferencePage extends FieldEditorPreferencePage
        implements IWorkbenchPreferencePage {

    public JadPreferencePage() {
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
        // command line
        StringFieldEditor cmd = new StringFieldEditor(JadPlugin.PREF_CMD,
                "Path to decompiler:", getFieldEditorParent());
        cmd.setEmptyStringAllowed(false);
        addField(cmd);

        // working dir
        StringFieldEditor tempd = new StringFieldEditor(JadPlugin.PREF_TMP_DIR,
                "Directory for temporary files:", getFieldEditorParent());
        tempd.setEmptyStringAllowed(false);
        addField(tempd);

        BooleanFieldEditor reusebuf = new BooleanFieldEditor(
                JadPlugin.PREF_REUSE_BUFFER, "Reuse code buffer",
                getFieldEditorParent());
        addField(reusebuf);

        BooleanFieldEditor alwaysUse = new BooleanFieldEditor(
                JadPlugin.PREF_IGNORE_EXISTING, "Ignore existing source",
                getFieldEditorParent());
        addField(alwaysUse);
    }

    /**
     * @see IWorkbenchPreferencePage#init(IWorkbench)
     */
    public void init(IWorkbench arg0) {
    }

}
