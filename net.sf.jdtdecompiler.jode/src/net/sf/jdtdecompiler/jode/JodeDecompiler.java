package net.sf.jdtdecompiler.jode;

import java.io.IOException;
import java.io.StringWriter;

import net.sf.jdtdecompiler.core.IDecompiler;
import net.sf.jode.bytecode.ClassInfo;
import net.sf.jode.bytecode.ClassPath;
import net.sf.jode.decompiler.ClassAnalyzer;
import net.sf.jode.decompiler.ImportHandler;
import net.sf.jode.decompiler.TabbedPrintWriter;

/**
 * This implementation of {@link IDecompiler} uses
 * <a href="http://jode.sourceforge.net/">JODE</a>
 * as the underlying decompiler.
 */
public class JodeDecompiler implements IDecompiler {

    public String decompile(String rootPath, boolean isArchive, String fullClassName) {
        String source = null;

        try {
            ClassPath cp = new ClassPath(rootPath);
            ImportHandler ih = new ImportHandler(cp);
            ClassInfo ci = cp.getClassInfo(fullClassName);
            ClassAnalyzer analyzer = new ClassAnalyzer(ci, ih);

            StringWriter sw = new StringWriter();
            TabbedPrintWriter writer = new TabbedPrintWriter(sw);
            
            analyzer.dumpJavaFile(writer);
            writer.close();
            source = sw.toString();
        } catch (IOException e) {
            JodePlugin.logError(e, "Unable to extract source with JODE.");
        }

        return source;
    }
}

