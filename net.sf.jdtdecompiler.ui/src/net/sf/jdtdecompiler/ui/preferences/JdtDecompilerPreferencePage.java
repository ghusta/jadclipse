package net.sf.jdtdecompiler.ui.preferences;

import net.sf.jdtdecompiler.core.DecompilerType;
import net.sf.jdtdecompiler.ui.JdtDecompilerUiPlugin;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Main preference page
 * 
 * @author Valdimir Grishchenko
 * @author Johann Gyger
 */
public class JdtDecompilerPreferencePage extends FieldEditorPreferencePage
        implements IWorkbenchPreferencePage {

    public JdtDecompilerPreferencePage() {
        super(FieldEditorPreferencePage.GRID);
        setPreferenceStore(JdtDecompilerUiPlugin.getDefault().getPreferenceStore());
    }

    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
    }

    @Override
    protected void createFieldEditors() {
        DecompilerType[] decompilerTypes = DecompilerType.getTypes();
        String[][] entryNamesAndValues = new String[decompilerTypes.length][2];
        for (int i = 0; i < decompilerTypes.length; i++) {
            DecompilerType t = decompilerTypes[i];
            entryNamesAndValues[i][0] = t.getName();
            entryNamesAndValues[i][1] = t.getId();
        }
        ComboFieldEditor decompiler = new ComboFieldEditor(
                JdtDecompilerUiPlugin.PREF_DECOMPILER,  "Decompiler: ", entryNamesAndValues,
                getFieldEditorParent());
        addField(decompiler);

        BooleanFieldEditor eclipseFormatter = new BooleanFieldEditor(
                JdtDecompilerUiPlugin.PREF_USE_ECLIPSE_FORMATTER,
                "Use Eclipse code formatter (reformats decompiler output).",
                getFieldEditorParent());
        addField(eclipseFormatter);
    }

    public void init(IWorkbench arg0) {
    }
}
