import java.io.Reader;

public class DoubleBuffer {
    private final Reader in;
    private final char[] buffer1;
    private final char[] buffer2;
    private final int bufferSize = 10;
    private int currentBuffer;
    private int pos;
    private int count;
    private boolean eof = false;
    
    public DoubleBuffer(Reader in) throws Exception {
        this.in = in;
        buffer1 = new char[bufferSize];
        buffer2 = new char[bufferSize];
        currentBuffer = 1;
        fillBuffer();
    }
    
    private void fillBuffer() throws Exception {
        if (currentBuffer == 1) {
            count = in.read(buffer1, 0, bufferSize);
        } else {
            count = in.read(buffer2, 0, bufferSize);
        }
        if (count == -1) {
            eof = true;
            count = 0;
        }
        pos = 0;
    }
    
    public char nextChar() throws Exception {
        if (pos >= count) {
            currentBuffer = (currentBuffer == 1) ? 2 : 1;
            fillBuffer();
            if (eof)
                return (char)-1;
        }
        return (currentBuffer == 1) ? buffer1[pos++] : buffer2[pos++];
    }
    
    public void unread() {
        if (pos > 0)
            pos--;
    }
}
