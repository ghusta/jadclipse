package net.sf.jdtdecompiler.ui;

import net.sf.jdtdecompiler.core.DecompilerType;
import net.sf.jdtdecompiler.core.IDecompiler;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class JdtDecompilerUiPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "net.sf.jdtdecompiler.ui";

    public static final String PREF_USE_ECLIPSE_FORMATTER = "net.sf.jdtdecompiler.use_eclipse_formatter";

    public static final String PREF_DECOMPILER = "net.sf.jdtdecompiler.decompiler";

    // The shared instance
    private static JdtDecompilerUiPlugin plugin;
    
    /**
     * The constructor
     */
    public JdtDecompilerUiPlugin() {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
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
    public static JdtDecompilerUiPlugin getDefault() {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given
     * plug-in relative path
     *
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    public static void logError(Throwable t, String message) {
        getDefault().getLog().log(new Status(Status.ERROR, PLUGIN_ID, 0, message, t));
    }

    /**
     * @return Lazily created decompiler instance.
     */
    public static IDecompiler getPreferredDecompiler() {
        IPreferenceStore prefs = getDefault().getPreferenceStore();
        String decompilerId = prefs.getString(PREF_DECOMPILER);
        DecompilerType[] types = DecompilerType.getTypes();
        for (int i = 0; i < types.length; i++) {
            DecompilerType type = types[i];
            if (decompilerId.equals(type.getId())) {
                return type.getDecompiler();
            }
        }

        // No decompiler found, return the first one available.
        if (types.length > 0) {
            return types[0].getDecompiler();
        }
        
        return null;
    }
    
}
