package cgiserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SocketHandler implements Runnable {

    private static final Pattern FIRST_LINE_PATTERN = Pattern.compile("^(GET|POST|PUT|DELETE|OPTIONS|HEAD|TRACE|CONNECT)\\s(.*)\\s(HTTP)\\/(.*)$");

    private final Logger LOG = Logger.getLogger(SocketHandler.class.getName());
    private final Socket SOCKET;
    private final int PREFIX_LENGTH;
    private final String SCRIPT_FOLDER;
    private final ScriptExecutor SCRIPT_EXECUTOR;

    private CGIRequestParams params = new CGIRequestParams();
    private String scriptToCall = null;

    public SocketHandler(final Socket socket, final SocketHandlerConfig config, ScriptExecutor executor) {
        this.SOCKET = socket;
        this.PREFIX_LENGTH = config.getUrlPrefix().length() + 1;
        this.SCRIPT_FOLDER = config.getCgiScriptFolder();
        this.SCRIPT_EXECUTOR = executor;
    }

    @Override
    public void run() {
        LOG.entering(getClass().getName(), "run");
        String line;
        BufferedReader in;
        try {
            in = new BufferedReader(new InputStreamReader(SOCKET.getInputStream()));
            
            line = in.readLine();
            handleHttpRequestLine(line);

            line = in.readLine();
            while (line != null && line.trim().length() > 0) {
                handleHttpHeaderLine(line);
                line = in.readLine();
            }

            runScript();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getMessage());
        }
        LOG.exiting(getClass().getName(), "run");
    }

    private void runScript() throws IOException {
        LOG.entering(getClass().getName(), "runScript");
        Optional<File> script = getScript(scriptToCall);
        
        if (script.isPresent()) {
            SOCKET.getOutputStream().write("HTTP/1.1 200 OK\nServer: CGI-Server-0.1\n".getBytes());
            handle(script.get(), params.toArray(), SOCKET.getInputStream(), SOCKET.getOutputStream());
        } else {
            SOCKET.getOutputStream().write("HTTP/1.1 404 ERROR\n\n".getBytes());
            SOCKET.getOutputStream().close();
        }
        LOG.exiting(getClass().getName(), "runScript");
    }

    private void handleHttpRequestLine(String line) {
        LOG.entering(getClass().getName(), "handleHttpRequestLine", line);
        if (line != null) {
            Matcher matcher = FIRST_LINE_PATTERN.matcher(line);
            if (matcher.matches()) {
                params.setMethod(matcher.group(1));
                String path = matcher.group(2);
                params.setPath(path);
                params.setProtocolType(matcher.group(3));
                params.setProtocolVersion(matcher.group(4));
                params.setQueryString(getQueryString(path));

                scriptToCall = getScriptToCall(path);
            }
        }
        LOG.exiting(getClass().getName(), "handleHttpRequestLine");
    }

    private void handleHttpHeaderLine(String line) {
        LOG.entering(getClass().getName(), "handleHttpHeaderLine", line);
        if (line.contains(":")) {
            int poz = line.indexOf(":");
            String key = line.substring(0, poz).trim();
            String value = line.substring(poz + 1).trim();
            params.add(key, value);
        }
        LOG.exiting(getClass().getName(), "handleHttpHeaderLine");
    }

    private Optional<File> getScript(String script) {
        LOG.entering(getClass().getName(), "getScript", script);
        Optional<File> ret = Optional.empty();
        if (script != null && script.contains(SCRIPT_FOLDER) && !script.contains("..")) {
            File scriptToCall = new File(script);
            if (scriptToCall.exists() && scriptToCall.isFile()) {
                ret = Optional.of(scriptToCall);
            }
        }
        LOG.exiting(getClass().getName(), "getScript", ret);
        return ret;
    }

    private void handle(File script, String[] params, InputStream in, OutputStream out) {
        LOG.entering(getClass().getName(), "handle", params);
        SCRIPT_EXECUTOR.execute(script, params, in, out);
        LOG.exiting(getClass().getName(), "handle");
    }

    private String getQueryString(String path) {
        LOG.entering(getClass().getName(), "getQueryString", path);
        String ret = "";
        int qsPosition = path.indexOf("?");
        if (qsPosition != -1 && path.length() > qsPosition + 1) {
            ret = path.substring(qsPosition + 1);
        }
        LOG.exiting(getClass().getName(), "getQueryString", ret);
        return ret;
    }

    private String getScriptToCall(String path) {
        LOG.entering(getClass().getName(), "getScriptToCall", path);
        String ret = SCRIPT_FOLDER + File.separator + path.substring(PREFIX_LENGTH);
        int qsPosition = ret.indexOf("?");
        if (qsPosition != -1) {
            ret = ret.substring(0, qsPosition);
        }
        LOG.exiting(getClass().getName(), "getScriptToCall", ret);
        return ret;
    }

}