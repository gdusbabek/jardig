package org.dusbabek.tools.jardig;

import java.io.OutputStream;
import java.io.IOException;
import javax.swing.JTextArea;

/**
 * OutputStream that pipes to a text area.  Handy if you want to direct a
 * PrintStream (like System.out) to a text area.
 *
 * This class is not thread safe at all.  To do that would require an input
 * queue (maybe one per thread), and I don't feel like it.  This is cutting
 * the mustard for now.
 */
class TextAreaOutputStream extends OutputStream {

    private final JTextArea txt;
    TextAreaOutputStream(final JTextArea txt) {
        this.txt = txt;
    }

    public void write(int b) throws IOException {
        txt.append(new String(new char[]{(char)b}));
        txt.setCaretPosition(txt.getText().length());
    }

    public void write(byte b[]) throws IOException {
        txt.append(new String(b));
        txt.setCaretPosition(txt.getText().length());
    }

    public void write(byte b[], int off, int len) throws IOException {
        txt.append(new String(b,off,len));
        txt.setCaretPosition(txt.getText().length());
    }

    public void flush() throws IOException {
        super.flush();
    }

    public void close() throws IOException {
        txt.setEditable(false);
        super.close();
    }
}
