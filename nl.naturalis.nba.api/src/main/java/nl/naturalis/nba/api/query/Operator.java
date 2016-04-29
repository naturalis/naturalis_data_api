package nl.naturalis.nba.api.query;

public enum Operator
{

	EQUALS("="), NOT_EQUALS("!="), LT("<"), LTE("<="), GT(">"), GTE(">="), BETWEEN, NOT_BETWEEN;

	public static Operator parse(String s)
	{
		for (Operator co : values()) {
			if (co.symbol != null && s.equals(co.symbol))
				return co;
			if (s.equalsIgnoreCase(co.name()))
				return co;
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

	public String toString()
	{
		return symbol;
	}

}
