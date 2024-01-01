package cgiserver.config;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

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

        List<String> argsFiltered = filterArguments(args, suffix);

        for (int i = 0; i < argsFiltered.size(); i++) {
            switch(argsFiltered.get(i)) {
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

    private List<String> filterArguments(String [] args, String suffix) {
        List<String> ret = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            if ("-help".equals(args[i])) {
                ret.add(args[i]);
            } else
            if (args[i].endsWith(suffix)) {
                ret.add(args[i].substring(0, args[i].length() - suffix.length()));
                ret.add(args[++i]);
            } else 
            if (args[i].startsWith("-") && !args[i].contains(".")) {
                ret.add(args[i]);
                ret.add(args[++i]);
            }
        }
        return ret;
    }
}