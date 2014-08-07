package nl.naturalis.nda.domain;

public class Monomial {

	private final String rank;
	private final String name;


	public Monomial(String rank, String name)
	{
		this.rank = rank;
		this.name = name;
	}


	public String getRank()
	{
		return rank;
	}


	public String getName()
	{
		return name;
	}

}
