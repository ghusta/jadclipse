package net.sf.jdtdecompiler.ui.fieldeditors;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * Field editor that allows no white space
 * 
 * @author Valdimir Grishchenko
 */
public class NoWSFieldEditor extends StringFieldEditor {
    public NoWSFieldEditor(String name, String labelText, Composite parent) {
        super(name, labelText, parent);
        setErrorMessage("White space is not allowed here");
    }

    protected boolean doCheckState() {
        String value = getStringValue();

        if (value == null) {
            return true;
        }

        return value.indexOf(' ') == -1 && value.indexOf('\t') == -1
                && value.indexOf('\r') == -1 && value.indexOf('\n') == -1;
    }
}
