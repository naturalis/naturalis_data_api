package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.api.model.SourceSystem.COL;
import static nl.naturalis.nba.dao.es.DocumentType.TAXON;
import static nl.naturalis.nba.dao.es.util.ESUtil.getElasticsearchId;
import static nl.naturalis.nba.etl.col.CoLVernacularNameCsvField.language;
import static nl.naturalis.nba.etl.col.CoLVernacularNameCsvField.taxonID;
import static nl.naturalis.nba.etl.col.CoLVernacularNameCsvField.vernacularName;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.VernacularName;
import nl.naturalis.nba.dao.es.types.ESTaxon;
import nl.naturalis.nba.dao.es.util.ESUtil;
import nl.naturalis.nba.etl.AbstractCSVTransformer;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.CSVTransformer;
import nl.naturalis.nba.etl.ETLRegistry;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.Transformer;

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

	private final CoLTaxonLoader loader;

	CoLVernacularNameTransformer(ETLStatistics stats, CoLTaxonLoader loader)
	{
		super(stats);
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
		try {
			String id = getElasticsearchId(COL, objectID);
			ESTaxon taxon = loader.findInQueue(id);
			if (taxon != null) {
				VernacularName vn = createVernacularName();
				if (!taxon.getVernacularNames().contains(vn)) {
					stats.objectsAccepted++;
					taxon.addVernacularName(vn);
				}
				else {
					stats.objectsRejected++;
					if (!suppressErrors) {
						error("Duplicate vernacular name: " + vn);
					}
				}
				return null;
			}
			taxon = ESUtil.find(TAXON, id);
			VernacularName vn = createVernacularName();
			if (taxon != null) {
				if (taxon.getVernacularNames() == null
						|| !taxon.getVernacularNames().contains(vn)) {
					stats.objectsAccepted++;
					taxon.addVernacularName(vn);
					return Arrays.asList(taxon);
				}
				if (!suppressErrors) {
					error("Duplicate vernacular name: " + vn);
				}
			}
			else {
				if (!suppressErrors) {
					error("Orphan vernacular name: " + vn);
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
				taxon = ESUtil.find(TAXON, id);
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