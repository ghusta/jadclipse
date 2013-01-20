package net.sf.jdtdecompiler.core;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class JdtDecompilerCorePlugin extends Plugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "net.sf.jdtdecompiler.core";

    // The shared instance
    private static JdtDecompilerCorePlugin plugin;

    public JdtDecompilerCorePlugin() {
    }

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
		InstanceScope.INSTANCE.getNode(PLUGIN_ID).flush();
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
