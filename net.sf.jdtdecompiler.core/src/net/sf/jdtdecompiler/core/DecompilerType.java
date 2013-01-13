package net.sf.jdtdecompiler.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;

public class DecompilerType {

    /**
     * "decompiler" element name
     */
    public static String TAG_DECOMPILER = "decompiler";

    /**
     * Attribute "name" of decompiler extension point.
     */
    public static String ATT_NAME = "name";

    /**
     * Attribute "id" of decompiler extension point.
     */
    public static String ATT_ID = "id";

    /**
     * Attribute "class" of decompiler extension point
     */
    public static String ATT_CLASS = "class";

    private static DecompilerType[] cachedTypes;
    
    private final IConfigurationElement configElement;

    private final String id;

    private final String name;

    private DecompilerType(IConfigurationElement element) {
        this.configElement = element;
        id = getAttribute(element, ATT_ID);
        name = getAttribute(element, ATT_NAME);

        // Make sure that class is defined,
        // but don't load it.
        getAttribute(element, ATT_CLASS);
    }

    /**
     * @return Id attribute.
     */
    public String getId() {
        return id;
    }

    /**
     * @return Name attribute.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Lazily created decompiler instance.
     */
    public IDecompiler getDecompiler() {
        IDecompiler decompiler = null;
        try {
            decompiler = (IDecompiler) configElement
                    .createExecutableExtension(ATT_CLASS);
        } catch (CoreException e) {
            JdtDecompilerCorePlugin.logError(e,
                    "Creating decompiler instance failed for id=" + id);
        }
        return decompiler;
    }

    /**
     * Proxy for an IDecompiler instance. Attributes are all resolved but the
     * decompiler itself will only be created upon request.
     * 
     * @param element
     *            IConfigurationElement
     */
    public static DecompilerType[] getTypes() {
        if (cachedTypes != null) {
            return cachedTypes;
        }

        IExtension[] extensions = Platform.getExtensionRegistry()
                .getExtensionPoint(JdtDecompilerCorePlugin.PLUGIN_ID, "decompilers")
                .getExtensions();
        List<DecompilerType> found = new ArrayList<DecompilerType>();
        for (int i = 0; i < extensions.length; i++) {
            IConfigurationElement[] configElements = extensions[i]
                    .getConfigurationElements();
            for (int j = 0; j < configElements.length; j++) {
                DecompilerType proxy = parseType(configElements[j]);
                if (proxy != null)
                    found.add(proxy);
            }
        }
        cachedTypes = found.toArray(new DecompilerType[found.size()]);
        return cachedTypes;
    }
    
    /**
     * Plugin.stop() is supposed to call this method.
     */
    static void disposeTypes() {
        if (cachedTypes == null) {
            return;
        }
        cachedTypes = null;
    }

    private static DecompilerType parseType(
            IConfigurationElement configElement) {
        if (!configElement.getName().equals(TAG_DECOMPILER)) {
            return null;
        }
        try {
            return new DecompilerType(configElement);
        } catch (Exception e) {
            String name = configElement.getAttribute(ATT_NAME);
            if (name == null) {
                name = "[missing name attribute]";
            }
            String msg = "Failed to load decompiler named "
                    + name
                    + " in "
                    + configElement.getDeclaringExtension().getContributor()
                            .getName();
            JdtDecompilerCorePlugin.logError(e, msg);
            return null;
        }
    }

    private static String getAttribute(IConfigurationElement configElem,
            String name) {
        String value = configElem.getAttribute(name);
        if (value != null) {
            return value;
        }
        throw new IllegalArgumentException("Missing " + name + " attribute");
    }

}
