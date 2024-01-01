package cgiserver;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import cgiserver.config.Configuration;
import cgiserver.config.Configurations;
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
        Configurations configs = new Configurations(args);

        System.out.println("Starting.");

        if (configs.isHelp()) {
            printHelp();
            return;
        } else {
            List<Configuration> confs = configs.getConfig();
            for(Configuration it : confs) {
                new Thread(() -> runApp(it)).start(); 
            }
        }
    }

    private static void runApp(Configuration configuration) {
        try {
            new App(configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printHelp() {
        //TODO: print help.
    }
}
