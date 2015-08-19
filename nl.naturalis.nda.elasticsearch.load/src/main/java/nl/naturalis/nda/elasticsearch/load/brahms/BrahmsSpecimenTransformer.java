package nl.naturalis.nda.elasticsearch.load.brahms;

import static nl.naturalis.nda.domain.SourceSystem.BRAHMS;
import static nl.naturalis.nda.elasticsearch.load.CSVImportUtil.getFloat;
import static nl.naturalis.nda.elasticsearch.load.CSVImportUtil.val;
import static nl.naturalis.nda.elasticsearch.load.DocumentType.SPECIMEN;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.LICENCE;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.LICENCE_TYPE;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.PURL_SERVER_BASE_URL;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.SOURCE_INSTITUTION_ID;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsImportUtil.getGatheringEvent;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsImportUtil.getSpecimenIdentification;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.CsvField.BARCODE;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.CsvField.CATEGORY;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.CsvField.NOTONLINE;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.CsvField.PLANTDESC;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.CsvField.TYPE;
import static org.domainobject.util.StringUtil.lpad;
import static org.domainobject.util.StringUtil.rpad;

import java.util.Arrays;
import java.util.List;

import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.load.CSVRecordInfo;
import nl.naturalis.nda.elasticsearch.load.CSVTransformer;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import nl.naturalis.nda.elasticsearch.load.Registry;
import nl.naturalis.nda.elasticsearch.load.ThemeCache;
import nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.CsvField;
import nl.naturalis.nda.elasticsearch.load.normalize.SpecimenTypeStatusNormalizer;

import org.apache.commons.csv.CSVRecord;
import org.domainobject.util.ConfigObject;
import org.slf4j.Logger;

/**
 * @author Ayco Holleman
 *
 */
class BrahmsSpecimenTransformer implements CSVTransformer<ESSpecimen> {

	private static final Logger logger = Registry.getInstance().getLogger(BrahmsSpecimenTransformer.class);

	private final SpecimenTypeStatusNormalizer typeStatusNormalizer;
	private final ThemeCache themeCache;
	private final boolean suppressErrors;

	private String specimenID;
	private int lineNo;

	public BrahmsSpecimenTransformer()
	{
		typeStatusNormalizer = SpecimenTypeStatusNormalizer.getInstance();
		themeCache = ThemeCache.getInstance();
		suppressErrors = ConfigObject.TRUE("brahms.suppress-errors");
	}

	@Override
	public List<ESSpecimen> transform(CSVRecordInfo info)
	{
		CSVRecord record = info.getRecord();
		specimenID = val(record, BARCODE.ordinal());
		if (specimenID == null) {
			error("Missing barcode");
			return null;
		}

		ESSpecimen specimen = new ESSpecimen();
		specimen.setSourceSystem(BRAHMS);
		specimen.setSourceSystemId(specimenID);
		specimen.setUnitID(specimenID);
		specimen.setUnitGUID(getPurl(specimenID));

		specimen.setSourceInstitutionID(SOURCE_INSTITUTION_ID);
		specimen.setOwner(SOURCE_INSTITUTION_ID);
		specimen.setSourceID("Brahms");
		specimen.setLicenceType(LICENCE_TYPE);
		specimen.setLicence(LICENCE);
		specimen.setCollectionType("Botany");

		List<String> themes = themeCache.getThemesForDocument(specimenID, SPECIMEN, BRAHMS);
		specimen.setTheme(themes);

		String recordBasis = val(record, CATEGORY.ordinal());
		if (recordBasis == null) {
			specimen.setRecordBasis("Preserved Specimen");
		}
		else {
			specimen.setRecordBasis(recordBasis);
		}

		specimen.setAssemblageID(getAssemblageID(record));
		specimen.setNotes(val(record, PLANTDESC.ordinal()));
		specimen.setTypeStatus(getTypeStatus(record));
		String notOnline = val(record, NOTONLINE.ordinal());
		if (notOnline == null || notOnline.equals("0")) {
			specimen.setObjectPublic(true);
		}
		else {
			specimen.setObjectPublic(false);
		}
		specimen.setGatheringEvent(getGatheringEvent(record));
		specimen.addIndentification(getSpecimenIdentification(record));
		return Arrays.asList(specimen);
	}

	private void error(String pattern, Object... args)
	{
		if (!suppressErrors) {
			String msg = messagePrefix() + String.format(pattern, args);
			logger.debug(msg);
		}
	}

	@SuppressWarnings("unused")
	private void warn(String pattern, Object... args)
	{
		if (!suppressErrors) {
			String msg = messagePrefix() + String.format(pattern, args);
			logger.debug(msg);
		}
	}

	@SuppressWarnings("unused")
	private void info(String pattern, Object... args)
	{
		String msg = messagePrefix() + String.format(pattern, args);
		logger.debug(msg);
	}

	@SuppressWarnings("unused")
	private void debug(String pattern, Object... args)
	{
		String msg = messagePrefix() + String.format(pattern, args);
		logger.debug(msg);
	}

	private String messagePrefix()
	{
		return "Line " + lpad(lineNo, 6, '0', " | ") + rpad(specimenID, 16, " | ");
	}

	private static String getPurl(String specimenID)
	{
		return PURL_SERVER_BASE_URL + "/naturalis/specimen/" + LoadUtil.urlEncode(specimenID);
	}

	private static String getAssemblageID(CSVRecord record)
	{
		Float f = getFloat(record, CsvField.BRAHMS.ordinal());
		if (f == null)
			return null;
		return BrahmsImportAll.ID_PREFIX + f.intValue();
	}

	private String getTypeStatus(CSVRecord record)
	{
		return typeStatusNormalizer.getNormalizedValue(val(record, TYPE.ordinal()));
	}

}
