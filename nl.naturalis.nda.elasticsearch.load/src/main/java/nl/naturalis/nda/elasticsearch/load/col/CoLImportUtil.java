package nl.naturalis.nda.elasticsearch.load.col;

import static nl.naturalis.nda.elasticsearch.load.CSVImportUtil.val;
import static nl.naturalis.nda.elasticsearch.load.col.CoLTaxonCsvField.*;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.TaxonomicStatus;
import nl.naturalis.nda.elasticsearch.load.normalize.TaxonomicStatusNormalizer;

import org.apache.commons.csv.CSVRecord;

/**
 * Provides common functionality related to the CoL ETL cycle.
 * 
 * @author Ayco Holleman
 *
 */
class CoLImportUtil {

	static final TaxonomicStatusNormalizer statusNormalizer;

	static {
		statusNormalizer = TaxonomicStatusNormalizer.getInstance();
	}

	private CoLImportUtil()
	{
	}

	static ScientificName getScientificName(CSVRecord record)
	{
		ScientificName sn = new ScientificName();
		sn.setFullScientificName(val(record, scientificName));
		sn.setGenusOrMonomial(val(record, genericName));
		sn.setSpecificEpithet(val(record, specificEpithet));
		sn.setInfraspecificEpithet(val(record, infraspecificEpithet));
		sn.setAuthorshipVerbatim(val(record, scientificNameAuthorship));
		TaxonomicStatus status = statusNormalizer.getEnumConstant(val(record, taxonomicStatus));
		sn.setTaxonomicStatus(status);
		return sn;
	}

	static ScientificName getScientificName(CSVRecord record, TaxonomicStatus status)
	{
		ScientificName sn = new ScientificName();
		sn.setFullScientificName(val(record, scientificName));
		sn.setGenusOrMonomial(val(record, genericName));
		sn.setSpecificEpithet(val(record, specificEpithet));
		sn.setInfraspecificEpithet(val(record, infraspecificEpithet));
		sn.setAuthorshipVerbatim(val(record, scientificNameAuthorship));
		sn.setTaxonomicStatus(status);
		return sn;
	}

}
