package nl.naturalis.nda.elasticsearch.load.brahms;

import static nl.naturalis.nda.domain.SourceSystem.BRAHMS;
import static nl.naturalis.nda.elasticsearch.load.CSVImportUtil.val;
import static nl.naturalis.nda.elasticsearch.load.DocumentType.MULTI_MEDIA_OBJECT;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.LICENCE;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.LICENCE_TYPE;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.SOURCE_INSTITUTION_ID;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsImportUtil.getDate;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsImportUtil.getDefaultClassification;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsImportUtil.getScientificName;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsImportUtil.getSystemClassification;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.getGatheringEvent;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.CsvField.BARCODE;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.CsvField.DAYIDENT;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.CsvField.IMAGELIST;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.CsvField.MONTHIDENT;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.CsvField.PLANTDESC;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.CsvField.TYPE;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.CsvField.VERNACULAR;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.CsvField.YEARIDENT;
import static org.domainobject.util.StringUtil.lpad;
import static org.domainobject.util.StringUtil.rpad;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.naturalis.nda.domain.DefaultClassification;
import nl.naturalis.nda.domain.MultiMediaContentIdentification;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.ServiceAccessPoint;
import nl.naturalis.nda.domain.ServiceAccessPoint.Variant;
import nl.naturalis.nda.domain.VernacularName;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;
import nl.naturalis.nda.elasticsearch.load.CSVRecordInfo;
import nl.naturalis.nda.elasticsearch.load.CSVTransformer;
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
class BrahmsMultiMediaTransformer implements CSVTransformer<ESMultiMediaObject> {

	private static final SpecimenTypeStatusNormalizer typeStatusNormalizer = SpecimenTypeStatusNormalizer.getInstance();
	private static final Logger logger = Registry.getInstance().getLogger(BrahmsMultiMediaTransformer.class);

	private final ThemeCache themeCache;
	private final boolean suppressErrors;

	private String specimenID;
	private int lineNo;

	public BrahmsMultiMediaTransformer()
	{
		themeCache = ThemeCache.getInstance();
		suppressErrors = ConfigObject.TRUE("brahms.suppress-errors");
	}

	@Override
	public List<ESMultiMediaObject> transform(CSVRecordInfo info)
	{
		specimenID = val(info.getRecord(), BARCODE.ordinal());
		if (specimenID == null) {
			error("Missing barcode");
			return null;
		}
		lineNo = info.getLineNumber();
		ArrayList<ESMultiMediaObject> result = new ArrayList<>(4);
		String s = val(info.getRecord(), IMAGELIST.ordinal());
		if (s != null) {
			String[] urls = s.split(",");
			for (int i = 0; i < urls.length; ++i) {
				String url = urls[i].trim();
				if (url.charAt(1) == ':') {
					/*
					 * This is a local file system path like Q:\foo.jpg. Skip
					 * expensive URI parsing.
					 */
					error("Invalid image URL: " + url);
					continue;
				}
				URI uri;
				try {
					uri = new URI(url);
				}
				catch (URISyntaxException e) {
					error("Invalid image URL: " + url);
					continue;
				}
				ESMultiMediaObject mmo = transferOne(info, uri);
				if (mmo != null) {
					result.add(mmo);
				}
			}
		}
		return result;
	}

	private ESMultiMediaObject transferOne(CSVRecordInfo info, URI uri)
	{
		CSVRecord record = info.getRecord();
		ESMultiMediaObject mmo = new ESMultiMediaObject();
		String uriHash = String.valueOf(uri.toString().hashCode()).replace('-', '0');
		mmo.setUnitID(specimenID + '_' + uriHash);
		mmo.setSourceSystemId(mmo.getUnitID());
		mmo.setSourceSystem(BRAHMS);
		mmo.setSourceInstitutionID(SOURCE_INSTITUTION_ID);
		mmo.setOwner(SOURCE_INSTITUTION_ID);
		mmo.setSourceID("Brahms");
		mmo.setLicenceType(LICENCE_TYPE);
		mmo.setLicence(LICENCE);
		mmo.setCollectionType("Botany");
		mmo.setAssociatedSpecimenReference(specimenID);
		List<String> themes = themeCache.getThemesForDocument(specimenID, MULTI_MEDIA_OBJECT, BRAHMS);
		mmo.setTheme(themes);
		mmo.setDescription(val(record, PLANTDESC.ordinal()));
		mmo.setGatheringEvents(Arrays.asList(getGatheringEvent(record)));
		mmo.setIdentifications(Arrays.asList(getIdentification(record)));
		mmo.setSpecimenTypeStatus(typeStatusNormalizer.getNormalizedValue(val(record, TYPE.ordinal())));
		mmo.addServiceAccessPoint(newServiceAccessPoint(uri));
		return mmo;
	}

	private static MultiMediaContentIdentification getIdentification(CSVRecord record)
	{
		MultiMediaContentIdentification identification = new MultiMediaContentIdentification();
		String s = val(record, VERNACULAR.ordinal());
		if (s != null) {
			identification.setVernacularNames(Arrays.asList(new VernacularName(s)));
		}
		String y = val(record, YEARIDENT.ordinal());
		String m = val(record, MONTHIDENT.ordinal());
		String d = val(record, DAYIDENT.ordinal());
		identification.setDateIdentified(getDate(y, m, d));
		ScientificName sn = getScientificName(record);
		DefaultClassification dc = getDefaultClassification(record, sn);
		identification.setScientificName(sn);
		identification.setDefaultClassification(dc);
		identification.setSystemClassification(getSystemClassification(dc));
		return identification;
	}

	private static ServiceAccessPoint newServiceAccessPoint(URI uri)
	{
		return new ServiceAccessPoint(uri, "image/jpeg", Variant.MEDIUM_QUALITY);
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

}
