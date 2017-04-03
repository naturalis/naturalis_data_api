package nl.naturalis.nba.etl.brahms;

import static nl.naturalis.nba.api.model.SourceSystem.BRAHMS;
import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;
import static nl.naturalis.nba.etl.ETLUtil.getSpecimenPurl;
import static nl.naturalis.nba.etl.LoadConstants.BRAHMS_ABCD_COLLECTION_TYPE;
import static nl.naturalis.nba.etl.LoadConstants.BRAHMS_ABCD_SOURCE_ID;
import static nl.naturalis.nba.etl.LoadConstants.LICENCE;
import static nl.naturalis.nba.etl.LoadConstants.LICENCE_TYPE;
import static nl.naturalis.nba.etl.LoadConstants.SOURCE_INSTITUTION_ID;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.CATEGORY;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.DAYIDENT;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.DETBY;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.MONTHIDENT;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.NOTONLINE;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.PLANTDESC;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.VERNACULAR;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.YEARIDENT;
import static nl.naturalis.nba.etl.brahms.BrahmsImportUtil.getDefaultClassification;
import static nl.naturalis.nba.etl.brahms.BrahmsImportUtil.getScientificName;
import static nl.naturalis.nba.etl.brahms.BrahmsImportUtil.getSystemClassification;
import static nl.naturalis.nba.etl.brahms.BrahmsImportUtil.getTaxonRank;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import nl.naturalis.nba.api.model.Agent;
import nl.naturalis.nba.api.model.DefaultClassification;
import nl.naturalis.nba.api.model.GatheringEvent;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.api.model.VernacularName;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.ETLUtil;
import nl.naturalis.nba.etl.ThemeCache;

/**
 * The transformer component in the Brahms ETL cycle for specimens.
 * 
 * @author Ayco Holleman
 *
 */
class BrahmsSpecimenTransformer extends BrahmsTransformer<Specimen> {

	private static final ThemeCache themeCache;
	private static final String UNIT_ID_REGEX = "([a-zA-Z0-9_.-]){3,}";
	private static final Pattern unitIDPattern;

	static {
		themeCache = ThemeCache.getInstance();
		unitIDPattern = Pattern.compile(UNIT_ID_REGEX);
	}

	BrahmsSpecimenTransformer(ETLStatistics stats)
	{
		super(stats);
	}

	@Override
	protected List<Specimen> doTransform()
	{
		// No record-level validations, so:
		stats.recordsAccepted++;
		stats.objectsProcessed++;
		try {
			Specimen specimen = new Specimen();
			specimen.setSourceSystemId(objectID);
			specimen.setUnitID(objectID);
			if (unitIDPattern.matcher(objectID).matches()) {
				specimen.setUnitGUID(getSpecimenPurl(objectID));
			}
			else if (!suppressErrors) {
				warn("PURL generation suppressed for problematic UnitID: \"%s\"", objectID);
			}
			setConstants(specimen);
			List<String> themes = themeCache.lookup(objectID, SPECIMEN, BRAHMS);
			specimen.setTheme(themes);
			String s = input.get(CATEGORY);
			if (s == null)
				specimen.setRecordBasis("Preserved Specimen");
			else
				specimen.setRecordBasis(s);
			specimen.setAssemblageID(getAssemblageID());
			specimen.setNotes(input.get(PLANTDESC));
			s = input.get(NOTONLINE);
			if (s == null || s.equals("0"))
				specimen.setObjectPublic(true);
			else
				specimen.setObjectPublic(false);
			specimen.setGatheringEvent(getGatheringEvent(input));
			specimen.addIndentification(getSpecimenIdentification(input));
			stats.objectsAccepted++;
			return Arrays.asList(specimen);
		}
		catch (Throwable t) {
			stats.objectsRejected++;
			if (!suppressErrors) {
				error(t.getMessage());
				error(input.getLine());
			}
			return null;
		}
	}

	private GatheringEvent getGatheringEvent(CSVRecordInfo<BrahmsCsvField> record)
	{
		GatheringEvent ge = new GatheringEvent();
		populateGatheringEvent(ge, record);
		return ge;
	}

	private SpecimenIdentification getSpecimenIdentification(CSVRecordInfo<BrahmsCsvField> record)
	{
		SpecimenIdentification identification = new SpecimenIdentification();
		identification.setTypeStatus(getTypeStatus());
		String s = record.get(DETBY);
		if (s != null)
			identification.addIdentifier(new Agent(s));
		s = record.get(VERNACULAR);
		if (s != null)
			identification.setVernacularNames(Arrays.asList(new VernacularName(s)));
		String y = record.get(YEARIDENT);
		String m = record.get(MONTHIDENT);
		String d = record.get(DAYIDENT);
		identification.setDateIdentified(getDate(y, m, d));
		ScientificName sn = getScientificName(record);
		DefaultClassification dc = getDefaultClassification(record, sn);
		identification.setTaxonRank(getTaxonRank(record));
		identification.setScientificName(sn);
		identification.setDefaultClassification(dc);
		identification.setSystemClassification(getSystemClassification(dc));
		String nameGroup = ETLUtil.createScientificNameGroup(identification);
		identification.setScientificNameGroup(nameGroup);
		return identification;
	}

	private static void setConstants(Specimen specimen)
	{
		specimen.setSourceSystem(BRAHMS);
		specimen.setSourceInstitutionID(SOURCE_INSTITUTION_ID);
		specimen.setOwner(SOURCE_INSTITUTION_ID);
		specimen.setSourceID(BRAHMS_ABCD_SOURCE_ID);
		specimen.setLicenseType(LICENCE_TYPE);
		specimen.setLicense(LICENCE);
		specimen.setCollectionType(BRAHMS_ABCD_COLLECTION_TYPE);
	}

	/*
	 * Returns the id of the "botanical" record, which in Brahms is the
	 * relational parent of the specimen record. Multiple specimens (twigs,
	 * leaves, etc.) can belong to the same botanical record. Because we need to
	 * make sure this id is not just unique within Brahms but NBA-wide, we
	 * append the Brahm system code to it.
	 */
	private String getAssemblageID()
	{
		Float f = getFloat(input, BrahmsCsvField.BRAHMS);
		if (f == null) {
			return null;
		}
		return ESUtil.getElasticsearchId(BRAHMS, f.intValue());
	}

}
