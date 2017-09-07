package nl.naturalis.nba.api;

import static nl.naturalis.nba.api.GroupByScientificNameQuerySpec.GroupSort.TOP_HIT_SCORE;

import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nba.api.model.ScientificNameGroup;

/**
 * An extension of the {@link QuerySpec} class specifically meant for
 * {@link ITaxonAccess#groupByScientificName(GroupByScientificNameQuerySpec)
 * ITaxonAccess.groupByScientificName} and
 * {@link ISpecimenAccess#groupByScientificName(GroupByScientificNameQuerySpec)
 * ISpecimenAccess.groupByScientificName}.
 * 
 * @author Ayco Holleman
 *
 */
public class GroupByScientificNameQuerySpec extends QuerySpec {

	/**
	 * Enumerates options for sorting the {@link ScientificNameGroup} objects
	 * returned from the {@code groupByScientificName} APIs.
	 * 
	 * @author Ayco Holleman
	 *
	 */
	public static enum GroupSort
	{
		/**
		 * Sorts the {@link ScientificNameGroup} objects in descending order
		 * their the best-scoring specimen/taxon. This is the default.
		 */
		TOP_HIT_SCORE,
		/**
		 * Sorts the {@link ScientificNameGroup} objects in descending order of
		 * the number of specimens c.q. taxa associated with a scientific name.
		 * This is the default sort order.
		 */
		COUNT_DESC,
		/**
		 * Sorts the {@link ScientificNameGroup} objects in ascending order of
		 * the number of specimens c.q. taxa associated with a scientific name.
		 * Note that Elasticsearch discourages this as it error margin on
		 * document counts.
		 */
		COUNT_ASC,
		/**
		 * Sorts the {@link ScientificNameGroup} objects by scientific name
		 * (ascending).
		 */
		NAME_ASC,
		/**
		 * Sorts the {@link ScientificNameGroup} objects by scientific name
		 * (descending).
		 */
		NAME_DESC;

		public static GroupSort parse(String name)
		{
			if (name == null) {
				return null;
			}
			for (GroupSort gs : values()) {
				if (gs.name().equalsIgnoreCase(name)) {
					return gs;
				}
			}
			throw new IllegalArgumentException("Invalid GroupSort: " + name);
		}
	}

	private GroupSort groupSort;
	private Filter groupFilter;
	private Integer specimensFrom;
	private Integer specimensSize;
	private List<SortField> specimensSortFields;
	private boolean noTaxa;

	public GroupByScientificNameQuerySpec()
	{
	}

	/**
	 * Copy constructor.
	 * 
	 * @param other
	 */
	public GroupByScientificNameQuerySpec(GroupByScientificNameQuerySpec other)
	{
		super(other);
		groupSort = other.groupSort;
		if (other.groupFilter != null) {
			groupFilter = new Filter(other.groupFilter);
		}
		specimensFrom = other.specimensFrom;
		specimensSize = other.specimensSize;
		if (specimensSortFields != null) {
			specimensSortFields = new ArrayList<>(other.specimensSortFields.size());
			for (SortField sortField : other.specimensSortFields) {
				specimensSortFields.add(new SortField(sortField));
			}
		}
		noTaxa = other.noTaxa;
	}

	/**
	 * Returns which way the {@link ScientificNameGroup} objects returned by
	 * {code groupByScientificName} are sorted.
	 * 
	 * @return
	 */
	public GroupSort getGroupSort()
	{
		return groupSort;
	}

	/**
	 * Determines which way the {@link ScientificNameGroup} objects returned by
	 * {code groupByScientificName} are sorted.
	 * 
	 * @param groupSort
	 */
	public void setGroupSort(GroupSort groupSort)
	{
		this.groupSort = groupSort;
	}

	/**
	 * Returns the desired filter for the returned {@link ScientificNameGroup}
	 * objects.
	 * 
	 * @return
	 */
	public Filter getGroupFilter()
	{
		return groupFilter;
	}

	/**
	 * <p>
	 * Sets an extra filter on the {@link ScientificNameGroup} objects to be
	 * returned. For example, to exclude all {@code ScientificNameGroup} objects
	 * whose {@link ScientificNameGroup#getName() name} contains a question
	 * mark, use the following filter:
	 * </p>
	 * <p>
	 * 
	 * <pre>
	 * Filter filter = new Filter();
	 * filter.rejectRegexp(".*\\?.*";
	 * </pre>
	 * </p>
	 * <p>
	 * To only include taxa/specimens whose genus is "Larus":
	 * </p>
	 * <p>
	 * 
	 * <pre>
	 * Filter filter = new Filter();
	 * filter.acceptRegexp("larus.*");
	 * </pre>
	 * </p>
	 * <p>
	 * To only include "Larus fuscus" and "Larus fuscus fuscus":
	 * </p>
	 * <p>
	 * 
	 * <pre>
	 * Filter filter = new Filter();
	 * filter.acceptValues(new String[] { "larus fuscus", "larus fuscus fuscus" });
	 * </pre>
	 * </p>
	 * 
	 * @param groupFilter
	 */
	public void setGroupFilter(Filter groupFilter)
	{
		this.groupFilter = groupFilter;
	}

	/**
	 * Returns the desired offset within the {@link List} of specimens
	 * associated with a scientific name.
	 * 
	 * @return
	 */
	public Integer getSpecimensFrom()
	{
		return specimensFrom;
	}

