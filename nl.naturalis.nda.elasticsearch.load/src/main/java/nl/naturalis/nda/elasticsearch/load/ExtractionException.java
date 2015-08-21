package nl.naturalis.nda.elasticsearch.load;

/**
 * @author Ayco Holleman
 *
 */
public class ExtractionException extends RuntimeException {
	
	private final String line;
	private final int lineNo;

	/**
	 * @param message
	 */
	public ExtractionException(String message, String line, int lineNo)
	{
		super(message);
		this.line = line;
		this.lineNo = lineNo;
	}

	/**
	 * @param cause
	 */
	public ExtractionException(String line, int lineNo, Throwable cause)
	{
		super(cause);
		this.line = line;
		this.lineNo = lineNo;
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ExtractionException(String message, String line, int lineNo, Throwable cause)
	{
		super(message, cause);
		this.line = line;
		this.lineNo = lineNo;
	}

	public String getLine()
	{
		return line;
	}

	public int getLineNumber()
	{
		return lineNo;
	}

}
