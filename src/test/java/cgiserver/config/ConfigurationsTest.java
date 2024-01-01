package cgiserver.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.junit.Test;

public class ConfigurationsTest {

    @Test
    public void testOneConfig() throws UnknownHostException {
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

        Configurations configs = new Configurations(args);

        assertEquals(Integer.valueOf(1), Integer.valueOf(configs.getConfig().size()));
    }

    @Test
    public void testTwoConfigs() throws UnknownHostException {
        String[] args = new String[]{
            "-cgiFolder.1", "./cgi-bin-1",
            "-execFolder.1", "/tmp",
            "-urlPrefix.1", "urlPrefix",
            "-help",
            "-port.1", "8081",
            "-threads.1", "16",
            "-socketBacklog.1", "24",
            "-index.1", "index-1",
            "-host.1", "www.google.com",
            "-cgiFolder.2", "./cgi-bin-2",
            "-execFolder.2", "/tmp-2",
            "-urlPrefix.2", "urlPrefix-2",
            "-port.2", "8082",
            "-threads.2", "17",
            "-socketBacklog.2", "25",
            "-index.2", "index-2",
            "-host.2", "www.amazon.com"
        };

        Configurations configs = new Configurations(args);

        List<Configuration> confs = configs.getConfig();

        assertNotNull(confs);
        assertEquals(Integer.valueOf(2), Integer.valueOf(confs.size()));


        Configuration config = confs.get(0);

        assertEquals(InetAddress.getByName("www.google.com"), config.getHost());
        assertEquals(Integer.valueOf(8081), Integer.valueOf(config.getPort()));
        assertEquals("urlPrefix", config.getUrlPrefix());
        assertEquals("./cgi-bin-1", config.getCgiScriptFolder());
        assertEquals("/tmp", config.getExecDir());
        assertEquals("index-1", config.getIndex());
        assertEquals(Integer.valueOf(16), Integer.valueOf(config.getParallelThreads()));
        assertEquals(Integer.valueOf(24), Integer.valueOf(config.getSocketBacklog()));
        assertEquals(Boolean.TRUE, Boolean.valueOf(config.isHelp()));        

        config = confs.get(1);

        assertEquals(InetAddress.getByName("www.amazon.com"), config.getHost());
        assertEquals(Integer.valueOf(8082), Integer.valueOf(config.getPort()));
        assertEquals("urlPrefix-2", config.getUrlPrefix());
        assertEquals("./cgi-bin-2", config.getCgiScriptFolder());
        assertEquals("/tmp-2", config.getExecDir());
        assertEquals("index-2", config.getIndex());
        assertEquals(Integer.valueOf(17), Integer.valueOf(config.getParallelThreads()));
        assertEquals(Integer.valueOf(25), Integer.valueOf(config.getSocketBacklog()));
        assertEquals(Boolean.TRUE, Boolean.valueOf(config.isHelp()));        
    }
    
}
