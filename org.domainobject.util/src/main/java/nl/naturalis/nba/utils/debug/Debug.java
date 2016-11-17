package nl.naturalis.nba.utils.debug;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Debug {

	public enum PrintResult
	{
		OK, COULD_NOT_CREATE_FILE, FILE_NOT_WRITABLE, RUNTIME_EXCEPTION
	}


	/**
	 * A bare-knuckle logging method. If your real logging system (log4j,
	 * java.util.logging, etc.) seems to be not behaving, and something somehow
	 * seems to have hijacked System.out, you can use this method to make sure
	 * log messages are written to a file in a location you know (because you
	 * specifify it yourself).
	 * 
	 * @param toFile Full path to the file to write the log message to. The file
	 *            will be created if it does not exist.
	 * @param msg The message to write
	 * 
	 * @return The result of the print operation
	 */
	public static PrintResult print(String toFile, String msg)
	{
		try {
			File f = new File(toFile);
			if (!f.exists()) {
				try {
					f.createNewFile();
				}
				catch (IOException e) {
					return PrintResult.COULD_NOT_CREATE_FILE;
				}
			}
			if (!f.canWrite()) {
				return PrintResult.FILE_NOT_WRITABLE;
			}
			PrintWriter pw = new PrintWriter(f);
			pw.append(msg);
			pw.flush();
			pw.close();
			return PrintResult.OK;
		}
		catch (Throwable t) {
			return PrintResult.RUNTIME_EXCEPTION;
		}
	}


	/**
	 * PA bare-knuckle logging method. See {@link #print(String, String)}.
	 * 
	 * @param toFile Full path to the file to write the log message to
	 * @param msg The message to write
	 * 
	 * @return The result of the print operation
	 */
	public static PrintResult println(String toFile, String msg)
	{
		return print(toFile, msg + System.getProperty("line.separator"));
	}

}
