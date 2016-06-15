package nl.naturalis.nba.etl.brahms;

import static nl.naturalis.nba.api.model.SourceSystem.BRAHMS;
import static nl.naturalis.nba.etl.LoadConstants.*;
import static nl.naturalis.nba.etl.LoadUtil.getSpecimenPurl;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.BARCODE;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.CATEGORY;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.NOTONLINE;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.PLANTDESC;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.TYPE;
import static nl.naturalis.nba.etl.brahms.BrahmsImportUtil.getFloat;
import static nl.naturalis.nba.etl.brahms.BrahmsImportUtil.getGatheringEvent;
import static nl.naturalis.nba.etl.brahms.BrahmsImportUtil.getSpecimenIdentification;

import static nl.naturalis.nba.dao.es.util.DocumentType.*;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.api.model.SpecimenTypeStatus;
import nl.naturalis.nba.dao.es.types.ESSpecimen;
import nl.naturalis.nba.dao.es.util.ESUtil;
import nl.naturalis.nba.etl.AbstractCSVTransformer;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.ThemeCache;
import nl.naturalis.nba.etl.normalize.SpecimenTypeStatusNormalizer;

import org.domainobject.util.ConfigObject;

/**
 * The transformer component in the Brahms ETL cycle for specimens.
 * 
 * @author Ayco Holleman
 *
 */
class BrahmsSpecimenTransformer extends AbstractCSVTransformer<BrahmsCsvField, ESSpecimen> {

	private static final SpecimenTypeStatusNormalizer typeStatusNormalizer;
	private static final ThemeCache themeCache;
	private static final String UNIT_ID_REGEX = "([a-zA-Z0-9_1.-]){3,}";
	private static final Pattern unitIDPattern;

	static {
		typeStatusNormalizer = SpecimenTypeStatusNormalizer.getInstance();
		themeCache = ThemeCache.getInstance();
		unitIDPattern = Pattern.compile(UNIT_ID_REGEX);
	}

	public BrahmsSpecimenTransformer(ETLStatistics stats)
	{
		super(stats);
		suppressErrors = ConfigObject.isEnabled("brahms.suppress-errors");
	}

	@Override
	protected String getObjectID()
	{
		return input.get(BARCODE);
	}

	@Override
	protected List<ESSpecimen> doTransform()
	{
		// No record-level validations, so:
		stats.recordsAccepted++;
		stats.objectsProcessed++;
		try {
			ESSpecimen specimen = new ESSpecimen();
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
			specimen.setTypeStatus(getTypeStatus());
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
		return ESUtil.getElasticsearchId(SourceSystem.BRAHMS, f.intValue());
	}

	private SpecimenTypeStatus getTypeStatus()
	{
		return typeStatusNormalizer.getEnumConstant(input.get(TYPE));
	}

}
