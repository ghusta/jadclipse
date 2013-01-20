package net.sf.jdtdecompiler.jd;

import net.sf.jdtdecompiler.core.IDecompiler;

/**
 * This implementation of {@link IDecompiler} uses
 * <a href="http://java.decompiler.free.fr/">JD</a>
 * as the underlying decompiler.
 */
public class JdDecompiler implements IDecompiler {

    public String decompile(String rootPath, boolean isArchive,
            String fullClassName) {
        return "/*\n * Java Decompiler JD is not supported yet.\n * Vote for it: http://java.decompiler.free.fr/?q=node/207\n */";
    }

}
