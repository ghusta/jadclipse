package net.sf.jdtdecompiler.jad;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;

import net.sf.jdtdecompiler.core.IDecompiler;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * This implementation of {@link IDecompiler} uses Jad as the underlying
 * decompiler.
 * 
 * @author Vladimir Grishchenko
 * @author Johann Gyger
 */
public class JadDecompiler implements IDecompiler {

    public String decompile(String rootPath, boolean isArchive, String fullClassName) {
        String source = null;

        File workingDir = null;
        String packagePath = extractPackagePath(fullClassName);
        String className = extractClassFilename(fullClassName);
        if (isArchive) {
            String tmpDir = JadPlugin.getDefault().getTmpDir() + "/"
                    + System.currentTimeMillis();
            workingDir = new File(tmpDir);
            workingDir.mkdirs();
            JarClassExtractor.extract(rootPath, packagePath, className, workingDir.getAbsolutePath());
        } else {
            workingDir = new File(rootPath, packagePath);
        }
        source = jad(workingDir, className);

        return source;
    }

    /**
     * Extract package path from className: foo.bar.Test -> foo/bar/
     */
    private String extractPackagePath(String className) {
        String packagePath = null;

        // Strip class
        int index = className.lastIndexOf('.');
        if (index != -1) {
            packagePath = className.substring(0, index + 1).replace('.', '/');
        }

        return packagePath;
    }

    /**
     * Extract unqualified class name from className: foo.bar.Test -> Test
     */
    private String extractClassFilename(String className) {
        String classFileName = className;

        // Strip package
        int index = className.lastIndexOf('.');
        if (index != -1) {
            classFileName = className.substring(index + 1, className.length());
        }

        return classFileName;
    }

    /**
     * Performs a <code>Runtime.exec()</code> on jad executable with selected
     * options.
     */
    public String jad(File workingDir, String className) {
        String source = null;

        try {
            Writer outWriter = createOutWriter();
            Writer errWriter = createOutWriter();
            String[] cmdLine = buildCmdLine(className);

            Process p = Runtime.getRuntime().exec(cmdLine, new String[] {}, workingDir);
            StreamRedirectThread outRedirect = new StreamRedirectThread(
                    "output_reader", p.getInputStream(), outWriter);
            StreamRedirectThread errRedirect = new StreamRedirectThread(
                    "error_reader", p.getErrorStream(), errWriter);

            outRedirect.start();
            errRedirect.start();
            int rc = p.waitFor(); // wait for jad to finish
            outRedirect.join(); // wait for stdout redirect
            errRedirect.join(); // wait for stderr redirect

            if (rc != 0) {
                JadPlugin.logError(new Throwable(),
                        "Invocation of jad failed: " + Arrays.toString(cmdLine)
                                + " in working dir: " + workingDir);
                return null;
            }

            source = outWriter.toString();
            if (source == null || source.length() == 0) {
                JadPlugin.logError(new Throwable(), "Couldn't get source from jad");
                return null;
            }

            String err = errWriter.toString();
            if (err != null && err.length() > 1) {
                err = "// Messages from Jad:\n" + err;
                err = err.replace("\r", "");
                err = err.replace("\n", "\n// ");
                source = source + "\n" + err;
            }
        } catch (Exception e) {
            JadPlugin.logError(e, "Invocation of jad failed.");
        }

        return source;
    }

    private Writer createOutWriter() {
        IPreferenceStore settings = JadPlugin.getDefault().getPreferenceStore();
        boolean jadSpitsOutLineNumber = settings.getBoolean(IJadOptions.OPTION_LNC); 
        boolean prefAlign = settings.getBoolean(JadPlugin.PREF_ALIGN);
        
        Writer outWriter = new StringWriter();
        if (jadSpitsOutLineNumber && prefAlign) {
            return outWriter = new DebugAlignWriter(outWriter);
        }
        
        return outWriter;
    }

    private String[] buildCmdLine(String classFileName) {
        ArrayList<String> cmdLine = new ArrayList<String>();
        IPreferenceStore settings = JadPlugin.getDefault().getPreferenceStore();

        // command and special options
        cmdLine.add(settings.getString(JadPlugin.PREF_CMD));
        cmdLine.add(IJadOptions.OPTION_SENDSTDOUT);

        String indent = settings.getString(IJadOptions.OPTION_INDENT_SPACE);
        if (indent.equals(IJadOptions.USE_TAB)) {
            cmdLine.add(IJadOptions.OPTION_INDENT_TAB);
        }
        else {
            try {
                Integer.parseInt(indent);
                cmdLine.add(IJadOptions.OPTION_INDENT_SPACE + indent);
            } catch (Exception e) {
            }
        }

        // toggles
        for (int i = 0; i < IJadOptions.TOGGLE_OPTION.length; i++) {
            if (settings.getBoolean(IJadOptions.TOGGLE_OPTION[i])) {
                cmdLine.add(IJadOptions.TOGGLE_OPTION[i]);
            }
        }

        // integers, 0 means disabled
        int iValue;
        for (int i = 0; i < IJadOptions.VALUE_OPTION_INT.length; i++) {
            iValue = settings.getInt(IJadOptions.VALUE_OPTION_INT[i]);
            if (iValue > 0) {
                cmdLine.add(IJadOptions.VALUE_OPTION_INT[i] + iValue);
            }
        }

        // strings, "" means disabled
        String sValue;
        for (int i = 0; i < IJadOptions.VALUE_OPTION_STRING.length; i++) {
            sValue = settings.getString(IJadOptions.VALUE_OPTION_STRING[i]);
            if (sValue != null && sValue.length() > 0) {
                cmdLine.add(IJadOptions.VALUE_OPTION_STRING[i] + " " + sValue);
            }

        }

        cmdLine.add(classFileName);
        return cmdLine.toArray(new String[cmdLine.size()]);
    }

}
