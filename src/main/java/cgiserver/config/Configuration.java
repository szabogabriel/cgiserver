package cgiserver.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

import lombok.Data;

@Data
public class Configuration {

    private String urlPrefix = "";
    private String cgiScriptFolder = "./cgi-bin";
    private String execDir = ".";
    private String index = "index";
    private boolean help = false;
    private int port = 9080;
    private int parallelThreads = 32;
    private int socketBacklog = 32;
    private InetAddress host;

    public Configuration() throws UnknownHostException {
        host = InetAddress.getByName("localhost");
    }

    public Configuration(String[] args) throws UnknownHostException {
        host = InetAddress.getByName("localhost");

        for (int i = 0; i < args.length; i++) {
            switch(args[i]) {
                case "-cgiFolder" : cgiScriptFolder = args[++i]; break;
                case "-execFolder" : execDir = args[++i]; break;
                case "-urlPrefix" : urlPrefix = args[++i]; break;
                case "-help" : help = true; break;
                case "-port" : port = Integer.parseInt(args[++i]); break;
                case "-threads" : parallelThreads = Integer.parseInt(args[++i]); break;
                case "-socketBacklog" : socketBacklog = Integer.parseInt(args[++i]); break;
                case "-host" : host = InetAddress.getByName(args[++i]);
                case "-index" : index = args[++i];
            }
        }
    }
}