package nl.naturalis.nba.api;

import nl.naturalis.nba.api.model.NameGroup;

/**
 * An extension of the {@link QuerySpec} class specifically meant for queries
 * agains the {@link NameGroup} document type.
 * 
 * @author Ayco Holleman
 *
 */
public class NameGroupQuerySpec extends QuerySpec {

	private Integer specimensFrom;
	private Integer specimensSize;
	private Boolean noTaxa;

	/**
	 * Returns the offset within the list of specimens within the NameGroup
	 * document. Default 0.
	 * 
	 * @return
	 */
	public Integer getSpecimensFrom()
	{
		return specimensFrom;
	}

	/**
	 * Sets the offset within the list of specimens within the NameGroup
	 * document. Default 0.
	 * 
	 * @param specimensFrom
	 */
	public void setSpecimensFrom(Integer specimensFrom)
	{
		this.specimensFrom = specimensFrom;
	}

	/**
	 * Returns the maxmimum number of specimens to return per NameGroup
	 * document. Default all.
	 * 
	 * @return
	 */
	public Integer getSpecimensSize()
	{
		return specimensSize;
	}

	/**
	 * Sets the maxmimum number of specimens to return per NameGroup document.
	 * Default all. You can specify 0 (zero) to indicate that you are only
	 * interested in the taxa associated with the name group's name, or only in
	 * statistics like the total specimen count for the name.
	 * 
	 * @return
	 */
	public void setSpecimensSize(Integer specimensSize)
	{
		this.specimensSize = specimensSize;
	}

	/**
	 * Returns whether or not to show the taxa associated with this name.
	 * 
	 * @return
	 */
	public Boolean isNoTaxa()
	{
		return noTaxa;
	}

	/**
	 * Determines whether or not to show the taxa associated with the name
	 * group's name.
	 */
	public void setNoTaxa(Boolean noTaxa)
	{
		this.noTaxa = noTaxa;
	}
}
