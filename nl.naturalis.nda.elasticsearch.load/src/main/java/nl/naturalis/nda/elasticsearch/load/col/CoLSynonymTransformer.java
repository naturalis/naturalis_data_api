package nl.naturalis.nda.elasticsearch.load.col;

import static nl.naturalis.nda.elasticsearch.load.CSVImportUtil.val;
import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_TAXON;
import static nl.naturalis.nda.elasticsearch.load.col.CoLImportUtil.getScientificName;
import static nl.naturalis.nda.elasticsearch.load.col.CoLTaxonCsvField.acceptedNameUsageID;
import static nl.naturalis.nda.elasticsearch.load.col.CoLTaxonCsvField.scientificName;

import java.util.Arrays;
import java.util.List;

import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.load.AbstractCSVTransformer;
import nl.naturalis.nda.elasticsearch.load.CSVRecordInfo;
import nl.naturalis.nda.elasticsearch.load.CSVTransformer;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;
import nl.naturalis.nda.elasticsearch.load.LoadConstants;
import nl.naturalis.nda.elasticsearch.load.Registry;

import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;

/**
 * A subclass of {@link CSVTransformer} that enriches {@link ESTaxon} objects
 * with synonyms from the taxa.txt file
 * 
 * @author Ayco Holleman
 *
 */
class CoLSynonymTransformer extends AbstractCSVTransformer<ESTaxon> {

	static Logger logger = Registry.getInstance().getLogger(CoLSynonymTransformer.class);

	private final IndexNative index;

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
	public List<ESTaxon> transform(CSVRecordInfo info)
	{

		stats.recordsProcessed++;
		recInf = info;
		CSVRecord record = info.getRecord();
		objectID = val(record, acceptedNameUsageID);

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
					error("Synonym already exists: " + synonym);
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
			stats.objectsRejected++;
			if (!suppressErrors) {
				error(t.getMessage());
				t.printStackTrace();
			}
			return null;
		}
	}
}
