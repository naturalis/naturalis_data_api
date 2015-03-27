/*
 *  Description: Create a new DwCA export tool for the NBA
 *  Date: January 28th 2015
 *  Developer: Reinier Kartowikromo
 *  
 */
package nl.naturalis.nda.export.dwca;

import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_SPECIMEN;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
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

import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;

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
import org.elasticsearch.search.sort.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;




/**
 * @author Reinier.Kartowikromo
 *
 */
public class DwCAExporter {
	static final Logger logger = LoggerFactory.getLogger(DwCAExporter.class);
	public static final String CSVComma = "\t";
	private static final String csvOutPutFile = "occurence.txt";
	private static final String FILE_NAME_META = "meta.xml";
	private static File FILE_NAME_EML = null;
	private static final String dwcUrlTdwgOrg = "http://rs.tdwg.org/dwc/terms/";
	private static final String zipExtension = ".zip";
	private static final String propertiesExtension = ".properties";
	private static String MAPPING_FILE_NAME = null;
	CsvFileWriter.CsvRow headerRow = null;
	private static String outputDirectory = null;
	private static String sourceSystemCode = null;
	private static String zipOutputDirectory = null;
	private static String propertyName = null;
	private static String propertyValue = null;
	private static String result = null;
	private static String nameCollectiontypeCrs = null;
	private static String nameCollectiontypeBrahms = null;
	List<ESSpecimen> list;
	private static String zipArchiveDirectory = null;
	private static String emlDirectory = null;
	private static File destinationPathEml = null;
	private static String nameCollectiontypeAnd = null;
	private static String collectionName = null;
    static IndexNative index = null;
    static String indexname = LoadUtil.getConfig().required("elasticsearch.index.name").toString();
	private static Client eslasticClient = null;
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static CsvFileWriter filewriter = null;


	/**
	 * @param args
	 * @throws Exception
	 */

	public static void main(String[] args) throws Exception {

		logger.info("-----------------------------------------------------------------");
		logger.info("Start");

		/* Create NBA Directory */
		StringUtilities.createOutPutDirectory();
		StringUtilities.createZipOutPutDirectory();
		StringUtilities.createArchiveZipDirectory();

		/* Get the arguments: "OutPut", "Size", "Mammalia" */
		logger.info("Start reading properties value from OutPut.properties");
		/* args[1] get the total records from ElasticSearch */
		String totalsize = StringUtilities.readPropertyvalue(args[0], args[1]);
		emlDirectory = StringUtilities.readPropertyvalue(args[0], "EMLDirectory");

		/* Output directory for the files EML.xml, Meta.xml and Ocurrence.txt */
		outputDirectory = StringUtilities.readPropertyvalue(args[0], "Directory") + "\\";

		collectionName = StringUtilities.readPropertyvalue(args[0],	"Collectionname");

		/* Get the SourceSystem: CRS or BRAHMS, COL etc. */
		if (collectionName != null) 
		{
			sourceSystemCode = StringUtilities.readPropertyvalue(collectionName, "sourceSystemCode");
			/* Get the Ocurrencefields value */
			MAPPING_FILE_NAME = StringUtilities.readPropertyvalue(collectionName, "occurrenceFields");
		}

		/* args[2] Get the Collectiontype */
		try {
			if (sourceSystemCode.equals("CRS")) {
				nameCollectiontypeCrs = StringUtilities.readPropertyvalue(collectionName, "collectionType");
			}
			
			if (sourceSystemCode.toUpperCase().equals("BRAHMS")) 
			{
				collectionName = StringUtilities.readPropertyvalue(collectionName, "collectionName");
				nameCollectiontypeBrahms = collectionName;
			}

		} catch (Exception ex) {
			logger.info(collectionName + " properties filename is not correct.");
		}

		/* Directoy where zipfile will be created */
		zipOutputDirectory = StringUtilities.readPropertyvalue(args[0],	"ZipDirectory") + "\\";
		/* Copy the DwCAZip file to the DwCAZipArchive directory. */
		zipArchiveDirectory = StringUtilities.readPropertyvalue(args[0], "ZipArchiveDirectory") + "\\";

		/* Get the directory and zipfilename */
		String zipfilename = null;
		if (sourceSystemCode.toUpperCase().equals("CRS") || sourceSystemCode.toUpperCase().equals("BRAHMS")) 
		{
			zipfilename = zipOutputDirectory + collectionName;
		}

		logger.info("Loading Elastcisearch");
		index = new IndexNative(LoadUtil.getESClient(), LoadUtil.getConfig().required("elasticsearch.index.name"));
		try 
		{
			DwCAExporter exp = new DwCAExporter(LoadUtil.getESClient(), index,	indexname);
			/* Delete the CSV file if Exists */
			boolean success = (new File(outputDirectory + csvOutPutFile)).delete();
			if (success) {
				logger.info("The file " + csvOutPutFile	+ " has been successfully deleted");
			}
			if (sourceSystemCode.toUpperCase().equals("CRS")) 
			{
				exp.ExportDwca(zipfilename, nameCollectiontypeCrs, totalsize);
			}
			if (sourceSystemCode.toUpperCase().equals("BRAHMS")) 
			{
				exp.ExportDwca(zipfilename, nameCollectiontypeBrahms, totalsize);
			}
		} finally {
			index.getClient().close();
		}

		logger.info("Ready");
	}

