package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.api.model.SourceSystem.COL;
import static nl.naturalis.nba.dao.DocumentType.TAXON;
import static nl.naturalis.nba.dao.util.es.ESUtil.getElasticsearchId;
import static nl.naturalis.nba.etl.ETLConstants.SYSPROP_DRY_RUN;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.getTestGenera;
import static nl.naturalis.nba.etl.col.CoLVernacularNameCsvField.language;
import static nl.naturalis.nba.etl.col.CoLVernacularNameCsvField.taxonID;
import static nl.naturalis.nba.etl.col.CoLVernacularNameCsvField.vernacularName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.logging.log4j.Logger;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.VernacularName;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.utils.ConfigObject;

class CoLVernacularNameBatchTransformer {

	private static final Logger logger = getLogger(CoLVernacularNameBatchTransformer.class);

	// The number of vernacular names created
	private int numCreated;
	// The number of taxa with vernacular names
	private int numUpdated;
	private int numDuplicates;
	private int numOrphans;

	private String[] testGenera;
	private boolean dry = ConfigObject.isEnabled(SYSPROP_DRY_RUN);

	CoLVernacularNameBatchTransformer()
	{
		testGenera = getTestGenera();
	}

	Collection<Taxon> transform(ArrayList<CSVRecordInfo<CoLVernacularNameCsvField>> records)
	{
		HashMap<String, Taxon> lookupTable = createLookupTable(records);
		for (CSVRecordInfo<CoLVernacularNameCsvField> record : records) {
			String id = record.get(taxonID);
			Taxon taxon = lookupTable.get(id);
			VernacularName vernacular = createVernacularName(record);
			if (taxon == null) {
				/*
				 * When executing a dry run or importing a test set, we're bound
				 * to have huge amounts of orphans, so we are not going to
				 * report on this in that case.
				 */
				if (dry == false && testGenera == null) {
					++numOrphans;
					logger.error("{} | Orphan: {} ", id, vernacular);
				}
			}
			else if (taxon.getVernacularNames() == null) {
				++numCreated;
				++numUpdated;
				taxon.addVernacularName(vernacular);
			}
			else if (!taxon.getVernacularNames().contains(vernacular)) {
				++numCreated;
				taxon.addVernacularName(vernacular);
			}
			else {
				++numDuplicates;
				logger.error("{} | Duplicate vernacular name: {}", id, vernacular);
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
			ArrayList<CSVRecordInfo<CoLVernacularNameCsvField>> records)
	{
		HashSet<String> ids = new HashSet<>(records.size());
		for (CSVRecordInfo<CoLVernacularNameCsvField> record : records) {
			ids.add(getElasticsearchId(COL, record.get(taxonID)));
		}
		DocumentType<Taxon> dt = TAXON;
		SearchRequest searchRequest = ESUtil.newSearchRequest(dt);
		IdsQueryBuilder query = QueryBuilders.idsQuery();
		query.addIds(ids.toArray(new String[ids.size()]));

		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(query);
		searchSourceBuilder.size(ids.size());
		
		SearchResponse response = ESUtil.executeSearchRequest(searchRequest);		
		SearchHits hits = response.getHits();
		SearchHit[] searchHits = hits.getHits();
		
		HashMap<String, Taxon> taxa = new HashMap<>(searchHits.length + 4, 1F);
		ObjectMapper om = dt.getObjectMapper();
		
		for (SearchHit hit : hits) {
			Taxon taxon = om.convertValue(hit.getSourceAsMap(), dt.getJavaType());
		  taxon.setId(hit.getId());
			taxa.put(taxon.getSourceSystemId(), taxon);
		}

		return taxa;
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
