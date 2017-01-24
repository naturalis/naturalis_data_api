package nl.naturalis.nba.etl.enrich01;

import static nl.naturalis.nba.dao.DocumentType.TAXON;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;

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

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.TaxonomicEnrichment;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.util.es.DocumentIterator;
import nl.naturalis.nba.dao.util.es.ESUtil;

public class TaxonomicEnricher {

	public static void main(String[] args) throws Exception
	{

	}

	@SuppressWarnings("unused")
	private static final Logger logger = getLogger(TaxonomicEnricher.class);

	public TaxonomicEnricher()
	{
	}

	public void addTaxonomicEnrichments()
	{
		long start = System.currentTimeMillis();
		DocumentIterator<Specimen> extractor = new DocumentIterator<>(DocumentType.SPECIMEN);
		List<Specimen> batch = extractor.nextBatch();
		while (batch != null) {
			Set<String> names = extractNames(batch);
			HashMap<String, Taxon> taxonCache = cacheTaxa(names);
			HashMap<String, TaxonomicEnrichment> enrichments = new HashMap<>(4);
			for (Specimen specimen : batch) {
				for (SpecimenIdentification si : specimen.getIdentifications()) {
					String fsn = si.getScientificName().getFullScientificName();
					names.add(fsn);
				}
			}
		}
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
		SearchRequestBuilder request = ESUtil.newSearchRequest(dt);
		TermsQueryBuilder query = QueryBuilders.termsQuery("acceptedName.fullScientificName",
				names);
		request.setQuery(query);
		SearchResponse response = ESUtil.executeSearchRequest(request);
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