	/**
	 * Sets the desired offset within the {@link List} of specimens associated
	 * with a scientific name. Default 0. In other words: the regular
	 * {@link QuerySpec#getFrom() from} property of the {@link QuerySpec} object
	 * determines the offset within the list of scientific names while the
	 * {@code specimensFrom} property determines the offset in the list of
	 * specimens associated with each scientific name. If the specified offset
	 * is beyond the number of specimens for a particular scientific name, the
	 * list of specimens for that scientific name will be empty.
	 * 
	 * @param specimensFrom
	 */
	public void setSpecimensFrom(Integer specimensFrom)
	{
		this.specimensFrom = specimensFrom;
	}

	/**
	 * Returns the number of specimens to be retrieved per scientific name.
	 * 
	 * @return
	 */
	public Integer getSpecimensSize()
	{
		return specimensSize;
	}

	/**
	 * Determines how many specimens to retrieve per scientific name. Default
	 * 10. In other words: the regular {@link QuerySpec#getSize() size} property
	 * of the {@link QuerySpec} objects determines the desired number of
	 * {@link ScientificNameGroup} instances while the {@code specimensSize}
	 * property determines the desired number of specimens per
	 * {@link ScientificNameGroup}. You can specify 0 (zero) if you want to
	 * suppress the retrieval of specimens (i.e. if you are only interested in
	 * the taxa). If you suppress the retrieval of specimens, no
	 * {@link ScientificNameGroup#getSpecimenCount() specimen count} will be
	 * calculated.
	 * 
	 * @return
	 */
	public void setSpecimensSize(Integer specimensSize)
	{
		this.specimensSize = specimensSize;
	}

	/**
	 * Returns the sort order within the {@link List} of specimens associated
	 * with a scientific name.
	 * 
	 * @return
	 */
	public List<SortField> getSpecimensSortFields()
	{
		return specimensSortFields;
	}

	/**
	 * Sets the sort order within the {@link List} of specimens associated with
	 * a scientific name. This property is ignored when calling
	 * {@link ISpecimenAccess#groupByScientificName(GroupByScientificNameQuerySpec)
	 * ISpecimenAccess.groupByScientificName}. It is only applicable for
	 * {@link ITaxonAccess#groupByScientificName(GroupByScientificNameQuerySpec)
	 * ITaxonAccess.groupByScientificName}.
	 * 
	 * @param specimensSortFields
	 */
	public void setSpecimensSortFields(List<SortField> specimensSortFields)
	{
		this.specimensSortFields = specimensSortFields;
	}

	/**
	 * Returns whether or not to suppress the retrieval of the taxa associated
	 * with a scientific name.
	 * 
	 * @return
	 */
	public boolean isNoTaxa()
	{
		return noTaxa;
	}

	/**
	 * Determines whether or not to suppress the retrieval of the taxa
	 * associated with a scientific name. If you suppress the retrieval of taxa,
	 * no {@link ScientificNameGroup#getTaxonCount() taxon count} is calculated
	 * either.
	 * 
	 * @param noTaxa
	 */
	public void setNoTaxa(boolean noTaxa)
	{
		this.noTaxa = noTaxa;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof GroupByScientificNameQuerySpec)) {
			return false;
		}
		GroupByScientificNameQuerySpec other = (GroupByScientificNameQuerySpec) obj;
		if (groupSort == null || groupSort == TOP_HIT_SCORE) {
			if (!(other.groupSort == null || other.groupSort == TOP_HIT_SCORE)) {
				return false;
			}
		}
		else if (other.groupSort == null || other.groupSort == TOP_HIT_SCORE) {
			return false;
		}
		else if (groupSort != other.groupSort) {
			return false;
		}
		if (groupFilter == null) {
			if (other.groupFilter != null) {
				return false;
			}
		}
		else if (other.groupFilter == null) {
			return false;
		}
		else if (!groupFilter.equals(other.groupFilter)) {
			return false;
		}
		if (specimensFrom == null || specimensFrom == 0) {
			if (!(other.specimensFrom == null || other.specimensFrom == 0)) {
				return false;
			}
		}
		else if (other.specimensFrom == null || other.specimensFrom == 0) {
			return false;
		}
		else if (!specimensFrom.equals(other.specimensFrom)) {
			return false;
		}
		if (specimensSize == null || specimensSize == 10) {
			if (!(other.specimensSize == null || other.specimensSize == 10)) {
				return false;
			}
		}
		else if (other.specimensSize == null || other.specimensSize == 10) {
			return false;
		}
		else if (!specimensSize.equals(other.specimensSize)) {
			return false;
		}
		if (specimensSortFields == null) {
			if (other.specimensSortFields != null) {
				return false;
			}
		}
		else if (other.specimensSortFields == null) {
			return false;
		}
		else if (!specimensSortFields.equals(other.specimensSortFields)) {
			return false;
		}
		if (noTaxa != other.noTaxa) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = 17;
		hash = (hash * 31) + (groupSort == null ? TOP_HIT_SCORE.hashCode() : groupSort.hashCode());
		hash = (hash * 31) + (groupFilter == null ? 0 : groupFilter.hashCode());
		hash = (hash * 31) + (specimensFrom == null ? 0 : specimensFrom.hashCode());
		hash = (hash * 31) + (specimensSize == null ? 10 : specimensFrom.hashCode());
		hash = (hash * 31) + (specimensSortFields == null ? 10 : specimensSortFields.hashCode());
		hash = (hash * 31) + (noTaxa ? 1 : 0);
		return hash;
	}

}
