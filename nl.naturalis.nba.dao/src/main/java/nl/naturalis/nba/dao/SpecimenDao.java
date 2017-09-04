package nl.naturalis.nba.dao;

import static nl.naturalis.nba.api.GroupByScientificNameQuerySpec.GroupSort.COUNT_ASC;
import static nl.naturalis.nba.api.GroupByScientificNameQuerySpec.GroupSort.COUNT_DESC;
import static nl.naturalis.nba.api.GroupByScientificNameQuerySpec.GroupSort.NAME_ASC;
import static nl.naturalis.nba.api.GroupByScientificNameQuerySpec.GroupSort.NAME_DESC;
import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;
import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;
import static nl.naturalis.nba.dao.util.es.ESUtil.newSearchRequest;
import static nl.naturalis.nba.utils.debug.DebugUtil.printCall;
import static org.elasticsearch.index.query.QueryBuilders.constantScoreQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.search.aggregations.AggregationBuilders.max;
import static org.elasticsearch.search.aggregations.AggregationBuilders.nested;
import static org.elasticsearch.search.aggregations.AggregationBuilders.terms;
import static org.elasticsearch.search.aggregations.AggregationBuilders.topHits;

import java.io.File;
import java.io.FileFilter;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.ConstantScoreQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.support.IncludeExclude;
import org.elasticsearch.search.aggregations.metrics.max.MaxAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHitsAggregationBuilder;

import nl.naturalis.nba.api.Filter;
import nl.naturalis.nba.api.GroupByScientificNameQuerySpec;
import nl.naturalis.nba.api.ISpecimenAccess;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.NoSuchDataSetException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QueryResultItem;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.SortField;
import nl.naturalis.nba.api.model.ScientificNameGroup;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.dao.exception.DaoException;
import nl.naturalis.nba.dao.format.DataSetConfigurationException;
import nl.naturalis.nba.dao.format.DataSetWriteException;
import nl.naturalis.nba.dao.format.dwca.DwcaConfig;
import nl.naturalis.nba.dao.format.dwca.DwcaDataSetType;
import nl.naturalis.nba.dao.format.dwca.DwcaUtil;
import nl.naturalis.nba.dao.format.dwca.IDwcaWriter;
import nl.naturalis.nba.dao.translate.QuerySpecTranslator;

public class SpecimenDao extends NbaDao<Specimen> implements ISpecimenAccess {

	private static Logger logger = getLogger(SpecimenDao.class);

	public SpecimenDao()
	{
		super(SPECIMEN);
	}

