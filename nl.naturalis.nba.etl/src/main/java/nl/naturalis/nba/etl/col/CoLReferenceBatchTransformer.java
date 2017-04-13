package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.api.model.SourceSystem.COL;
import static nl.naturalis.nba.dao.DocumentType.TAXON;
import static nl.naturalis.nba.dao.util.es.ESUtil.getElasticsearchId;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.col.CoLReferenceCsvField.creator;
import static nl.naturalis.nba.etl.col.CoLReferenceCsvField.date;
import static nl.naturalis.nba.etl.col.CoLReferenceCsvField.description;
import static nl.naturalis.nba.etl.col.CoLReferenceCsvField.taxonID;
import static nl.naturalis.nba.etl.col.CoLReferenceCsvField.title;
import static org.elasticsearch.index.query.QueryBuilders.constantScoreQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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

import nl.naturalis.nba.api.model.Person;
import nl.naturalis.nba.api.model.Reference;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.TransformUtil;

public class CoLReferenceBatchTransformer {

	private static final Logger logger = getLogger(CoLReferenceBatchTransformer.class);

	// Tweak option
	private static final boolean LOAD_BY_ID = false;

	// The number of references created
	private int numCreated;
	// The number of taxa with references
	private int numUpdated;
	private int numDuplicates;

	public CoLReferenceBatchTransformer()
	{
	}

	public Collection<Taxon> transform(ArrayList<CSVRecordInfo<CoLReferenceCsvField>> records)
	{
		HashMap<String, Taxon> taxa;
		if (LOAD_BY_ID) {
			taxa = loadTaxaById(records);
		}
		else {
			taxa = loadTaxaBySourceSystemId(records);
		}
		for (CSVRecordInfo<CoLReferenceCsvField> record : records) {
			Reference ref = createReference(record);
			Taxon taxon = taxa.get(record.get(taxonID));
			if (taxon == null) {
				String fmt = "Orphan reference. No taxon found for ID {} ({})";
//				logger.error(fmt, record.get(taxonID), ref);
				continue;
			}
			if (taxon.getReferences() == null) {
				++numCreated;
				++numUpdated;
				taxon.addReference(ref);
			}
			else if (taxon.getReferences().contains(ref) == false) {
				++numCreated;
				taxon.addReference(ref);
			}
			else {
				++numDuplicates;
				logger.error("Duplicate reference: {}", ref);
			}
		}
		return taxa.values();
	}

	public int getNumCreated()
	{
		return numCreated;
	}

	public int getNumUpdated()
	{
		return numUpdated;
	}

	public int getNumDuplicates()
	{
		return numDuplicates;
	}

	private static HashMap<String, Taxon> loadTaxaById(
			ArrayList<CSVRecordInfo<CoLReferenceCsvField>> records)
	{
		HashSet<String> ids = new HashSet<>(records.size());
		for (CSVRecordInfo<CoLReferenceCsvField> record : records) {
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
			ArrayList<CSVRecordInfo<CoLReferenceCsvField>> records)
	{
		HashSet<String> ids = new HashSet<>(records.size());
		for (CSVRecordInfo<CoLReferenceCsvField> record : records) {
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

	private static Reference createReference(CSVRecordInfo<CoLReferenceCsvField> record)
	{
		Reference ref = new Reference();
		ref.setTitleCitation(record.get(title));
		ref.setCitationDetail(record.get(description));
		String s;
		if ((s = record.get(date)) != null) {
			Date pubDate = TransformUtil.parseDate(s);
			ref.setPublicationDate(pubDate);
		}
		if ((s = record.get(creator)) != null) {
			ref.setAuthor(new Person(s));
		}
		return ref;
	}

}
