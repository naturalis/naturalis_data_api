package nl.naturalis.nba.etl.enrich;

import static nl.naturalis.nba.etl.SummaryObjectUtil.copyScientificName;
import static nl.naturalis.nba.etl.SummaryObjectUtil.copySourceSystem;
import static nl.naturalis.nba.etl.SummaryObjectUtil.copySummaryVernacularName;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QueryResultItem;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.MultiMediaContentIdentification;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.TaxonomicEnrichment;
import nl.naturalis.nba.api.model.VernacularName;
import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.TaxonDao;
import nl.naturalis.nba.etl.ETLRuntimeException;

class EnrichmentUtil {

	static final List<TaxonomicEnrichment> NOT_ENRICHABLE = new ArrayList<>(0);

	static Map<String, List<Taxon>> extractTaxaFromSpecimens(List<Specimen> specimens)
	{
		String[] names = extractNamesFromSpecimens(specimens);
		return createTaxonLookupTable(names);
	}

	static Map<String, List<Taxon>> extractTaxaFromMultiMedia(List<MultiMediaObject> multimedia)
	{
		String[] names = extractNamesFromMultiMedia(multimedia);
		return createTaxonLookupTable(names);
	}

	static List<TaxonomicEnrichment> createEnrichments(List<Taxon> taxa)
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
		return enrichments.isEmpty() ? NOT_ENRICHABLE : enrichments;
	}

	static File createTempFile(DocumentType<?> dt) throws IOException
	{
		File tmpDir = DaoRegistry.getInstance().getFile("../tmp").getCanonicalFile();
		if (!tmpDir.isDirectory()) {
			tmpDir.mkdir();
		}
		StringBuilder name = new StringBuilder(100);
		name.append(dt.getName().toLowerCase());
		name.append(".enrich.");
		name.append(System.currentTimeMillis());
		name.append(".");
		name.append(System.identityHashCode(new Object()));
		name.append(".json");
		return null;
	}

	private static HashMap<String, List<Taxon>> createTaxonLookupTable(String[] names)
	{
		HashMap<String, List<Taxon>> table = new HashMap<>(names.length);
		int from = 0;
		while (from < names.length) {
			int len = Math.min(1024, names.length - from);
			String[] chunk = new String[len];
			System.arraycopy(names, from, chunk, 0, len);
			QueryResult<Taxon> taxa = loadTaxa(names);
			addTaxaToLookupTable(taxa, table);
			from += 1024;
		}
		return table;
	}

	private static String[] extractNamesFromSpecimens(List<Specimen> specimens)
	{
		// Assume 3 identification per specimen
		HashSet<String> names = new HashSet<>(specimens.size() * 3);
		for (Specimen s : specimens) {
			for (SpecimenIdentification identification : s.getIdentifications()) {
				names.add(identification.getScientificName().getScientificNameGroup());
			}
		}
		return names.toArray(new String[names.size()]);
	}

	private static String[] extractNamesFromMultiMedia(List<MultiMediaObject> multimedia)
	{
		HashSet<String> names = new HashSet<>(multimedia.size() * 3);
		for (MultiMediaObject m : multimedia) {
			for (MultiMediaContentIdentification identification : m.getIdentifications()) {
				names.add(identification.getScientificName().getScientificNameGroup());
			}
		}
		return names.toArray(new String[names.size()]);
	}

	private static QueryResult<Taxon> loadTaxa(String[] names)
	{
		String field = "acceptedName.scientificNameGroup";
		QueryCondition condition = new QueryCondition(field, "IN", names);
		QuerySpec query = new QuerySpec();
		query.addCondition(condition);
		query.setConstantScore(true);
		query.setSize(1024);
		TaxonDao taxonDao = new TaxonDao();
		QueryResult<Taxon> result;
		try {
			result = taxonDao.query(query);
		}
		catch (InvalidQueryException e) {
			throw new ETLRuntimeException(e);
		}
		return result;
	}

	private static void addTaxaToLookupTable(QueryResult<Taxon> taxa,
			HashMap<String, List<Taxon>> table)
	{
		for (QueryResultItem<Taxon> item : taxa) {
			Taxon taxon = item.getItem();
			String sng = taxon.getAcceptedName().getScientificNameGroup();
			List<Taxon> stored = table.get(sng);
			if (stored == null) {
				stored = new ArrayList<>(2);
				table.put(sng, stored);
			}
			stored.add(taxon);
		}
	}

}
