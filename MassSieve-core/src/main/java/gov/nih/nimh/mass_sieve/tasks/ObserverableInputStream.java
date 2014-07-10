package gov.nih.nimh.mass_sieve.tasks;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Alex Turbin (alex.academATgmail.com)
 */
public class ObserverableInputStream extends FilterInputStream {

    private long nread;
    private final InputStreamObserver observer;

    public ObserverableInputStream(InputStream is, InputStreamObserver observer) {
        super(is);

        this.observer = observer;
    }

    @Override
    public int read() throws IOException {
        int c = super.read();
        if (c >= 0 && observer != null) {
            observer.onRead(++nread);
        }
        return c;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int nr = super.read(b);
        if (nr > 0 && observer != null) {
            observer.onRead(nread += nr);
        }

        return nr;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int nr = super.read(b, off, len);
        if (nr > 0 && observer != null) {
            observer.onRead(nread += nr);
        }

        return nr;
    }

    @Override
    public void close() throws IOException {
        super.close();
    }

}
