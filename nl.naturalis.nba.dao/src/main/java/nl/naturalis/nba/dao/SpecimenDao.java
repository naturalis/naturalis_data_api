package nl.naturalis.nba.dao;

import static nl.naturalis.nba.api.GroupByScientificNameQuerySpec.GroupSort.COUNT_ASC;
import static nl.naturalis.nba.api.GroupByScientificNameQuerySpec.GroupSort.NAME_ASC;
import static nl.naturalis.nba.api.GroupByScientificNameQuerySpec.GroupSort.NAME_DESC;
import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;
import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;
import static nl.naturalis.nba.dao.util.es.ESUtil.newSearchRequest;
import static org.elasticsearch.index.query.QueryBuilders.constantScoreQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.search.aggregations.AggregationBuilders.nested;
import static org.elasticsearch.search.aggregations.AggregationBuilders.terms;

import java.io.File;
import java.io.FileFilter;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.ConstantScoreQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.xml.sax.SAXParseException;

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
import nl.naturalis.nba.utils.xml.XmlFileUpdater;

public class SpecimenDao extends NbaDao<Specimen> implements ISpecimenAccess {

	private static Logger logger = getLogger(SpecimenDao.class);

	public SpecimenDao()
	{
		super(SPECIMEN);
	}

	@Override
	public boolean exists(String unitID)
	{
		if (logger.isDebugEnabled())
			logger.debug("exists(\"{}\")", unitID);
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
		if (logger.isDebugEnabled())
			logger.debug("findByUnitID(\"{}\")", unitID);
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
		if (logger.isDebugEnabled())
			logger.debug("getUnitIDsInCollection(\"{}\")", collectionName);
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
		try {
			DwcaConfig config = new DwcaConfig(name, DwcaDataSetType.SPECIMEN);
			////////////////////// BEGIN TEMPORARY CODE
			// Remove when Jeroen Creuwels has corrected all EML files. By
			// attempting to read the EML here already, rather than when we
			// are already busy sending the DwCA file to the client, the
			// client gets informed about what is wrong with the EML file.
			XmlFileUpdater emlUpdater = new XmlFileUpdater(config.getEmlFile());
			try {
				emlUpdater.readFile();
			}
			catch (SAXParseException e) {
				throw new DaoException("Error while parsing EML file: " + e.toString());
			}
			////////////////////// END TEMPORARY CODE
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
		QueryResult<ScientificNameGroup> result = new QueryResult<>();
		/*
		 * First, just get ALL groups a.k.a. buckets without anything else.
		 */
		int from = sngQuery.getFrom() == null ? 0 : sngQuery.getFrom();
		int size = sngQuery.getSize() == null ? 10 : sngQuery.getSize();
		List<SortField> sortFields = sngQuery.getSortFields();
		sngQuery.setFrom(null);
		sngQuery.setSize(0);
		/*
		 * Elasticsearch is probably intelligent enough to not bother sorting
		 * when size equals 0, but just to be sure ... (we put the sort fields
		 * back on the QuerySpec later on)
		 */
		sngQuery.setSortFields(null);
		QuerySpecTranslator translator = new QuerySpecTranslator(sngQuery, SPECIMEN);
		SearchRequestBuilder request = translator.translate();
		request.addAggregation(createAggregation(sngQuery));
		SearchResponse response = executeSearchRequest(request);
		Nested nested = response.getAggregations().get("NESTED");
		Terms terms = nested.getAggregations().get("TERMS");
		List<Bucket> buckets = terms.getBuckets();
		buckets = filterBuckets(buckets, sngQuery.getGroupFilter());
		result.setTotalSize(buckets.size());
		/*
		 * Now extract the from-th to size-th bucket and for each bucket
		 * retrieve the specimens satisfying the original search criteria PLUS
		 * an extra condition limiting the specimens to those whose scientific
		 * name equals the value of the bucket. NB from is the offset in the
		 * list of buckets, while specimensFrom is the offset in the list of
		 * specimens for each bucket.
		 */
		sngQuery.setFrom(sngQuery.getSpecimensFrom());
		sngQuery.setSize(sngQuery.getSpecimensSize());
		sngQuery.setSortFields(sortFields);
		String field = "identifications.scientificName.scientificNameGroup";
		QueryCondition extraCondition = new QueryCondition(field, "=", null);
		extraCondition.setConstantScore(true);
		sngQuery.addCondition(extraCondition);
		int to = Math.min(buckets.size(), from + size);
		List<QueryResultItem<ScientificNameGroup>> resultSet = new ArrayList<>(size);
		for (int i = from; i < to; i++) {
			String name = buckets.get(i).getKeyAsString();
			ScientificNameGroup sng = new ScientificNameGroup(name);
			resultSet.add(new QueryResultItem<ScientificNameGroup>(sng, 0));
			if (sngQuery.getSpecimensSize() == null || sngQuery.getSpecimensSize() > 0) {
				extraCondition.setValue(name);
				QueryResult<Specimen> specimens = query(sngQuery);
				sng.setSpecimenCount((int) specimens.getTotalSize());
				for (QueryResultItem<Specimen> specimen : specimens) {
					sng.addSpecimen(specimen.getItem());
				}
			}
			if (!sngQuery.isNoTaxa()) {
				addTaxaToGroup(sng);
			}
		}
		result.setResultSet(resultSet);
		return result;
	}

	private static NestedAggregationBuilder createAggregation(
			GroupByScientificNameQuerySpec sngQuery)
	{
		TermsAggregationBuilder tab = terms("TERMS");
		tab.field("identifications.scientificName.scientificNameGroup");
		tab.size(10000000);
		if (sngQuery.getGroupSort() == NAME_ASC) {
			tab.order(Terms.Order.term(true));
		}
		else if (sngQuery.getGroupSort() == NAME_DESC) {
			tab.order(Terms.Order.term(false));
		}
		else if (sngQuery.getGroupSort() == COUNT_ASC) {
			tab.order(Terms.Order.count(true));
		}
		NestedAggregationBuilder nab = nested("NESTED", "identifications");
		nab.subAggregation(tab);
		return nab;
	}

	private static List<Bucket> filterBuckets(List<Bucket> buckets, Set<String> filter)
	{
		if (filter == null || filter.size() == 0) {
			return buckets;
		}
		List<Bucket> filtered = new ArrayList<>(filter.size());
		for (Bucket bucket : buckets) {
			if (filter.contains(bucket.getKey())) {
				filtered.add(bucket);
			}
		}
		return filtered;
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

	@Override
	Specimen[] createDocumentObjectArray(int length)
	{
		return new Specimen[length];
	}

}