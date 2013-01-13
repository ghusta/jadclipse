package net.sf.jdtdecompiler.core;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class JdtDecompilerCorePlugin extends Plugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "net.sf.jdtdecompiler.core";

    // The shared instance
    private static JdtDecompilerCorePlugin plugin;

    /**
     * The constructor
     */
    public JdtDecompilerCorePlugin() {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        savePluginPreferences();
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static JdtDecompilerCorePlugin getDefault() {
        return plugin;
    }

    public static void logError(Throwable t, String message) {
        getDefault().getLog().log(new Status(Status.ERROR, PLUGIN_ID, 0, message, t));
    }
    
}
