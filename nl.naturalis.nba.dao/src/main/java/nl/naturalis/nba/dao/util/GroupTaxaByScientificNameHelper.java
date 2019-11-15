package nl.naturalis.nba.dao.util;

import static nl.naturalis.nba.api.GroupByScientificNameQuerySpec.GroupSort.COUNT_ASC;
import static nl.naturalis.nba.api.GroupByScientificNameQuerySpec.GroupSort.COUNT_DESC;
import static nl.naturalis.nba.api.GroupByScientificNameQuerySpec.GroupSort.NAME_ASC;
import static nl.naturalis.nba.api.GroupByScientificNameQuerySpec.GroupSort.NAME_DESC;
import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.DocumentType.TAXON;
import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;

import static org.elasticsearch.search.aggregations.AggregationBuilders.max;
import static org.elasticsearch.search.aggregations.AggregationBuilders.terms;
import static org.elasticsearch.search.aggregations.AggregationBuilders.topHits;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.BucketOrder;
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
import nl.naturalis.nba.dao.translate.QuerySpecTranslator;
import nl.naturalis.nba.utils.ConfigObject;

public class GroupTaxaByScientificNameHelper {

	@SuppressWarnings("unused")
	private static Logger logger = getLogger(GroupTaxaByScientificNameHelper.class);

	private static final QueryCache<GroupByScientificNameQueryResult> queryCache = new QueryCache<>(
			getCacheSize());

	public static GroupByScientificNameQueryResult groupByScientificName(
			GroupByScientificNameQuerySpec query) throws InvalidQueryException
	{
		GroupByScientificNameQueryResult result = queryCache.get(query);
		if (result != null) {
			return result;
		}
		result = new GroupByScientificNameQueryResult();
		/*
		 * We are going to manipulate the original query quite heavily further
		 * down, but we still want to cache only the original query, so we make
		 * a copy of the incoming query and work on the copy from now on:
		 */
		GroupByScientificNameQuerySpec queryCopy = new GroupByScientificNameQuerySpec(query);
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
		QuerySpecTranslator translator = new QuerySpecTranslator(queryCopy, TAXON);
		
		SearchRequest request = translator.translate();
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.aggregation(createAggregation(queryCopy));
		SearchResponse response = executeSearchRequest(request);
		
		Terms terms = response.getAggregations().get("TERMS");
		result.setSumOfOtherDocCounts(terms.getSumOfOtherDocCounts());
		List<? extends Bucket> buckets = terms.getBuckets();
		result.setTotalSize(buckets.size());
		/*
		 * With taxa we can have at most 2 Taxon documents per scientific name
		 * (NSR and/or COL). So contrary to the corresponding method in
		 * SpecimenDao, we do not allow paging through taxa within a group. We
		 * still set size to 5, though, just to see if there are any surprises
		 * with the data (more than 2 Taxon documents per scientific name).
		 */
		queryCopy.setFrom(0);
		queryCopy.setSize(5);
		queryCopy.setSortFields(sortFields);
		// Now, for each group retrieve the associated specimens and taxa:
		String field = "acceptedName.scientificNameGroup";
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
			if (!queryCopy.isNoTaxa()) {
				extraCondition.setValue(name);
				QueryResult<Taxon> taxa = new TaxonDao().query(queryCopy);
				sng.setTaxonCount((int) taxa.getTotalSize());
				for (QueryResultItem<Taxon> taxon : taxa) {
					sng.addTaxon(taxon.getItem());
					if (taxon.getScore() > qri.getScore()) {
						qri.setScore(taxon.getScore());
					}
				}
			}
			if (queryCopy.getSpecimensSize() == null || queryCopy.getSpecimensSize() > 0) {
				addSpecimens(sng, queryCopy);
			}
		}
		result.setResultSet(resultSet);
		TimeValue took = response.getTook();
		if (getCacheSize() > 0 && took.getMillis() > getCacheTreshold()) {
			queryCache.put(query, result);
		}
		return result;
	}

	private static TermsAggregationBuilder createAggregation(
			GroupByScientificNameQuerySpec sngQuery) throws InvalidQueryException
	{
		TermsAggregationBuilder tab = terms("TERMS");
		tab.field("acceptedName.scientificNameGroup");
		tab.size(getMaxNumBuckets());
		if (sngQuery.getGroupSort() == NAME_ASC) {
			tab.order(BucketOrder.key(true));
		}
		else if (sngQuery.getGroupSort() == NAME_DESC) {
			tab.order(BucketOrder.key(false));
		}
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
		return tab;
	}

	private static void addSpecimens(ScientificNameGroup sng,
			GroupByScientificNameQuerySpec sngQuery) throws InvalidQueryException
	{
		SpecimenDao specimenDao = new SpecimenDao();
		QuerySpec specimenQuery = new QuerySpec();
		specimenQuery.setFrom(sngQuery.getSpecimensFrom());
		specimenQuery.setSize(sngQuery.getSpecimensSize());
		specimenQuery.setSortFields(sngQuery.getSpecimensSortFields());
		specimenQuery.setConstantScore(true);
		String field = "identifications.scientificName.scientificNameGroup";
		QueryCondition taxonCondition = new QueryCondition(field, "=", sng.getName());
		specimenQuery.addCondition(taxonCondition);
		QueryResult<Specimen> specimens = specimenDao.query(specimenQuery);
		sng.setSpecimenCount((int) specimens.getTotalSize());
		for (QueryResultItem<Specimen> specimen : specimens) {
			sng.addSpecimen(specimen.getItem());
		}
	}

	private static int getMaxNumBuckets()
	{
		ConfigObject config = DaoRegistry.getInstance().getConfiguration();
		String property = "nl.naturalis.nba.taxon.groupByScientificName.maxNumBuckets";
		return config.required(property, int.class);
	}

	private static int getCacheTreshold()
	{
		ConfigObject config = DaoRegistry.getInstance().getConfiguration();
		String property = "nl.naturalis.nba.taxon.groupByScientificName.cacheTreshold";
		return config.required(property, int.class);
	}

	private static int getCacheSize()
	{
		ConfigObject config = DaoRegistry.getInstance().getConfiguration();
		String property = "nl.naturalis.nba.taxon.groupByScientificName.queryCacheSize";
		return config.required(property, int.class);
	}

}
