package nl.naturalis.nba.api.query;

public enum LogicalOperator
{
	AND, OR;

	public static LogicalOperator parse(String s)
	{
		if (s == null || s.isEmpty()) {
			return null;
		}
		if (s.equalsIgnoreCase("AND") || s.equals("&&")) {
			return AND;
		}
		if (s.equalsIgnoreCase("OR") || s.equals("||")) {
			return OR;
		}
		throw new IllegalArgumentException("No such logical operator: " + s);
	}

}
