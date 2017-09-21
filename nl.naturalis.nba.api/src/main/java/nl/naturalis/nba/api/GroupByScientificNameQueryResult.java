package nl.naturalis.nba.api;

import nl.naturalis.nba.api.model.ScientificNameGroup;

/**
 * A extension of {@link QueryResult} capturing the result of a call to
 * {@link ISpecimenAccess#groupByScientificName(GroupByScientificNameQuerySpec)
 * ISpecimenAccess.groupByScientificName} or
 * {@link ITaxonAccess#groupByScientificName(GroupByScientificNameQuerySpec)
 * ITaxonAccess.groupByScientificName}. Note that the
 * {@link QueryResult#getTotalSize() totalSize} has a different meaning in
 * instances of this class. It is the number of buckets that were collected from
 * the documents satifsying the query criteria. It is <i>not</i> the number of
 * documents itself.
 */
public class GroupByScientificNameQueryResult extends QueryResult<ScientificNameGroup> {

	private long sumOfOtherDocCounts;

	/**
	 * Returns the number of &#34;unbucketed&#34; documents. For performance
	 * reasons there is a cap on the number of buckets (a.k.a. groups) that will
	 * be collected from the documents satisfying the query criteria. As soon as
	 * this maximum is reached Elasticsearch will stop search for new buckets in
	 * the remaining documents. This method gives an indication of the number of
	 * documents not present in any of the buckets. It may not be an accurate
	 * indication. Only if this method returns 0 (zero) do you know that
	 * <i>all</i> documents satisfying the query criteria were bucketed.
	 * 
	 * @return
	 */
	public long getSumOfOtherDocCounts()
	{
		return sumOfOtherDocCounts;
	}

	public void setSumOfOtherDocCounts(long sumOfOtherDocCounts)
	{
		this.sumOfOtherDocCounts = sumOfOtherDocCounts;
	}

}
