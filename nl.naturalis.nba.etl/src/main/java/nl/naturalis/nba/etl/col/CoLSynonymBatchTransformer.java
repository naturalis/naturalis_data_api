package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.api.model.SourceSystem.COL;
import static nl.naturalis.nba.dao.DocumentType.TAXON;
import static nl.naturalis.nba.dao.util.es.ESUtil.getElasticsearchId;
import static nl.naturalis.nba.etl.ETLConstants.SYSPROP_DRY_RUN;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.getTestGenera;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.acceptedNameUsageID;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.genericName;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.infraspecificEpithet;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.scientificName;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.scientificNameAuthorship;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.specificEpithet;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.taxonID;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.taxonomicStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.TaxonomicStatus;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.TransformUtil;
import nl.naturalis.nba.etl.normalize.TaxonomicStatusNormalizer;
import nl.naturalis.nba.etl.normalize.UnmappedValueException;
import nl.naturalis.nba.utils.ConfigObject;

class CoLSynonymBatchTransformer {

	private static final Logger logger;
	private static final TaxonomicStatusNormalizer statusNormalizer;

	static {
		logger = getLogger(CoLSynonymBatchTransformer.class);
		statusNormalizer = TaxonomicStatusNormalizer.getInstance();
	}

	// The number of synonyms created
	private int numCreated;
	// The number of taxa with synonyms
	private int numUpdated;
	private int numDuplicates;
	private int numOrphans;

	private String[] testGenera;
	private boolean dry = ConfigObject.isEnabled(SYSPROP_DRY_RUN);

	CoLSynonymBatchTransformer()
	{
		testGenera = getTestGenera();
	}

	Collection<Taxon> transform(ArrayList<CSVRecordInfo<CoLTaxonCsvField>> records)
	{
		HashMap<String, Taxon> lookupTable = createLookupTable(records);
		for (CSVRecordInfo<CoLTaxonCsvField> record : records) {
			String id = record.get(acceptedNameUsageID);
			Taxon taxon = lookupTable.get(id);
			ScientificName synonym = createSynonym(record);
			if (taxon == null) {
				/*
				 * When executing a dry run or importing a test set, we're bound
				 * to have huge amounts of orphans, so we are not going to
				 * report on this in that case.
				 */
				if (dry == false || testGenera == null) {
					++numOrphans;
					logger.error("{} | Orphan: {} ", id, synonym.getFullScientificName());
				}
			}
			else if (taxon.getSynonyms() == null) {
				++numCreated;
				++numUpdated;
				taxon.addSynonym(synonym);
			}
			else if (!hasSynonym(taxon, synonym)) {
				++numCreated;
				taxon.addSynonym(synonym);
			}
			else {
				++numDuplicates;
				logger.error("{} | Duplicate synonym: {}", id, synonym.getScientificNameGroup());
			}
		}
		return lookupTable.values();
	}

	int getNumCreated()
	{
		return numCreated;
	}

	int getNumUpdated()
	{
		return numUpdated;
	}

	int getNumDuplicates()
	{
		return numDuplicates;
	}

	int getNumOrphans()
	{
		return numOrphans;
	}

	private static HashMap<String, Taxon> createLookupTable(
			ArrayList<CSVRecordInfo<CoLTaxonCsvField>> records)
	{
		HashSet<String> ids = new HashSet<>(records.size());
		for (CSVRecordInfo<CoLTaxonCsvField> record : records) {
			ids.add(getElasticsearchId(COL, record.get(acceptedNameUsageID)));
		}
		DocumentType<Taxon> dt = TAXON;
		SearchRequestBuilder request = ESUtil.newSearchRequest(dt);
		IdsQueryBuilder query = QueryBuilders.idsQuery(dt.getName());
		query.addIds(ids.toArray(new String[ids.size()]));
		request.setQuery(query);
		request.setSize(ids.size());
		SearchResponse response = ESUtil.executeSearchRequest(request);
		SearchHit[] hits = response.getHits().getHits();
		HashMap<String, Taxon> taxa = new HashMap<>(hits.length + 4, 1F);
		ObjectMapper om = dt.getObjectMapper();
		for (SearchHit hit : hits) {
			Taxon taxon = om.convertValue(hit.getSource(), dt.getJavaType());
			taxon.setId(hit.getId());
			taxa.put(taxon.getSourceSystemId(), taxon);
		}
		return taxa;
	}

	private static ScientificName createSynonym(CSVRecordInfo<CoLTaxonCsvField> record)
	{
		ScientificName sn = new ScientificName();
		sn.setFullScientificName(record.get(scientificName));
		sn.setGenusOrMonomial(record.get(genericName));
		sn.setSpecificEpithet(record.get(specificEpithet));
		sn.setInfraspecificEpithet(record.get(infraspecificEpithet));
		sn.setAuthorshipVerbatim(record.get(scientificNameAuthorship));
		TaxonomicStatus status = null;
		try {
			status = statusNormalizer.map(record.get(taxonomicStatus));
		}
		catch (UnmappedValueException e) {
			String id = record.get(taxonID);
			logger.warn("{} | {}", id, e.getMessage());
		}
		sn.setTaxonomicStatus(status);
		TransformUtil.setScientificNameGroup(sn);
		return sn;
	}

	private static boolean hasSynonym(Taxon taxon, ScientificName syn)
	{
		for (ScientificName sn : taxon.getSynonyms()) {
			if (equals(sn.getFullScientificName(), syn.getFullScientificName())
					&& sn.getTaxonomicStatus() == syn.getTaxonomicStatus()
					&& equals(sn.getAuthorshipVerbatim(), syn.getAuthorshipVerbatim())
					&& equals(sn.getYear(), syn.getYear())
					&& sn.getScientificNameGroup().equals(syn.getScientificNameGroup())) {
				return true;
			}
		}
		return false;
	}

	private static boolean equals(Object o1, Object o2)
	{
		if (o1 == null) {
			if (o2 == null) {
				return true;
			}
			return false;
		}
		if (o2 == null) {
			return false;
		}
		return o1.equals(o2);
	}

}
