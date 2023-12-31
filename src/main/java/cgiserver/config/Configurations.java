package cgiserver.config;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Configurations {

    private List<Configuration> configs = new ArrayList<>();

    public Configurations(String [] args) throws UnknownHostException {
        if (hasSuffix(args, ".1")) {
            int i = 1;
            while (hasSuffix(args, "." + i)) {
                configs.add(new Configuration(args, i));
                i++;
            }
        } else {
            configs.add(new Configuration(args));
        }
    }

    private boolean hasSuffix(String[] args, String suffix) {
        for (String it : args) {
            if (it.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    public boolean isHelp() {
        for (Configuration it : configs) {
            if (it.isHelp()) {
                return true;
            }
        }
        return false;
    }

    public List<Configuration> getConfig() {
        return configs;
    }
    
}