	private static String GetEmlFileName(String emlDir, String emlfilename) {
		String filename = null;
		File[] filelist = FindFile.getFileList(emlDir);
		if (filelist != null) 
		{
			for (File file : filelist) {
				filename = file.getName();
				String eml = null;
				if (filename.contains("_eml")) {
					int index = filename.indexOf("_eml");
					eml = filename.substring(0, index);
				}
				if (eml.toLowerCase().contains(emlfilename.toLowerCase()) && eml.equalsIgnoreCase(emlfilename)) {
					result = file.getName();
					break;
				}
			}
		}
		else
		{
			logger.info("Eml files not found '" + emlDir + "'");
		}
		return result;
	}

	public DwCAExporter(Client client, IndexNative index, String indexname) {
		DwCAExporter.index = index;
		DwCAExporter.eslasticClient = client;
		DwCAExporter.indexname = indexname;
		
	}

	

	public void ExportDwca(String zipFileName, String namecollectiontype,
			String totalsize) throws Exception 
	{
		printHeaderRowAndDataForCSV(namecollectiontype, totalsize);

		logger.info("Creating the Meta.xml file.");
		Files files = new Files();
		files.setLocation("occurence.txt");
		Id id = new Id();
		id.setIndex(0);

		Core cores = new Core();
		cores.setEncoding("UTF-8");
		cores.setFieldsEnclosedBy("'");
		cores.setFieldsTerminatedBy("\t");
		cores.setLinesTerminatedBy("\n");
		cores.setIgnoreHeaderLines("0");
		cores.setRowtype("http://rs.tdwg.org/dwc/terms/Occurrence");
		cores.setFiles(files);
		cores.setId(id);

		/* Create field index, term Atrribute */
		Integer cnt = new Integer(0);
		Iterator<String> fieldIter = headerRow.iterator();
		while (fieldIter.hasNext()) 
		{
			cnt = Integer.valueOf(cnt.intValue() + 1);
			Field field = new Field(cnt.toString(), dwcUrlTdwgOrg	+ fieldIter.next());
			cores.addField(field);
		}

		/* Create Meta.xml file for NBA */
		Meta xmlspecimen = new Meta();
		xmlspecimen.setMetadata("eml.xml");
		xmlspecimen.setXmlnsxsi("http://www.w3.org/2001/XMLSchema-instance");
		xmlspecimen.setXmlnstdwg("http://rs.tdwg.org/dwc/text/");
		xmlspecimen.add(cores);
		DwCAObjectToXML(xmlspecimen);

		String emlfilefromdir = null;
		if (sourceSystemCode.equals("CRS")) 
		{
			/* Get the EML Filename */
			emlfilefromdir = GetEmlFileName(emlDirectory, collectionName);
			logger.info("Reading the file from: '" + emlDirectory + "\\" + emlfilefromdir + "'.");
			/* Directory Source EML File */
			FILE_NAME_EML = new File(emlDirectory + "\\" + emlfilefromdir);
			/* Destination directory for eml file */
			destinationPathEml = new File(outputDirectory + "\\" + emlfilefromdir);
			logger.info("Copy the file to: '" + outputDirectory + emlfilefromdir + "'.");
		}
		if (sourceSystemCode.toUpperCase().equals("BRAHMS")) 
		{
			nameCollectiontypeBrahms = sourceSystemCode;
			/* Get the EML Filename */
			emlfilefromdir = GetEmlFileName(emlDirectory, MAPPING_FILE_NAME.toLowerCase());
			logger.info("Reading the file from: '" + emlDirectory + "\\"+ emlfilefromdir + "'.");
			/* Directory Source EML File */
			FILE_NAME_EML = new File(emlDirectory + "\\" + emlfilefromdir);
			/* Destination directory for eml file */
			destinationPathEml = new File(outputDirectory + "\\" + emlfilefromdir);
			logger.info("Copy the file to: '" + outputDirectory + emlfilefromdir + "'.");
		}
		/* Copy the file from the source directory to the Destination directory */
		StringUtilities.CopyAFile(FILE_NAME_EML, destinationPathEml);
		/* Rename the (example: amphibia_and_reptilia_eml.xml) eml file to the exact name "eml.xml" */
		StringUtilities.renameDwCAEMLFile(destinationPathEml);

		/* Create the zipfile with a given filename */
		createZipFiles(zipFileName);

		File source = new File(zipFileName + zipExtension);
		File destination = null;
		if (sourceSystemCode.equals("CRS")|| sourceSystemCode.toUpperCase().equals("BRAHMS")) 
		{
			destination = new File(zipArchiveDirectory + collectionName	+ zipExtension);
		}

		/* Backup the zipfile */
		StringUtilities.CopyAFile(source, destination);
		/* Renamed the zip file into bak in de Archive map */
		StringUtilities.renameDwCAZipFile(destination);
	}

