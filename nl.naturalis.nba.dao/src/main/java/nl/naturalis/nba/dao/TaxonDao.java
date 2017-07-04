package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.DocumentType.TAXON;
import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;
import static org.elasticsearch.search.aggregations.AggregationBuilders.terms;

import java.io.File;
import java.io.FileFilter;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;

import nl.naturalis.nba.api.ITaxonAccess;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.NoSuchDataSetException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QueryResultItem;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.ScientificNameGroupQuerySpec;
import nl.naturalis.nba.api.model.ScientificNameGroup2;
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

public class TaxonDao extends NbaDao<Taxon> implements ITaxonAccess {

	@SuppressWarnings("unused")
	private static final Logger logger = getLogger(TaxonDao.class);

	public TaxonDao()
	{
		super(TAXON);
	}

	@Override
	public void dwcaQuery(QuerySpec querySpec, OutputStream out) throws InvalidQueryException
	{
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
		try {
			DwcaConfig config = new DwcaConfig(name, DwcaDataSetType.TAXON);
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
		return names;
	}

	@Override
	public QueryResult<ScientificNameGroup2> groupByScientificName(
			ScientificNameGroupQuerySpec sngQuery) throws InvalidQueryException
	{
		QueryResult<ScientificNameGroup2> result = new QueryResult<>();
		QuerySpecTranslator translator = new QuerySpecTranslator(sngQuery, TAXON);
		SearchRequestBuilder request = translator.translate();
		TermsAggregationBuilder tab = terms("TERMS");
		String sngField = "acceptedName.scientificNameGroup";
		tab.field(sngField);
		tab.size(1000000);
		request.addAggregation(tab);
		SearchResponse response = executeSearchRequest(request);
		Terms terms = response.getAggregations().get("TERMS");
		List<Bucket> buckets = terms.getBuckets();
		result.setTotalSize(buckets.size());
		int f = sngQuery.getFrom() == null ? 0 : sngQuery.getFrom();
		int s = sngQuery.getSize() == null ? 10 : sngQuery.getSize();
		int to = Math.min(buckets.size(), f + s);
		QueryCondition extraCondition = new QueryCondition(sngField, "=", null);
		extraCondition.setConstantScore(true);
		sngQuery.addCondition(extraCondition);
		List<QueryResultItem<ScientificNameGroup2>> resultSet = new ArrayList<>(to);
		for (int i = f; i < to; i++) {
			String name = buckets.get(i).getKeyAsString();
			extraCondition.setValue(name);
			QueryResult<Taxon> taxa = query(sngQuery);
			ScientificNameGroup2 sng = new ScientificNameGroup2(name);
			sng.setTaxonCount((int) taxa.getTotalSize());
			for (QueryResultItem<Taxon> taxon : taxa) {
				sng.addTaxon(taxon.getItem());
			}
			QueryResultItem<ScientificNameGroup2> item;
			item = new QueryResultItem<ScientificNameGroup2>(sng, 0);
			resultSet.add(item);
			if (sngQuery.getSpecimensSize() == null || sngQuery.getSpecimensSize() > 0) {
				addSpecimens(sng, sngQuery);
			}
		}
		result.setResultSet(resultSet);
		return result;
	}

	private static void addSpecimens(ScientificNameGroup2 sng,
			ScientificNameGroupQuerySpec sngQuery) throws InvalidQueryException
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
