package nl.naturalis.nba.dao;

import static nl.naturalis.nba.api.GroupByScientificNameQuerySpec.GroupSort.COUNT_ASC;
import static nl.naturalis.nba.api.GroupByScientificNameQuerySpec.GroupSort.COUNT_DESC;
import static nl.naturalis.nba.api.GroupByScientificNameQuerySpec.GroupSort.NAME_ASC;
import static nl.naturalis.nba.api.GroupByScientificNameQuerySpec.GroupSort.NAME_DESC;
import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.DocumentType.TAXON;
import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;
import static nl.naturalis.nba.utils.debug.DebugUtil.printCall;
import static org.elasticsearch.search.aggregations.AggregationBuilders.max;
import static org.elasticsearch.search.aggregations.AggregationBuilders.terms;
import static org.elasticsearch.search.aggregations.AggregationBuilders.topHits;

import java.io.File;
import java.io.FileFilter;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.support.IncludeExclude;
import org.elasticsearch.search.aggregations.metrics.max.MaxAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHitsAggregationBuilder;
import org.xml.sax.SAXParseException;

import nl.naturalis.nba.api.Filter;
import nl.naturalis.nba.api.GroupByScientificNameQuerySpec;
import nl.naturalis.nba.api.ITaxonAccess;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.NoSuchDataSetException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QueryResultItem;
import nl.naturalis.nba.api.QuerySpec;
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

public class TaxonDao extends NbaDao<Taxon> implements ITaxonAccess {

	private static final Logger logger = getLogger(TaxonDao.class);

	public TaxonDao()
	{
		super(TAXON);
	}

	@Override
	public void dwcaQuery(QuerySpec querySpec, OutputStream out) throws InvalidQueryException
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("dwcaQuery", querySpec, out));
		}
		try {
			DwcaConfig config = DwcaConfig.getDynamicDwcaConfig(DwcaDataSetType.TAXON);
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
			DwcaConfig config = new DwcaConfig(name, DwcaDataSetType.TAXON);
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
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("dwcaGetDataSetNames"));
		}
		File dir = DwcaUtil.getDwcaConfigurationDirectory(DwcaDataSetType.TAXON);
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
		QuerySpecTranslator translator = new QuerySpecTranslator(sngQuery, TAXON);
		SearchRequestBuilder request = translator.translate();
		request.addAggregation(createAggregation(sngQuery));
		SearchResponse response = executeSearchRequest(request);
		Terms terms = response.getAggregations().get("TERMS");
		List<Bucket> buckets = terms.getBuckets();
		//buckets = filterBuckets(buckets, sngQuery.getGroupFilter());
		result.setTotalSize(buckets.size());
		int from = sngQuery.getFrom() == null ? 0 : sngQuery.getFrom();
		int size = sngQuery.getSize() == null ? 10 : sngQuery.getSize();
		int to = Math.min(buckets.size(), from + size);
		/*
		 * Contrary to SpecimenDao.groupByScientificName you cannot page through
		 * the taxa associated with a scientific name, because there can be no
		 * more than 2 taxa per scientific name. So you just always get all taxa
		 * associated with a name, unless sngQuery.noTaxa == true.
		 */
		sngQuery.setFrom(0);
		/*
		 * Unless strange things are going on we can have at most two taxa per
		 * scientific name (NSR, COL). So setting size to 2 should be enough,
		 * but just to capture those strange things:
		 */
		sngQuery.setSize(1000);
		String field = "acceptedName.scientificNameGroup";
		QueryCondition extraCondition = new QueryCondition(field, "=", null);
		extraCondition.setConstantScore(true);
		sngQuery.addCondition(extraCondition);
		List<QueryResultItem<ScientificNameGroup>> resultSet = new ArrayList<>(size);
		for (int i = from; i < to; i++) {
			String name = buckets.get(i).getKeyAsString();
			ScientificNameGroup sng = new ScientificNameGroup(name);
			resultSet.add(new QueryResultItem<ScientificNameGroup>(sng, 0));
			if (!sngQuery.isNoTaxa()) {
				extraCondition.setValue(name);
				QueryResult<Taxon> taxa = query(sngQuery);
				sng.setTaxonCount((int) taxa.getTotalSize());
				for (QueryResultItem<Taxon> taxon : taxa) {
					sng.addTaxon(taxon.getItem());
				}
			}
			if (sngQuery.getSpecimensSize() == null || sngQuery.getSpecimensSize() > 0) {
				addSpecimens(sng, sngQuery);
			}
		}
		result.setResultSet(resultSet);
		return result;
	}

	private static TermsAggregationBuilder createAggregation(
			GroupByScientificNameQuerySpec sngQuery) throws InvalidQueryException
	{
		TermsAggregationBuilder tab = terms("TERMS");
		tab.field("acceptedName.scientificNameGroup");
		tab.size(10000000);
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
		else {
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

	@Override
	Taxon[] createDocumentObjectArray(int length)
	{
		return new Taxon[length];
	}

}
