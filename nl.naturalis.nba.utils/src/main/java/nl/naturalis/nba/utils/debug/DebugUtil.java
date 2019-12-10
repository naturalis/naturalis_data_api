package nl.naturalis.nba.utils.debug;

import static nl.naturalis.nba.utils.ClassUtil.isNumber;
import static nl.naturalis.nba.utils.StringUtil.zpad;
import static org.apache.commons.text.StringEscapeUtils.escapeJava;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

public class DebugUtil {

	private DebugUtil()
	{
	}

	/**
	 * Constants returned by the {@link DebugUtil#log(String, String)} method
	 * 
	 * @author Ayco Holleman
	 *
	 */
	public static enum PrintResult
	{
		/**
		 * Returned when the log message was successfully written.
		 */
		OK,
		/**
		 * Returned when the specified log file could not be created.
		 */
		COULD_NOT_CREATE_FILE,
		/**
		 * Returned when the log file could not be written to.
		 */
		FILE_NOT_WRITABLE,
		/**
		 * Returned when a {@link RuntimeException} was thrown while executing
		 * the log method.
		 */
		RUNTIME_EXCEPTION
	}

	public static String printCall(String methodName)
	{
		return methodName + "()";
	}

	public static String printCall(String methodName, Object arg0)
	{
		return printCall(methodName, new Object[] { arg0 });
	}

	public static String printCall(String methodName, Object arg0, Object arg1)
	{
		return printCall(methodName, new Object[] { arg0, arg1 });
	}

	public static String printCall(String methodName, Object arg0, Object arg1, Object arg2)
	{
		return printCall(methodName, new Object[] { arg0, arg1, arg2 });
	}

	public static String printCall(String methodName, Object arg0, Object arg1, Object arg2,
			Object arg3)
	{
		return printCall(methodName, new Object[] { arg0, arg1, arg2, arg3 });
	}

	public static String printCall(String methodName, Object arg0, Object arg1, Object arg2,
			Object arg3, Object arg4)
	{
		return printCall(methodName, new Object[] { arg0, arg1, arg2, arg3, arg4 });
	}

	public static String printCall(String methodName, Object[] args)
	{
		StringBuilder sb = new StringBuilder(64);
		sb.append(methodName).append('(');
		if (args == null) {
			sb.append("null");
		}
		else {
			for (int i = 0; i < args.length; i++) {
				if (i != 0) {
					sb.append(", ");
				}
				Object arg = args[i];
				if (arg == null) {
					sb.append("null");
				}
				else if (arg instanceof CharSequence) {
					sb.append('"').append(escapeJava(arg.toString())).append('"');
				}
				else if (isNumber(arg)) {
					sb.append(arg);
				}
				else if (arg.getClass().isArray()) {
					printArray(sb, arg);
				}
				else if (arg instanceof Collection) {
					printCollection(sb, arg);
				}
				else if (arg.getClass() == char.class || arg.getClass() == Character.class) {
					sb.append('\'').append(arg).append('\'');
				}
				else {
					sb.append(arg.getClass().getSimpleName());
				}
			}
		}
		sb.append(')');
		return sb.toString();
	}

	private static void printArray(StringBuilder sb, Object arg)
	{
		Class<?> type = arg.getClass().getComponentType();
		sb.append(type.getSimpleName());
		Object[] array = (Object[]) arg;
		sb.append('[').append(array.length).append(']');
		if (array.length > 0) {
			sb.append(" {");
			for (int j = 0; j < array.length && j < 4; j++) {
				if (j != 0) {
					sb.append(", ");
				}
				printElement(sb, array[j]);
			}
			sb.append('}');
		}
	}

	private static void printCollection(StringBuilder sb, Object arg)
	{
		Collection<?> c = (Collection<?>) arg;
		int size = c.size();
		sb.append(arg.getClass().getSimpleName());
		sb.append('(').append(size).append(')');
		if (size > 0) {
			sb.append(" {");
			int j = 0;
			for (Object obj : c) {
				if (j != 0) {
					sb.append(", ");
				}
				printElement(sb, obj);
				if (j++ == 3) {
					break;
				}
			}
			sb.append('}');
		}
	}

	private static void printElement(StringBuilder sb, Object e)
	{
		if (e == null) {
			sb.append("null");
		}
		else if (e instanceof CharSequence) {
			sb.append('"').append(escapeJava(e.toString())).append('"');
		}
		else if (isNumber(e)) {
			sb.append(e);
		}
		else if (e.getClass() == char.class || e.getClass() == Character.class) {
			sb.append('\'').append(e).append('\'');
		}
	}

	/**
	 * A bare-knuckle logging method printing the specified message to the
	 * specified file. If your real logging system (log4j, java.util.logging,
	 * etc.) seems to be not behaving, and something somehow seems to have
	 * hijacked System.out, you can use this method to make sure log messages
	 * are written to a file in a location you know (because you specifify it
	 * yourself).
	 * 
	 * @param toFile
	 *            Full path to the file to write the log message to. The file
	 *            will be created if it does not exist.
	 * @param msg
	 *            The message to write
	 * 
	 * @return The result of the print operation
	 */
	public static PrintResult log(String toFile, String msg)
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
			System.out.println(t.toString());
			return PrintResult.RUNTIME_EXCEPTION;
		}
	}

	/**
	 * A bare-knuckle logging method printing the specified message plus a
	 * newline character to the specified file. See
	 * {@link #print(String, String)}.
	 * 
	 * @param toFile
	 *            Full path to the file to write the log message to
	 * @param msg
	 *            The message to write
	 * 
	 * @return The result of the print operation
	 */
	public static PrintResult logln(String toFile, String msg)
	{
		return log(toFile, msg + System.getProperty("line.separator"));
	}

	/**
	 * Returns the time elapsed since {@code start} using format
	 * hours:minutes:seconds
	 * 
	 * @param start
	 * @return
	 */
	public static String getDuration(long start)
	{
		return getDuration(start, System.currentTimeMillis());
	}

	/**
	 * Returns the time elapsed between {@code start} and {@code end} using
	 * format hours:minutes:seconds
	 * 
	 * @param start
	 * @return
	 */
	public static String getDuration(long start, long end)
	{
		int millis = (int) (end - start);
		int hours = millis / (60 * 60 * 1000);
		millis = millis % (60 * 60 * 1000);
		int minutes = millis / (60 * 1000);
		millis = millis % (60 * 1000);
		int seconds = millis / 1000;
		return zpad(hours, 2, ":") + zpad(minutes, 2, ":") + zpad(seconds, 2);
	}
}
