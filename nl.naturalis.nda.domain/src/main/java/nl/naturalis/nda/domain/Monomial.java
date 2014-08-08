package nl.naturalis.nda.domain;

/**
 * A {@code Monomial} represents a node in a taxon hierarchy. {@code Monomial} s
 * are used to create "free-style" taxon classifications in case the source
 * system's classification system does not conform to the NDA's
 * {@link DefaultClassification} (i.e. when it has more and or other ranks in
 * the taxon hierarchy).
 * 
 */
public class Monomial {

	private String rank;
	private String name;


	public Monomial()
	{

	}


	public Monomial(String rank, String name)
	{
		this.rank = rank;
		this.name = name;
	}


	public String getRank()
	{
		return rank;
	}


	public void setRank(String rank)
	{
		this.rank = rank;
	}


	public String getName()
	{
		return name;
	}


	public void setName(String name)
	{
		this.name = name;
	}

}
