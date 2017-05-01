package nl.naturalis.nba.etl.enrich;

import static nl.naturalis.nba.dao.DocumentType.MULTI_MEDIA_OBJECT;
import static nl.naturalis.nba.dao.DocumentType.TAXON;
import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;
import static nl.naturalis.nba.dao.util.es.ESUtil.newSearchRequest;
import static nl.naturalis.nba.dao.util.es.ESUtil.refreshIndex;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;
import static nl.naturalis.nba.etl.SummaryObjectUtil.copyScientificName;
import static nl.naturalis.nba.etl.SummaryObjectUtil.copySourceSystem;
import static nl.naturalis.nba.etl.SummaryObjectUtil.copySummaryVernacularName;
import static org.elasticsearch.index.query.QueryBuilders.constantScoreQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.MultiMediaContentIdentification;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.TaxonomicEnrichment;
import nl.naturalis.nba.api.model.VernacularName;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.DocumentIterator;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.BulkIndexException;
import nl.naturalis.nba.etl.BulkIndexer;
import nl.naturalis.nba.etl.ETLRuntimeException;

public class MultiMediaObjectEnricher {

	public static void main(String[] args)
	{
		MultiMediaObjectEnricher enricher = new MultiMediaObjectEnricher();
		try {
			enricher.enrich();
		}
		catch (Throwable t) {
			logger.error(t.getMessage());
			System.exit(1);
		}
		finally {
			ESUtil.refreshIndex(MULTI_MEDIA_OBJECT);
			ESClientManager.getInstance().closeClient();
		}
		System.exit(0);
	}

	private static final Logger logger = getLogger(MultiMediaObjectEnricher.class);
	private static final List<TaxonomicEnrichment> NONE = new ArrayList<>(0);
	private static final int READ_BATCH_SIZE = 500;
	private static final int WRITE_BATCH_SIZE = 500;

	public void enrich() throws BulkIndexException
	{
		long start = System.currentTimeMillis();
		logger.info("Starting taxonomic enrichment of MultiMediaObject documents");
		QuerySpec qs = new QuerySpec();
		qs.sortBy("identifications.scientificName.scientificNameGroup");
		DocumentIterator<MultiMediaObject> extractor;
		extractor = new DocumentIterator<>(MULTI_MEDIA_OBJECT, qs);
		extractor.setBatchSize(READ_BATCH_SIZE);
		BulkIndexer<MultiMediaObject> indexer = new BulkIndexer<>(MULTI_MEDIA_OBJECT);
		int processed = 0;
		int enriched = 0;
		List<MultiMediaObject> queue = new ArrayList<>((int) (WRITE_BATCH_SIZE * 1.5));
		logger.info("Loading first batch of multimedia documents");
		List<MultiMediaObject> batch = extractor.nextBatch();
		try {
			while (batch != null) {
				List<MultiMediaObject> enrichedMultimedia = enrichSpecimens(batch);
				if (logger.isDebugEnabled()) {
					logger.debug("Number of multimedia documents enriched in current batch: {}",
							enrichedMultimedia.size());
				}
				enriched += enrichedMultimedia.size();
				queue.addAll(enrichedMultimedia);
				if (queue.size() >= WRITE_BATCH_SIZE) {
					if (logger.isDebugEnabled()) {
						logger.debug("Re-indexing enriched {} multimedia documents", queue.size());
					}
					indexer.index(queue);
					refreshIndex(MULTI_MEDIA_OBJECT);
					queue.clear();
				}
				processed += batch.size();
				if (processed % 100000 == 0) {
					logger.info("Multimedia documents processed: {}", processed);
					logger.info("Multimedia documents enriched: {}", enriched);
					MultiMediaObject last = batch.get(batch.size() - 1);
					List<MultiMediaContentIdentification> sis = last.getIdentifications();
					if (sis != null) {
						String group = sis.get(0).getScientificName().getScientificNameGroup();
						logger.info("Most recent name group: {}", group);
					}
				}
				if (logger.isDebugEnabled()) {
					logger.debug("");
					logger.debug(">>>>>>>>>>>>>>> Loading next batch of mmos <<<<<<<<<<<<<<<");
				}
				batch = extractor.nextBatch();
			}
			if (!queue.isEmpty()) {
				if (logger.isDebugEnabled()) {
					logger.debug("Re-indexing enriched {} multimedia documents", queue.size());
				}
				indexer.index(queue);
			}
			refreshIndex(MULTI_MEDIA_OBJECT);
		}
		catch (Throwable t) {
			logger.error("****************************************************************");
			logger.error("Dumping contents of queue:\n" + JsonUtil.toPrettyJson(queue));
			logger.error("****************************************************************");
			logger.error("Error while enriching mmos", t);
			throw t;
		}
		finally {
			logger.info("Multimedia documents processed: {}", processed);
			logger.info("Multimedia documents enriched: {}", enriched);
			logDuration(logger, getClass(), start);
		}
	}

