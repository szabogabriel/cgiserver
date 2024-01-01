package cgiserver.exec;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class ScriptExecutorTest {

    @Test
    public void testSimpleExecute() throws IOException {
        ScriptExecutor executor = new ScriptExecutor(new File("/tmp"));

        ByteArrayInputStream bais = new ByteArrayInputStream(new byte[]{});
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        executor.execute(new File("src/test/resources/test.sh"), new String[]{}, bais, baos);

        String dataReceived = baos.toString();

        bais.close();

        assertEquals("Hello, world!\n", dataReceived);
    }

    @Test
    public void testWithUserInput() throws IOException {
        ScriptExecutor executor = new ScriptExecutor(new File("/tmp"));

        ByteArrayInputStream bais = new ByteArrayInputStream(new byte [] {});
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        executor.execute(new File("src/test/resources/testEcho.sh"), new String[]{"User"}, new String[]{}, bais, baos);

        String dataReceived = baos.toString();

        bais.close();

        assertEquals("Hello, User!\n", dataReceived);
    }

    @Test
    public void testEnvironmentSetting() throws IOException {
        ScriptExecutor executor = new ScriptExecutor(new File("/tmp"));

        ByteArrayInputStream bais = new ByteArrayInputStream(new byte [] {});
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        executor.execute(new File("src/test/resources/testEnv.sh"), new String[]{}, new String[]{"CUSTOM=value"}, bais, baos);

        String dataReceived = baos.toString();

        bais.close();

        assertEquals("value\n", dataReceived);  
    }
    
}
