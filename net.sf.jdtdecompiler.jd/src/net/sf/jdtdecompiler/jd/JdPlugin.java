package net.sf.jdtdecompiler.jd;

import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class JdPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "net.sf.jdtdecompiler.jd";

    // The shared instance
    private static JdPlugin plugin;

    /**
     * The constructor
     */
    public JdPlugin() {
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
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
    public static JdPlugin getDefault() {
        return plugin;
    }

    public static void logError(Throwable t, String message) {
        getDefault().getLog().log(
                new Status(Status.ERROR, PLUGIN_ID, 0, message, t));
    }

}
