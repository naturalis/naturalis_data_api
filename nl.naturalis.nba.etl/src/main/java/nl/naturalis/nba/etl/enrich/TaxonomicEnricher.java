package nl.naturalis.nba.etl.enrich;

import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;
import static nl.naturalis.nba.dao.DocumentType.TAXON;
import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;
import static nl.naturalis.nba.dao.util.es.ESUtil.newSearchRequest;
import static nl.naturalis.nba.dao.util.es.ESUtil.refreshIndex;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;
import static org.elasticsearch.index.query.QueryBuilders.constantScoreQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.TaxonomicEnrichment;
import nl.naturalis.nba.api.model.VernacularName;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.DocumentIterator;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.BulkIndexException;
import nl.naturalis.nba.etl.BulkIndexer;

public class TaxonomicEnricher {

	public static void main(String[] args)
	{
		TaxonomicEnricher enricher = new TaxonomicEnricher();
		try {
			enricher.enrich();
		}
		catch (Throwable t) {
			logger.error(t.getMessage());
			System.exit(1);
		}
		finally {
			ESUtil.refreshIndex(SPECIMEN);
			ESClientManager.getInstance().closeClient();
		}
		System.exit(0);
	}

	private static final Logger logger = getLogger(TaxonomicEnricher.class);
	private static final List<TaxonomicEnrichment> EMPTY = new ArrayList<>(0);
	private static final int BATCH_SIZE = 1000;
	private static final int FLUSH_TRESHOLD = 1000;

	public void enrich() throws BulkIndexException
	{
		logger.info("Starting taxonomic enrichment of Specimen documents");
		long start = System.currentTimeMillis();
		QuerySpec qs = new QuerySpec();
		qs.sortBy("identifications.scientificNameGroup");
		DocumentIterator<Specimen> extractor = new DocumentIterator<>(SPECIMEN, qs);
		extractor.setBatchSize(BATCH_SIZE);
		BulkIndexer<Specimen> indexer = new BulkIndexer<>(SPECIMEN);
		int processed = 0;
		int enriched = 0;
		List<Specimen> queue = new ArrayList<>(FLUSH_TRESHOLD + BATCH_SIZE);
		List<Specimen> batch = extractor.nextBatch();
		while (batch != null) {
			List<Specimen> enrichedSpecimens = enrichBatch(batch);
			enriched += enrichedSpecimens.size();
			queue.addAll(enrichedSpecimens);
			if (queue.size() >= FLUSH_TRESHOLD) {
				indexer.index(queue);
				queue.clear();
			}
			processed += batch.size();
			if (processed % 100000 == 0) {
				logger.info("Specimen documents processed: {}", processed);
				logger.info("Specimen documents enriched: {}", enriched);
				logger.info("Most recent name group: {}", batch.get(batch.size() - 1)
						.getIdentifications().get(0).getScientificNameGroup());
			}
			batch = extractor.nextBatch();
		}
		if (!queue.isEmpty()) {
			indexer.index(queue);
		}
		refreshIndex(SPECIMEN);
		logger.info("Specimen documents processed: {}", processed);
		logger.info("Specimen documents enriched: {}", enriched);
		logDuration(logger, getClass(), start);
	}

	private static List<Specimen> enrichBatch(List<Specimen> specimens)
	{
		List<Specimen> result = new ArrayList<>(specimens.size());
		HashMap<String, List<TaxonomicEnrichment>> cache;
		cache = new HashMap<>((int) (specimens.size() / .75F) + 1);
		for (Specimen specimen : specimens) {
			boolean enriched = false;
			if (specimen.getIdentifications() == null) {
				continue;
			}
			for (SpecimenIdentification si : specimen.getIdentifications()) {
				if (si.getTaxonomicEnrichments() != null) {
					/*
					 * This allows for incremental enrichment, in case the
					 * program aborted, or if we re-imported Brahms or CRS.
					 */
					continue;
				}
				String nameGroup = si.getScientificNameGroup();
				List<TaxonomicEnrichment> enrichments = cache.get(nameGroup);
				if (enrichments == null) {
					List<Taxon> taxa = getTaxa(nameGroup);
					if (taxa == null) {
						cache.put(nameGroup, EMPTY);
					}
					else {
						enrichments = createEnrichments(taxa);
						cache.put(nameGroup, enrichments);
						if (enrichments != EMPTY) {
							si.setTaxonomicEnrichments(enrichments);
							enriched = true;
						}
					}
				}
			}
			if (enriched) {
				result.add(specimen);
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
					enrichment.addVernacularName(vn.getName());
				}
			}
			if (taxon.getSynonyms() != null) {
				for (ScientificName sn : taxon.getSynonyms()) {
					enrichment.addSynonym(sn.getFullScientificName());
				}
			}
			enrichments.add(enrichment);
		}
		return enrichments.isEmpty() ? EMPTY : enrichments;
	}

	private static List<Taxon> getTaxa(String nameGroup)
	{
		DocumentType<Taxon> dt = TAXON;
		SearchRequestBuilder request = newSearchRequest(dt);
		TermQueryBuilder query = termQuery("scientificNameGroup", nameGroup);
		request.setQuery(constantScoreQuery(query));
		SearchResponse response = executeSearchRequest(request);
		SearchHit[] hits = response.getHits().getHits();
		if (hits.length == 0) {
			return null;
		}
		List<Taxon> result = new ArrayList<>(hits.length);
		ObjectMapper om = dt.getObjectMapper();
		for (SearchHit hit : hits) {
			Taxon sns = om.convertValue(hit.getSource(), dt.getJavaType());
			result.add(sns);
		}
		return result;
	}

}
