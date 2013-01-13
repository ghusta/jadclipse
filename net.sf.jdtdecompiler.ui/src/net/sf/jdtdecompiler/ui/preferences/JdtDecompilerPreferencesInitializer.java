package net.sf.jdtdecompiler.ui.preferences;

import net.sf.jdtdecompiler.ui.JdtDecompilerUiPlugin;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

public class JdtDecompilerPreferencesInitializer extends AbstractPreferenceInitializer {
    
    @Override
    public void initializeDefaultPreferences() {
        IEclipsePreferences node = DefaultScope.INSTANCE.getNode(JdtDecompilerUiPlugin.PLUGIN_ID);
        node.put(JdtDecompilerUiPlugin.PREF_DECOMPILER, "net.sf.jdtdecompiler.jad.JadDecompiler");
    }
}
