package net.sf.jdtdecompiler.jad;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sf.jdtdecompiler.core.JdtDecompilerCorePlugin;

/**
 * Class file extraction (including inner classes) from a jar/zip archive.
 * 
 * @author V. Grishchenko
 * @author Johann Gyger
 */
public class JarClassExtractor {

    /**
     * Extract className including all inner classes to directory targetPath.
     * 
     * @param archivePath
     *            Path to jar/zip archive
     * @param packagePath
     *            (foo.Bar -> foo/) Package path of class
     * @param className
     *            Unqualified class name (foo.Bar -> Bar)
     * @param targetDir
     *            Target directory to extract class files to
     */
    public static void extract(String archivePath, String packagePath, String className, String targetDir) {
        String prefix = null;
        try {
            prefix = packagePath + className;
            ZipFile archive = new ZipFile(archivePath);
            Enumeration<? extends ZipEntry> entries = archive.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String entryName = entry.getName();
                if (entryName.startsWith(prefix)) {
                    extractClass(targetDir, archive, entry);
                }
            }
        } catch (IOException e) {
            JdtDecompilerCorePlugin.logError(e, "Unable to extract class " + prefix
                    + " from " + archivePath + " to " + targetDir);
        }
    }

    private static void extractClass(String targetDir, ZipFile archive, ZipEntry entry) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = archive.getInputStream(entry);

            // Determine basename (class filename without parent dirs)
            String baseName = entry.getName();
            int dirIndex = baseName.lastIndexOf('/');
            if (dirIndex != -1) {
                baseName = baseName.substring(dirIndex + 1);
            }

            out = new FileOutputStream(targetDir + File.separator + baseName);
            byte[] buffer = new byte[2048];
            int amountRead;
            while ((amountRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, amountRead);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

}