package nl.naturalis.nba.etl.enrich;

import static nl.naturalis.nba.etl.ETLUtil.getLogger;
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

import org.apache.logging.log4j.Logger;

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
import nl.naturalis.nba.dao.TaxonDao;
import nl.naturalis.nba.etl.ETLRuntimeException;
import nl.naturalis.nba.utils.FileUtil;

class EnrichmentUtil {

	private static final Logger logger = getLogger(EnrichmentUtil.class);

	static final List<TaxonomicEnrichment> NOT_ENRICHABLE = new ArrayList<>(0);

	static Map<String, List<Taxon>> extractTaxaFromSpecimens(List<Specimen> specimens)
	{
		String[] names = extractNamesFromSpecimens(specimens);
		if (logger.isDebugEnabled()) {
			String fmt = "{} unique scientific names extracted from {} specimens";
			logger.debug(fmt, names.length, specimens.size());
		}
		return createTaxonLookupTable(names);
	}

	static Map<String, List<Taxon>> extractTaxaFromMultiMedia(List<MultiMediaObject> multimedia)
	{
		String[] names = extractNamesFromMultiMedia(multimedia);
		if (logger.isDebugEnabled()) {
			String fmt = "{} unique scientific names extracted from {} multimedia";
			logger.debug(fmt, names.length, multimedia.size());
		}
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
		return enrichments;
	}

	static File createTempFile(String prefix) throws IOException
	{
		File tmpDir = DaoRegistry.getInstance().getFile("../tmp").getCanonicalFile();
		if (!tmpDir.isDirectory()) {
			tmpDir.mkdir();
		}
		StringBuilder name = new StringBuilder(100);
		name.append(prefix);
		name.append(".");
		name.append(System.currentTimeMillis());
		name.append(".");
		name.append(System.identityHashCode(new Object()));
		name.append(".json");
		return FileUtil.newFile(tmpDir, name.toString());
	}

	private static HashMap<String, List<Taxon>> createTaxonLookupTable(String[] names)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Creating taxon lookup table");
		}
		HashMap<String, List<Taxon>> table = new HashMap<>(names.length);
		for (int from = 0; from < names.length; from += 1024) {
			int len = Math.min(1024, names.length - from);
			String[] chunk = new String[len];
			System.arraycopy(names, from, chunk, 0, len);
			if (logger.isDebugEnabled()) {
				logger.debug("Loading {} taxa for insertion into lookup table", chunk.length);
			}
			QueryResult<Taxon> taxa = loadTaxa(chunk);
			addTaxaToLookupTable(taxa, table);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Lookup table created ({} entries)", table.size());
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
		/*
		 * We can't have more than 2 taxa per name (NSR and/or COL), so this
		 * should be more than enough
		 */
		query.setSize(10000);
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
			String name = taxon.getAcceptedName().getScientificNameGroup();
			List<Taxon> otherTaxaWithName = table.get(name);
			if (otherTaxaWithName == null) {
				otherTaxaWithName = new ArrayList<>(2);
				table.put(name, otherTaxaWithName);
			}
			otherTaxaWithName.add(taxon);
		}
	}

}