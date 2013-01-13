package net.sf.jdtdecompiler.jd;

import net.sf.jdtdecompiler.core.IDecompiler;

/**
 * This implementation of <code>IDecompiler</code> uses JD
 * (http://java.decompiler.free.fr/) as the underlying decompiler.
 */
public class JdDecompiler implements IDecompiler {

    /* (non-Javadoc)
     * @see net.sf.jdtdecompiler.core.IDecompiler#decompile(java.lang.String, boolean, java.lang.String)
     */
    public String decompile(String rootPath, boolean isArchive,
            String fullClassName) {
        return "/*\n * Java Decompiler JD is not supported yet.\n * Vote for it: http://java.decompiler.free.fr/?q=node/207\n */";
    }

}
