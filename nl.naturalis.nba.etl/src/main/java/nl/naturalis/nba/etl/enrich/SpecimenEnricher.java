package nl.naturalis.nba.etl.enrich;

import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;
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
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.TaxonomicEnrichment;
import nl.naturalis.nba.api.model.TaxonomicIdentification;
import nl.naturalis.nba.api.model.VernacularName;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.DocumentIterator;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.BulkIndexException;
import nl.naturalis.nba.etl.BulkIndexer;
import nl.naturalis.nba.etl.ETLRuntimeException;

import static nl.naturalis.nba.etl.ETLConstants.*;

public class SpecimenEnricher {

	public static void main(String[] args)
	{
		SpecimenEnricher enricher = new SpecimenEnricher();
		try {
			enricher.configureWithSystemProperties();
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

	private static final Logger logger = getLogger(SpecimenEnricher.class);
	private static final List<TaxonomicEnrichment> NONE = new ArrayList<>(0);

	private int readBatchSize = 500;
	private int writeBatchSize = 500;
	private int scrollTimeout = 60000;

	public void enrich() throws BulkIndexException
	{
		long start = System.currentTimeMillis();
		logger.info("Starting taxonomic enrichment of Specimen documents");
		QuerySpec qs = new QuerySpec();
		qs.sortBy("identifications.scientificName.scientificNameGroup");
		DocumentIterator<Specimen> extractor = new DocumentIterator<>(SPECIMEN, qs);
		extractor.setBatchSize(readBatchSize);
		extractor.setTimeout(scrollTimeout);
		BulkIndexer<Specimen> indexer = new BulkIndexer<>(SPECIMEN);
		int processed = 0;
		int enriched = 0;
		List<Specimen> queue = new ArrayList<>((int) (writeBatchSize * 1.5));
		logger.info("Loading first batch of specimens");
		List<Specimen> batch = extractor.nextBatch();
		try {
			while (batch != null) {
				List<Specimen> enrichedSpecimens = enrichSpecimens(batch);
				if (logger.isDebugEnabled()) {
					logger.debug("Number of specimens enriched in current batch: {}",
							enrichedSpecimens.size());
				}
				enriched += enrichedSpecimens.size();
				queue.addAll(enrichedSpecimens);
				if (queue.size() >= writeBatchSize) {
					if (logger.isDebugEnabled()) {
						logger.debug("Re-indexing enriched {} specimens", queue.size());
					}
					indexer.index(queue);
					refreshIndex(SPECIMEN);
					queue.clear();
				}
				processed += batch.size();
				if (processed % 100000 == 0) {
					logger.info("Specimen documents processed: {}", processed);
					logger.info("Specimen documents enriched: {}", enriched);
					Specimen last = batch.get(batch.size() - 1);
					List<SpecimenIdentification> sis = last.getIdentifications();
					if (sis != null) {
						String group = sis.get(0).getScientificName().getScientificNameGroup();
						logger.info("Most recent name group: {}", group);
					}
				}
				if (logger.isDebugEnabled()) {
					logger.debug("");
					logger.debug(">>>>>>>>>>>>>>> Loading next batch of specimens <<<<<<<<<<<<<<<");
				}
				batch = extractor.nextBatch();
			}
			if (!queue.isEmpty()) {
				if (logger.isDebugEnabled()) {
					logger.debug("Re-indexing enriched {} specimens", queue.size());
				}
				indexer.index(queue);
			}
			refreshIndex(SPECIMEN);
		}
		catch (Throwable t) {
			logger.error("****************************************************************");
			logger.error("Dumping contents of queue:\n" + JsonUtil.toPrettyJson(queue));
			logger.error("****************************************************************");
			logger.error("Error while enriching specimens", t);
			throw t;
		}
		finally {
			logger.info("Specimen documents processed: {}", processed);
			logger.info("Specimen documents enriched: {}", enriched);
			logDuration(logger, getClass(), start);
		}
	}

	public void configureWithSystemProperties()
	{
		String prop = System.getProperty(SYS_PROP_ENRICH_READ_BATCH_SIZE, "500");
		try {
			setReadBatchSize(Integer.parseInt(prop));
		}
		catch (NumberFormatException e) {
			throw new ETLRuntimeException("Invalid read batch size: " + prop);
		}
		prop = System.getProperty(SYS_PROP_ENRICH_WRITE_BATCH_SIZE, "500");
		try {
			setWriteBatchSize(Integer.parseInt(prop));
		}
		catch (NumberFormatException e) {
			throw new ETLRuntimeException("Invalid write batch size: " + prop);
		}
		prop = System.getProperty(SYS_PROP_ENRICH_SCROLL_TIMEOUT, "10000");
		try {
			setScrollTimeout(Integer.parseInt(prop));
		}
		catch (NumberFormatException e) {
			throw new ETLRuntimeException("Invalid scroll timeout: " + prop);
		}
	}

	public int getReadBatchSize()
	{
		return readBatchSize;
	}

	public void setReadBatchSize(int readBatchSize)
	{
		this.readBatchSize = readBatchSize;
	}

	public int getWriteBatchSize()
	{
		return writeBatchSize;
	}

	public void setWriteBatchSize(int writeBatchSize)
	{
		this.writeBatchSize = writeBatchSize;
	}

	public int getScrollTimeout()
	{
		return scrollTimeout;
	}

	public void setScrollTimeout(int scrollTimeout)
	{
		this.scrollTimeout = scrollTimeout;
	}

	private static List<Specimen> enrichSpecimens(List<Specimen> specimens)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Creating taxon lookup table");
		}
		HashMap<String, List<Taxon>> taxonLookupTable = createLookupTable(specimens);
		if (taxonLookupTable == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("No taxa found for current batch of specimens");
			}
			return Collections.emptyList();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Lookup table created. {} unique name group(s) found in Taxon index",
					taxonLookupTable.size());
		}
		HashMap<String, List<TaxonomicEnrichment>> cache = new HashMap<>(specimens.size());
		List<Specimen> result = new ArrayList<>(specimens.size());
		for (Specimen specimen : specimens) {
			boolean enriched = false;
			if (specimen.getIdentifications() == null) {
				continue;
			}
			for (TaxonomicIdentification si : specimen.getIdentifications()) {
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

	private static HashMap<String, List<Taxon>> createLookupTable(List<Specimen> specimens)
	{
		HashSet<String> groups = new HashSet<>(specimens.size());
		for (Specimen specimen : specimens) {
			for (TaxonomicIdentification si : specimen.getIdentifications()) {
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
			throw new ETLRuntimeException("Too many taxa found for current batch of specimens");
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