	private static List<MultiMediaObject> enrichSpecimens(List<MultiMediaObject> mmos)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Creating taxon lookup table");
		}
		HashMap<String, List<Taxon>> taxonLookupTable = createLookupTable(mmos);
		if (taxonLookupTable == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("No taxa found for current batch of mmos");
			}
			return Collections.emptyList();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Lookup table created. {} unique name group(s) found in Taxon index",
					taxonLookupTable.size());
		}
		HashMap<String, List<TaxonomicEnrichment>> cache = new HashMap<>(mmos.size());
		List<MultiMediaObject> result = new ArrayList<>(mmos.size());
		for (MultiMediaObject mmo : mmos) {
			boolean enriched = false;
			if (mmo.getIdentifications() == null) {
				continue;
			}
			for (MultiMediaContentIdentification si : mmo.getIdentifications()) {
				String nameGroup = si.getScientificName().getScientificNameGroup();
				List<TaxonomicEnrichment> enrichments = cache.get(nameGroup);
				if (enrichments == null) {
					List<Taxon> taxa = taxonLookupTable.get(nameGroup);
					if (taxa == null) {
						/*
						 * There are no taxon documents with this nameGroup
						 */
						cache.put(nameGroup, NONE);
					}
					else {
						enrichments = createEnrichments(taxa);
						cache.put(nameGroup, enrichments);
						if (enrichments != NONE) {
							si.setTaxonomicEnrichments(enrichments);
							enriched = true;
						}
						/*
						 * Else there were taxon documents with this nameGroup,
						 * but none of them had vernacular names and/or
						 * synonyms.
						 */
					}
				}
				else if (enrichments != NONE) {
					si.setTaxonomicEnrichments(enrichments);
					enriched = true;
				}
			}
			if (enriched) {
				result.add(mmo);
			}
		}
		return result;
	}

	private static List<TaxonomicEnrichment> createEnrichments(List<Taxon> taxa)
	{
		List<TaxonomicEnrichment> enrichments = new ArrayList<>(taxa.size());
		for (Taxon taxon : taxa) {
			if (taxon.getVernacularNames() == null && taxon.getSynonyms() == null) {
				continue;
			}
			TaxonomicEnrichment enrichment = new TaxonomicEnrichment();
			if (taxon.getVernacularNames() != null) {
				for (VernacularName vn : taxon.getVernacularNames()) {
					enrichment.addVernacularName(copySummaryVernacularName(vn));
				}
			}
			if (taxon.getSynonyms() != null) {
				for (ScientificName sn : taxon.getSynonyms()) {
					enrichment.addSynonym(copyScientificName(sn));
				}
			}
			enrichment.setSourceSystem(copySourceSystem(taxon.getSourceSystem()));
			enrichment.setTaxonId(taxon.getId());
			enrichments.add(enrichment);
		}
		return enrichments.isEmpty() ? NONE : enrichments;
	}

	private static HashMap<String, List<Taxon>> createLookupTable(List<MultiMediaObject> mmos)
	{
		HashSet<String> groups = new HashSet<>(mmos.size());
		for (MultiMediaObject mmo : mmos) {
			for (MultiMediaContentIdentification si : mmo.getIdentifications()) {
				groups.add(si.getScientificName().getScientificNameGroup());
			}
		}
		DocumentType<Taxon> dt = TAXON;
		SearchRequestBuilder request = newSearchRequest(dt);
		TermsQueryBuilder query = termsQuery("acceptedName.scientificNameGroup", groups);
		request.setQuery(constantScoreQuery(query));
		request.setSize(10000);
		SearchResponse response = executeSearchRequest(request);
		SearchHit[] hits = response.getHits().getHits();
		if (hits.length == 0) {
			return null;
		}
		if (response.getHits().getTotalHits() > 10000) {
			throw new ETLRuntimeException("Too many taxa found for current batch of multimedia");
		}
		HashMap<String, List<Taxon>> table = new HashMap<>(groups.size());
		ObjectMapper om = dt.getObjectMapper();
		for (SearchHit hit : hits) {
			Taxon taxon = om.convertValue(hit.getSource(), dt.getJavaType());
			taxon.setId(hit.getId());
			List<Taxon> taxa = table.get(taxon.getAcceptedName().getScientificNameGroup());
			if (taxa == null) {
				taxa = new ArrayList<>(2);
				table.put(taxon.getAcceptedName().getScientificNameGroup(), taxa);
			}
			taxa.add(taxon);
		}
		return table;
	}

	@SuppressWarnings("unused")
	private static List<Taxon> loadTaxaWithNameGroup(String nameGroup)
	{
		DocumentType<Taxon> dt = TAXON;
		SearchRequestBuilder request = newSearchRequest(dt);
		TermQueryBuilder query = termQuery("acceptedName.scientificNameGroup", nameGroup);
		request.setQuery(constantScoreQuery(query));
		int maxDocs = 10000;
		request.setSize(maxDocs);
		SearchResponse response = executeSearchRequest(request);
		SearchHit[] hits = response.getHits().getHits();
		if (hits.length == 0) {
			return null;
		}
		if (response.getHits().getTotalHits() > maxDocs) {
			/*
			 * That would be really interesting because ordinarily you would
			 * expect one or two (COL and/or NSR).
			 */
			throw new ETLRuntimeException("Too many taxa for name group " + nameGroup);
		}
		List<Taxon> result = new ArrayList<>(hits.length);
		ObjectMapper om = dt.getObjectMapper();
		for (SearchHit hit : hits) {
			Taxon taxon = om.convertValue(hit.getSource(), dt.getJavaType());
			taxon.setId(hit.getId());
			result.add(taxon);
		}
		return result;
	}

}
