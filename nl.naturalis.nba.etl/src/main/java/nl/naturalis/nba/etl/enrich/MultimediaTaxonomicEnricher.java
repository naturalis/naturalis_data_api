package nl.naturalis.nba.etl.enrich;

import static nl.naturalis.nba.dao.DocumentType.MULTI_MEDIA_OBJECT;
import static nl.naturalis.nba.dao.DocumentType.TAXON;
import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;
import static nl.naturalis.nba.dao.util.es.ESUtil.newSearchRequest;
import static nl.naturalis.nba.dao.util.es.ESUtil.refreshIndex;
import static nl.naturalis.nba.etl.ETLConstants.SYS_PROP_ENRICH_READ_BATCH_SIZE;
import static nl.naturalis.nba.etl.ETLConstants.SYS_PROP_ENRICH_SCROLL_TIMEOUT;
import static nl.naturalis.nba.etl.ETLConstants.SYS_PROP_ENRICH_WRITE_BATCH_SIZE;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;
import static nl.naturalis.nba.etl.SummaryObjectUtil.copyScientificName;
import static nl.naturalis.nba.etl.SummaryObjectUtil.copySourceSystem;
import static nl.naturalis.nba.etl.SummaryObjectUtil.copySummaryVernacularName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.lucene.search.TotalHits;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.ConstantScoreQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

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
import nl.naturalis.nba.dao.exception.DaoException;
import nl.naturalis.nba.dao.util.es.AcidDocumentIterator;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.BulkIndexException;
import nl.naturalis.nba.etl.BulkIndexer;
import nl.naturalis.nba.etl.ETLRuntimeException;

public class MultimediaTaxonomicEnricher {

	public static void main(String[] args)
	{
		MultimediaTaxonomicEnricher enricher = new MultimediaTaxonomicEnricher();
		try {
			enricher.configureWithSystemProperties();
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

	private static final Logger logger = getLogger(MultimediaTaxonomicEnricher.class);
	private static final List<TaxonomicEnrichment> NONE = new ArrayList<>(0);

	private int readBatchSize = 500;
	private int writeBatchSize = 500;
	private int scrollTimeout = 60000;

	public void enrich() throws BulkIndexException
	{
		long start = System.currentTimeMillis();
		logger.info("Starting taxonomic enrichment of MultiMediaObject documents");
		QuerySpec qs = new QuerySpec();
		qs.sortBy("identifications.scientificName.scientificNameGroup");
		AcidDocumentIterator<MultiMediaObject> extractor;
		extractor = new AcidDocumentIterator<>(MULTI_MEDIA_OBJECT, qs);
		extractor.setTimeout(scrollTimeout);
		extractor.setBatchSize(readBatchSize);
		BulkIndexer<MultiMediaObject> indexer = new BulkIndexer<>(MULTI_MEDIA_OBJECT);
		int processed = 0;
		int enriched = 0;
		List<MultiMediaObject> queue = new ArrayList<>((int) (writeBatchSize * 1.5));
		logger.info("Loading first batch of multimedia documents");
		List<MultiMediaObject> batch = extractor.nextBatch();
		try {
			while (batch != null) {
				List<MultiMediaObject> enrichedMultimedia = enrichMultiMedia(batch);
				if (logger.isDebugEnabled()) {
					logger.debug("Number of multimedia documents enriched in current batch: {}",
							enrichedMultimedia.size());
				}
				enriched += enrichedMultimedia.size();
				queue.addAll(enrichedMultimedia);
				if (queue.size() >= writeBatchSize) {
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

	private static List<MultiMediaObject> enrichMultiMedia(List<MultiMediaObject> mmos)
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
		
    SearchRequest searchRequest = newSearchRequest(dt);    
    TermQueryBuilder termQuery = QueryBuilders.termQuery("acceptedName.scientificNameGroup", groups);
    ConstantScoreQueryBuilder constantScoreQuery = QueryBuilders.constantScoreQuery(termQuery); 

    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.query(constantScoreQuery);
    searchSourceBuilder.from(0);
    searchSourceBuilder.size(10000);

    searchRequest.source(searchSourceBuilder);
    SearchResponse searchResponse;
    try {
      searchResponse = ESClientManager.getInstance().getClient().search(searchRequest, RequestOptions.DEFAULT);
      SearchHits hits = searchResponse.getHits();
      TotalHits totalHits = hits.getTotalHits();
      long numHits = totalHits.value;
      if (numHits == 0) {
        return null;
      }
      if (numHits > 10000) {
        throw new ETLRuntimeException("Too many taxa found for current batch of multimedia");
      }
      
      HashMap<String, List<Taxon>> table = new HashMap<>(groups.size());
      ObjectMapper om = dt.getObjectMapper();
      for (SearchHit hit : hits) {
        Taxon taxon = om.convertValue(hit.getSourceAsString(), dt.getJavaType());
        taxon.setId(hit.getId());
        List<Taxon> taxa = table.get(taxon.getAcceptedName().getScientificNameGroup());
        if (taxa == null) {
          taxa = new ArrayList<>(2);
          table.put(taxon.getAcceptedName().getScientificNameGroup(), taxa);
        }
        taxa.add(taxon);
      
      }
    return table;
    } catch (IOException e) {
      throw new DaoException(String.format("Failed to query the taxon index: %s", e.getMessage())); 
    }
	}

	@SuppressWarnings("unused")
	private static List<Taxon> loadTaxaWithNameGroup(String nameGroup)
	{
		DocumentType<Taxon> dt = TAXON;
    SearchRequest searchRequest = newSearchRequest(dt);
    TermQueryBuilder termQuery = new TermQueryBuilder("acceptedName.scientificNameGroup", nameGroup);
    ConstantScoreQueryBuilder constantScoreQuery = new ConstantScoreQueryBuilder(termQuery);

    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    int maxDocs = 10000;
    sourceBuilder.from(0); 
    sourceBuilder.size(maxDocs);    
    sourceBuilder.query(constantScoreQuery);

    searchRequest.source(sourceBuilder);
    SearchResponse response = executeSearchRequest(searchRequest);
    SearchHit[] hits = response.getHits().getHits();
    if (hits.length == 0) {
      return null;
    }
    if (response.getHits().getTotalHits().value > maxDocs) {
      /*
       * That would be really interesting because ordinarily you would
       * expect one or two (COL and/or NSR).
       */
      throw new ETLRuntimeException("Too many taxa for name group " + nameGroup);
    }
    List<Taxon> result = new ArrayList<>(hits.length);
    ObjectMapper om = dt.getObjectMapper();
    for (SearchHit hit : hits) {
      Taxon taxon = om.convertValue(hit.getSourceAsString(), dt.getJavaType());
      taxon.setId(hit.getId());
      result.add(taxon);
    }
    return result;
	}

}
