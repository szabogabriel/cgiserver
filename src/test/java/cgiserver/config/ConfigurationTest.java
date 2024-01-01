package cgiserver.config;

import static org.junit.Assert.assertEquals;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

public class ConfigurationTest {

    @Test
    public void testDefaultConfig() throws UnknownHostException {
        Configuration config = new Configuration();

        assertEquals(InetAddress.getByName("localhost"), config.getHost());
        assertEquals(Integer.valueOf(9080), Integer.valueOf(config.getPort()));
        assertEquals("", config.getUrlPrefix());
        assertEquals("./cgi-bin", config.getCgiScriptFolder());
        assertEquals(".", config.getExecDir());
        assertEquals("index", config.getIndex());
        assertEquals(Integer.valueOf(32), Integer.valueOf(config.getParallelThreads()));
        assertEquals(Integer.valueOf(32), Integer.valueOf(config.getSocketBacklog()));
        assertEquals(Boolean.FALSE, Boolean.valueOf(config.isHelp()));
    }

    @Test
    public void testExternalArgsWithoutSuffix() throws UnknownHostException {
        String[] args = new String[]{
            "-cgiFolder", "./cgi-bin-2",
            "-execFolder", "/tmp",
            "-urlPrefix", "urlPrefix",
            "-help",
            "-port", "8080",
            "-threads", "16",
            "-socketBacklog", "24",
            "-index", "index2",
            "-host", "www.google.com"
        };
        Configuration config = new Configuration(args);

        assertEquals(InetAddress.getByName("www.google.com"), config.getHost());
        assertEquals(Integer.valueOf(8080), Integer.valueOf(config.getPort()));
        assertEquals("urlPrefix", config.getUrlPrefix());
        assertEquals("./cgi-bin-2", config.getCgiScriptFolder());
        assertEquals("/tmp", config.getExecDir());
        assertEquals("index2", config.getIndex());
        assertEquals(Integer.valueOf(16), Integer.valueOf(config.getParallelThreads()));
        assertEquals(Integer.valueOf(24), Integer.valueOf(config.getSocketBacklog()));
        assertEquals(Boolean.TRUE, Boolean.valueOf(config.isHelp()));
    }

    @Test
    public void testExternalArgsWithSuffix() throws UnknownHostException {
        String[] args = new String[]{
            "-cgiFolder.1", "./cgi-bin-2",
            "-execFolder.1", "/tmp",
            "-urlPrefix.1", "urlPrefix",
            "-help",
            "-port.1", "8080",
            "-threads.1", "16",
            "-socketBacklog.1", "24",
            "-index.1", "index2",
            "-host.1", "www.google.com"
        };
        Configuration config = new Configuration(args, 1);

        assertEquals(InetAddress.getByName("www.google.com"), config.getHost());
        assertEquals(Integer.valueOf(8080), Integer.valueOf(config.getPort()));
        assertEquals("urlPrefix", config.getUrlPrefix());
        assertEquals("./cgi-bin-2", config.getCgiScriptFolder());
        assertEquals("/tmp", config.getExecDir());
        assertEquals("index2", config.getIndex());
        assertEquals(Integer.valueOf(16), Integer.valueOf(config.getParallelThreads()));
        assertEquals(Integer.valueOf(24), Integer.valueOf(config.getSocketBacklog()));
        assertEquals(Boolean.TRUE, Boolean.valueOf(config.isHelp()));
    }
    
}
