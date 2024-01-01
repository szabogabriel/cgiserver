package cgiserver.exec;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ScriptExecutor {

	private final File EXEC_DIR;

	public ScriptExecutor(File execDir) {
		this.EXEC_DIR = execDir;
	}

	public void execute(File scriptToRun, String[] env, InputStream in, OutputStream out) {
		execute(scriptToRun, new String[]{}, env, in, out);
	}

	public void execute(File scriptToRun, String[] params, String[] env, InputStream in, OutputStream out) {
		Process process = null;
		try {
			String name = scriptToRun.getName();

			process = createProcess(scriptToRun, params, env);

			redirectProcessPipes(process, in, out, name);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (process != null && process.isAlive()){
				try { process.destroy(); } catch (Exception ex) {}
			}
		}
	}

	private void redirectProcessPipes(Process process, InputStream in, OutputStream out, String name)
			throws IOException {
		InputStream processIn = process.getInputStream();
		OutputStream processOut = process.getOutputStream();

		do {
			handleRedirects(process, in, out, processIn, processOut);
		} while (process.isAlive());

		handleRedirects(process, in, out, processIn, processOut);
		
		out.close();
		in.close();
	}

	private void handleRedirects(Process process, InputStream in, OutputStream out, InputStream tmpIn,
			OutputStream tmpOut) throws IOException {
		byte[] buffer = new byte[128];
		int read;
		while (in.available() > 0) {
			read = in.read(buffer);
			tmpOut.write(buffer, 0, read);
		}
		while (tmpIn.available() > 0) {
			read = tmpIn.read(buffer);
			out.write(buffer, 0, read);
		}
		while (process.getErrorStream().available() > 0) {
			read = process.getErrorStream().read(buffer);
			System.err.write(buffer, 0, read);
		}
	}

	private Process createProcess(File scriptToRun, String [] params, String[] env) throws IOException {
		String cmd = scriptToRun.getAbsolutePath();
		String args = "";

		if (params != null) {
			for (String it : params) {
				args += " " + it;
			}
		}

		Process process = Runtime.getRuntime().exec(cmd + args, env, EXEC_DIR);
		return process;
	}

}