	@Override
	public boolean exists(String unitID)
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("exists", unitID));
		}
		SearchRequestBuilder request = newSearchRequest(SPECIMEN);
		TermQueryBuilder tqb = termQuery("unitID", unitID);
		ConstantScoreQueryBuilder csq = constantScoreQuery(tqb);
		request.setQuery(csq);
		request.setSize(0);
		SearchResponse response = executeSearchRequest(request);
		return response.getHits().getTotalHits() != 0;
	}

	@Override
	public Specimen[] findByUnitID(String unitID)
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("findByUnitID", unitID));
		}
		SearchRequestBuilder request = newSearchRequest(SPECIMEN);
		TermQueryBuilder tqb = termQuery("unitID", unitID);
		ConstantScoreQueryBuilder csq = constantScoreQuery(tqb);
		request.setQuery(csq);
		return processSearchRequest(request);
	}

	private static String[] namedCollections;

	@Override
	public String[] getNamedCollections()
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("getNamedCollections"));
		}
		if (namedCollections == null) {
			try {
				Set<String> themes = getDistinctValues("theme", null).keySet();
				namedCollections = themes.toArray(new String[themes.size()]);
			}
			catch (InvalidQueryException e) {
				assert (false);
				return null;
			}
		}
		return namedCollections;
	}

	@Override
	public String[] getIdsInCollection(String collectionName)
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("getIdsInCollection", collectionName));
		}
		TermQueryBuilder tq = termQuery("theme", collectionName);
		ConstantScoreQueryBuilder csq = constantScoreQuery(tq);
		SearchRequestBuilder request = newSearchRequest(SPECIMEN);
		request.setQuery(csq);
		request.setFetchSource(false);
		SearchResponse response = executeSearchRequest(request);
		SearchHit[] hits = response.getHits().getHits();
		String[] ids = new String[hits.length];
		for (int i = 0; i < hits.length; ++i) {
			ids[i] = hits[i].getId();
		}
		return ids;
	}

	@Override
	public void dwcaQuery(QuerySpec querySpec, OutputStream out) throws InvalidQueryException
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("dwcaQuery", querySpec, out));
		}
		try {
			DwcaConfig config = DwcaConfig.getDynamicDwcaConfig(DwcaDataSetType.SPECIMEN);
			IDwcaWriter writer = config.getWriter(out);
			writer.writeDwcaForQuery(querySpec);
		}
		catch (DataSetConfigurationException | DataSetWriteException e) {
			throw new DaoException(e);
		}
	}

	@Override
	public void dwcaGetDataSet(String name, OutputStream out) throws NoSuchDataSetException
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("dwcaGetDataSet", name, out));
		}
		try {
			DwcaConfig config = new DwcaConfig(name, DwcaDataSetType.SPECIMEN);
			IDwcaWriter writer = config.getWriter(out);
			writer.writeDwcaForDataSet();
		}
		catch (DataSetConfigurationException | DataSetWriteException e) {
			throw new DaoException(e);
		}
	}

	@Override
	public String[] dwcaGetDataSetNames()
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("dwcaGetDataSetNames"));
		}
		File dir = DwcaUtil.getDwcaConfigurationDirectory(DwcaDataSetType.SPECIMEN);
		File[] files = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				if (f.getName().startsWith("dynamic")) {
					return false;
				}
				if (f.isFile() && f.getName().endsWith(DwcaConfig.CONF_FILE_EXTENSION)) {
					return true;
				}
				return false;
			}
		});
		String[] names = new String[files.length];
		for (int i = 0; i < files.length; i++) {
			String name = files[i].getName();
			names[i] = name.substring(0, name.indexOf('.'));
		}
		Arrays.sort(names);
		return names;
	}

	@Override
	public QueryResult<ScientificNameGroup> groupByScientificName(
			GroupByScientificNameQuerySpec sngQuery) throws InvalidQueryException
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("groupByScientificName", sngQuery));
		}
		QueryResult<ScientificNameGroup> result = new QueryResult<>();

		/*
		 * First, get groups (a.k.a. buckets) without anything else. We don't
		 * want any documents to come back so in the QuerySpec we set from to
		 * null and size to 0. However we DO use the value of from and size,
		 * albeit with different semantics. Within the context of a
		 * groupByScientificName query, from is the offset in the list of
		 * buckets that we retrieve, and size is the number of buckets to
		 * retrieve. For those buckets we are going to subsequently retrieve
		 * taxa and specimens.
		 */
		int from = sngQuery.getFrom() == null ? 0 : sngQuery.getFrom();
		int size = sngQuery.getSize() == null ? 10 : sngQuery.getSize();
		List<SortField> sortFields = sngQuery.getSortFields();
		sngQuery.setFrom(null);
		sngQuery.setSize(0);
		sngQuery.setSortFields(null);
		QuerySpecTranslator translator = new QuerySpecTranslator(sngQuery, SPECIMEN);
		SearchRequestBuilder request = translator.translate();
		request.addAggregation(createAggregation(sngQuery));
		SearchResponse response = executeSearchRequest(request);
		Nested nested = response.getAggregations().get("NESTED");
		Terms terms = nested.getAggregations().get("TERMS");
		List<Bucket> buckets = terms.getBuckets();
		result.setTotalSize(buckets.size());

		/*
		 * Now morph the query into a regular Specimen query
		 */
		sngQuery.setFrom(sngQuery.getSpecimensFrom());
		sngQuery.setSize(sngQuery.getSpecimensSize());
		sngQuery.setSortFields(sortFields);

		/*
		 * Now, for the from-th to size-th bucket, retrieve the associated
		 * specimens and taxa.
		 */
		String field = "identifications.scientificName.scientificNameGroup";
		QueryCondition extraCondition = new QueryCondition(field, "=", null);
		extraCondition.setConstantScore(true);
		sngQuery.addCondition(extraCondition);
		int to = Math.min(buckets.size(), from + size);
		List<QueryResultItem<ScientificNameGroup>> resultSet = new ArrayList<>(size);
		for (int i = from; i < to; i++) {
			String name = buckets.get(i).getKeyAsString();
			ScientificNameGroup sng = new ScientificNameGroup(name);
			QueryResultItem<ScientificNameGroup> qri = new QueryResultItem<>(sng, 0);
			resultSet.add(qri);
			if (sngQuery.getSpecimensSize() == null || sngQuery.getSpecimensSize() > 0) {
				extraCondition.setValue(name);
				QueryResult<Specimen> specimens = query(sngQuery);
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
			if (!sngQuery.isNoTaxa() && buckets.size() > 1024) {
				addTaxaToGroup(sng);
			}
		}
		result.setResultSet(resultSet);
		if (!sngQuery.isNoTaxa() && buckets.size() <= 1024) {
			addTaxaToResult(result);
		}
		return result;
	}

	private static NestedAggregationBuilder createAggregation(
			GroupByScientificNameQuerySpec sngQuery) throws InvalidQueryException
	{
		TermsAggregationBuilder tab = terms("TERMS");
		tab.field("identifications.scientificName.scientificNameGroup");
		tab.size(Integer.parseInt(
				DaoRegistry.getInstance().getConfiguration().required("nl.naturalis.nba.dao.specimenDao.maxNumBuckets"))
				);
		if (sngQuery.getGroupSort() == NAME_ASC) {
			tab.order(Terms.Order.term(true));
		}
		else if (sngQuery.getGroupSort() == NAME_DESC) {
			tab.order(Terms.Order.term(false));
		}
		else if (sngQuery.getGroupSort() == COUNT_DESC) {
			tab.order(Terms.Order.count(false));
		}
		else if (sngQuery.getGroupSort() == COUNT_ASC) {
			tab.order(Terms.Order.count(true));
		}
		else { // TOP_HIT_SCORE
			TopHitsAggregationBuilder thab = topHits("TOP_HITS");
			thab.size(1);
			tab.subAggregation(thab);
			MaxAggregationBuilder mab = max("MAX");
			mab.script(new Script("_score"));
			tab.subAggregation(mab);
			tab.order(Terms.Order.aggregation("MAX", false));
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

	@Override
	Specimen[] createDocumentObjectArray(int length)
	{
		return new Specimen[length];
	}

}