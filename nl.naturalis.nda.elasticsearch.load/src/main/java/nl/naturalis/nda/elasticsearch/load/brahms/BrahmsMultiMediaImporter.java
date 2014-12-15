package nl.naturalis.nda.elasticsearch.load.brahms;

import static nl.naturalis.nda.elasticsearch.load.LoadConstants.LICENCE;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.LICENCE_TYPE;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.SOURCE_INSTITUTION_ID;
import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.DEFAULT_NDA_INDEX_NAME;
import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_MULTIMEDIA_OBJECT;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.checkSpData;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.getDate;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.getDefaultClassification;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.getGatheringEvent;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.getScientificName;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.getSystemClassification;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.CsvField.BARCODE;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.CsvField.DAYIDENT;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.CsvField.IMAGELIST;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.CsvField.MONTHIDENT;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.CsvField.PLANTDESC;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.CsvField.VERNACULAR;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsSpecimensImporter.CsvField.YEARIDENT;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import org.domainobject.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrahmsMultiMediaImporter extends CSVImporter<ESMultiMediaObject> {

	public static void main(String[] args) throws Exception
	{

		logger.info("-----------------------------------------------------------------");
		logger.info("-----------------------------------------------------------------");

		IndexNative index = new IndexNative(LoadUtil.getESClient(), DEFAULT_NDA_INDEX_NAME);
		
		// Check thematic search is configured properly
		ThematicSearchConfig.getInstance();		
		
		String rebuild = System.getProperty("rebuild", "false");
		if (rebuild.equalsIgnoreCase("true") || rebuild.equals("1")) {
			index.deleteType(LUCENE_TYPE_MULTIMEDIA_OBJECT);
			String mapping = StringUtil.getResourceAsString("/es-mappings/MultiMediaObject.json");
			index.addType(LUCENE_TYPE_MULTIMEDIA_OBJECT, mapping);
		}
		else {
			index.deleteWhere(LUCENE_TYPE_MULTIMEDIA_OBJECT, "sourceSystem.code", SourceSystem.BRAHMS.getCode());
		}

		try {
			BrahmsMultiMediaImporter importer = new BrahmsMultiMediaImporter(index);
			importer.importCsvFiles();
		}
		finally {
			index.getClient().close();
		}
	}

	private static final SpecimenTypeStatusNormalizer typeStatusNormalizer = SpecimenTypeStatusNormalizer.getInstance();
	private static final Logger logger = LoggerFactory.getLogger(BrahmsMultiMediaImporter.class);
	private static final String ID_PREFIX = "BRAHMS-";

	private final boolean rename;


	public BrahmsMultiMediaImporter(IndexNative index)
	{
		super(index, LUCENE_TYPE_MULTIMEDIA_OBJECT);
		this.delimiter = ',';
		this.suppressErrors = true;
		setSpecifyId(true);
		setSpecifyParent(false);
		String prop = System.getProperty("bulkRequestSize", "1000");
		setBulkRequestSize(Integer.parseInt(prop));
		prop = System.getProperty("maxRecords", "0");
		setMaxRecords(Integer.parseInt(prop));
		prop = System.getProperty("rename", "false");
		rename = prop.equals("1") || prop.equalsIgnoreCase("true");
	}


	public void importCsvFiles() throws Exception
	{

		ThematicSearchConfig.getInstance().resetMatchCounters();
		
		BrahmsExportEncodingConverter.convertFiles();

		String csvDir = LoadUtil.getConfig().required("brahms.csv_dir");
		File file = new File(csvDir);
		if (!file.isDirectory()) {
			throw new Exception(String.format("No such directory: \"%s\"", csvDir));
		}
		File[] csvFiles = file.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name)
			{
				return name.toLowerCase().endsWith(".csv");
			}
		});
		if (csvFiles.length == 0) {
			logger.info("No CSV files to process");
			return;
		}
		for (File f : csvFiles) {
			importCsv(f.getCanonicalPath());
			if (rename) {
				String now = new SimpleDateFormat("yyyyMMdd").format(new Date());
				f.renameTo(new File(f.getCanonicalPath() + "." + now + ".bak"));
			}
		}
		
		ThematicSearchConfig.getInstance().logMatchInfo();
		
	}


	@Override
	protected List<ESMultiMediaObject> transfer(CSVRecord record, String csvRecord, int lineNo) throws Exception
	{
		List<ESMultiMediaObject> mmos = new ArrayList<ESMultiMediaObject>(4);
		String s = val(record, IMAGELIST.ordinal());
		if (s != null) {
			String[] urls = s.split(",");
			for (int i = 0; i < urls.length; ++i) {
				ESMultiMediaObject mmo = transferOne(record, i, urls[i], lineNo);
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
		String base = ID_PREFIX + val(record, BARCODE.ordinal());
		List<String> ids = new ArrayList<String>(4);
		String s = val(record, IMAGELIST.ordinal());
		if (s != null) {
			String[] urls = s.split(",");
			for (int i = 0; i < urls.length; ++i) {
				ids.add(base + "_" + i);
			}
		}
		return ids;
	}


	private static ESMultiMediaObject transferOne(CSVRecord record, int imageNo, String imageUrl, int lineNo) throws Exception
	{
		String specimenUnitId = val(record, BARCODE.ordinal());
		if (specimenUnitId == null) {
			logger.debug(String.format("Error at line %s: missing barcode", lineNo));
			return null;
		}
		try {
			checkSpData(record);
		}
		catch (Exception e) {
			logger.debug(String.format("Error at line %s: %s", lineNo, e.getMessage()));
			return null;
		}
		ESMultiMediaObject mmo = new ESMultiMediaObject();
		mmo.setUnitID(specimenUnitId + ":" + imageNo);
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
			URI uri = new URI(imageUrl);
			mmo.addServiceAccessPoint(new ServiceAccessPoint(uri, null, Variant.MEDIUM_QUALITY));
		}
		catch (URISyntaxException e) {
			throw new Exception("Invalid URL: " + imageUrl);
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
