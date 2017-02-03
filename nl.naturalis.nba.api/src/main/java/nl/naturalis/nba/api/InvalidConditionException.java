package nl.naturalis.nba.api;

/**
 * Throw when a {@link QueryCondition query condition} contains an error.
 * 
 * @author Ayco Holleman
 *
 */
public class InvalidConditionException extends InvalidQueryException {

	public InvalidConditionException(String message)
	{
		super(message);
	}

	public InvalidConditionException(SearchCondition condition, String message, Object... msgArgs)
	{
		super(createMessage(condition, message, msgArgs));
	}

	public InvalidConditionException(Throwable cause)
	{
		super(cause);
	}

	private static String createMessage(SearchCondition condition, String msg, Object... msgArgs)
	{
		StringBuilder sb = new StringBuilder(100);
		sb.append("Invalid query using operator ");
		sb.append(condition.getOperator());
		sb.append(" on field ");
		sb.append(condition.getFields().iterator().next().toString());
		sb.append(". ");
		sb.append(String.format(msg, msgArgs));
		return sb.toString();
	}

}
