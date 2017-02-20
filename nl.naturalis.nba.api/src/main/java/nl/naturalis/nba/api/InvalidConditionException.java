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

	public InvalidConditionException(QueryCondition condition, String message, Object... msgArgs)
	{
		super(createMessage(condition, message, msgArgs));
	}

	public InvalidConditionException(Throwable cause)
	{
		super(cause);
	}

	private static String createMessage(QueryCondition condition, String msg, Object... msgArgs)
	{
		StringBuilder sb = new StringBuilder(200);
		sb.append("Invalid condition on field ");
		sb.append(condition.getField());
		sb.append(" using operator ").append((condition.getOperator()));
		sb.append(". ");
		sb.append(String.format(msg, msgArgs));
		return sb.toString();
	}

}
