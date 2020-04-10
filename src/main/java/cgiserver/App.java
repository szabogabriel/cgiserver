package cgiserver;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

public final class App {

    private CGIServer server;

    private ScriptExecutor scriptExecutor;

    private App(Configuration args) throws UnknownHostException, IOException {
        scriptExecutor = new ScriptExecutor(new File(args.getExecDir()), args.getParallelThreads() * 3);

        server = new CGIServer(args, scriptExecutor);
        server.run();
    }

    public static void main(String[] args) throws UnknownHostException, IOException {
        Configuration config = new Configuration(args);

        System.out.println("Starting.");

        if (config.isHelp()) {
            printHelp();
            return;
        } else {
            new App(config);
        }
    }

    private static void printHelp() {
        //TODO: print help.
    }
}
