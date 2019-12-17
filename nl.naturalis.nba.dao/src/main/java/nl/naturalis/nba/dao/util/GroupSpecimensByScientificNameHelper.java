package nl.naturalis.nba.dao.util;

import static nl.naturalis.nba.api.GroupByScientificNameQuerySpec.GroupSort.COUNT_ASC;
import static nl.naturalis.nba.api.GroupByScientificNameQuerySpec.GroupSort.COUNT_DESC;
import static nl.naturalis.nba.api.GroupByScientificNameQuerySpec.GroupSort.NAME_ASC;
import static nl.naturalis.nba.api.GroupByScientificNameQuerySpec.GroupSort.NAME_DESC;
import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;
import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;

import static org.elasticsearch.search.aggregations.AggregationBuilders.max;
import static org.elasticsearch.search.aggregations.AggregationBuilders.nested;
import static org.elasticsearch.search.aggregations.AggregationBuilders.topHits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.IncludeExclude;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.MaxAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.TopHitsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import nl.naturalis.nba.api.Filter;
import nl.naturalis.nba.api.GroupByScientificNameQueryResult;
import nl.naturalis.nba.api.GroupByScientificNameQuerySpec;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QueryResultItem;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.SortField;
import nl.naturalis.nba.api.model.ScientificNameGroup;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.dao.DaoUtil;
import nl.naturalis.nba.dao.SpecimenDao;
import nl.naturalis.nba.dao.TaxonDao;
import nl.naturalis.nba.dao.exception.DaoException;
import nl.naturalis.nba.dao.translate.QuerySpecTranslator;
import nl.naturalis.nba.utils.ConfigObject;

public class GroupSpecimensByScientificNameHelper {

	@SuppressWarnings("unused")
	private static Logger logger = getLogger(GroupSpecimensByScientificNameHelper.class);

	private static final QueryCache<GroupByScientificNameQueryResult> queryCache = new QueryCache<>(
			getCacheSize());

	public static GroupByScientificNameQueryResult groupByScientificName(
			GroupByScientificNameQuerySpec query) throws InvalidQueryException
	{
		GroupByScientificNameQueryResult result = queryCache.get(query);
		if (result != null) {
			return result;
		}
		/*
		 * We are going to manipulate the original query quite heavily further
		 * down, but we still want to cache only the original query, so we make
		 * a copy of the incoming query and work on the copy from now on:
		 */
		GroupByScientificNameQuerySpec queryCopy = new GroupByScientificNameQuerySpec(query);
		result = new GroupByScientificNameQueryResult();
		/*
		 * First, ONLY retrieve the unique scientific names (the buckets), given
		 * the query conditions. We don't want any documents to come back so in
		 * the QuerySpec we set from to null and size to 0. However we WILL use
		 * the value of from and size, albeit with different semantics. Within
		 * the context of a groupByScientificName query, from is the offset in
		 * the list of buckets that we retrieve, and size is the number of
		 * buckets to retrieve. For THOSE buckets we are going to subsequently
		 * retrieve taxa and specimens.
		 */
		int from = queryCopy.getFrom() == null ? 0 : queryCopy.getFrom();
		int size = queryCopy.getSize() == null ? 10 : queryCopy.getSize();
		if ((from + size) > getMaxNumBuckets()) {
			String fmt = "Too many groups requested. from + size must not exceed "
					+ "%s (was %s)";
			String msg = String.format(fmt, getMaxNumBuckets(), (from + size));
			throw new InvalidQueryException(msg);
		}
		List<SortField> sortFields = queryCopy.getSortFields();
		queryCopy.setFrom(null);
		queryCopy.setSize(0);
		queryCopy.setSortFields(null);
		QuerySpecTranslator translator = new QuerySpecTranslator(queryCopy, SPECIMEN);
		
		SearchRequest request = translator.translate();
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.trackTotalHits(false);
		searchSourceBuilder.aggregation(createAggregation(queryCopy));
		SearchResponse response = executeSearchRequest(request);
		
		Nested nested = response.getAggregations().get("NESTED");
		Terms terms = nested.getAggregations().get("TERMS");
		result.setSumOfOtherDocCounts(terms.getSumOfOtherDocCounts());
		List<? extends Bucket> buckets = terms.getBuckets();
		result.setTotalSize(buckets.size());

		// Morph the query into a regular Specimen query:
		queryCopy.setFrom(queryCopy.getSpecimensFrom());
		queryCopy.setSize(queryCopy.getSpecimensSize());
		queryCopy.setSortFields(sortFields);

		// Now, for each scientific name retrieve the associated specimens and taxa:
		String field = "identifications.scientificName.scientificNameGroup";
		QueryCondition extraCondition = new QueryCondition(field, "=", null);
		extraCondition.setConstantScore(true);
		queryCopy.addCondition(extraCondition);
		int to = Math.min(buckets.size(), from + size);
		List<QueryResultItem<ScientificNameGroup>> resultSet = new ArrayList<>(size);
		for (int i = from; i < to; i++) {
			String name = buckets.get(i).getKeyAsString();
			ScientificNameGroup sng = new ScientificNameGroup(name);
			QueryResultItem<ScientificNameGroup> qri = new QueryResultItem<>(sng, 0);
			resultSet.add(qri);
			if (queryCopy.getSpecimensSize() == null || queryCopy.getSpecimensSize() > 0) {
				/*
				 * Adjust the extra condition so as to only retrieve specimens
				 * whose scientific name equals the one we're currently dealing
				 * with
				 */
				extraCondition.setValue(name);
				QueryResult<Specimen> specimens = new SpecimenDao().query(queryCopy);
				sng.setSpecimenCount((int) specimens.getTotalSize());
				for (QueryResultItem<Specimen> specimen : specimens) {
					sng.addSpecimen(specimen.getItem());
					if (specimen.getScore() > qri.getScore()) {
						qri.setScore(specimen.getScore());
					}
				}
			}
			/*
			 * If the client has requested (and gets) more than 1024 buckets, he
			 * pays a stiff penalty, because in that case taxa are retrieved
			 * separately for each bucket. Otherwise all taxa are retrieved at
			 * once and then distributed over the groups. The latter method uses
			 * a query condition with the IN operator, and you cannot have more
			 * than 1024 values following the IN operator.
			 */
			if (!queryCopy.isNoTaxa() && buckets.size() > 1024) {
				addTaxaToGroup(sng);
			}
		}
		result.setResultSet(resultSet);
		if (!queryCopy.isNoTaxa() && buckets.size() <= 1024) {
			addTaxaToResult(result);
		}
		TimeValue took = response.getTook();
		if (getCacheSize() > 0 && took.getMillis() > getCacheTreshold()) {
			queryCache.put(query, result);
		}
		return result;
	}

