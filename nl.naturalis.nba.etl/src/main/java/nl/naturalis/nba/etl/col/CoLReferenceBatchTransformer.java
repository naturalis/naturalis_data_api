package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.api.model.SourceSystem.COL;
import static nl.naturalis.nba.dao.DocumentType.TAXON;
import static nl.naturalis.nba.dao.util.es.ESUtil.getElasticsearchId;
import static nl.naturalis.nba.etl.ETLConstants.SYSPROP_DRY_RUN;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.getTestGenera;
import static nl.naturalis.nba.etl.col.CoLReferenceCsvField.creator;
import static nl.naturalis.nba.etl.col.CoLReferenceCsvField.date;
import static nl.naturalis.nba.etl.col.CoLReferenceCsvField.description;
import static nl.naturalis.nba.etl.col.CoLReferenceCsvField.taxonID;
import static nl.naturalis.nba.etl.col.CoLReferenceCsvField.title;

import java.time.OffsetDateTime;
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

import nl.naturalis.nba.api.model.Person;
import nl.naturalis.nba.api.model.Reference;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.common.es.ESDateInput;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.utils.ConfigObject;

class CoLReferenceBatchTransformer {

	private static final Logger logger = getLogger(CoLReferenceBatchTransformer.class);

	// The number of references created
	private int numCreated;
	// The number of taxa with references
	private int numUpdated;
	private int numDuplicates;
	private int numOrphans;

	private String[] testGenera;
	private boolean dry = ConfigObject.isEnabled(SYSPROP_DRY_RUN);

	CoLReferenceBatchTransformer()
	{
		testGenera = getTestGenera();
	}

	Collection<Taxon> transform(ArrayList<CSVRecordInfo<CoLReferenceCsvField>> records)
	{
		HashMap<String, Taxon> lookupTable = createLookupTable(records);
		for (CSVRecordInfo<CoLReferenceCsvField> record : records) {
			String id = record.get(taxonID);
			Taxon taxon = lookupTable.get(id);
			Reference reference = createReference(record);
			if (taxon == null) {
				/*
				 * When executing a dry run or importing a test set, we're bound
				 * to have huge amounts of orphans, so we are not going to
				 * report on this in that case.
				 */
				if (dry == false && testGenera == null) {
					++numOrphans;
					logger.error("{} | Orphan: {} ", id, reference);
				}
			}
			else if (taxon.getReferences() == null) {
				++numCreated;
				++numUpdated;
				taxon.addReference(reference);
			}
			else if (!taxon.getReferences().contains(reference)) {
				++numCreated;
				taxon.addReference(reference);
			}
			else {
				++numDuplicates;
				logger.error("{} | Duplicate reference: {}", id, reference);
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
			ArrayList<CSVRecordInfo<CoLReferenceCsvField>> records)
	{
		HashSet<String> ids = new HashSet<>(records.size());
		for (CSVRecordInfo<CoLReferenceCsvField> record : records) {
			ids.add(getElasticsearchId(COL, record.get(taxonID)));
		}
		DocumentType<Taxon> dt = TAXON;

		// ES 5
//		SearchRequestBuilder request = ESUtil.newSearchRequest(dt);
//		IdsQueryBuilder query = QueryBuilders.idsQuery(dt.getName());
//		query.addIds(ids.toArray(new String[ids.size()]));
//		request.setQuery(query);
//		request.setSize(ids.size());
//		SearchResponse response = ESUtil.executeSearchRequest(request);
//		SearchHit[] hits = response.getHits().getHits();
//		HashMap<String, Taxon> taxa = new HashMap<>(hits.length + 4, 1F);
//		ObjectMapper om = dt.getObjectMapper();
//		for (SearchHit hit : hits) {
//			Taxon taxon = om.convertValue(hit.getSource(), dt.getJavaType());
//			taxon.setId(hit.getId());
//			taxa.put(taxon.getSourceSystemId(), taxon);
//		}
//		return taxa;
		
		// ES 7
    SearchRequest searchRequest = ESUtil.newSearchRequest(dt);
    IdsQueryBuilder query = QueryBuilders.idsQuery();
    query.addIds(ids.toArray(new String[ids.size()]));
    
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.query(query);
    searchSourceBuilder.size(ids.size());

    SearchResponse response = ESUtil.executeSearchRequest(searchRequest);
    SearchHits searchHits = response.getHits();
    SearchHit[] hits = searchHits.getHits();
    
    HashMap<String, Taxon> taxa = new HashMap<>(hits.length + 4, 1F);
    ObjectMapper om = dt.getObjectMapper();
    
    for (SearchHit hit : hits) {
      Taxon taxon = om.convertValue(hit.getSourceAsMap(), dt.getJavaType());
      taxon.setId(hit.getId());
      taxa.put(taxon.getSourceSystemId(), taxon);
    }
    
    return taxa;
	}

	private static Reference createReference(CSVRecordInfo<CoLReferenceCsvField> record)
	{
		Reference ref = new Reference();
		ref.setTitleCitation(record.get(title));
		ref.setCitationDetail(record.get(description));
		String s;
		if ((s = record.get(date)) != null) {
			OffsetDateTime odt = new ESDateInput(s).parseAsYear();
			if (odt == null) {
				logger.warn("Invalid date: {}", s);
			}
			else {
				ref.setPublicationDate(odt);
			}
		}
		if ((s = record.get(creator)) != null) {
			ref.setAuthor(new Person(s));
		}
		return ref;
	}

}
