package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.api.model.SourceSystem.COL;
import static nl.naturalis.nba.dao.DocumentType.TAXON;
import static nl.naturalis.nba.dao.util.es.ESUtil.getElasticsearchId;
import static nl.naturalis.nba.etl.ETLUtil.getTestGenera;
import static nl.naturalis.nba.etl.col.CoLVernacularNameCsvField.language;
import static nl.naturalis.nba.etl.col.CoLVernacularNameCsvField.taxonID;
import static nl.naturalis.nba.etl.col.CoLVernacularNameCsvField.vernacularName;

import java.util.Arrays;
import java.util.List;

import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.VernacularName;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.AbstractCSVTransformer;
import nl.naturalis.nba.etl.CSVTransformer;
import nl.naturalis.nba.etl.ETLStatistics;

/**
 * A subclass of {@link CSVTransformer} that transforms CSV records into {@link Taxon}
 * objects enriched with vernacular names.
 * 
 * @author Ayco Holleman
 *
 */
class CoLVernacularNameTransformer
		extends AbstractCSVTransformer<CoLVernacularNameCsvField, Taxon> {

	private final CoLTaxonLoader loader;
	private int orphans;
	private String[] testGenera;

	CoLVernacularNameTransformer(ETLStatistics stats, CoLTaxonLoader loader)
	{
		super(stats);
		this.loader = loader;
		testGenera = getTestGenera();
	}

	@Override
	protected String getObjectID()
	{
		return input.get(taxonID);
	}

	@Override
	protected List<Taxon> doTransform()
	{
		stats.recordsAccepted++;
		stats.objectsProcessed++;
		try {
			String id = getElasticsearchId(COL, objectID);
			Taxon taxon = loader.findInQueue(id);
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
				++orphans;
				if (!suppressErrors && testGenera == null) {
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

	public int getNumOrphans()
	{
		return orphans;
	}

	private VernacularName createVernacularName()
	{
		VernacularName vn = new VernacularName();
		vn.setName(input.get(vernacularName));
		vn.setLanguage(input.get(language));
		return vn;
	}
}
