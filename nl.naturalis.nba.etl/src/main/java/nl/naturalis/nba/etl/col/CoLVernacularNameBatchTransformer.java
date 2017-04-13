package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.api.model.SourceSystem.COL;
import static nl.naturalis.nba.dao.DocumentType.TAXON;
import static nl.naturalis.nba.dao.util.es.ESUtil.getElasticsearchId;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.getTestGenera;
import static nl.naturalis.nba.etl.col.CoLVernacularNameCsvField.language;
import static nl.naturalis.nba.etl.col.CoLVernacularNameCsvField.taxonID;
import static nl.naturalis.nba.etl.col.CoLVernacularNameCsvField.vernacularName;
import static org.elasticsearch.index.query.QueryBuilders.constantScoreQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.VernacularName;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.CSVRecordInfo;

class CoLVernacularNameBatchTransformer {

	private static final Logger logger = getLogger(
			CoLVernacularNameBatchTransformer.class);

	// Tweak option
	private static final boolean LOAD_BY_ID = false;

	// The number of vernacular names created
	private int numCreated;
	// The number of taxa with vernacular names
	private int numUpdated;
	private int numDuplicates;
	private int numOrphans;

	private String[] testGenera;

	CoLVernacularNameBatchTransformer()
	{
		testGenera = getTestGenera();
	}

	Collection<Taxon> transform(
			ArrayList<CSVRecordInfo<CoLVernacularNameCsvField>> records)
	{
		HashMap<String, Taxon> taxa;
		if (LOAD_BY_ID) {
			taxa = loadTaxaById(records);
		}
		else {
			taxa = loadTaxaBySourceSystemId(records);
		}
		for (CSVRecordInfo<CoLVernacularNameCsvField> record : records) {
			VernacularName vn = createVernacularName(record);
			String id = record.get(taxonID);
			Taxon taxon = taxa.get(id);
			if (taxon == null) {
				++numOrphans;
				if (testGenera == null) {
					logger.error("{} | Orphan vernacular name: {} ", id, vn);
				}
				continue;
			}
			if (taxon.getVernacularNames() == null) {
				++numCreated;
				++numUpdated;
				taxon.addVernacularName(vn);
			}
			else if (taxon.getVernacularNames().contains(vn) == false) {
				++numCreated;
				taxon.addVernacularName(vn);
			}
			else {
				++numDuplicates;
				logger.error("{} | Duplicate vernacular name: {}", id, vn);
			}
		}
		return taxa.values();
	}

	public int getNumCreated()
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

	private static HashMap<String, Taxon> loadTaxaById(
			ArrayList<CSVRecordInfo<CoLVernacularNameCsvField>> records)
	{
		HashSet<String> ids = new HashSet<>(records.size());
		for (CSVRecordInfo<CoLVernacularNameCsvField> record : records) {
			String id = getElasticsearchId(COL, record.get(taxonID));
			ids.add(id);
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

	private static HashMap<String, Taxon> loadTaxaBySourceSystemId(
			ArrayList<CSVRecordInfo<CoLVernacularNameCsvField>> records)
	{
		HashSet<String> ids = new HashSet<>(records.size());
		for (CSVRecordInfo<CoLVernacularNameCsvField> record : records) {
			ids.add(record.get(taxonID));
		}
		DocumentType<Taxon> dt = TAXON;
		SearchRequestBuilder request = ESUtil.newSearchRequest(dt);
		TermsQueryBuilder query = QueryBuilders.termsQuery("sourceSystemId", ids);
		request.setQuery(constantScoreQuery(query));
		request.setSize(ids.size());
		SearchResponse response = ESUtil.executeSearchRequest(request);
		SearchHit[] hits = response.getHits().getHits();
		HashMap<String, Taxon> result = new HashMap<>(hits.length + 4, 1F);
		ObjectMapper om = dt.getObjectMapper();
		for (SearchHit hit : hits) {
			Taxon taxon = om.convertValue(hit.getSource(), dt.getJavaType());
			taxon.setId(hit.getId());
			result.put(taxon.getSourceSystemId(), taxon);
		}
		return result;
	}

	private static VernacularName createVernacularName(
			CSVRecordInfo<CoLVernacularNameCsvField> input)
	{
		VernacularName vn = new VernacularName();
		vn.setName(input.get(vernacularName));
		vn.setLanguage(input.get(language));
		return vn;
	}

}
