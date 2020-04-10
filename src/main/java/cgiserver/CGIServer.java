package cgiserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CGIServer implements Runnable {

    private final Configuration CONFIGURATION;
    private final ServerSocket SERVER;
    private final Executor EXECUTOR;
    private final ScriptExecutor SCRIPT_EXECUTOR;

    public CGIServer(final Configuration configuration, ScriptExecutor scriptExecutor) throws IOException {
        this.CONFIGURATION = configuration;
        this.SERVER = new ServerSocket(
            CONFIGURATION.getPort(),
            CONFIGURATION.getSocketBacklog(),
            CONFIGURATION.getHost());
        
        this.SCRIPT_EXECUTOR = scriptExecutor;

        EXECUTOR = Executors.newFixedThreadPool(configuration.getParallelThreads());
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = SERVER.accept();

                EXECUTOR.execute(new SocketHandler(socket, CONFIGURATION, SCRIPT_EXECUTOR));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}