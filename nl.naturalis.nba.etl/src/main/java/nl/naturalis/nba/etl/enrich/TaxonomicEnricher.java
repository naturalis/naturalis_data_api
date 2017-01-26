package nl.naturalis.nba.etl.enrich;

import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;
import static nl.naturalis.nba.dao.DocumentType.TAXON;
import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;
import static nl.naturalis.nba.dao.util.es.ESUtil.newSearchRequest;
import static nl.naturalis.nba.dao.util.es.ESUtil.refreshIndex;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.TaxonomicEnrichment;
import nl.naturalis.nba.api.model.VernacularName;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.DocumentIterator;
import nl.naturalis.nba.etl.BulkIndexException;
import nl.naturalis.nba.etl.BulkUpdater;

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
			ESClientManager.getInstance().closeClient();
		}
		System.exit(0);
	}

	private static final Logger logger = getLogger(TaxonomicEnricher.class);
	private static final int BATCH_SIZE = 1000;
	private static final int FLUSH_TRESHOLD = 1000;

	public void enrich() throws BulkIndexException
	{
		logger.info("Starting taxonomic enrichment of Specimen documents");
		long start = System.currentTimeMillis();
		DocumentIterator<Specimen> extractor = new DocumentIterator<>(SPECIMEN);
		extractor.setBatchSize(BATCH_SIZE);
		BulkUpdater<Specimen> updater = new BulkUpdater<>(SPECIMEN);
		int processed = 0;
		int updated = 0;
		List<Specimen> queue = new ArrayList<>(FLUSH_TRESHOLD + BATCH_SIZE);
		List<Specimen> batch = extractor.nextBatch();
		while (batch != null) {
			List<Specimen> enrichedSpecimens = enrichBatch(batch);
			updated += enrichedSpecimens.size();
			queue.addAll(enrichedSpecimens);
			if (queue.size() >= FLUSH_TRESHOLD) {
				updater.update(enrichedSpecimens);
				queue.clear();
			}
			processed += batch.size();
			if (processed % 100000 == 0) {
				logger.info("Specimen documents processed: {}", processed);
				logger.info("Specimen documents updated: {}", updated);
			}
			batch = extractor.nextBatch();
		}
		refreshIndex(SPECIMEN);
		logger.info("Specimen documents processed: {}", processed);
		logger.info("Specimen documents updated: {}", updated);
		logDuration(logger, getClass(), start);
	}

	private static List<Specimen> enrichBatch(List<Specimen> specimens)
	{
		Set<String> names = extractNames(specimens);
		HashMap<String, Taxon> taxonCache = cacheTaxa(names);
		List<Specimen> enriched = new ArrayList<>(specimens.size());
		for (Specimen specimen : specimens) {
			if (specimen.getIdentifications() == null) {
				continue;
			}
			HashMap<String, TaxonomicEnrichment> enrichments = new HashMap<>(8);
			for (int i = 0; i < specimen.getIdentifications().size(); i++) {
				SpecimenIdentification si = specimen.getIdentifications().get(i);
				String fsn = si.getScientificName().getFullScientificName();
				Taxon taxon = taxonCache.get(fsn);
				if (taxon == null) {
					continue;
				}
				TaxonomicEnrichment enrichment = enrichments.get(fsn);
				if (enrichment == null) {
					enrichment = new TaxonomicEnrichment();
					if (copyTaxonAttrs(taxon, enrichment, i)) {
						enrichments.put(fsn, enrichment);
					}
				}
				else {
					copyTaxonAttrs(taxon, enrichment, i);
				}
			}
			if (enrichments.size() != 0) {
				specimen.setTaxonomicEnrichments(new ArrayList<>(enrichments.values()));
				enriched.add(specimen);
			}
		}
		return enriched;
	}

	private static boolean copyTaxonAttrs(Taxon taxon, TaxonomicEnrichment enrichment,
			int identification)
	{
		boolean enriched = false;
		if (taxon.getVernacularNames() != null) {
			ArrayList<String> vernaculars = new ArrayList<>(taxon.getVernacularNames().size());
			for (VernacularName vn : taxon.getVernacularNames()) {
				vernaculars.add(vn.getName());
				enrichment.addVernacularNames(vernaculars);
			}
			enriched = true;
		}
		if (taxon.getSynonyms() != null) {
			ArrayList<String> synonyms = new ArrayList<>(taxon.getSynonyms().size());
			for (ScientificName sn : taxon.getSynonyms()) {
				synonyms.add(sn.getFullScientificName());
				enrichment.addSynonyms(synonyms);
			}
			enriched = true;
		}
		if (enriched) {
			if (enrichment.getIdentifications() == null) {
				enrichment.setIdentifications(new ArrayList<Integer>(4));
			}
			enrichment.getIdentifications().add(identification);
			enrichment.setTaxonId(taxon.getId());
			enrichment.setTaxonSourceSystem(taxon.getSourceSystem().getName());
			return true;
		}
		return false;
	}

	private static HashMap<String, Taxon> cacheTaxa(Set<String> names)
	{
		List<Taxon> taxa = loadTaxa(names);
		HashMap<String, Taxon> result = new HashMap<>(taxa.size() + 8, 1F);
		for (Taxon taxon : taxa) {
			result.put(taxon.getAcceptedName().getFullScientificName(), taxon);
		}
		return result;
	}

	private static List<Taxon> loadTaxa(Collection<String> names)
	{
		DocumentType<Taxon> dt = TAXON;
		SearchRequestBuilder request = newSearchRequest(dt);
		String field = "acceptedName.fullScientificName";
		TermsQueryBuilder query = QueryBuilders.termsQuery(field, names);
		request.setQuery(query);
		SearchResponse response = executeSearchRequest(request);
		SearchHit[] hits = response.getHits().getHits();
		if (hits.length == 0) {
			return Collections.emptyList();
		}
		List<Taxon> result = new ArrayList<>(hits.length);
		ObjectMapper om = dt.getObjectMapper();
		for (SearchHit hit : hits) {
			Taxon sns = om.convertValue(hit.getSource(), dt.getJavaType());
			result.add(sns);
		}
		return result;
	}

	private static Set<String> extractNames(List<Specimen> specimens)
	{
		Set<String> names = new HashSet<>(specimens.size() * 3);
		for (Specimen specimen : specimens) {
			for (SpecimenIdentification si : specimen.getIdentifications()) {
				names.add(si.getScientificName().getFullScientificName());
			}
		}
		return names;
	}

}
