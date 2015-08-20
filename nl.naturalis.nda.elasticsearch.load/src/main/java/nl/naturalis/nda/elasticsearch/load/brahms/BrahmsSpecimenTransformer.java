package nl.naturalis.nda.elasticsearch.load.brahms;

import static nl.naturalis.nda.domain.SourceSystem.BRAHMS;
import static nl.naturalis.nda.elasticsearch.load.CSVImportUtil.getFloat;
import static nl.naturalis.nda.elasticsearch.load.CSVImportUtil.val;
import static nl.naturalis.nda.elasticsearch.load.DocumentType.SPECIMEN;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.BRAHMS_ABCD_COLLECTION_TYPE;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.BRAHMS_ABCD_SOURCE_ID;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.ES_ID_PREFIX_BRAHMS;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.LICENCE;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.LICENCE_TYPE;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.PURL_SERVER_BASE_URL;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.SOURCE_INSTITUTION_ID;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsCsvField.BARCODE;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsCsvField.CATEGORY;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsCsvField.NOTONLINE;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsCsvField.PLANTDESC;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsCsvField.TYPE;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsImportUtil.getGatheringEvent;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsImportUtil.getSpecimenIdentification;
import static org.domainobject.util.StringUtil.lpad;
import static org.domainobject.util.StringUtil.rpad;

import java.util.Arrays;
import java.util.List;

import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.load.CSVRecordInfo;
import nl.naturalis.nda.elasticsearch.load.CSVTransformer;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import nl.naturalis.nda.elasticsearch.load.Registry;
import nl.naturalis.nda.elasticsearch.load.ThemeCache;
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

	private final ETLStatistics stats;
	private final SpecimenTypeStatusNormalizer typeStatusNormalizer;
	private final ThemeCache themeCache;
	private final boolean suppressErrors;

	private CSVRecordInfo recInf;
	private String specimenID;

	public BrahmsSpecimenTransformer(ETLStatistics stats)
	{
		this.stats = stats;
		typeStatusNormalizer = SpecimenTypeStatusNormalizer.getInstance();
		themeCache = ThemeCache.getInstance();
		suppressErrors = ConfigObject.TRUE("brahms.suppress-errors");
	}

	@Override
	public List<ESSpecimen> transform(CSVRecordInfo info)
	{
		stats.recordsProcessed++;
		recInf = info;
		CSVRecord record = info.getRecord();
		specimenID = val(record, BARCODE);
		if (specimenID == null) {
			stats.recordsRejected++;
			error("Missing barcode");
			return null;
		}

		ESSpecimen specimen = new ESSpecimen();

		try {
			specimen.setSourceSystemId(specimenID);
			specimen.setUnitID(specimenID);
			specimen.setUnitGUID(getPurl(specimenID));
			setConstants(specimen);
			List<String> themes = themeCache.getThemesForDocument(specimenID, SPECIMEN, BRAHMS);
			specimen.setTheme(themes);
			String s = val(record, CATEGORY);
			if (s == null)
				specimen.setRecordBasis("Preserved Specimen");
			else
				specimen.setRecordBasis(s);
			specimen.setAssemblageID(getAssemblageID(record));
			specimen.setNotes(val(record, PLANTDESC));
			specimen.setTypeStatus(getTypeStatus(record));
			s = val(record, NOTONLINE);
			if (s == null || s.equals("0"))
				specimen.setObjectPublic(true);
			else
				specimen.setObjectPublic(false);
			specimen.setGatheringEvent(getGatheringEvent(record));
			specimen.addIndentification(getSpecimenIdentification(record));
		}
		catch (Throwable t) {
			stats.recordsRejected++;
			if (!suppressErrors) {
				error(t.getMessage());
				error(recInf.getLine());
			}
		}
		stats.recordsAccepted++;
		stats.objectsProcessed++;
		return Arrays.asList(specimen);
	}

	private static void setConstants(ESSpecimen specimen)
	{
		specimen.setSourceSystem(SourceSystem.BRAHMS);
		specimen.setSourceInstitutionID(SOURCE_INSTITUTION_ID);
		specimen.setOwner(SOURCE_INSTITUTION_ID);
		specimen.setSourceID(BRAHMS_ABCD_SOURCE_ID);
		specimen.setLicenceType(LICENCE_TYPE);
		specimen.setLicence(LICENCE);
		specimen.setCollectionType(BRAHMS_ABCD_COLLECTION_TYPE);
	}

	private static String getPurl(String specimenID)
	{
		return PURL_SERVER_BASE_URL + "/naturalis/specimen/" + LoadUtil.urlEncode(specimenID);
	}

	private static String getAssemblageID(CSVRecord record)
	{
		Float f = getFloat(record, BrahmsCsvField.BRAHMS);
		if (f == null)
			return null;
		return ES_ID_PREFIX_BRAHMS + f.intValue();
	}

	private String getTypeStatus(CSVRecord record)
	{
		return typeStatusNormalizer.getNormalizedValue(val(record, TYPE));
	}

	private void error(String pattern, Object... args)
	{
		String msg = messagePrefix() + String.format(pattern, args);
		logger.error(msg);
	}

	@SuppressWarnings("unused")
	private void warn(String pattern, Object... args)
	{
		String msg = messagePrefix() + String.format(pattern, args);
		logger.warn(msg);
	}

	@SuppressWarnings("unused")
	private void info(String pattern, Object... args)
	{
		String msg = messagePrefix() + String.format(pattern, args);
		logger.info(msg);
	}

	@SuppressWarnings("unused")
	private void debug(String pattern, Object... args)
	{
		String msg = messagePrefix() + String.format(pattern, args);
		logger.debug(msg);
	}

	private String messagePrefix()
	{
		return "Line " + lpad(recInf.getLineNumber(), 6, '0', " | ") + rpad(specimenID, 16, " | ");
	}

}
