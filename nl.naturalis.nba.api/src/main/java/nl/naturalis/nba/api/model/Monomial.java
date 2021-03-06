package nl.naturalis.nba.api.model;

import static nl.naturalis.nba.api.annotations.Analyzer.CASE_INSENSITIVE;
import static nl.naturalis.nba.api.annotations.Analyzer.DEFAULT;
import static nl.naturalis.nba.api.annotations.Analyzer.LIKE;

import nl.naturalis.nba.api.annotations.Analyzers;

/**
 * A {@code Monomial} represents a node in a taxon hierarchy. It has a
 * {@code rank} (e.g. "kingdom") and a {@code name} (e.g. "Plantae").
 * {@code Monomial}s allow you to build "free-style" classification hierarchies
 * and can therefore be used to accomodate any classification system used by the
 * NBA's species providers. This is necessary because the taxon hierarchies they
 * provide may have more and/or different ranks than those explicitly catered
 * for in the {@link DefaultClassification} class.
 * 
 * @see Taxon#getSystemClassification()
 * @see Taxon#getDefaultClassification()
 */
public class Monomial implements INbaModelObject {

	private String rank;
	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String name;

	public Monomial()
	{
	}

	/**
	 * Creates a monomial with an unspecified rank.
	 * 
	 * @param name
	 */
	public Monomial(String name)
	{
		this.rank = null;
		this.name = name;
	}

	/**
	 * Creates a monomial with the specified rank and name.
	 * 
	 * @param rank
	 * @param name
	 */
	public Monomial(String rank, String name)
	{
		this.rank = rank;
		this.name = name;
	}

	/**
	 * Creates a monomial using the english name of the specified
	 * {@link TaxonomicRank}.
	 * 
	 * @param rank
	 * @param name
	 */
	public Monomial(TaxonomicRank rank, String name)
	{
		this.rank = rank.getEnglishName();
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
