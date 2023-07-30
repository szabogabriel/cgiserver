package cgiserver.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import cgiserver.config.Configuration;
import cgiserver.exec.ScriptExecutor;

public class CGIServer implements Runnable {

    private final ServerSocket SERVER;
    private final Executor EXECUTOR;
    private final ScriptExecutor SCRIPT_EXECUTOR;
    private final int PREFIX_LENGTH;
    private final String SCRIPT_FOLDER;
    private final String INDEX;

    public CGIServer(final Configuration configuration, ScriptExecutor scriptExecutor) throws IOException {
        this.PREFIX_LENGTH = configuration.getUrlPrefix().length();
        this.SCRIPT_FOLDER = configuration.getCgiScriptFolder();
        this.INDEX = configuration.getIndex();
        this.SERVER = new ServerSocket(
        		configuration.getPort(),
        		configuration.getSocketBacklog(),
        		configuration.getHost());
        
        this.SCRIPT_EXECUTOR = scriptExecutor;

        EXECUTOR = Executors.newFixedThreadPool(configuration.getParallelThreads());
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = SERVER.accept();

                EXECUTOR.execute(new SocketHandler(socket, PREFIX_LENGTH, SCRIPT_FOLDER, SCRIPT_EXECUTOR, INDEX));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}