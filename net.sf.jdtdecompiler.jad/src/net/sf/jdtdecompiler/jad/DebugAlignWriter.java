package net.sf.jdtdecompiler.jad;

import java.io.IOException;
import java.io.Writer;

/**
 * Aligns the content of the stream according to Jad line number comments by
 * inserting new line characters. Neighbor lines with the same number are
 * collapsed into one line. Lines that are out of oder are commented as
 * MISALIGNED. All line comments are removed from the stream, block comments
 * left intact. Original empty lines are also removed to improve chances of
 * successful alignment
 * 
 * @author Valdimir Grishchenko
 */
public class DebugAlignWriter extends Writer {
    private int curLine = 0; // line # that's is being output to
    private StringBuffer lineContent = new StringBuffer();
    private Writer out;

    // long write;

    /**
     * Constructs a <code>DebugAlignWriter</code> with the specified
     * <code>writer</code> as its tearget.
     */
    public DebugAlignWriter(Writer out) {
        super(out);
        this.out = out;
    }

    /**
     * Writes whatever is left in internal buffer to the underlying writer, then
     * flushes and closes it.
     * 
     * @see Writer#close()
     */
    public void close() throws IOException {
        if (lineContent.length() != 0) {
            out.write(lineContent.toString());
        }
        out.flush();
        out.close();
        // System.err.println("Time spent in write: " + write);
    }

    /**
     * Flushes the underlying writer.
     * 
     * @see Writer#flush()
     */
    public void flush() throws IOException {
        out.flush();
    }

    /**
     * Performs necessary alignment logic.
     * 
     * @see Writer#write(char[], int, int)
     */
    public void write(char cbuf[], int off, int len) throws IOException {
        // long start = System.currentTimeMillis();
        String aLine;
        int align;

        for (int i = off; i < off + len; i++) {
            switch (cbuf[i]) {
            case '\n':
                aLine = cleanComment(lineContent.toString());
                lineContent.setLength(0);

                if (aLine.length() == 0) {
                    continue;
                }

                if ((align = getAlignTarget(aLine)) != -1) {
                    if (align < curLine) {
                        // not in initial state
                        if (curLine != 0) {
                            out.write('\n');
                        }
                        out.write("/* <-MISALIGNED-> */ ");
                        out.write(aLine);
                        curLine++;
                    } else if (align == curLine) {
                        // System.err.println(align);
                        out.write(aLine);
                    } else {
                        while (align > curLine) {
                            out.write('\n');
                            curLine++;
                        }
                        out.write(aLine);
                    }
                } else {
                    // not in initial state
                    if (curLine != 0) {
                        out.write('\n');
                    }
                    curLine++;
                    out.write(aLine);
                }
                break;
            case '\r': // discard CR's
                break;
            default:
                lineContent.append(cbuf[i]);

            }

        }
        // write += System.currentTimeMillis() - start;
    }

    /**
     * Extracts target line number.
     * 
     * @return target line number or -1 if this line doesn't need to be aligned
     */
    int getAlignTarget(String line) {
        if (!line.startsWith("/*")) {
            return -1;
        }

        int end = line.indexOf("*/", 2);

        if (end == -1) {
            return -1;
        }

        try {
            return Integer.parseInt(line.substring(2, end).trim());
        } catch (Exception e) {
            return -1;
        }

    }

    /**
     * Removes line comment from the supplied line
     * 
     * @return a <code>String</code> which is the original <code>line</code>
     *         with line commens removed. If the original line was a comment
     *         line empty string is returned
     */
    String cleanComment(String line) {
        int comment = line.indexOf("//");

        if (comment == -1) {
            return line;
        }

        if (comment == 0 || line.trim().startsWith("//")) {
            return "";
        }

        return line.substring(0, comment);
    }

    public String toString() {
        return out.toString();
    }

}
