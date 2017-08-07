package nl.naturalis.nba.common.rest;

/**
 * A mirror of the regular Java {@link StackTraceElement}. See here why we can't
 * just use {@link StackTraceElement} itself: <a href=
 * "https://stackoverflow.com/questions/17855538/issues-while-deserializing-exception-throwable-using-jackson-in-java">https://stackoverflow.com/questions/17855538/issues-while-deserializing-exception-throwable-using-jackson-in-java</a>
 * 
 * @author Ayco Holleman
 *
 */
public class TransferableStackTraceElement {

	private String sourceClass;
	private String methodName;
	private String fileName;
	private int lineNumber;

	public TransferableStackTraceElement()
	{
	}

	public TransferableStackTraceElement(StackTraceElement e)
	{
		this.sourceClass = e.getClassName();
		this.methodName = e.getMethodName();
		this.fileName = e.getFileName();
		this.lineNumber = e.getLineNumber();
	}

	public StackTraceElement toStackTraceElement()
	{
		return new StackTraceElement(sourceClass, methodName, fileName, lineNumber);
	}

	public String getSourceClass()
	{
		return sourceClass;
	}

	public void setSourceClass(String sourceClass)
	{
		this.sourceClass = sourceClass;
	}

	public String getMethodName()
	{
		return methodName;
	}

	public void setMethodName(String methodName)
	{
		this.methodName = methodName;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public int getLineNumber()
	{
		return lineNumber;
	}

	public void setLineNumber(int lineNumber)
	{
		this.lineNumber = lineNumber;
	}

}
