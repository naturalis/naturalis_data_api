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
import static nl.naturalis.nda.elasticsearch.load.LoadUtil.urlEncode;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsCsvField.BARCODE;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsCsvField.CATEGORY;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsCsvField.NOTONLINE;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsCsvField.PLANTDESC;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsCsvField.TYPE;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsImportUtil.getGatheringEvent;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsImportUtil.getSpecimenIdentification;

import java.util.Arrays;
import java.util.List;

import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.load.AbstractCSVTransformer;
import nl.naturalis.nda.elasticsearch.load.CSVRecordInfo;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;
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
class BrahmsSpecimenTransformer extends AbstractCSVTransformer<ESSpecimen> {

	@SuppressWarnings("unused")
	private static final Logger logger;
	private static final SpecimenTypeStatusNormalizer typeStatusNormalizer;
	private static final ThemeCache themeCache;

	static {
		logger = Registry.getInstance().getLogger(BrahmsSpecimenTransformer.class);
		typeStatusNormalizer = SpecimenTypeStatusNormalizer.getInstance();
		themeCache = ThemeCache.getInstance();
	}

	public BrahmsSpecimenTransformer(ETLStatistics stats)
	{
		super(stats);
		suppressErrors = ConfigObject.isEnabled("brahms.suppress-errors");
	}

	@Override
	public List<ESSpecimen> transform(CSVRecordInfo info)
	{
		stats.recordsProcessed++;
		recInf = info;
		CSVRecord record = info.getRecord();
		objectID = val(record, BARCODE);
		if (objectID == null) {
			stats.recordsRejected++;
			if (!suppressErrors) {
				objectID = "?";
				error("Missing barcode");
			}
			return null;
		}
		
		stats.recordsAccepted++;
		stats.objectsProcessed++;
		
		try {
			ESSpecimen specimen = new ESSpecimen();
			specimen.setSourceSystemId(objectID);
			specimen.setUnitID(objectID);
			specimen.setUnitGUID(getPurl());
			setConstants(specimen);
			List<String> themes = themeCache.lookup(objectID, SPECIMEN, BRAHMS);
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
			return Arrays.asList(specimen);
		}
		catch (Throwable t) {
			stats.objectsRejected++;
			if (!suppressErrors) {
				error(t.getMessage());
				error(recInf.getLine());
			}
			return null;
		}
	}

	private String getPurl()
	{
		return PURL_SERVER_BASE_URL + "/naturalis/specimen/" + urlEncode(objectID);
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

	private static String getAssemblageID(CSVRecord record)
	{
		Float f = getFloat(record, BrahmsCsvField.BRAHMS);
		if (f == null)
			return null;
		return ES_ID_PREFIX_BRAHMS + f.intValue();
	}

	private static String getTypeStatus(CSVRecord record)
	{
		return typeStatusNormalizer.normalize(val(record, TYPE));
	}

}
