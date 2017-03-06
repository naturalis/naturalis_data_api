package nl.naturalis.nba.api;

import java.util.List;

import nl.naturalis.nba.api.model.NameGroup;
import nl.naturalis.nba.api.model.SummarySpecimen;

/**
 * An extension of the {@link QuerySpec} class specifically meant for queries
 * against the {@link NameGroup} index.
 * 
 * @author Ayco Holleman
 *
 */
public class NameGroupQuerySpec extends QuerySpec {

	private Integer specimensFrom;
	private Integer specimensSize;
	private List<SortField> specimensSortFields;
	private boolean noTaxa;

	/**
	 * Returns the offset within the {@link List} of specimens. Default 0.
	 * 
	 * @return
	 */
	public Integer getSpecimensFrom()
	{
		return specimensFrom;
	}

	/**
	 * Sets the offset within the {@link List} of specimens. Default 0. This
	 * enables paging through specimens within a single {@code NameGroup}. For
	 * each {@code NameGroup} returned from the query, only specimens at or
	 * after the offset are included.
	 * 
	 * @param specimensFrom
	 */
	public void setSpecimensFrom(Integer specimensFrom)
	{
		this.specimensFrom = specimensFrom;
	}

	/**
	 * Returns the maxmimum number of specimens to include per {@code NameGroup}
	 * document. Default all.
	 * 
	 * @return
	 */
	public Integer getSpecimensSize()
	{
		return specimensSize;
	}

	/**
	 * Sets the maxmimum number of specimens to include per {@code NameGroup}
	 * document. Default all. You can specify 0 (zero) to indicate that you are
	 * only interested in the taxa associated with the name group's name, or
	 * only in statistics like the total specimen count for the name.
	 * 
	 * @return
	 */
	public void setSpecimensSize(Integer specimensSize)
	{
		this.specimensSize = specimensSize;
	}

	/**
	 * Returns the sort order within the {@link List} of specimens. Default
	 * {@link SummarySpecimen#getUnitID() unitID}.
	 * 
	 * @return
	 */
	public List<SortField> getSpecimensSortFields()
	{
		return specimensSortFields;
	}

	/**
	 * Sets the sort order within the {@link List} of specimens. Default
	 * {@link SummarySpecimen#getUnitID() unitID}. For each {@code NameGroup},
	 * specimens are sorted on the sort fields specified through this method.
	 * Thus, you can sort the {@code NameGroup} documents according to one set
	 * of sort fields (using {@link QuerySpec#setSortFields(List)
	 * QuerySpec.setSortFields}) while sorting the specimens within each of them
	 * according to another set of sort fields. Sort fields must be specified
	 * relative to {@link SummarySpecimen} object rather than to the root of the
	 * NameGroup document. In other words, the path should <b>not</b> start with
	 * "specimens".
	 * 
	 * @param specimensSortFields
	 */
	public void setSpecimensSortFields(List<SortField> specimensSortFields)
	{
		this.specimensSortFields = specimensSortFields;
	}

	/**
	 * Returns whether or not to include the taxa associated with this name.
	 * 
	 * @return
	 */
	public boolean isNoTaxa()
	{
		return noTaxa;
	}

	/**
	 * Determines whether or not to include the taxa associated with the name
	 * group's name.
	 */
	public void setNoTaxa(boolean noTaxa)
	{
		this.noTaxa = noTaxa;
	}

}
