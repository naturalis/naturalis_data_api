package nl.naturalis.nda.elasticsearch.load.col;

import static nl.naturalis.nda.elasticsearch.load.CSVImportUtil.val;
import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_TAXON;
import static nl.naturalis.nda.elasticsearch.load.col.CoLTaxonCsvField.acceptedNameUsageID;
import static nl.naturalis.nda.elasticsearch.load.col.CoLTaxonCsvField.scientificName;
import static org.domainobject.util.StringUtil.lpad;
import static org.domainobject.util.StringUtil.rpad;

import java.util.Arrays;
import java.util.List;

import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.load.CSVRecordInfo;
import nl.naturalis.nda.elasticsearch.load.CSVTransformer;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;
import nl.naturalis.nda.elasticsearch.load.LoadConstants;
import nl.naturalis.nda.elasticsearch.load.Registry;
import static nl.naturalis.nda.elasticsearch.load.col.CoLImportUtil.*;

import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;

/**
 * A subclass of {@link CSVTransformer} that transforms CSV records into
 * {@link ESTaxon} objects.
 * 
 * @author Ayco Holleman
 *
 */
class CoLSynonymTransformer implements CSVTransformer<ESTaxon> {

	static Logger logger = Registry.getInstance().getLogger(CoLSynonymTransformer.class);

	private final IndexNative index;
	private final ETLStatistics stats;

	private String objectID;
	private int lineNo;
	private boolean suppressErrors;
	private CoLTaxonLoader loader;

	CoLSynonymTransformer(ETLStatistics stats)
	{
		this.index = Registry.getInstance().getNbaIndexManager();
		this.stats = stats;
	}

	void setSuppressErrors(boolean suppressErrors)
	{
		this.suppressErrors = suppressErrors;
	}

	void setLoader(CoLTaxonLoader loader)
	{
		this.loader = loader;
	}

	@Override
	public List<ESTaxon> transform(CSVRecordInfo info)
	{

		stats.recordsProcessed++;
		CSVRecord record = info.getRecord();
		lineNo = info.getLineNumber();

		String objectID = val(record, acceptedNameUsageID);

		if (objectID == null) {
			// This is an accepted name
			stats.recordsSkipped++;
			return null;
		}

		stats.recordsAccepted++;
		stats.objectsProcessed++;

		try {

			String elasticID = LoadConstants.ES_ID_PREFIX_COL + objectID;
			String synonym = val(record, scientificName);

			ESTaxon taxon = loader.findInQueue(elasticID);
			if (taxon != null) {
				/*
				 * Taxon apparently already queued for indexing because it was
				 * enriched before (from a previous CSV record). Return null,
				 * because we don't want to queue it again; just add the current
				 * synonym to the list of synonyms.
				 */
				if (!taxon.getSynonyms().contains(synonym))
					taxon.addSynonym(getScientificName(record));
				return null;
			}
			taxon = index.get(LUCENE_TYPE_TAXON, elasticID, ESTaxon.class);
			if (taxon != null) {
				if (taxon.getSynonyms() == null || !taxon.getSynonyms().contains(synonym)) {
					taxon.addSynonym(getScientificName(record));
					return Arrays.asList(taxon);
				}
				if (!suppressErrors)
					warn("Synonym already exists: " + synonym);
			}
			else if (!suppressErrors)
				warn("Orphan synonym: " + synonym);
			stats.objectsRejected++;
			return null;
		}
		catch (Throwable t) {
			stats.objectsRejected++;
			if (!suppressErrors) {
				error(t.getMessage());
			}
			return null;
		}
	}

	private void error(String pattern, Object... args)
	{
		String msg = messagePrefix() + String.format(pattern, args);
		logger.error(msg);
	}

	private void warn(String pattern, Object... args)
	{
		String msg = messagePrefix() + String.format(pattern, args);
		logger.warn(msg);
	}

	@SuppressWarnings("unused")
	private void info(String pattern, Object... args)
	{
		String msg = messagePrefix() + String.format(pattern, args);
		logger.info(msg);
	}

	@SuppressWarnings("unused")
	private void debug(String pattern, Object... args)
	{
		String msg = messagePrefix() + String.format(pattern, args);
		logger.debug(msg);
	}

	private String messagePrefix()
	{
		return "Line " + lpad(lineNo, 6, '0', " | ") + rpad(objectID, 16, " | ");
	}

}
