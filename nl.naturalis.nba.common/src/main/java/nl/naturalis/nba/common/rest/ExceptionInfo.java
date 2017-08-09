package nl.naturalis.nba.common.rest;

/**
 * Java bean representing an exception thrown by NBA server-side code.
 * 
 * @author Ayco Holleman
 *
 */
public class ExceptionInfo {

	private String message;
	private Class<? extends Throwable> type;
	private TransferableStackTraceElement[] stackTrace;

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public Class<? extends Throwable> getType()
	{
		return type;
	}

	public void setType(Class<? extends Throwable> type)
	{
		this.type = type;
	}

	public TransferableStackTraceElement[] getStackTrace()
	{
		return stackTrace;
	}

	public void setStackTrace(TransferableStackTraceElement[] stackTrace)
	{
		this.stackTrace = stackTrace;
	}

	public StackTraceElement[] getRegularStackTrace()
	{
		StackTraceElement[] trace = new StackTraceElement[stackTrace.length];
		for (int i = 0; i < trace.length; i++) {
			trace[i] = stackTrace[i].toStackTraceElement();
		}
		return trace;
	}

	public void setRegularStackTrace(StackTraceElement[] trace)
	{
		stackTrace = new TransferableStackTraceElement[trace.length];
		for (int i = 0; i < stackTrace.length; ++i) {
			stackTrace[i] = new TransferableStackTraceElement(trace[i]);
		}
	}

}