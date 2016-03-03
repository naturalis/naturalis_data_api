package nl.naturalis.nda.export.dwca;

import static nl.naturalis.nda.export.dwca.ExportDwCAUtilities.getFullPath;
import static nl.naturalis.nda.export.dwca.ExportDwCAUtilities.getProperty;
import static nl.naturalis.nda.export.dwca.ExportDwCAUtilities.newFile;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.export.ExportUtil;

import org.domainobject.util.ConfigObject;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * <h1>DwCAExporter </h1>
 *  Description: Create a new DwCA export tool for the NBA
 *  Query the data from JSON Docs and export data values to CSV.
 *  <p>
 *  
 *  @version	1.0
 *  @since   	January 28th 2015
 *  @author 	Reinier Kartowikromo
 *  
 */
public class DwCAExporter {

	static final Logger logger = LoggerFactory.getLogger(DwCAExporter.class);
	public static final String CSVComma = "\t";
	
	private static String indexname = null;
	private static final String csvOutPutFile = "occurrence.txt";
	private static final String FILE_NAME_META = "meta.xml";
	private static File FILE_NAME_EML = null;
	private static final String dwcUrlTdwgOrg = "http://rs.tdwg.org/dwc/terms/";
	private static final String dwcTargetName = "http://rs.tdwg.org/dwc/text/";
	private static final String zipExtension = ".zip";
	private static String MAPPING_FILE_NAME = null;
	
	private static String sourceSystemCode = null;
	private static String propertyName = null;
	private static String propertyValue = null;
	private static String result = null;
	private static String nameCollectiontypeCrs = null;
	private static String nameCollectiontypeBrahms = null;
	
	private static File destinationPathEml = null;
	private static String nameCollectiontypeAnd = null;
	private static String collectionName = null;

	
	private static Client elasticClient = null;
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static CsvFileWriter filewriter = null;

	private static File emlDirectory = null;
	private static File outputDirectory = null;
	private static File zipOutputDirectory = null;
	private static File zipArchiveDirectory = null;
	
	CsvFileWriter.CsvRow headerRow = null;
	List<ESSpecimen> list;
	

	/**
	 * @param args no arguments
	 * @throws Exception Elasticsearch is not running
	 * Get the directories path of the EML, Meta, OutPut, Zip and Backup files
	 * Execute the Method DwCAExport with the params "ColectionName and TotalSize of Shards"
	 */
	public static void main(String[] args) throws Exception {

		
		logger.info("-----------------------------------------------------------------");
		logger.info("Start");
		
		String totalsize = System.getProperty(
				"nl.naturalis.nda.export.dwca.batchsize", "1000");

		emlDirectory = ExportDwCAUtilities.getCollectionConfigDir();
		logger.info("EML Directory: " + emlDirectory);
		/* Output directory for the files EML.xml, Meta.xml and Occurrence.txt */
		outputDirectory = ExportDwCAUtilities.getWorkingDirectory();
		logger.info("Output Directory: " + outputDirectory);
		zipOutputDirectory = ExportDwCAUtilities.getZipOutputDirectory();
		logger.info("Zip Output Directory: " + zipOutputDirectory);
		zipArchiveDirectory = ExportDwCAUtilities.getBackupDirectory();
		logger.info("Zip Archive Directory: " + zipArchiveDirectory);

		indexname = ExportUtil.getConfig().required("elasticsearch.index.name");
		logger.info("IndexName: " + indexname);

		collectionName = args.length > 0 ? args[0] : null;
		logger.info("Preparing CollectioName: " + collectionName);

		if (collectionName != null) 
		{
			executeDwCaExport(collectionName, totalsize);
		} 
		else 
		{
			StringBuilder builder = new StringBuilder();
			final String extensions = ".properties";
			FilenameFilter filter = new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(extensions);
				}
			};

