package cgiserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StreamCopy implements Runnable {

    private final Logger LOG = Logger.getLogger(StreamCopy.class.getName());
    private final String NAME;

    private final InputStream IN;
    private final OutputStream OUT;

    public StreamCopy(String name, InputStream in, OutputStream out) {
        this.NAME = name;
        this.IN = in;
        this.OUT = out;
    }

    @Override
    public void run() {
        LOG.entering(getClass().getName(), "run");
        LOG.log(Level.FINE, "Running: " + NAME);

        int bufferSize = 2048;
        byte[] buffer = new byte[bufferSize];
        int read;

        try {
            while ((read = IN.read(buffer)) > 0) {
                OUT.write(buffer, 0, read);
            }
        } catch (IOException e) {
            LOG.log(Level.FINE, "Stream closed for " + NAME);
        }
        LOG.exiting(getClass().getName(), "run");
    }

}