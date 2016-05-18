package nl.naturalis.nba.api.query;

public enum Operator
{

	EQUALS("="),
	NOT_EQUALS("!="),
	EQUALS_CI,
	NOT_EQUALS_CI,
	LT("<"),
	LTE("<="),
	GT(">"),
	GTE(">="),
	BETWEEN,
	NOT_BETWEEN,
	LIKE,
	NOT_LIKE;

	public static Operator parse(String s)
	{
		for (Operator op : values()) {
			if (op.symbol != null && s.equals(op.symbol)) {
				return op;
			}
			if (s.equalsIgnoreCase(op.name())) {
				return op;
			}
		}
		return null;
	}

	private final String symbol;

	private Operator()
	{
		this.symbol = null;
	}

	private Operator(String symbol)
	{
		this.symbol = symbol;
	}

}
