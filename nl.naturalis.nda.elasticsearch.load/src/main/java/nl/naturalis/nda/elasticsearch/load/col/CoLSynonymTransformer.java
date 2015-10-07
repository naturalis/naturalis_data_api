package nl.naturalis.nda.elasticsearch.load.col;

import static nl.naturalis.nda.elasticsearch.load.CSVImportUtil.val;
import static nl.naturalis.nda.elasticsearch.load.NBAImportAll.LUCENE_TYPE_TAXON;
import static nl.naturalis.nda.elasticsearch.load.col.CoLImportUtil.getScientificName;
import static nl.naturalis.nda.elasticsearch.load.col.CoLTaxonCsvField.acceptedNameUsageID;
import static nl.naturalis.nda.elasticsearch.load.col.CoLTaxonCsvField.scientificName;

import java.util.Arrays;
import java.util.List;

import nl.naturalis.nda.elasticsearch.client.IndexManagerNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.load.AbstractCSVTransformer;
import nl.naturalis.nda.elasticsearch.load.CSVRecordInfo;
import nl.naturalis.nda.elasticsearch.load.CSVTransformer;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;
import nl.naturalis.nda.elasticsearch.load.LoadConstants;
import nl.naturalis.nda.elasticsearch.load.Registry;
import nl.naturalis.nda.elasticsearch.load.Transformer;

import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;

/**
 * A subclass of {@link CSVTransformer} that enriches {@link ESTaxon} objects
 * with synonyms from the taxa.txt file.
 * 
 * @author Ayco Holleman
 *
 */
class CoLSynonymTransformer extends AbstractCSVTransformer<ESTaxon> {

	static Logger logger = Registry.getInstance().getLogger(CoLSynonymTransformer.class);

	private final IndexManagerNative index;

	private CoLTaxonLoader loader;

	CoLSynonymTransformer(ETLStatistics stats)
	{
		super(stats);
		this.index = Registry.getInstance().getNbaIndexManager();
	}

	void setLoader(CoLTaxonLoader loader)
	{
		this.loader = loader;
	}

	@Override
	protected String getObjectID()
	{
		return val(input.getRecord(), acceptedNameUsageID);
	}

	@Override
	protected List<ESTaxon> doTransform()
	{
		stats.recordsAccepted++;
		stats.objectsProcessed++;
		try {
			CSVRecord record = input.getRecord();
			String elasticID = LoadConstants.ES_ID_PREFIX_COL + objectID;
			String synonym = val(record, scientificName);

			ESTaxon taxon = loader.findInQueue(elasticID);
			if (taxon != null) {
				/*
				 * Taxon apparently already queued because of a previous CSV
				 * record. Return null, because we don't want to add it to the
				 * queue again. Just add the current synonym to the list of
				 * synonyms.
				 */
				if (!taxon.getSynonyms().contains(synonym)) {
					stats.objectsAccepted++;
					taxon.addSynonym(getScientificName(record));
				}
				else {
					stats.objectsRejected++;
					if (!suppressErrors) {
						warn("Synonym already exists: " + synonym);
					}
				}
				return null;
			}

			taxon = index.get(LUCENE_TYPE_TAXON, elasticID, ESTaxon.class);
			if (taxon != null) {
				if (taxon.getSynonyms() == null || !taxon.getSynonyms().contains(synonym)) {
					stats.objectsAccepted++;
					taxon.addSynonym(getScientificName(record));
					return Arrays.asList(taxon);
				}
				if (!suppressErrors) {
					error("Duplicate synonym: " + synonym);
				}
			}
			else {
				if (!suppressErrors) {
					error("Orphan synonym: " + synonym);
				}
			}
			stats.objectsRejected++;
			return null;
		}
		catch (Throwable t) {
			handleError(t);
			return null;
		}
	}

	/**
	 * Removes all synonyms from the taxon specified in the CSV record. Not part
	 * of the {@link Transformer} API, but used by the
	 * {@link CoLReferenceCleaner} to clean up taxa before starting the
	 * {@link CoLReferenceImporter}.
	 * 
	 * @param recInf
	 * @return
	 */
	public List<ESTaxon> clean(CSVRecordInfo recInf)
	{
		stats.recordsProcessed++;
		this.input = recInf;
		CSVRecord record = recInf.getRecord();
		objectID = val(record, acceptedNameUsageID);
		if (objectID == null) {
			// This is an accepted name
			stats.recordsSkipped++;
			return null;
		}
		stats.recordsAccepted++;
		stats.objectsProcessed++;
		List<ESTaxon> result = null;
		try {
			String elasticID = LoadConstants.ES_ID_PREFIX_COL + objectID;
			ESTaxon taxon = loader.findInQueue(elasticID);
			if (taxon == null) {
				taxon = index.get(LUCENE_TYPE_TAXON, elasticID, ESTaxon.class);
				if (taxon != null && taxon.getSynonyms() != null) {
					stats.objectsAccepted++;
					taxon.setSynonyms(null);
					result = Arrays.asList(taxon);
				}
				else {
					stats.objectsSkipped++;
				}
			}
			else {
				stats.objectsSkipped++;
			}
		}
		catch (Throwable t) {
			handleError(t);
		}
		return result;
	}

}
