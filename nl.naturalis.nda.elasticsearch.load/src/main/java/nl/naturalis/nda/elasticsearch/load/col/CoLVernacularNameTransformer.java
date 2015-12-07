package nl.naturalis.nda.elasticsearch.load.col;

import static nl.naturalis.nda.elasticsearch.load.NBAImportAll.LUCENE_TYPE_TAXON;
import static nl.naturalis.nda.elasticsearch.load.col.CoLVernacularNameCsvField.language;
import static nl.naturalis.nda.elasticsearch.load.col.CoLVernacularNameCsvField.taxonID;
import static nl.naturalis.nda.elasticsearch.load.col.CoLVernacularNameCsvField.vernacularName;

import java.util.Arrays;
import java.util.List;

import nl.naturalis.nda.domain.VernacularName;
import nl.naturalis.nda.elasticsearch.client.IndexManagerNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.load.AbstractCSVTransformer;
import nl.naturalis.nda.elasticsearch.load.CSVRecordInfo;
import nl.naturalis.nda.elasticsearch.load.CSVTransformer;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;
import nl.naturalis.nda.elasticsearch.load.LoadConstants;
import nl.naturalis.nda.elasticsearch.load.Registry;
import nl.naturalis.nda.elasticsearch.load.Transformer;

import org.slf4j.Logger;

/**
 * A subclass of {@link CSVTransformer} that transforms CSV records into
 * {@link ESTaxon} objects enriched with vernacular names.
 * 
 * @author Ayco Holleman
 *
 */
class CoLVernacularNameTransformer extends
		AbstractCSVTransformer<CoLVernacularNameCsvField, ESTaxon> {

	static Logger logger = Registry.getInstance().getLogger(CoLVernacularNameTransformer.class);

	private final IndexManagerNative index;
	private final CoLTaxonLoader loader;

	CoLVernacularNameTransformer(ETLStatistics stats, CoLTaxonLoader loader)
	{
		super(stats);
		this.index = Registry.getInstance().getNbaIndexManager();
		this.loader = loader;
	}

	@Override
	protected String getObjectID()
	{
		return input.get(taxonID);
	}

	@Override
	protected List<ESTaxon> doTransform()
	{
		stats.recordsAccepted++;
		stats.objectsProcessed++;
		List<ESTaxon> result = null;
		try {
			String docID = LoadConstants.ES_ID_PREFIX_COL + objectID;
			boolean isNew = false;
			ESTaxon taxon = loader.findInQueue(docID);
			if (taxon == null) {
				isNew = true;
				taxon = index.get(LUCENE_TYPE_TAXON, docID, ESTaxon.class);
			}
			if (taxon == null) {
				stats.objectsRejected++;
				if (!suppressErrors) {
					error("Orphan vernacular name: " + input.get(vernacularName));
				}
			}
			else {
				VernacularName vn = createVernacularName();
				if (taxon.getVernacularNames() == null || !taxon.getVernacularNames().contains(vn)) {
					stats.objectsAccepted++;
					taxon.addVernacularName(vn);
					if (isNew) {
						result = Arrays.asList(taxon);
					}
					/*
					 * else we have added the vernacular name to a taxon that's
					 * already queued for indexing, so we're fine
					 */
				}
				else {
					stats.objectsRejected++;
					if (!suppressErrors) {
						error("Duplicate vernacular name for taxon: " + vn);
					}
				}
			}
		}
		catch (Throwable t) {
			handleError(t);
		}
		return result;
	}

	/**
	 * Removes all literature references from the taxon specified in the CSV
	 * record. Not part of the {@link Transformer} API, but used by the
	 * {@link CoLReferenceCleaner} to clean up taxa before starting the
	 * {@link CoLReferenceImporter}.
	 * 
	 * @param recInf
	 * @return
	 */
	public List<ESTaxon> clean(CSVRecordInfo<CoLVernacularNameCsvField> recInf)
	{
		this.input = recInf;
		objectID = input.get(taxonID);
		// Not much can go wrong here, so:
		stats.recordsProcessed++;
		stats.recordsAccepted++;
		stats.objectsProcessed++;
		List<ESTaxon> result = null;
		try {
			String elasticID = LoadConstants.ES_ID_PREFIX_COL + objectID;
			ESTaxon taxon = loader.findInQueue(elasticID);
			if (taxon == null) {
				taxon = index.get(LUCENE_TYPE_TAXON, elasticID, ESTaxon.class);
				if (taxon != null && taxon.getVernacularNames() != null) {
					stats.objectsAccepted++;
					taxon.setVernacularNames(null);
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

	private VernacularName createVernacularName()
	{
		VernacularName vn = new VernacularName();
		vn.setName(input.get(vernacularName));
		vn.setLanguage(input.get(language));
		return vn;
	}
}
