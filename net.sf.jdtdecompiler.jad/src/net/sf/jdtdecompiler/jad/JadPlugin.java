package net.sf.jdtdecompiler.jad;

import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class JadPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "net.sf.jdtdecompiler.jad";

    public static final String PREF_TMP_DIR = "net.sf.jdtdecompiler.jad.tmpdir";
    public static final String PREF_CMD = "net.sf.jdtdecompiler.jad.cmd";
    public static final String PREF_ALIGN = "net.sf.jdtdecompiler.jad.align";
    public static final String PREF_REUSE_BUFFER = "net.sf.jdtdecompiler.jad.reusebuff";
    public static final String PREF_IGNORE_EXISTING = "net.sf.jdtdecompiler.jad.alwaysuse";

    // The shared instance
    private static JadPlugin plugin;

    /**
     * The constructor
     */
    public JadPlugin() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static JadPlugin getDefault() {
        return plugin;
    }

    public static void logError(Throwable t, String message) {
        getDefault().getLog().log(new Status(Status.ERROR, PLUGIN_ID, 0, message, t));
    }

    public String getTmpDir() {
        return getPreferenceStore().getString(JadPlugin.PREF_TMP_DIR);
    }
}
