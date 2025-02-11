import java.io.Reader;

public class DoubleBuffer {
    private final Reader reader;
    private final char[] buf1;
    private final char[] buf2;
    private final int bufferSize = 10;
    private int activeBuffer;
    private int bufIndex;
    private int bufCount;
    private boolean eof = false;
    
    public DoubleBuffer(Reader reader) throws Exception {
        this.reader = reader;
        buf1 = new char[bufferSize];
        buf2 = new char[bufferSize];
        activeBuffer = 1;
        fillBuffer();
    }
    
    // fill the current buffer with new chars
    private void fillBuffer() throws Exception {
        if (activeBuffer == 1) {
            bufCount = reader.read(buf1, 0, bufferSize);
        } else {
            bufCount = reader.read(buf2, 0, bufferSize);
        }
        if (bufCount == -1) {
            eof = true;
            bufCount = 0;
        }
        bufIndex = 0;
    }
    
    // get the next character from our current buffer
    public char getNextChar() throws Exception {
        if (bufIndex >= bufCount) {
            activeBuffer = (activeBuffer == 1) ? 2 : 1;
            fillBuffer();
            if (eof)
                return (char) -1;
        }
        return (activeBuffer == 1) ? buf1[bufIndex++] : buf2[bufIndex++];
    }
    
    // move one char back in the buffer
    public void goBack() {
        if (bufIndex > 0)
            bufIndex--;
    }
}
