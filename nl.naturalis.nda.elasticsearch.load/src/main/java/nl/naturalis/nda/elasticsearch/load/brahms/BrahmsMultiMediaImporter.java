package nl.naturalis.nda.elasticsearch.load.brahms;

import static nl.naturalis.nda.elasticsearch.load.LoadConstants.LICENCE;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.LICENCE_TYPE;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.SOURCE_INSTITUTION_ID;
import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_MULTIMEDIA_OBJECT;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsImportUtil.getCsvFiles;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsImportUtil.getDate;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsImportUtil.getDefaultClassification;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsImportUtil.getScientificName;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsImportUtil.getSystemClassification;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.checkSpData;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.getGatheringEvent;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.CsvField.BARCODE;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.CsvField.DAYIDENT;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.CsvField.IMAGELIST;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.CsvField.MONTHIDENT;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.CsvField.PLANTDESC;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.CsvField.VERNACULAR;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.CsvField.YEARIDENT;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.naturalis.nda.domain.DefaultClassification;
import nl.naturalis.nda.domain.MultiMediaContentIdentification;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.ServiceAccessPoint;
import nl.naturalis.nda.domain.ServiceAccessPoint.Variant;
import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.domain.VernacularName;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;
import nl.naturalis.nda.elasticsearch.load.CSVImporter;
import nl.naturalis.nda.elasticsearch.load.DocumentType;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import nl.naturalis.nda.elasticsearch.load.ThematicSearchConfig;
import nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.CsvField;
import nl.naturalis.nda.elasticsearch.load.normalize.SpecimenTypeStatusNormalizer;

import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrahmsMultiMediaImporter extends CSVImporter<ESMultiMediaObject> {

	public static void main(String[] args) throws Exception
	{
		logger.info("-----------------------------------------------------------------");
		logger.info("-----------------------------------------------------------------");
		IndexNative index = null;
		try {
			index = new IndexNative(LoadUtil.getESClient(), LoadUtil.getConfig().required("elasticsearch.index.name"));
			BrahmsMultiMediaImporter importer = new BrahmsMultiMediaImporter(index);
			importer.importCsvFiles();
		}
		finally {
			if (index != null) {
				index.getClient().close();
			}
		}
	}

	private static final SpecimenTypeStatusNormalizer typeStatusNormalizer = SpecimenTypeStatusNormalizer.getInstance();
	private static final Logger logger = LoggerFactory.getLogger(BrahmsMultiMediaImporter.class);
	private static final String ID_PREFIX = "BRAHMS-";


	public BrahmsMultiMediaImporter(IndexNative index)
	{
		super(index, LUCENE_TYPE_MULTIMEDIA_OBJECT);
		this.delimiter = ',';
		this.charset = Charset.forName("Windows-1252");
		//this.suppressErrors = true;
		setSpecifyId(true);
		setSpecifyParent(false);
		String prop = System.getProperty(BrahmsImportAll.SYSPROP_BATCHSIZE, "1000");
		setBulkRequestSize(Integer.parseInt(prop));
		prop = System.getProperty(BrahmsImportAll.SYSPROP_MAXRECORDS, "0");
		setMaxRecords(Integer.parseInt(prop));
	}


	public void importCsvFiles() throws Exception
	{
		ThematicSearchConfig.getInstance().resetMatchCounters();
		File[] csvFiles = getCsvFiles();
		if (csvFiles.length == 0) {
			logger.info("No new CSV files to import");
			return;
		}
		index.deleteWhere(LUCENE_TYPE_MULTIMEDIA_OBJECT, "sourceSystem.code", SourceSystem.BRAHMS.getCode());
		for (File f : csvFiles) {
			importCsv(f.getCanonicalPath());
		}
		ThematicSearchConfig.getInstance().logMatchInfo();

	}

	private ArrayList<String> multimediaIds;


	@Override
	protected List<ESMultiMediaObject> transfer(CSVRecord record, String csvRecord, int lineNo) throws Exception
	{
		List<ESMultiMediaObject> mmos = new ArrayList<ESMultiMediaObject>(4);
		multimediaIds = new ArrayList<String>(4);
		String s = val(record, IMAGELIST.ordinal());
		if (s != null) {
			String[] urls = s.split(",");
			for (int i = 0; i < urls.length; ++i) {
				ESMultiMediaObject mmo = transferOne(record, urls[i], lineNo);
				if (mmo != null) {
					mmos.add(mmo);
				}
			}
		}
		return mmos;
	}


	@Override
	protected List<String> getIds(CSVRecord record)
	{
		ArrayList<String> ids = new ArrayList<String>(multimediaIds);
		for (int i = 0; i < ids.size(); ++i) {
			ids.set(i, ID_PREFIX + ids.get(i));
		}
		// String base = ID_PREFIX + val(record, BARCODE.ordinal());
		// List<String> ids = new ArrayList<String>(4);
		// String s = val(record, IMAGELIST.ordinal());
		// if (s != null) {
		// String[] urls = s.split(",");
		// for (int i = 0; i < urls.length; ++i) {
		// ids.add(base + "_" + i);
		// }
		// }
		return ids;
	}


	private ESMultiMediaObject transferOne(CSVRecord record, String imageUrl, int lineNo) throws Exception
	{
		String specimenUnitId = val(record, BARCODE.ordinal());
		if (specimenUnitId == null) {
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Error at line %s: missing barcode", lineNo));
			}
			return null;
		}
		try {
			checkSpData(record);
		}
		catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Error at line %s: %s", lineNo, e.getMessage()));
			}
			return null;
		}
		ESMultiMediaObject mmo = new ESMultiMediaObject();
		mmo.setUnitID(specimenUnitId + ':' + String.valueOf(imageUrl.hashCode()).replace('-', '0'));
		multimediaIds.add(mmo.getUnitID());
		mmo.setSourceSystemId(mmo.getUnitID());
		mmo.setSourceSystem(SourceSystem.BRAHMS);
		mmo.setSourceInstitutionID(SOURCE_INSTITUTION_ID);
		mmo.setOwner(SOURCE_INSTITUTION_ID);
		mmo.setSourceID("Brahms");
		mmo.setLicenceType(LICENCE_TYPE);
		mmo.setLicence(LICENCE);
		mmo.setAssociatedSpecimenReference(specimenUnitId);

		ThematicSearchConfig tsc = ThematicSearchConfig.getInstance();
		List<String> themes = tsc.getThemesForDocument(specimenUnitId, DocumentType.MULTI_MEDIA_OBJECT, SourceSystem.BRAHMS);
		mmo.setTheme(themes);

		mmo.setDescription(val(record, PLANTDESC.ordinal()));
		mmo.setGatheringEvents(Arrays.asList(getGatheringEvent(record)));
		mmo.setIdentifications(Arrays.asList(getIdentification(record)));
		mmo.setSpecimenTypeStatus(typeStatusNormalizer.getNormalizedValue(val(record, CsvField.TYPE.ordinal())));
		try {
			URI uri = new URI(imageUrl.trim());
			mmo.addServiceAccessPoint(new ServiceAccessPoint(uri, null, Variant.MEDIUM_QUALITY));
		}
		catch (URISyntaxException e) {
			throw new Exception(String.format("Invalid URL: \"%s\"", imageUrl));
		}

		return mmo;
	}


	private static MultiMediaContentIdentification getIdentification(CSVRecord record)
	{
		final MultiMediaContentIdentification identification = new MultiMediaContentIdentification();
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

}
