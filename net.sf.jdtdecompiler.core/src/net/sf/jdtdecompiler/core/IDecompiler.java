package net.sf.jdtdecompiler.core;

/**
 * Generic Java decompiler interface.
 * 
 * @author Johann Gyger
 */
public interface IDecompiler {

    /**
     * Decompiles a class file.
     * 
     * @param rootPath
     *            Path to directory/archive containing the class
     * @param isArchive
     *            Is rootPath a zip/jar archive?
     * @param className
     *            Fully qualified class name
     * @return Decompiled source
     */
    public String decompile(String rootPath, boolean isArchive, String className);

}