	/* Creating the Meta.xml */
	private static void DwCAObjectToXML(Meta meta) {
		try {
			JAXBContext context = JAXBContext.newInstance(Meta.class);
			Marshaller m = context.createMarshaller();
			// for pretty-print XML in JAXB
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			// Write to File
			m.marshal(meta, new File(outputDirectory + FILE_NAME_META));
			logger.info("Saved '" + FILE_NAME_META + "' to '" + outputDirectory
					+ "'");
			m.marshal(meta, System.out);

		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	/* Creating the zip file */
	public static void createZipFiles(String zipFileName) {
		ZipDwCA zip = new ZipDwCA();
		try {
			logger.info("Creating the zipfile: '" + zipFileName + zipExtension
					+ "'");
			zip.zipDirectory(outputDirectory, zipFileName + zipExtension);
			logger.info("Zipfile '" + zipFileName + zipExtension
					+ "' created successfull.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* Printing tghe records to a CSV file named: "Occurrence.txt" */
	private void printHeaderRowAndDataForCSV(String namecollectiontype,	String totalsize) throws IOException 
	{
		try 
		{ 
			/* Create new CSV File object and output File */
			
			/* Zoology Occurrence */
			if (sourceSystemCode.equalsIgnoreCase("CRS")) 
			{
				runCrsZoologyElasticsearch(namecollectiontype, totalsize);
				runCrsGeologyElasticsearch(namecollectiontype, totalsize);
			}
			
			/* BRAHMS Occurrence */
			if (sourceSystemCode.equalsIgnoreCase("BRAHMS")) 
			{
				runBrahmsElasticsearch(namecollectiontype, totalsize);
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		finally 
		{ /* Close the filewriter */
			if (filewriter != null) {
				try {
					filewriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void writeCSVHeader() throws IOException
	{
		/* Create new CSV File object and output File */
		filewriter = new CsvFileWriter(outputDirectory + csvOutPutFile);
		
		headerRow = filewriter.new CsvRow();

		Properties configFile = new Properties();
		try { /* load the values from the properties file */
			logger.info("Load '" + MAPPING_FILE_NAME + propertiesExtension	+ "' Ocurrencefields.");
			configFile.load(getClass().getClassLoader().getResourceAsStream(MAPPING_FILE_NAME + propertiesExtension));
		} catch (IOException e) 
		{
			logger.info("Fault: property file '" + MAPPING_FILE_NAME + "' not found in the classpath");
			return;
		}
		/* Sort the value from the properties file when loaded */
		SortedMap<Object, Object> sortedSystemProperties = new TreeMap<Object, Object>(
				configFile);
		Set<?> keySet = sortedSystemProperties.keySet();
		Iterator<?> iterator = keySet.iterator();
		while (iterator.hasNext()) 
		{
			propertyName = (String) iterator.next();
			propertyValue = configFile.getProperty(propertyName);
			/* Add the headers to the CSV File */
			if (propertyValue.contains("1")) 
			{
				headerRow.add(propertyValue.substring(0, propertyValue.length() - 2));
			}
			// System.out.println(propertyName + ": " + propertyValue);
		}
		/* Write the headers columns */
		logger.info("Writing headers row to the Occurence.txt file.");
		filewriter.WriteRow(headerRow);
		logger.info("CSV Fieldsheader: " + headerRow.toString());
	}
	
	private void runCrsZoologyElasticsearch(String namecollectiontype, String totalsize) throws IOException
	{
		/* Get the result from ElasticSearch */
		if (MAPPING_FILE_NAME.equalsIgnoreCase("Zoology")) 	
		{
			writeCSVHeader();
			/* Add the value from ElasticSearch to the CSV File */
			logger.info("Writing values from ElasticSearch to the '" + namecollectiontype + "' '" + MAPPING_FILE_NAME + "' Occurence.txt file.");
			long lStartTime = new Date().getTime();
			getResultsList(LUCENE_TYPE_SPECIMEN, namecollectiontype, sourceSystemCode,Integer.parseInt(totalsize), ESSpecimen.class);
			long lEndTime = new Date().getTime();
			long difference = lEndTime - lStartTime;
			//int minutes = (int) ((difference / (1000*60)) % 60);
			String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(difference),
				    TimeUnit.MILLISECONDS.toMinutes(difference) % TimeUnit.HOURS.toMinutes(1),
				    TimeUnit.MILLISECONDS.toSeconds(difference) % TimeUnit.MINUTES.toSeconds(1));
			logger.info("CSV file written on : '" + hms + " hour(s)/minute(s)/second(s).'");
			logger.info("CSV file written in : '" + TimeUnit.MILLISECONDS.toMinutes(difference) + " minutes.'");
		}
	}
	
	private void runCrsGeologyElasticsearch(String namecollectiontype, String totalsize) throws IOException
	{
		if (MAPPING_FILE_NAME.equalsIgnoreCase("Geology")) 
		{
			writeCSVHeader();
			logger.info("Writing values from ElasticSearch to the '" + namecollectiontype + "' '" + MAPPING_FILE_NAME + "' Occurence.txt file.");
			long lStartGeoTime = new Date().getTime();
			getResultsList(LUCENE_TYPE_SPECIMEN, namecollectiontype, sourceSystemCode, Integer.parseInt(totalsize), ESSpecimen.class);
			long lEndGeoTime = new Date().getTime();
			long differenceGeo = lEndGeoTime - lStartGeoTime;
			//int minutesGeo = (int) ((differenceGeo / (1000*60)) % 60);
			String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(differenceGeo),
				    TimeUnit.MILLISECONDS.toMinutes(differenceGeo) % TimeUnit.HOURS.toMinutes(1),
				    TimeUnit.MILLISECONDS.toSeconds(differenceGeo) % TimeUnit.MINUTES.toSeconds(1));
			logger.info("CSV file written on : '" + hms + " hour(s)/minute(s)/second(s).'");
			logger.info("CSV file written in : '" + TimeUnit.MILLISECONDS.toMinutes(differenceGeo) + " minutes.'");
		}
	}
	
	
	/* Get the data from Elasticsearch for BRAHMS and write to CSV file */
	private void runBrahmsElasticsearch(String namecollectiontype,	String totalsize) throws IOException
	{
		if (sourceSystemCode.toUpperCase().equals("BRAHMS")) 
		{
			writeCSVHeader();
			logger.info("Writing values from ElasticSearch to the '" + namecollectiontype + "' '" + MAPPING_FILE_NAME + "' Occurence.txt file.");
			long lStartBrahmsTime = new Date().getTime();
			
			getResultsList(LUCENE_TYPE_SPECIMEN, null,	sourceSystemCode, Integer.parseInt(totalsize),
					ESSpecimen.class);
			
			long lEndBrahmsTime = new Date().getTime();
			long differenceBrahms = lEndBrahmsTime - lStartBrahmsTime;
			//int minutesBrahms = (int) ((differenceBrahms / (1000*60)) % 60);
			String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(differenceBrahms),
				    TimeUnit.MILLISECONDS.toMinutes(differenceBrahms) % TimeUnit.HOURS.toMinutes(1),
				    TimeUnit.MILLISECONDS.toSeconds(differenceBrahms) % TimeUnit.MINUTES.toSeconds(1));
			logger.info("CSV file written on : '" + hms + " hour(s)/minute(s)/second(s).'");
			logger.info("CSV file written in : '" + TimeUnit.MILLISECONDS.toMinutes(differenceBrahms) + " minutes.'");
		}

	}
	
	
	/*Create CRS Zoology Csv file.*/
	private void writeCRSZoologyCsvFile(List<ESSpecimen> listZoology, String namecollectiontype, String occurrenceFields) throws IOException
	{
		if (MAPPING_FILE_NAME.equalsIgnoreCase(occurrenceFields)) 
		{
			Zoology zoology = new Zoology();
			zoology.addZoologyOccurrencefield(listZoology, filewriter,	MAPPING_FILE_NAME);
		}
	}
	
	/*Create CRS Zoology Csv file.*/
	private void writeCRSGeologyCsvFile(List<ESSpecimen> listGeology, String namecollectiontype, String occurrenceFields) throws IOException
	{
		if (MAPPING_FILE_NAME.equalsIgnoreCase(occurrenceFields)) 
		{
			Geology geo = new Geology();
			geo.addGeologyOccurrencefield(listGeology, filewriter,	MAPPING_FILE_NAME);
		}
	}
	
	/* Create Brahms CSV file */
	private void writeBrahmsCsvHeader(List<ESSpecimen> listBrahms, String namecollectiontype, String occurrenceFields) throws IOException
	{
		/* BRAHMS Occurrence */
		if (MAPPING_FILE_NAME.equals(occurrenceFields)) 
		{
			Brahms brahms = new Brahms();
			if (brahms != null)
			{
				try {
					brahms.addBrahmsOccurrencefield(listBrahms, filewriter, MAPPING_FILE_NAME);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String getNameCollectiontypeAnd() {
		return nameCollectiontypeAnd;
	}

	public static void setNameCollectiontypeAnd(String nameCollectiontypeAnd) {
		DwCAExporter.nameCollectiontypeAnd = nameCollectiontypeAnd;
	}
	
	@SuppressWarnings("unchecked")
	public <T> void getResultsList(String type, String namecollectiontype,
			String sourcesystemcode, int size, Class<T> targetClass) 
	{
		SearchRequestBuilder searchRequestBuilder = null;

		if (sourcesystemcode.toUpperCase().equals("CRS")) {
			logger.info("Querying the data for '" + sourcesystemcode + "'");

			FilteredQueryBuilder builder = null;
			String nameCollectionType1 = null;
			String nameCollectionType2 = null;

			if (namecollectiontype.contains(",")) {
				int index = namecollectiontype.indexOf(",");
				nameCollectionType1 = namecollectiontype.substring(0, index);
				nameCollectionType2 = namecollectiontype.substring(index + 2, namecollectiontype.length());
				
				builder = QueryBuilders.filteredQuery(QueryBuilders.boolQuery()
						.must(QueryBuilders.matchQuery("sourceSystem.code.raw", sourcesystemcode)),
                        FilterBuilders.orFilter(FilterBuilders.termFilter("collectionType.raw", nameCollectionType1),
                                		        FilterBuilders.termFilter("collectionType.raw", nameCollectionType2)));
			}
			else 
			{
				if (namecollectiontype != null)
				{
					builder = QueryBuilders.filteredQuery(QueryBuilders.boolQuery()
							  .must(QueryBuilders.matchQuery("sourceSystem.code.raw", sourcesystemcode))
							  .must(QueryBuilders.matchQuery("collectionType.raw", namecollectiontype)), null);
				}
			}
			
			if (builder != null)
			{
				logger.info(builder.toString());
			}

			if (size <= 0) {
				CountResponse res = eslasticClient.prepareCount().setQuery(builder)
						.execute().actionGet();
				size = (int) res.getCount();
			}

			if (size > 0) 
			{
				SortOrder order = SortOrder.ASC;
				searchRequestBuilder = eslasticClient
						.prepareSearch()
						.setVersion(true)
						.setQuery(builder)
						.addSort(SortBuilders.fieldSort("sourceSystemId").order(order).missing("_last"))
						.setSearchType(SearchType.SCAN)
						.setExplain(true)
						.setTypes("best_fields")
						.setScroll(new TimeValue(60000))
						.setIndices(indexname)
						.setTypes(type)
						.setSize(size);
			}
		}

		/*BRAHMS*/ 
		SearchResponse response = null;
		if (sourcesystemcode.equalsIgnoreCase("Brahms")) 
		{
			FilteredQueryBuilder brahmsBuilder = QueryBuilders.filteredQuery(QueryBuilders.boolQuery()
					  .must(QueryBuilders.matchQuery("sourceSystem.code.raw", sourcesystemcode.toUpperCase())), null);
					  
			logger.info("Querying the data for BRAHMS");
			if(brahmsBuilder != null)
			{
				logger.info(brahmsBuilder.toString());
			}

			if (size <= 0) 
			{
				CountResponse res = eslasticClient.prepareCount()
						.setQuery(brahmsBuilder).execute().actionGet();
				size = (int) res.getCount();
			}

			if (size > 0) 
			{
				SortOrder order = SortOrder.ASC;
				searchRequestBuilder = eslasticClient.prepareSearch()
						.setVersion(true)
						.setQuery(brahmsBuilder)
						.addSort(SortBuilders.fieldSort("sourceSystemId").order(order).missing("_last"))
						.setSearchType(SearchType.SCAN)
						.setExplain(true)
						.setScroll(TimeValue.timeValueMinutes(60000))
						.setIndices(indexname)
						.setTypes(type)
						.setSize(size);
						
			}
		}

		response = searchRequestBuilder.execute().actionGet();

		logger.info("Status: " + response.status());

		logger.info("Scrollid:" + response.getScrollId());

		long totalHitCount = 0;
		totalHitCount = response.getHits().getTotalHits();
		logger.info("Total hits: " + totalHitCount);

		/* Show response properties */
		String output = response.toString();
		logger.info(output);

		logger.info("Total records in occurrence file: " + totalHitCount);
        logger.info("Indexname '" + indexname + "'");
        logger.info("Start writing data to occurence file.");
        int count =  response.getSuccessfulShards();
		while (true) 
		{
			try 
			{
				List<T> list = new ArrayList<T>();
				
				for (SearchHit hit : response.getHits()) 
				{
					T result = objectMapper.convertValue(hit.getSource(), targetClass); 
					list.add(result);
				}
				
				response = eslasticClient.prepareSearchScroll(response.getScrollId())
						.setScrollId(response.getScrollId())
						.setScroll(TimeValue.timeValueMinutes(60000)).execute().actionGet();
				logger.info("Shard hit.'" + count++ + "' succesfull");
				
				
				
				if (sourcesystemcode.equalsIgnoreCase("CRS") && MAPPING_FILE_NAME.equalsIgnoreCase("Zoology"))
				{
				  writeCRSZoologyCsvFile((List<ESSpecimen>) list, namecollectiontype.toUpperCase(), "Zoology");	
				}
				
				if (sourcesystemcode.equalsIgnoreCase("CRS") && MAPPING_FILE_NAME.equalsIgnoreCase("Geology"))
				{
				  writeCRSGeologyCsvFile((List<ESSpecimen>) list, namecollectiontype.toUpperCase(), "Geology");	
				}
				
				if (sourcesystemcode.equalsIgnoreCase("BRAHMS"))
				{
					writeBrahmsCsvHeader((List<ESSpecimen>) list, sourcesystemcode.toUpperCase(), "Botany");
				}
				Requests.flushRequest(indexname);
				Requests.refreshRequest(indexname);
				
				// Break condition: No hits are returned
				if (response.getHits().hits().length == 0) 
				{
					logger.info("no more shard hits.'" + response.getHits().hits().length + "'");
					logger.info("Finished writing data to occurrence file.");
					break;
				}
				//return list;
			}
			 catch (Exception e) {
				// e.printStackTrace();
				 logger.info("Failed to copy data from index " + indexname + " into " + size + ".", e);
			}
		 }
	}

	
	
}
