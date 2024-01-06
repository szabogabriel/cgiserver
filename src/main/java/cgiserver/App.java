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
        StringBuilder sb = new StringBuilder();

        sb.append("A simple CGI server implementation. It takes the follwing attributes:\n\n");
        sb.append("  -cgiFolder     folder with the CGI script binaries and/or scripts.\n\n");
        sb.append("  -execFolder    folder in which to execute the CGI script from.\n\n");
        sb.append("  -urlPrefix     URL paht prefixing the script names.\n\n");
        sb.append("  -help / -h     print this help.\n\n");
        sb.append("  -port          port to listen on.\n\n");
        sb.append("  -threads       number of worker threads. By default 32.\n\n");
        sb.append("  -socketBacklog size of HTTP request held in the socket's backlog.\n\n");
        sb.append("  -host          hostname to listen on.\n\n");
        sb.append("  -index         the default script to be executed, if no path set in URL.\n\n");
        sb.append("Every attribute except for the -h/-help can be entered multiple times with\n");
        sb.append("a numbered suffix at the end. E.g. -port.1 and -port.2. In this case the\n");
        sb.append("server will create two ServerSocket instances and will listen on both ports.\n\n");
        sb.append("Example:\n\n");
        sb.append("java -cp cgiserver-1.0-SNAPSHOT.jar cgiserver.App -port.1 18081 -port.2 18082");

        System.out.println(sb.toString());
    }
}
