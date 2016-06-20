package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.api.model.SourceSystem.COL;
import static nl.naturalis.nba.dao.es.util.DocumentType.TAXON;
import static nl.naturalis.nba.dao.es.util.ESUtil.getElasticsearchId;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.*;

import java.util.Arrays;
import java.util.List;

import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.TaxonomicStatus;
import nl.naturalis.nba.dao.es.types.ESTaxon;
import nl.naturalis.nba.etl.AbstractCSVTransformer;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.CSVTransformer;
import nl.naturalis.nba.etl.ETLRegistry;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.Transformer;
import nl.naturalis.nba.etl.elasticsearch.IndexManagerNative;
import nl.naturalis.nba.etl.normalize.TaxonomicStatusNormalizer;

/**
 * A implementation of {@link CSVTransformer} that enriches {@link ESTaxon}
 * objects with synonyms from the taxa.txt file.
 * 
 * @author Ayco Holleman
 *
 */
class CoLSynonymTransformer extends AbstractCSVTransformer<CoLTaxonCsvField, ESTaxon> {

	private final IndexManagerNative index;
	private final TaxonomicStatusNormalizer statusNormalizer;

	private CoLTaxonLoader loader;

	CoLSynonymTransformer(ETLStatistics stats)
	{
		super(stats);
		this.index = ETLRegistry.getInstance().getIndexManager(TAXON);
		this.statusNormalizer = TaxonomicStatusNormalizer.getInstance();
	}

	void setLoader(CoLTaxonLoader loader)
	{
		this.loader = loader;
	}

	@Override
	protected boolean skipRecord()
	{
		/*
		 * acceptedNameUsageID field is a foreign key to accepted name record.
		 * If it is empty, the record is itself IS an accepted name record, so
		 * we must skip it.
		 */
		return input.get(acceptedNameUsageID) == null;
	}

	@Override
	protected String getObjectID()
	{
		return input.get(acceptedNameUsageID);
	}

	@Override
	protected List<ESTaxon> doTransform()
	{
		stats.recordsAccepted++;
		stats.objectsProcessed++;
		try {
			String id = getElasticsearchId(COL, objectID);
			String synonym = input.get(scientificName);
			ESTaxon taxon = loader.findInQueue(id);
			if (taxon != null) {
				/*
				 * Taxon has already been queued for indexing because of a
				 * previous synonym belonging to the same taxon. Return null,
				 * because we don't want to index the taxon twice. Otherwise the
				 * taxon object with the previous synonym would be overwritten
				 * by the taxon object with the current synonym (thus
				 * obliterating the previous synonym). Instead, we want to
				 * append the current synonym to the list of synonyms of the
				 * already-queued taxon object, and then save it once with all
				 * its synonyms.
				 */
				if (!taxon.getSynonyms().contains(synonym)) {
					stats.objectsAccepted++;
					taxon.addSynonym(getScientificName());
				}
				else {
					stats.objectsRejected++;
					if (!suppressErrors) {
						warn("Synonym already exists: " + synonym);
					}
				}
				return null;
			}
			/*
			 * OK, taxon not queued yet. Look it up in the document store.
			 */
			taxon = index.get(TAXON.getName(), id, ESTaxon.class);
			if (taxon != null) {
				if (taxon.getSynonyms() == null || !taxon.getSynonyms().contains(synonym)) {
					stats.objectsAccepted++;
					taxon.addSynonym(getScientificName());
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
	public List<ESTaxon> clean(CSVRecordInfo<CoLTaxonCsvField> recInf)
	{
		stats.recordsProcessed++;
		this.input = recInf;
		objectID = input.get(acceptedNameUsageID);
		if (objectID == null) {
			// This is an accepted name
			stats.recordsSkipped++;
			return null;
		}
		stats.recordsAccepted++;
		stats.objectsProcessed++;
		List<ESTaxon> result = null;
		try {
			String id = getElasticsearchId(COL, objectID);
			ESTaxon taxon = loader.findInQueue(id);
			if (taxon == null) {
				taxon = index.get(TAXON.getName(), id, ESTaxon.class);
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

	private ScientificName getScientificName()
	{
		ScientificName sn = new ScientificName();
		sn.setFullScientificName(input.get(scientificName));
		sn.setGenusOrMonomial(input.get(genericName));
		sn.setSpecificEpithet(input.get(specificEpithet));
		sn.setInfraspecificEpithet(input.get(infraspecificEpithet));
		sn.setAuthorshipVerbatim(input.get(scientificNameAuthorship));
		TaxonomicStatus status = statusNormalizer.getEnumConstant(input.get(taxonomicStatus));
		sn.setTaxonomicStatus(status);
		return sn;
	}
}
