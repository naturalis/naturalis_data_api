package nl.naturalis.nba.utils;

/**
 * 
 * @author Ayco Holleman
 *
 */
public class ExceptionUtil {

	/**
	 * Returns the root cause of the specified throwable.
	 * 
	 * @param t
	 * @return
	 */
	public static Throwable rootOf(Throwable t)
	{
		while (t.getCause() != null)
			t = t.getCause();
		return t;
	}

	/**
	 * Returns the stack trace of the root cause of {@code t} as a string.
	 * 
	 * @param t
	 * @return
	 */
	public static String rootStackTrace(Throwable t)
	{
		t = rootOf(t);
		StringBuilder sb = new StringBuilder(6000);
		sb.append(t.toString());
		for (StackTraceElement e : t.getStackTrace()) {
			sb.append("\nat ");
			sb.append(e.getClassName()).append('.').append(e.getMethodName());
			sb.append('(').append(e.getFileName());
			sb.append(':').append(e.getLineNumber()).append(')');
		}
		return sb.toString();
	}

	/**
	 * Returns t if it already is a {@code RuntimeException}, else a
	 * {@code RuntimeException} wrapping t.
	 * 
	 * @param t
	 *            a {@code Throwable}
	 * @return t or a {@code RuntimeException} wrapping t
	 */
	public static RuntimeException smash(Throwable t)
	{
		if (t instanceof RuntimeException)
			return (RuntimeException) t;
		return new RuntimeException(t);
	}

}
