package nl.naturalis.nda.elasticsearch.load.col;

import static nl.naturalis.nda.elasticsearch.load.CSVImportUtil.val;
import static nl.naturalis.nda.elasticsearch.load.col.CoLTaxonCsvField.genericName;
import static nl.naturalis.nda.elasticsearch.load.col.CoLTaxonCsvField.infraspecificEpithet;
import static nl.naturalis.nda.elasticsearch.load.col.CoLTaxonCsvField.scientificName;
import static nl.naturalis.nda.elasticsearch.load.col.CoLTaxonCsvField.scientificNameAuthorship;
import static nl.naturalis.nda.elasticsearch.load.col.CoLTaxonCsvField.specificEpithet;
import static nl.naturalis.nda.elasticsearch.load.col.CoLTaxonCsvField.taxonomicStatus;

import java.io.File;

import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.TaxonomicStatus;
import nl.naturalis.nda.elasticsearch.load.CSVExtractor;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;
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

	static CSVExtractor createExtractor(ETLStatistics stats, File f, boolean suppressErrors)
	{
		CSVExtractor extractor;
		extractor = new CSVExtractor(f, stats);
		extractor.setSkipHeader(true);
		extractor.setDelimiter('\t');
		extractor.setSuppressErrors(suppressErrors);
		return extractor;
	}

}
