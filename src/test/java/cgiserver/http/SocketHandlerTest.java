package cgiserver.http;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

import org.junit.Test;

import cgiserver.exec.ScriptExecutor;

public class SocketHandlerTest {

    @Test
    public void testSocketHandler() throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream("GET / HTTP/1.1\n".getBytes());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String scriptFolder = "src/test/resources";
        String index = "test.sh";
        ScriptExecutor scriptExecutor = new ScriptExecutor(new File("/tmp"));

        Socket socket = mock(Socket.class);
        when(socket.getInputStream()).thenReturn(bais);
        when(socket.getOutputStream()).thenReturn(baos);

        SocketHandler socketHandler = new SocketHandler(socket, 0, scriptFolder, scriptExecutor, index);
        socketHandler.run();

        assertEquals("HTTP/1.1 200 OK\nServer: CGI-Server-0.1\nHello, world!\n", baos.toString());
    }
    
}
