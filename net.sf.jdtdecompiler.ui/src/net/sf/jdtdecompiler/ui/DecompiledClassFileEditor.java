
package net.sf.jdtdecompiler.ui;

import java.util.Map;

import net.sf.jdtdecompiler.core.DecompiledSourceMapper;
import net.sf.jdtdecompiler.core.DecompilerType;
import net.sf.jdtdecompiler.core.IDecompiler;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jdt.internal.core.SourceMapper;
import org.eclipse.jdt.internal.ui.javaeditor.ClassFileEditor;
import org.eclipse.jdt.internal.ui.javaeditor.IClassFileEditorInput;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchActionConstants;

/**
 * Subclass of standard ClassFileEditor which hijacks the source mapper to
 * decompile class files which don't have a source attachment. This means that
 * instead of presenting bytecode for the methods, the selected class is
 * decompiled on the fly with a customizable decompiler. The rest of the
 * functionality of ClassFileEditor remains untouched.
 * 
 * @author Johann Gyger
 */
public class DecompiledClassFileEditor extends ClassFileEditor {

    private IClassFileEditorInput classEditorInput;

    private final IPreferenceChangeListener preferenceChangeListener = new IPreferenceChangeListener() {
        public void preferenceChange(PreferenceChangeEvent event) {
            if (event.getKey().equals(JdtDecompilerUiPlugin.PREF_DECOMPILER)) {
                updateDecompilerInSourceMapper(JdtDecompilerUiPlugin.getPreferredDecompiler());
            }
        }};

    protected IEditorInput transformEditorInput(IEditorInput input) {
        input = super.transformEditorInput(input);

        if (input instanceof IClassFileEditorInput) {
            classEditorInput = (IClassFileEditorInput) input;
            PackageFragmentRoot root = extractPackageFragmentRoot();
            if (root != null) {
                hijackSourceMapper(root);
            }
        }

        return input;
    }

    private PackageFragmentRoot extractPackageFragmentRoot() {
        if (classEditorInput == null) {
            return null;
        }
        IClassFile file = classEditorInput.getClassFile();

        IJavaElement element = file.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
        if (element instanceof PackageFragmentRoot) {
            return (PackageFragmentRoot) element;
        }
        
        return null;
    }

    /*
     * @see AbstractTextEditor#editorContextMenuAboutToShow(IMenuManager)
     */
    public void editorContextMenuAboutToShow(IMenuManager menu) {
        super.editorContextMenuAboutToShow(menu);

        MenuManager decompilerMenu = new MenuManager("Decompile With");
        
        DecompilerType[] types = DecompilerType.getTypes();
        for (int i = 0; i < types.length; i++) {
            DecompilerType type = types[i];
            decompilerMenu.add(new DecompileWithAction(this, type));
        }
        decompilerMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

        menu.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, decompilerMenu);
    }

    private void hijackSourceMapper(PackageFragmentRoot root) {
        IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(JdtDecompilerUiPlugin.PLUGIN_ID);
        preferences.addPreferenceChangeListener(preferenceChangeListener);
        
        try {
            SourceMapper mapper = root.getSourceMapper();
            if (!(mapper instanceof DecompiledSourceMapper)) {
                IDecompiler decompiler = JdtDecompilerUiPlugin.getPreferredDecompiler();

                IPath sourcePath;
                sourcePath = root.getSourceAttachmentPath();
                if (sourcePath == null) {
                    // attach root to itself
                    sourcePath = root.getPath();
                }

                String rootPath = null;
                IPath iRootPath = root.getSourceAttachmentRootPath();
                if (iRootPath != null) {
                    rootPath = iRootPath.toOSString();
                }

                Map<?, ?> options = root.getJavaProject().getOptions(true);

                DecompiledSourceMapper newMapper = new DecompiledSourceMapper(
                        sourcePath, rootPath, options, root, decompiler);
                root.setSourceMapper(newMapper);
            }
        } catch (JavaModelException e) {
            JdtDecompilerUiPlugin.logError(e,
                    "Source mapper could not be hijacked for package fragment root "
                            + root.getPath());
        }
    }

    private void updateDecompilerInSourceMapper(IDecompiler decompiler) {
        PackageFragmentRoot root = extractPackageFragmentRoot();
        if (root == null) {
            return;
        }

        SourceMapper sourceMapper = root.getSourceMapper();
        if (sourceMapper instanceof DecompiledSourceMapper) {
            DecompiledSourceMapper decompiledMapper = (DecompiledSourceMapper) sourceMapper;
            decompiledMapper.setDecompiler(decompiler);
        }
    }

    public void forceDecompile() {
        try {
            classEditorInput.getClassFile().close();
            inputChanged(classEditorInput);
        } catch (Exception e) {
            JdtDecompilerUiPlugin.logError(e, "Could not force a recompile.");
        }
    }
    
    public void decompileWith(IDecompiler decompiler) {
        try {
            updateDecompilerInSourceMapper(decompiler);
            forceDecompile();
        } catch (Exception e) {
            JdtDecompilerUiPlugin.logError(e, "Could not decompile with: " + decompiler);
        }
    }

    public void dispose() {
	    IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(JdtDecompilerUiPlugin.PLUGIN_ID);
	    preferences.removePreferenceChangeListener(preferenceChangeListener);
        super.dispose();
    }

}
