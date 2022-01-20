package cgiserver;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

import cgiserver.config.Configuration;
import cgiserver.exec.ScriptExecutor;
import cgiserver.http.CGIServer;

public final class App {

    private CGIServer server;

    private ScriptExecutor scriptExecutor;

    private App(Configuration args) throws UnknownHostException, IOException {
        scriptExecutor = new ScriptExecutor(new File(args.getExecDir()));

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
