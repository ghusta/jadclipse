package net.sf.jdtdecompiler.jad;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

/**
 * StreamRedirectThread is a thread which copies it's input to it's output and
 * terminates when it completes.
 * 
 * @author Valdimir Grishchenko
 */
class StreamRedirectThread extends Thread {

    private final Reader in;
    private final Writer out;
    private Exception ex;

    private static final int BUFFER_SIZE = 2048;

    StreamRedirectThread(String name, InputStream in, Writer out) {
        super(name);
        this.in = new InputStreamReader(in);
        this.out = out;
        setPriority(Thread.MAX_PRIORITY - 1);
    }

    /**
     * Copy.
     */
    public void run() {
        try {
            char[] cbuf = new char[BUFFER_SIZE];
            int count;
            while ((count = in.read(cbuf, 0, BUFFER_SIZE)) >= 0) {
                out.write(cbuf, 0, count);
                out.flush();
            }
        } catch (IOException exc) {
            // System.err.println("Child I/O Transfer - " + exc);
            ex = exc;
        }
    }

    public Exception getException() {
        return ex;
    }
}