package cgiserver;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScriptExecutor {

    private final Logger LOG = Logger.getLogger(ScriptExecutor.class.getName());
    private final File EXEC_DIR;
    private final Executor SCRIPT_EXECUTOR;

    public ScriptExecutor(File execDir, int threadPoolSize) {
        this.EXEC_DIR = execDir;
        SCRIPT_EXECUTOR = Executors.newFixedThreadPool(threadPoolSize);
    }

    public void execute(File scriptToRun, String[] params, InputStream in, OutputStream out) {
        LOG.entering(getClass().getName(), "execute", params);
        try {
            String name = scriptToRun.getName();
            LOG.log(Level.FINE, "Starting process. " + name);
            Process process = Runtime.getRuntime().exec(scriptToRun.getAbsolutePath(), params, EXEC_DIR);

            StreamCopy toProcess = new StreamCopy(name + "::in", in, process.getOutputStream());
            StreamCopy fromProcess = new StreamCopy(name + "::out", process.getInputStream(), out);
            StreamCopy error = new StreamCopy(name + "::err", process.getErrorStream(), System.err);
           
            SCRIPT_EXECUTOR.execute(toProcess);
            SCRIPT_EXECUTOR.execute(fromProcess);
            SCRIPT_EXECUTOR.execute(error);

            process.waitFor();

            while (process.getInputStream().available()> 0) {
                Thread.sleep(5);
            }

            out.close();

            LOG.log(Level.FINE, "Process finished. " + name);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.toString());
        }
        LOG.exiting(getClass().getName(), "execute");
    }

}