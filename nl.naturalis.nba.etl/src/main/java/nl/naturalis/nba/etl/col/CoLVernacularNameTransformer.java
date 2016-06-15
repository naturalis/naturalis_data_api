package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.etl.col.CoLVernacularNameCsvField.language;
import static nl.naturalis.nba.etl.col.CoLVernacularNameCsvField.taxonID;
import static nl.naturalis.nba.etl.col.CoLVernacularNameCsvField.vernacularName;

import java.util.Arrays;
import java.util.List;

import nl.naturalis.nba.api.model.VernacularName;
import nl.naturalis.nba.dao.es.types.ESTaxon;
import nl.naturalis.nba.etl.AbstractCSVTransformer;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.CSVTransformer;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.LoadConstants;
import nl.naturalis.nba.etl.ETLRegistry;
import nl.naturalis.nba.etl.Transformer;
import nl.naturalis.nba.etl.elasticsearch.IndexManagerNative;

import org.apache.logging.log4j.Logger;

import static nl.naturalis.nba.api.model.SourceSystem.COL;
import static nl.naturalis.nba.dao.es.util.DocumentType.TAXON;
import static nl.naturalis.nba.dao.es.util.ESUtil.getElasticsearchId;

/**
 * A subclass of {@link CSVTransformer} that transforms CSV records into
 * {@link ESTaxon} objects enriched with vernacular names.
 * 
 * @author Ayco Holleman
 *
 */
class CoLVernacularNameTransformer
		extends AbstractCSVTransformer<CoLVernacularNameCsvField, ESTaxon> {

	static Logger logger = ETLRegistry.getInstance().getLogger(CoLVernacularNameTransformer.class);

	private final IndexManagerNative index;
	private final CoLTaxonLoader loader;

	CoLVernacularNameTransformer(ETLStatistics stats, CoLTaxonLoader loader)
	{
		super(stats);
		this.index = ETLRegistry.getInstance().getNbaIndexManager(TAXON);
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
			String id = getElasticsearchId(COL, objectID);
			boolean isNew = false;
			ESTaxon taxon = loader.findInQueue(id);
			if (taxon == null) {
				isNew = true;
				taxon = index.get(TAXON.getName(), id, ESTaxon.class);
			}
			if (taxon == null) {
				stats.objectsRejected++;
				if (!suppressErrors) {
					error("Orphan vernacular name: " + input.get(vernacularName));
				}
			}
			else {
				VernacularName vn = createVernacularName();
				if (taxon.getVernacularNames() == null
						|| !taxon.getVernacularNames().contains(vn)) {
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
			String id = getElasticsearchId(COL, objectID);
			ESTaxon taxon = loader.findInQueue(id);
			if (taxon == null) {
				taxon = index.get(TAXON.getName(), id, ESTaxon.class);
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