			int cnt = 0;
			try {
				File configpath = ExportDwCAUtilities.getCollectionConfigDir();
				File[] listOfFiles = configpath.listFiles(filter);
				for (File file : listOfFiles) {
					cnt++;
					ConfigObject config = new ConfigObject(file);
					if (config.hasProperty("collectionName")) {
						// config.required("collectionName");
						collectionName = config.required("collectionName");
						builder.append(collectionName);
						builder.append("\n");

						if (cnt > 1) {
							logger.info(" ");
							logger.info("=================================");
							logger.info("Start with execution collectioname: "
									+ collectionName);
						}
						executeDwCaExport(collectionName, totalsize);
					}
				}
				logger.info("All collection successful executed: "
						+ builder.toString());
				builder.setLength(0);
				builder.trimToSize();
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}

		logger.info("Ready");
	}
	
	/**
     * Execute the Export with the collectionName "CRS" or "BRAHMS"
     * Set the ZipFileName
	 * @param collectionName
	 * @param ptotalSize
	 * @throws Exception
	 */
	private static void executeDwCaExport(String collectionName,
			String ptotalSize) throws Exception {
		/* Get the SourceSystem: CRS or BRAHMS, COL etc. */
		if (collectionName != null) {
			if (collectionName.contains("botany")) {
				collectionName = "brahms";
			}
			sourceSystemCode = getProperty(collectionName, "sourceSystemCode");
			/* Get the Occurrencefields value */
			MAPPING_FILE_NAME = getProperty(collectionName, "occurrenceFields");
		}

		/* args[2] Get the Collectiontype */
		try {
			if (sourceSystemCode.equals("CRS")) {
				nameCollectiontypeCrs = getProperty(collectionName,
						"collectionType");
			}

			if (sourceSystemCode.toUpperCase().equals("BRAHMS")) {
				collectionName = getProperty(collectionName, "collectionName");
				nameCollectiontypeBrahms = collectionName;
			}
		} catch (Exception ex) {
			logger.info(collectionName + " properties filename is not correct.");
		}

		/* Get the directory and zipfilename */
		String zipfilename = null;
		if (sourceSystemCode.toUpperCase().equals("CRS")
				|| sourceSystemCode.toUpperCase().equals("BRAHMS")) {
			zipfilename = newFile(zipOutputDirectory, collectionName)
					.getAbsolutePath();
			logger.info("Zip Filename: " + zipfilename);
		}

		DwCAExporter exp = new DwCAExporter(ExportUtil.getESClient(), indexname);
		/* Delete the CSV file if Exists */
		boolean success = newFile(outputDirectory, csvOutPutFile).delete();
		if (success) {
			logger.info("The file " + csvOutPutFile
					+ " has been successfully deleted");
		}
		if (sourceSystemCode.toUpperCase().equals("CRS")) {
			exp.exportDwca(zipfilename, nameCollectiontypeCrs, ptotalSize);
			logger.info("Execute "+ nameCollectiontypeCrs +" export for CRS");
		}
		if (sourceSystemCode.toUpperCase().equals("BRAHMS")) {
			exp.exportDwca(zipfilename, nameCollectiontypeBrahms, ptotalSize);
			logger.info("Execute "+ nameCollectiontypeBrahms +" export for BRAHMS");
		}
	}

	/**
	 * Get the Eml File
	 * @param emlDir
	 * @param emlfilename
	 * @return
	 */
	private static String getEmlFileName(String emlDir, String emlfilename) {
		String filename = null;
		File[] filelist = FindFile.getFileList(emlDir);
		if (filelist != null) {
			for (File file : filelist) {
				filename = file.getName();
				String eml = "";
				if (filename.contains("_eml")) {
					int index = filename.indexOf("_eml");
					eml = filename.substring(0, index);
				}
				if (eml.toLowerCase().contains(emlfilename.toLowerCase())
						&& eml.equalsIgnoreCase(emlfilename)) {
					result = file.getName();
					break;
				}
			}
		} else {
			logger.info("Eml files not found '" + emlDir + "'");
		}
		logger.info("EML file: " + result);
		return result;
	}

	/**
	 * Get the ElasticClient and IndexName
	 * @param client Set client
	 * @param indexname Set indexname
	 */
	public DwCAExporter(Client client, String indexname) {
		DwCAExporter.elasticClient = client;
		DwCAExporter.indexname = indexname;
	}

	/**
	 * Params ZipFilename, NameColletiontype and Size
	 * Creating the output files Meta.xml and occurrence.txt and set the location for the files
	 * Get the EML file in the source map and rename the EML file to Eml.xml 
	 * @param zipFileName Set the filename
	 * @param namecollectiontype Set the collectionname
	 * @param totalsize the total of shards
	 * @throws Exception Export Failure
	 */
	public void exportDwca(String zipFileName, String namecollectiontype,
			String totalsize) throws Exception {
		logger.info("Writing the CSV header and data into occurrence.txt file");
		printHeaderRowAndDataForCSV(namecollectiontype, totalsize);

		logger.info("Creating the Meta.xml file.");
		Files files = new Files();
		files.setLocation("occurrence.txt");
		Id id = new Id();
		id.setIndex(0);

		Core cores = new Core();
		cores.setEncoding("UTF-8");
		cores.setFieldsEnclosedBy("");
		cores.setFieldsTerminatedBy("\t");
		cores.setLinesTerminatedBy("\n");
		cores.setIgnoreHeaderLines("1");
		cores.setRowtype("http://rs.tdwg.org/dwc/terms/Occurrence");
		cores.setFiles(files);
		cores.setId(id);

		/* Create field index, term Atrribute */
		Integer cnt = new Integer(0);

		/* Get the fields properties */
		Properties configFile = ExportDwCAUtilities.getCollectionConfiguration(
				MAPPING_FILE_NAME).getProperties();
		/* Sort the value from the properties file when loaded */
		SortedMap<Object, Object> sortedSystemProperties = new TreeMap<>(configFile);
		Set<?> keySet = sortedSystemProperties.keySet();
		Iterator<?> iterator = keySet.iterator();
		String resultvalue = null;
		logger.info("Adding the fields element into the Meta.xml file.");
		while (iterator.hasNext()) {
			propertyName = (String) iterator.next();
			propertyValue = configFile.getProperty(propertyName);
			/* Add the headers to the CSV File */
			String[] chunks = propertyValue.split(",");
			if (chunks[1].equals("1")) {
				int index = propertyValue.indexOf(",");
				if (!propertyValue.equalsIgnoreCase("id,1")) {
					resultvalue = propertyValue.substring(0, index);
				}
				if (resultvalue != null) {
					cnt = Integer.valueOf(cnt.intValue() + 1);
					Field field = new Field(cnt.toString(), dwcUrlTdwgOrg
							+ resultvalue);
					cores.addField(field);
				}
			}
		}
		/* Clear the map and keySet */
		sortedSystemProperties.clear();
		keySet.clear();

		/* Create Meta.xml file for NBA */
		Meta xmlspecimen = new Meta();
		xmlspecimen.setMetadata("eml.xml");
		xmlspecimen.setXmlnsxsi("http://www.w3.org/2001/XMLSchema-instance");
		xmlspecimen.setXmlnstdwg(dwcTargetName);
		xmlspecimen.add(cores);
		dwcaObjectToXML(xmlspecimen);

		String emlfilefromdir = null;
		if (sourceSystemCode.equals("CRS")) {
			/* Get the EML Filename */
			emlfilefromdir = getEmlFileName(emlDirectory.getAbsolutePath(),
					collectionName);
			logger.info("Reading the file from: '"
					+ getFullPath(emlDirectory, emlfilefromdir));
			/* Directory Source EML File */
			FILE_NAME_EML = ExportDwCAUtilities.newFile(emlDirectory,
					emlfilefromdir);
			/* Destination directory for eml file */
			destinationPathEml = ExportDwCAUtilities.newFile(outputDirectory,
					emlfilefromdir);
			logger.info("Copy the file to: '"
					+ getFullPath(outputDirectory, emlfilefromdir));
		}
		if (sourceSystemCode.toUpperCase().equals("BRAHMS")) {
			nameCollectiontypeBrahms = sourceSystemCode;
			/* Get the EML Filename */
			emlfilefromdir = getEmlFileName(emlDirectory.getAbsolutePath(),
					MAPPING_FILE_NAME.toLowerCase());
			logger.info("Reading the file from: '"
					+ getFullPath(emlDirectory, emlfilefromdir));
			/* Directory Source EML File */
			FILE_NAME_EML = ExportDwCAUtilities.newFile(emlDirectory,
					emlfilefromdir);
			/* Destination directory for eml file */
			destinationPathEml = newFile(outputDirectory, emlfilefromdir);
			logger.info("Copy the file to: '"
					+ getFullPath(outputDirectory, emlfilefromdir));
		}
		/* Copy the file from the source directory to the Destination directory */
		ExportDwCAUtilities.CopyAFile(FILE_NAME_EML, destinationPathEml);
		logger.info("Copy EML.xml file to the destinationpath successful.");
		/*
		 * Rename the (example: amphibia_and_reptilia_eml.xml) eml file to the
		 * exact name "eml.xml"
		 */
		ExportDwCAUtilities.renameDwCAEMLFile(destinationPathEml);
		logger.info("Rename "+ FILE_NAME_EML + " to EML.xml file successful.");
		File source = new File(zipFileName + zipExtension);
		File destination = null;
		if (sourceSystemCode.equals("CRS")) {
			destination = newFile(zipArchiveDirectory, collectionName
					+ zipExtension);
		}

		if (sourceSystemCode.toUpperCase().equals("BRAHMS")) {
			destination = newFile(zipArchiveDirectory, "botany" + zipExtension);
		}

		/* Backup the zipfile */
		ExportDwCAUtilities.CopyAFile(source, destination);
		
		/* Renamed the zip file into bak in de Archive map */
		ExportDwCAUtilities.renameDwCAZipFile(destination);
		
		logger.info("Backup the zipfiles successful created in directory: " + destination);	
		
		/* Create the zipfile with a given filename */
		createZipFiles(zipFileName);
	}

	/**
	 * Creating the Meta.xml
	 * @param meta
	 */
	private static void dwcaObjectToXML(Meta meta) {
		try {
			JAXBContext context = JAXBContext.newInstance(Meta.class);
			Marshaller m = context.createMarshaller();
			// for pretty-print XML in JAXB
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

			// Write to File
			m.marshal(meta, newFile(outputDirectory, FILE_NAME_META));
			logger.info("Saved '" + FILE_NAME_META + "' to '"
					+ outputDirectory.getAbsolutePath() + "'");
			m.marshal(meta, System.out);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

    /**
     * Creating the zip file
     * @param zipFileName Set Zip Filename
     */
	public static void createZipFiles(String zipFileName) {
		ZipDwCA zip = new ZipDwCA();
		try {
			logger.info("Creating the zipfile: '" + zipFileName + zipExtension
					+ "'");
			zip.zipDirectory(outputDirectory.getAbsolutePath(), zipFileName
					+ zipExtension);
			logger.info("Zipfile '" + zipFileName + zipExtension
					+ "' created successful.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Printing the records to a CSV file named: "Occurrence.txt" 
	 * @param namecollectiontype
	 * @param totalsize
	 */
	private void printHeaderRowAndDataForCSV(String namecollectiontype,
			String totalsize) {
		try {
			/* Create new CSV File object and output File */
			/* Zoology Occurrence */
			if (sourceSystemCode.equalsIgnoreCase("CRS")) {
				runCrsZoologyElasticsearch(namecollectiontype, totalsize);
				runCrsGeologyElasticsearch(namecollectiontype, totalsize);
			}

			/* BRAHMS Occurrence */
			if (sourceSystemCode.equalsIgnoreCase("BRAHMS")) {
				runBrahmsElasticsearch(namecollectiontype, totalsize);
			}
		} 
		catch (IOException e) 
		{
			logger.info("CSV RowHeader: " + e.getMessage());
			e.printStackTrace();
			return;
		} finally { /* Close the filewriter */
			if (filewriter != null) {
				try 
				{
					filewriter.close();
				} 
				catch (IOException e) 
				{
					logger.info("FileWriter: " + e.getMessage());
					e.printStackTrace();
					return;
				}
			}
		}
	}

	/**
	 * Writing the Header of the CSV file
	 * @throws IOException
	 */
	private void writeCSVHeader() throws IOException {
		/* Create new CSV File object and output File */
		filewriter = new CsvFileWriter(newFile(outputDirectory, csvOutPutFile));

		headerRow = filewriter.new CsvRow();

		Properties configFile = ExportDwCAUtilities.getCollectionConfiguration(
				MAPPING_FILE_NAME).getProperties();
		/* Sort the value from the properties file when loaded */
		SortedMap<Object, Object> sortedSystemProperties = new TreeMap<>(
				configFile);
		Set<?> keySet = sortedSystemProperties.keySet();
		Iterator<?> iterator = keySet.iterator();
		while (iterator.hasNext()) {
			propertyName = (String) iterator.next();
			propertyValue = configFile.getProperty(propertyName);
			/* Add the headers to the CSV File */
			String[] chunks = propertyValue.split(",");
			if (chunks[1].equals("1")) {
				headerRow.add(propertyValue.substring(0,
						propertyValue.length() - 2));
			}
		}
		/* Write the headers columns */
		logger.info("Writing headers row to the Occurrence.txt file.");
		filewriter.WriteRow(headerRow);
		logger.info("CSV Fieldsheader: " + headerRow.toString());
		/* Clear the Sorted map and KeySet */
		sortedSystemProperties.clear();
		keySet.clear();
	}

	/**
	 * Run the Zoology Elasticsearch query
	 * @param namecollectiontype
	 * @param totalsize
	 * @throws IOException
	 */
	private void runCrsZoologyElasticsearch(String namecollectiontype,
			String totalsize) throws IOException {
		/* Get the result from ElasticSearch */
		if (MAPPING_FILE_NAME.equalsIgnoreCase("Zoology")) {
			writeCSVHeader();
			/* Add the value from ElasticSearch to the CSV File */
			logger.info("Writing values from ElasticSearch to the '"
					+ namecollectiontype + "' '" + MAPPING_FILE_NAME
					+ "' Occurrence.txt file.");
			long lStartTime = new Date().getTime();
			getResultsList("Specimen", namecollectiontype, sourceSystemCode,
					Integer.parseInt(totalsize), ESSpecimen.class);
			long lEndTime = new Date().getTime();
			long difference = lEndTime - lStartTime;
			String hms = String.format("%02d:%02d:%02d",
					TimeUnit.MILLISECONDS.toHours(difference),
					TimeUnit.MILLISECONDS.toMinutes(difference)
							% TimeUnit.HOURS.toMinutes(1),
					TimeUnit.MILLISECONDS.toSeconds(difference)
							% TimeUnit.MINUTES.toSeconds(1));
			logger.info("CSV file written on : '" + hms
					+ " hour(s)/minute(s)/second(s).'");
			logger.info("CSV file written in : '"
					+ TimeUnit.MILLISECONDS.toMinutes(difference)
					+ " minutes.'");
		}
	}

	/**
	 * Run the Geology Elasticsearch query
	 * @param namecollectiontype
	 * @param totalsize
	 * @throws IOException
	 */
	private void runCrsGeologyElasticsearch(String namecollectiontype,
			String totalsize) throws IOException {
		if (MAPPING_FILE_NAME.equalsIgnoreCase("Geology")) {
			writeCSVHeader();
			logger.info("Writing values from ElasticSearch to the '"
					+ namecollectiontype + "' '" + MAPPING_FILE_NAME
					+ "' Occurrence.txt file.");
			long lStartGeoTime = new Date().getTime();
			getResultsList("Specimen", namecollectiontype, sourceSystemCode,
					Integer.parseInt(totalsize), ESSpecimen.class);
			long lEndGeoTime = new Date().getTime();
			long differenceGeo = lEndGeoTime - lStartGeoTime;
			String hms = String.format("%02d:%02d:%02d",
					TimeUnit.MILLISECONDS.toHours(differenceGeo),
					TimeUnit.MILLISECONDS.toMinutes(differenceGeo)
							% TimeUnit.HOURS.toMinutes(1),
					TimeUnit.MILLISECONDS.toSeconds(differenceGeo)
							% TimeUnit.MINUTES.toSeconds(1));
			logger.info("CSV file written on : '" + hms
					+ " hour(s)/minute(s)/second(s).'");
			logger.info("CSV file written in : '"
					+ TimeUnit.MILLISECONDS.toMinutes(differenceGeo)
					+ " minutes.'");
		}
	}

	/**
	 * Run the Brahms Elasticsearch query
	 * @param namecollectiontype
	 * @param totalsize
	 * @throws IOException
	 */
	private void runBrahmsElasticsearch(String namecollectiontype,
			String totalsize) throws IOException {
		if (sourceSystemCode.toUpperCase().equals("BRAHMS")) {
			writeCSVHeader();
			logger.info("Writing values from ElasticSearch to the '"
					+ namecollectiontype + "' '" + MAPPING_FILE_NAME
					+ "' Occurrence.txt file.");
			long lStartBrahmsTime = new Date().getTime();

			getResultsList("Specimen", null, sourceSystemCode,
					Integer.parseInt(totalsize), ESSpecimen.class);

			long lEndBrahmsTime = new Date().getTime();
			long differenceBrahms = lEndBrahmsTime - lStartBrahmsTime;
			String hms = String.format("%02d:%02d:%02d",
					TimeUnit.MILLISECONDS.toHours(differenceBrahms),
					TimeUnit.MILLISECONDS.toMinutes(differenceBrahms)
							% TimeUnit.HOURS.toMinutes(1),
					TimeUnit.MILLISECONDS.toSeconds(differenceBrahms)
							% TimeUnit.MINUTES.toSeconds(1));
			logger.info("CSV file written on : '" + hms
					+ " hour(s)/minute(s)/second(s).'");
			logger.info("CSV file written in : '"
					+ TimeUnit.MILLISECONDS.toMinutes(differenceBrahms)
					+ " minutes.'");
		}

	}

	/**
	 * Create CRS Zoology Csv file
	 * @param listZoology
	 * @param occurrenceFields
	 * @throws Exception
	 */
	private static void writeCRSZoologyCsvFile(List<ESSpecimen> listZoology,
			String occurrenceFields) throws Exception {
		if (MAPPING_FILE_NAME.equalsIgnoreCase(occurrenceFields)) {
			Zoology.addZoologyOccurrencefield(listZoology, filewriter,
					MAPPING_FILE_NAME);
		}
	}

	/**
	 * Create CRS Zoology Csv file.
	 * @param listGeology
	 * @param occurrenceFields
	 * @throws Exception
	 */
	private static void writeCRSGeologyCsvFile(List<ESSpecimen> listGeology,
			String occurrenceFields) throws Exception {
		if (MAPPING_FILE_NAME.equalsIgnoreCase(occurrenceFields)) {
			Geology.addGeologyOccurrencefield(listGeology, filewriter,
					MAPPING_FILE_NAME);
		}
	}

	/**
	 * Create Brahms CSV file
	 * @param listBrahms
	 * @param occurrenceFields
	 * @throws Exception
	 */
	private static void writeBrahmsCsvHeader(List<ESSpecimen> listBrahms,
			String occurrenceFields) throws Exception {
		/* BRAHMS Occurrence */
		if (MAPPING_FILE_NAME.equalsIgnoreCase(occurrenceFields)) {
			Brahms.addBrahmsOccurrencefield(listBrahms, filewriter,	MAPPING_FILE_NAME);
		}
	}

	/**
	 * 
	 * @return nameCollectiontypeAnd
	 */
	public static String getNameCollectiontypeAnd() {
		return nameCollectiontypeAnd;
	}

	/**
	 * 
	 * @param nameCollectiontypeAnd Set value nameCollectiontypeAnd
	 */
	public static void setNameCollectiontypeAnd(String nameCollectiontypeAnd) {
		DwCAExporter.nameCollectiontypeAnd = nameCollectiontypeAnd;
	}

	/**
	 * Execute the Elasticsearch query
	 * @param type The document types to execute the search against. Defaults to be executed against all types.
	 * @param namecollectiontype CollectionName what should be execute
	 * @param sourcesystemcode CRS or BRAHMS
	 * @param size Total of Shards
	 * @param targetClass Specimen
	 * @param <T> Specimen
	 */
	public static <T> void getResultsList(String type,
			String namecollectiontype, String sourcesystemcode, int size,
			Class<T> targetClass) {
		SearchRequestBuilder searchRequestBuilder = null;

		if (sourcesystemcode.toUpperCase().equals("CRS")) {
			logger.info("Querying the data for '" + sourcesystemcode + "'");

			FilteredQueryBuilder builder = null;
			String nameCollectionType1 = null;
			String nameCollectionType2 = null;

			if (namecollectiontype.contains(",")) {
				int index = namecollectiontype.indexOf(",");
				nameCollectionType1 = namecollectiontype.substring(0, index);
				nameCollectionType2 = namecollectiontype.substring(index + 2,
						namecollectiontype.length());

				builder = QueryBuilders.filteredQuery(
						QueryBuilders.boolQuery().must(
								QueryBuilders.matchQuery(
										"sourceSystem.code.raw",
										sourcesystemcode)), FilterBuilders
								.orFilter(FilterBuilders.termFilter(
										"collectionType.raw",
										nameCollectionType1), FilterBuilders
										.termFilter("collectionType.raw",
												nameCollectionType2)));
			} else {
				if (namecollectiontype.length() > 0) {
					builder = QueryBuilders.filteredQuery(
							QueryBuilders
									.boolQuery()
									.must(QueryBuilders.matchQuery(
											"sourceSystem.code.raw",
											sourcesystemcode))
									.must(QueryBuilders.matchQuery(
											"collectionType.raw",
											namecollectiontype)), null);
				}
			}

			if (builder != null) {
				logger.info(builder.toString());
			}

			if (size <= 0) {
				CountResponse res = elasticClient.prepareCount()
						.setQuery(builder).execute().actionGet();
				size = (int) res.getCount();
			}

			if (size > 0) {
				SortOrder order = SortOrder.ASC;
				searchRequestBuilder = elasticClient
						.prepareSearch()
						.setVersion(true)
						.setQuery(builder)
						.addSort(
								SortBuilders.fieldSort("sourceSystemId")
										.order(order).missing("_last"))
						.setSearchType(SearchType.SCAN).setExplain(true)
						.setTypes("best_fields")
						.setScroll(new TimeValue(60000)).setIndices(indexname)
						.setTypes(type).setSize(size);
			}
		}

		/* BRAHMS */
		SearchResponse response = null;
		if (sourcesystemcode.equalsIgnoreCase("Brahms")) {
			FilteredQueryBuilder brahmsBuilder = QueryBuilders.filteredQuery(
					QueryBuilders.boolQuery().must(
							QueryBuilders.matchQuery("sourceSystem.code.raw",
									sourcesystemcode.toUpperCase())), null);

			logger.info("Querying the data for BRAHMS");
			if (brahmsBuilder != null) {
				logger.info(brahmsBuilder.toString());
			}

			if (size <= 0) {
				CountResponse res = elasticClient.prepareCount()
						.setQuery(brahmsBuilder).execute().actionGet();
				size = (int) res.getCount();
			}

			if (size > 0) {
				SortOrder order = SortOrder.ASC;
				searchRequestBuilder = elasticClient
						.prepareSearch()
						.setVersion(true)
						.setQuery(brahmsBuilder)
						.addSort(
								SortBuilders.fieldSort("sourceSystemId")
										.order(order).missing("_last"))
						.setSearchType(SearchType.SCAN).setExplain(true)
						.setScroll(TimeValue.timeValueMinutes(60000))
						.setIndices(indexname).setTypes(type).setSize(size);

			}
		}

		if (searchRequestBuilder != null)
		{
			try
			{
				response = searchRequestBuilder.execute().actionGet();
			}
			catch(ElasticsearchException elx)
			{
				logger.info("Elasticsearch is not running.");
				logger.info("Elasticsearch: " + elx.getMessage());
				throw elx; //.printStackTrace();
			}

		logger.info("Status: " + response.status());

		logger.info("Scrollid:" + response.getScrollId());

		long totalHitCount = 0;
		totalHitCount = response.getHits().getTotalHits();
		logger.info(
				"[SearchByElasticsearch] [{}] record(s) found in scrolling SearchResponse",
				totalHitCount);

		/* Show response properties */
		String output = response.toString();
		logger.info(output);

		logger.info("Total records in occurrence file: " + totalHitCount);
		logger.info("Indexname '" + indexname + "'");
		logger.info("Start writing data to occurrence file.");
		
		int count = response.getSuccessfulShards();
		int restvalue = 0;
		int resultrecord = 0;
		int failure = 0;

		while (true) {
			try {
				ArrayList<ESSpecimen> list =  new ArrayList<>();

				for (SearchHit hit : response.getHits()) {
					T result = objectMapper.convertValue(hit.getSource(),
							targetClass);
					list.add((ESSpecimen) result);
				}

				response = elasticClient
						.prepareSearchScroll(response.getScrollId())
						.setScrollId(response.getScrollId())
						.setScroll(TimeValue.timeValueMinutes(60000)).execute()
						.actionGet();
				
				failure = response.getFailedShards();

				restvalue = (int) (totalHitCount / count);
				resultrecord = restvalue - resultrecord;
				logger.info("Shard '" + count++ + "'"
						+ " with '1000' records successful processed.");

				if (sourcesystemcode.equalsIgnoreCase("CRS")
						&& MAPPING_FILE_NAME.equalsIgnoreCase("Zoology")) {
					writeCRSZoologyCsvFile((list), "Zoology");
				}

				if (sourcesystemcode.equalsIgnoreCase("CRS")
						&& MAPPING_FILE_NAME.equalsIgnoreCase("Geology")) {
					writeCRSGeologyCsvFile(list, "Geology");
				}

				if (sourcesystemcode.equalsIgnoreCase("BRAHMS")) {
					writeBrahmsCsvHeader(list, "botany");
				}
				Requests.flushRequest(indexname);
				Requests.refreshRequest(indexname);

				// Break condition: No hits are returned
				if (response.getHits().getHits().length == 0) {
					logger.info("no more shard to hit.");
					logger.info("Finished writing data to occurrence file.");
					break;
				}
			} catch (Exception e) {
				if (failure > 0) {
					logger.info("There is a communication problem with Elasticsearch. " + e.getMessage());
				}
				else
				{
					e.printStackTrace();
					logger.info("Error: " + e.getMessage());
				}
			}
		}
	}
  }
}
