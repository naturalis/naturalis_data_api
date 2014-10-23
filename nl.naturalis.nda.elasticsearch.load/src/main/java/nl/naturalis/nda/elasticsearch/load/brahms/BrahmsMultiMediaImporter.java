package nl.naturalis.nda.elasticsearch.load.brahms;

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
import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import static nl.naturalis.nda.elasticsearch.load.NDASchemaManager.*;

import org.apache.commons.csv.CSVRecord;
import org.domainobject.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrahmsMultiMediaImporter extends CSVImporter<ESMultiMediaObject> {

	public static void main(String[] args) throws Exception
	{
		
		logger.info("-----------------------------------------------------------------");
		logger.info("-----------------------------------------------------------------");
		
		String rebuild = System.getProperty("rebuild", "false");
		IndexNative index = new IndexNative(LoadUtil.getDefaultClient(), DEFAULT_NDA_INDEX_NAME);
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

	static final Logger logger = LoggerFactory.getLogger(BrahmsMultiMediaImporter.class);
	static final String ID_PREFIX = "BRAHMS-";

	private final boolean rename;

	public BrahmsMultiMediaImporter(IndexNative index)
	{
		super(index, LUCENE_TYPE_MULTIMEDIA_OBJECT);
		this.delimiter = ',';
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
		String csvDir = System.getProperty("csvDir");
		if (csvDir == null) {
			throw new Exception("Missing -DcsvDir argument");
		}
		File file = new File(csvDir);
		if (!file.isDirectory()) {
			throw new Exception(String.format("No such directory: \"%s\"", csvDir));
		}
		File[] xmlFiles = file.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name)
			{
				return name.toLowerCase().endsWith(".csv");
			}
		});
		if (xmlFiles.length == 0) {
			logger.info("No CSV files to process");
			return;
		}
		for (File f : xmlFiles) {
			importCsv(f.getCanonicalPath());
			if (rename) {
				String now = new SimpleDateFormat("yyyyMMdd").format(new Date());
				f.renameTo(new File(f.getCanonicalPath() + "." + now + ".bak"));
			}
		}
	}

	@Override
	protected List<ESMultiMediaObject> transfer(CSVRecord record) throws Exception
	{
		List<ESMultiMediaObject> mmos = new ArrayList<ESMultiMediaObject>(4);
		String s = val(record, IMAGELIST.ordinal());
		if (s != null) {
			String[] urls = s.split(",");
			for (int i = 0; i < urls.length; ++i) {
				mmos.add(transferOne(record, i, urls[i]));
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


	private static ESMultiMediaObject transferOne(CSVRecord record, int imageNo, String imageUrl) throws Exception
	{
		String s = val(record, BARCODE.ordinal());
		if (s == null) {
			throw new Exception("Missing barcode");
		}
		ESMultiMediaObject mmo = new ESMultiMediaObject();
		mmo.setSourceSystemId(s + "_" + imageNo);
		mmo.setSourceSystem(SourceSystem.BRAHMS);
		mmo.setAssociatedSpecimenReference(s);
		mmo.setDescription(val(record, PLANTDESC.ordinal()));
		mmo.setGatheringEvents(Arrays.asList(BrahmsSpecimensImporter.getGatheringEvent(record)));
		mmo.setIdentifications(Arrays.asList(getIdentification(record)));
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
		identification.setDateIdentified(BrahmsSpecimensImporter.getDate(y, m, d));
		ScientificName sn = BrahmsSpecimensImporter.getScientificName(record);
		DefaultClassification dc = BrahmsSpecimensImporter.getDefaultClassification(record, sn);
		identification.setScientificName(sn);
		identification.setDefaultClassification(dc);
		identification.setSystemClassification(BrahmsSpecimensImporter.getSystemClassification(dc));
		return identification;
	}



}