	private static NestedAggregationBuilder createAggregation(
			GroupByScientificNameQuerySpec sngQuery) throws InvalidQueryException
	{
	    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
	    searchSourceBuilder.trackScores(false);
	    TermsAggregationBuilder tab = AggregationBuilders.terms("TERMS").field("identifications.scientificName.scientificNameGroup");
	    tab.size(getMaxNumBuckets());
	    searchSourceBuilder.aggregation(tab);
	    
	    // Ordering the buckets alphabetically by their terms in an ascending manner:
	    // BucketOrder.key(true);	    
	    if (sngQuery.getGroupSort() == NAME_ASC) {
	      tab.order(BucketOrder.key(true));
	    }
	    else if (sngQuery.getGroupSort() == NAME_DESC) {
	      tab.order(BucketOrder.key(false));
	    }
	    
	    // Ordering the buckets by their doc_count in an ascending manner:
	    // BucketOrder.count(true)
	    else if (sngQuery.getGroupSort() == COUNT_DESC) {
	      tab.order(BucketOrder.count(false));
	    }
	    else if (sngQuery.getGroupSort() == COUNT_ASC) {
	      tab.order(BucketOrder.count(true));
	    }
	    
	    else { // TOP_HIT_SCORE
	      TopHitsAggregationBuilder thab = topHits("TOP_HITS");
	      thab.size(1);
	      tab.subAggregation(thab);
	      MaxAggregationBuilder mab = max("MAX");
	      mab.script(new Script("_score"));
	      tab.subAggregation(mab);
	      tab.order(BucketOrder.aggregation("MAX", false));
	    }
	    Filter filter = sngQuery.getGroupFilter();
	    if (filter != null) {
	      IncludeExclude ie = DaoUtil.translateFilter(filter);
	      tab.includeExclude(ie);
	    }
	    NestedAggregationBuilder nab = nested("NESTED", "identifications");
	    nab.subAggregation(tab);
	    return nab;
	}

	private static void addTaxaToGroup(ScientificNameGroup sng) throws InvalidQueryException
	{
		TaxonDao taxonDao = new TaxonDao();
		QuerySpec taxonQuery = new QuerySpec();
		taxonQuery.setConstantScore(true);
		String field = "acceptedName.scientificNameGroup";
		QueryCondition taxonCondition = new QueryCondition(field, "=", sng.getName());
		taxonQuery.addCondition(taxonCondition);
		QueryResult<Taxon> taxa = taxonDao.query(taxonQuery);
		sng.setTaxonCount((int) taxa.getTotalSize());
		for (QueryResultItem<Taxon> taxon : taxa) {
			sng.addTaxon(taxon.getItem());
		}
	}

	private static void addTaxaToResult(QueryResult<ScientificNameGroup> result)
			throws InvalidQueryException
	{
		Map<String, ScientificNameGroup> groups = new HashMap<>(result.size() + 8, 1F);
		for (QueryResultItem<ScientificNameGroup> item : result) {
			groups.put(item.getItem().getName(), item.getItem());
		}
		QuerySpec query = new QuerySpec();
		/*
		 * Since we won't have more than 1024 groups, and since a group won't
		 * have more than two taxa (one NSR taxon, one CoL taxon), this should
		 * be enough:
		 */
		query.setSize(10000);
		query.setConstantScore(true);
		String field = "acceptedName.scientificNameGroup";
		QueryCondition condition = new QueryCondition(field, "IN", groups.keySet());
		query.addCondition(condition);
		TaxonDao dao = new TaxonDao();
		QueryResult<Taxon> taxa = dao.query(query);
		if (taxa.getTotalSize() > 10000) {
			throw new DaoException("Don't understand why we have more than 10000 taxa");
		}
		for (QueryResultItem<Taxon> taxon : taxa) {
			String name = taxon.getItem().getAcceptedName().getScientificNameGroup();
			ScientificNameGroup group = groups.get(name);
			group.addTaxon(taxon.getItem());
			group.setTaxonCount(group.getTaxa().size());
		}
	}

	private static int getMaxNumBuckets()
	{
		ConfigObject config = DaoRegistry.getInstance().getConfiguration();
		String property = "nl.naturalis.nba.specimen.groupByScientificName.maxNumBuckets";
		return config.required(property, int.class);
	}

	private static int getCacheTreshold()
	{
		ConfigObject config = DaoRegistry.getInstance().getConfiguration();
		String property = "nl.naturalis.nba.specimen.groupByScientificName.cacheTreshold";
		return config.required(property, int.class);
	}

	private static int getCacheSize()
	{
		ConfigObject config = DaoRegistry.getInstance().getConfiguration();
		String property = "nl.naturalis.nba.specimen.groupByScientificName.queryCacheSize";
		return config.required(property, int.class);
	}

}
