package net.sf.jdtdecompiler.jad.ui.preferences;

import net.sf.jdtdecompiler.jad.IJadOptions;
import net.sf.jdtdecompiler.jad.JadPlugin;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

public class JadPreferencesInitializer extends AbstractPreferenceInitializer {
    
    @Override
    public void initializeDefaultPreferences() {
        IEclipsePreferences node = DefaultScope.INSTANCE.getNode(JadPlugin.PLUGIN_ID);
        node.put(JadPlugin.PREF_CMD, "jad");
        node.put(JadPlugin.PREF_TMP_DIR, System.getProperty("user.home") + "/." + JadPlugin.PLUGIN_ID);
        node.putBoolean(JadPlugin.PREF_REUSE_BUFFER, true); // since 2.02
        node.putInt(IJadOptions.OPTION_INDENT_SPACE, 4);
        node.putInt(IJadOptions.OPTION_IRADIX, 10);
        node.putInt(IJadOptions.OPTION_LRADIX, 10);
        node.putInt(IJadOptions.OPTION_SPLITSTR_MAX, 0); // disable
        node.putInt(IJadOptions.OPTION_PI, 0); // disable
        node.putInt(IJadOptions.OPTION_PV, 0); // disable
    }
    
}
