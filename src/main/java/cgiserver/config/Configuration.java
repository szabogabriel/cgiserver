package cgiserver.config;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        this(args, "");
    }

    public Configuration(String[] args, int suffix) throws UnknownHostException {
        this(args, "." + suffix);
    }

    private Configuration(String[] args, String suffix) throws UnknownHostException {
        host = InetAddress.getByName("localhost");

        List<String> argsFiltered = Arrays.asList(args).stream().filter(e -> e.endsWith(suffix)).collect(Collectors.toList());

        for (int i = 0; i < argsFiltered.size(); i++) {
            switch(args[i]) {
                case "-cgiFolder" : cgiScriptFolder = argsFiltered.get(++i); break;
                case "-execFolder" : execDir = argsFiltered.get(++i); break;
                case "-urlPrefix" : urlPrefix = argsFiltered.get(++i); break;
                case "-help" : help = true; break;
                case "-port" : port = Integer.parseInt(argsFiltered.get(++i)); break;
                case "-threads" : parallelThreads = Integer.parseInt(argsFiltered.get(++i)); break;
                case "-socketBacklog" : socketBacklog = Integer.parseInt(argsFiltered.get(++i)); break;
                case "-host" : host = InetAddress.getByName(argsFiltered.get(++i));
                case "-index" : index = argsFiltered.get(++i);
            }
        }
    }
}