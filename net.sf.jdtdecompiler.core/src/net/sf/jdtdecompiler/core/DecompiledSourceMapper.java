package net.sf.jdtdecompiler.core;

import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.SourceMapper;

public class DecompiledSourceMapper extends SourceMapper {

    private IPackageFragmentRoot root;
    
    private IDecompiler decompiler;

    public DecompiledSourceMapper(IPath sourcePath, String rootPath,
            Map<?, ?> options, IPackageFragmentRoot root, IDecompiler decompiler) {
        super(sourcePath, rootPath, options);
        this.root = root;
        this.decompiler = decompiler;
    }

    public char[] findSource(String fullJavaName) {
        char[] source = super.findSource(fullJavaName);

        if (source == null) {
            source = invokeDecompiler(fullJavaName);
        }

        return source;
    }

    /*
     * Could not invoke decompiler. You can try another decompiler from the
     * context menu (Decompile With) of this editor.
     */
    
    protected char[] invokeDecompiler(String fullJavaName) {
        char[] source = null;

//        try {
            String decompiled = null;
            String rootPath = determineRootPath(root);
            boolean isArchive = root.isArchive();
            String fullClassName  = determineFullClassName(fullJavaName);

            decompiled = decompiler.decompile(rootPath, isArchive, fullClassName);
            if (decompiled != null) {
                source = decompiled.toCharArray();
            }
//        }
//        catch (Exception e) {
//            JdtDecompilerCorePlugin.logError(e, "Could not invoke decompiler.");
//            StringWriter nestedWriter = new StringWriter();
//            PrintWriter writer = new PrintWriter(nestedWriter);
//            writer.println("/*");
//            writer.println(" * Could not invoke decompiler. You can try another decompiler from the");
//            writer.println(" * context menu (Decompile With) of this editor.");
//            writer.println(" */");
//            writer.println();
//            e.printStackTrace(writer);
//            writer.close();
//            source = nestedWriter.toString().toCharArray();
//        }

        return source;
    }

    private static String determineRootPath(IPackageFragmentRoot root) {
        String rootPath = null;
        try {
            IResource resource = root.getUnderlyingResource();
            if (resource != null) {
                // jar in workspace
                rootPath = resource.getLocation().toOSString();
            } else {
                // external jar
                rootPath = root.getPath().toOSString();
            }
        } catch (JavaModelException e) {
            JdtDecompilerCorePlugin.logError(e, "Unexpected Java model exception");
        }
        return rootPath;
    }

    /**
     * Determine class name.
     * 
     * @param fullJavaName
     *            Package qualified source filename location (e.g.,
     *            "junit/framework/Test.java")
     * @return Package qualified class name (e.g., "org/junit/Test.class")
     */
    private static String determineFullClassName(String fullJavaName) {
        String result = null;

        if (fullJavaName.endsWith(".java")) {
            result = fullJavaName.substring(0, fullJavaName.length() - 5);
            result = result.replace('/', '.');
        }

        return result;
    }

    public void setDecompiler(IDecompiler decompiler) {
        this.decompiler = decompiler;
    }

}