/*
 *  Description: Create a new DwCA export tool for the NBA
 *  Date: January 28th 2015
 *  Developer: Reinier Kartowikromo
 *  
 */
package nl.naturalis.nda.export.dwca;

import static nl.naturalis.nda.export.dwca.StringUtilities.getFullPath;
import static nl.naturalis.nda.export.dwca.StringUtilities.getProperty;
import static nl.naturalis.nda.export.dwca.StringUtilities.newFile;

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
 * @author Reinier.Kartowikromo
 *
 */
public class DwCAExporter {

	static final Logger logger = LoggerFactory.getLogger(DwCAExporter.class);
	public static final String CSVComma = "\t";
	private static final String csvOutPutFile = "occurrence.txt";
	private static final String FILE_NAME_META = "meta.xml";
	private static File FILE_NAME_EML = null;
	private static final String dwcUrlTdwgOrg = "http://rs.tdwg.org/dwc/terms/";
	private static final String dwcTargetName = "http://rs.tdwg.org/dwc/text/";
	private static final String zipExtension = ".zip";
	private static String MAPPING_FILE_NAME = null;
	CsvFileWriter.CsvRow headerRow = null;
	private static String sourceSystemCode = null;
	private static String propertyName = null;
	private static String propertyValue = null;
	private static String result = null;
	private static String nameCollectiontypeCrs = null;
	private static String nameCollectiontypeBrahms = null;
	List<ESSpecimen> list;
	private static File destinationPathEml = null;
	private static String nameCollectiontypeAnd = null;
	private static String collectionName = null;

	// This sort of initializations is risky, because if
	// anything goes wrong, the Java VM won't start
	static String indexname = null; //ExportUtil.getConfig().required("elasticsearch.index.name").toString();
	private static Client eslasticClient = null;
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static CsvFileWriter filewriter = null;

	private static File emlDirectory = null;
	private static File outputDirectory = null;
	private static File zipOutputDirectory = null;
	private static File zipArchiveDirectory = null;


	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{

		logger.info("-----------------------------------------------------------------");
		logger.info("Start");

		String totalsize = System.getProperty("nl.naturalis.nda.export.dwca.batchsize", "1000");

		emlDirectory = StringUtilities.getCollectionConfigDir();
		/* Output directory for the files EML.xml, Meta.xml and Ocurrence.txt */
		outputDirectory = StringUtilities.getWorkingDirectory();
		zipOutputDirectory = StringUtilities.getZipOutputDirectory();
		zipArchiveDirectory = StringUtilities.getBackupDirectory();
		
		indexname = ExportUtil.getConfig().required("elasticsearch.index.name");

		collectionName = args.length > 0 ? args[0] : null;

	    if (collectionName != null) 
		{
	    	executeDwCaExport(zipOutputDirectory, totalsize);
		}
	    else
	    {
	    	StringBuilder builder = new StringBuilder();
	    	String extensions = ".properties";
	    	FilenameFilter filter = new FilenameFilter()
	    	{
	    		 public boolean accept(File dir, String name) {
	    	            return name.endsWith(extensions);
	    	        }
	    	};
	    	
	    	int cnt = 0;
        	try
        	{
        		File[] listOfFiles = emlDirectory.listFiles(filter);
        	    for (File file : listOfFiles)
        	    {
        	    	cnt++; 
        	    	ConfigObject config = new ConfigObject(file);
        	    	if (config.hasProperty("collectionName"))
        	    	{
        	    		config.required("collectionName");
         	    		collectionName = config.get("collectionName");
         	    		builder.append(collectionName);
         	    		builder.append("\n");
         	    		
         	    		if(cnt > 1)
         	    		{	
         	    			logger.info(" ");
         	    			logger.info("=================================");
         	    			logger.info("Start with execution collectioname: " + collectionName);
         	    		}
        	    		executeDwCaExport(zipOutputDirectory, totalsize);
        	    	}
        	    }
        	    logger.info("All collection successful executed: " + builder.toString());
        	}
       		catch (Exception e)
       		{
       			System.err.println(e.toString());
       		}
	    }
		
		logger.info("Ready");
	}
	
		
	private static void executeDwCaExport(File pZipOutPutDirectory, String ptotalSize) throws Exception
	{
		/* Get the SourceSystem: CRS or BRAHMS, COL etc. */
		if (collectionName != null) 
		{
			sourceSystemCode = getProperty(collectionName, "sourceSystemCode");
			/* Get the Ocurrencefields value */
			MAPPING_FILE_NAME = getProperty(collectionName, "occurrenceFields");
		}

		/* args[2] Get the Collectiontype */
		try {
			if (sourceSystemCode.equals("CRS")) 
			{
				nameCollectiontypeCrs = getProperty(collectionName, "collectionType");
			}

			if (sourceSystemCode.toUpperCase().equals("BRAHMS")) 
			{
				collectionName = getProperty(collectionName, "collectionName");
				nameCollectiontypeBrahms = collectionName;
			}

		}
		catch (Exception ex) {
			logger.info(collectionName + " properties filename is not correct.");
		}

		/* Get the directory and zipfilename */
		String zipfilename = null;
		if (sourceSystemCode.toUpperCase().equals("CRS") || sourceSystemCode.toUpperCase().equals("BRAHMS")) {
			zipfilename = newFile(zipOutputDirectory, collectionName).getAbsolutePath();
		}

		DwCAExporter exp = new DwCAExporter(ExportUtil.getESClient(), indexname);
		/* Delete the CSV file if Exists */
		boolean success = newFile(outputDirectory, csvOutPutFile).delete();
		if (success) {
			logger.info("The file " + csvOutPutFile + " has been successfully deleted");
		}
		if (sourceSystemCode.toUpperCase().equals("CRS")) {
			exp.exportDwca(zipfilename, nameCollectiontypeCrs, ptotalSize);
		}
		if (sourceSystemCode.toUpperCase().equals("BRAHMS")) {
			exp.exportDwca(zipfilename, nameCollectiontypeBrahms, ptotalSize);
		}
		
	}


	private static String getEmlFileName(String emlDir, String emlfilename)
	{
		String filename = null;
		File[] filelist = FindFile.getFileList(emlDir);
		if (filelist != null) {
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
		else {
			logger.info("Eml files not found '" + emlDir + "'");
		}
		return result;
	}


	public DwCAExporter(Client client, String indexname)
	{
		DwCAExporter.eslasticClient = client;
		DwCAExporter.indexname = indexname;
	}


	public void exportDwca(String zipFileName, String namecollectiontype, String totalsize) throws Exception
	{
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
		Iterator<String> fieldIter = headerRow.iterator();
		while (fieldIter.hasNext()) {
			cnt = Integer.valueOf(cnt.intValue() + 1);
			Field field = new Field(cnt.toString(), dwcUrlTdwgOrg + fieldIter.next());
			cores.addField(field);
		}

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
			emlfilefromdir = getEmlFileName(emlDirectory.getAbsolutePath(), collectionName);
			logger.info("Reading the file from: '" + getFullPath(emlDirectory, emlfilefromdir));
			/* Directory Source EML File */
			FILE_NAME_EML = StringUtilities.newFile(emlDirectory, emlfilefromdir);
			/* Destination directory for eml file */
			destinationPathEml = StringUtilities.newFile(outputDirectory, emlfilefromdir);
			logger.info("Copy the file to: '" + getFullPath(outputDirectory, emlfilefromdir));
		}
		if (sourceSystemCode.toUpperCase().equals("BRAHMS")) {
			nameCollectiontypeBrahms = sourceSystemCode;
			/* Get the EML Filename */
			emlfilefromdir = getEmlFileName(emlDirectory.getAbsolutePath(), MAPPING_FILE_NAME.toLowerCase());
			logger.info("Reading the file from: '" + getFullPath(emlDirectory, emlfilefromdir));
			/* Directory Source EML File */
			FILE_NAME_EML = StringUtilities.newFile(emlDirectory, emlfilefromdir);
			/* Destination directory for eml file */
			destinationPathEml = newFile(outputDirectory, emlfilefromdir);
			logger.info("Copy the file to: '" + getFullPath(outputDirectory, emlfilefromdir));
		}
		/* Copy the file from the source directory to the Destination directory */
		StringUtilities.CopyAFile(FILE_NAME_EML, destinationPathEml);
		/*
		 * Rename the (example: amphibia_and_reptilia_eml.xml) eml file to the
		 * exact name "eml.xml"
		 */
		StringUtilities.renameDwCAEMLFile(destinationPathEml);

		/* Create the zipfile with a given filename */
		createZipFiles(zipFileName);

		File source = new File(zipFileName + zipExtension);
		File destination = null;
		if (sourceSystemCode.equals("CRS") || sourceSystemCode.toUpperCase().equals("BRAHMS")) {
			destination = newFile(zipArchiveDirectory, collectionName + zipExtension);
		}

		/* Backup the zipfile */
		StringUtilities.CopyAFile(source, destination);
		/* Renamed the zip file into bak in de Archive map */
		StringUtilities.renameDwCAZipFile(destination);
	}


	/* Creating the Meta.xml */
	private static void dwcaObjectToXML(Meta meta)
	{
		try {
			JAXBContext context = JAXBContext.newInstance(Meta.class);
			Marshaller m = context.createMarshaller();
			// for pretty-print XML in JAXB
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

			// Write to File
			m.marshal(meta, newFile(outputDirectory, FILE_NAME_META));
			logger.info("Saved '" + FILE_NAME_META + "' to '" + outputDirectory.getAbsolutePath() + "'");
			m.marshal(meta, System.out);

		}
		catch (JAXBException e) {
			e.printStackTrace();
		}
	}


	/* Creating the zip file */
	public static void createZipFiles(String zipFileName)
	{
		ZipDwCA zip = new ZipDwCA();
		try {
			logger.info("Creating the zipfile: '" + zipFileName + zipExtension + "'");
			zip.zipDirectory(outputDirectory.getAbsolutePath(), zipFileName + zipExtension);
			logger.info("Zipfile '" + zipFileName + zipExtension + "' created successful.");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}


	/* Printing tghe records to a CSV file named: "Occurrence.txt" */
	private void printHeaderRowAndDataForCSV(String namecollectiontype, String totalsize) throws IOException
	{
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
		catch (IOException e) {
			e.printStackTrace();
		}
		finally { /* Close the filewriter */
			if (filewriter != null) {
				try {
					filewriter.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


	private void writeCSVHeader() throws IOException
	{
		/* Create new CSV File object and output File */
		filewriter = new CsvFileWriter(newFile(outputDirectory, csvOutPutFile));

		headerRow = filewriter.new CsvRow();

		Properties configFile = StringUtilities.getCollectionConfiguration(MAPPING_FILE_NAME).getProperties();
		/* Sort the value from the properties file when loaded */
		SortedMap<Object, Object> sortedSystemProperties = new TreeMap<Object, Object>(configFile);
		Set<?> keySet = sortedSystemProperties.keySet();
		Iterator<?> iterator = keySet.iterator();
		while (iterator.hasNext()) {
			propertyName = (String) iterator.next();
			propertyValue = configFile.getProperty(propertyName);
			/* Add the headers to the CSV File */
			String[] chunks = propertyValue.split(",");
			if (chunks[1].equals("1")) {
				headerRow.add(propertyValue.substring(0, propertyValue.length() - 2));
			}
		}
		/* Write the headers columns */
		logger.info("Writing headers row to the Occurrence.txt file.");
		//StringUtilities.writeLogToJSON(nameCollectiontypeCrs, "Writing headers row to the Occurrence.txt file.");
		filewriter.WriteRow(headerRow);
		logger.info("CSV Fieldsheader: " + headerRow.toString());
		//StringUtilities.writeLogToJSON(nameCollectiontypeCrs, "CSV Fieldsheader: " + headerRow.toString());
	}


	private void runCrsZoologyElasticsearch(String namecollectiontype, String totalsize) throws IOException
	{
		/* Get the result from ElasticSearch */
		if (MAPPING_FILE_NAME.equalsIgnoreCase("Zoology")) {
			writeCSVHeader();
			/* Add the value from ElasticSearch to the CSV File */
			logger.info("Writing values from ElasticSearch to the '" + namecollectiontype + "' '" + MAPPING_FILE_NAME + "' Occurrence.txt file.");
			long lStartTime = new Date().getTime();
			getResultsList("Specimen", namecollectiontype, sourceSystemCode, Integer.parseInt(totalsize), ESSpecimen.class);
			long lEndTime = new Date().getTime();
			long difference = lEndTime - lStartTime;
			//int minutes = (int) ((difference / (1000*60)) % 60);
			String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(difference), TimeUnit.MILLISECONDS.toMinutes(difference)
					% TimeUnit.HOURS.toMinutes(1), TimeUnit.MILLISECONDS.toSeconds(difference) % TimeUnit.MINUTES.toSeconds(1));
			logger.info("CSV file written on : '" + hms + " hour(s)/minute(s)/second(s).'");
			logger.info("CSV file written in : '" + TimeUnit.MILLISECONDS.toMinutes(difference) + " minutes.'");
		}
	}


	private void runCrsGeologyElasticsearch(String namecollectiontype, String totalsize) throws IOException
	{
		if (MAPPING_FILE_NAME.equalsIgnoreCase("Geology")) {
			writeCSVHeader();
			logger.info("Writing values from ElasticSearch to the '" + namecollectiontype + "' '" + MAPPING_FILE_NAME + "' Occurrence.txt file.");
			long lStartGeoTime = new Date().getTime();
			getResultsList("Specimen", namecollectiontype, sourceSystemCode, Integer.parseInt(totalsize), ESSpecimen.class);
			long lEndGeoTime = new Date().getTime();
			long differenceGeo = lEndGeoTime - lStartGeoTime;
			//int minutesGeo = (int) ((differenceGeo / (1000*60)) % 60);
			String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(differenceGeo), TimeUnit.MILLISECONDS.toMinutes(differenceGeo)
					% TimeUnit.HOURS.toMinutes(1), TimeUnit.MILLISECONDS.toSeconds(differenceGeo) % TimeUnit.MINUTES.toSeconds(1));
			logger.info("CSV file written on : '" + hms + " hour(s)/minute(s)/second(s).'");
			logger.info("CSV file written in : '" + TimeUnit.MILLISECONDS.toMinutes(differenceGeo) + " minutes.'");
		}
	}


	/* Get the data from Elasticsearch for BRAHMS and write to CSV file */
	private void runBrahmsElasticsearch(String namecollectiontype, String totalsize) throws IOException
	{
		if (sourceSystemCode.toUpperCase().equals("BRAHMS")) {
			writeCSVHeader();
			logger.info("Writing values from ElasticSearch to the '" + namecollectiontype + "' '" + MAPPING_FILE_NAME + "' Occurrence.txt file.");
			long lStartBrahmsTime = new Date().getTime();

			getResultsList("Specimen", null, sourceSystemCode, Integer.parseInt(totalsize), ESSpecimen.class);

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


	/* Create CRS Zoology Csv file. */
	private void writeCRSZoologyCsvFile(List<ESSpecimen> listZoology, String namecollectiontype, String occurrenceFields) throws IOException
	{
		if (MAPPING_FILE_NAME.equalsIgnoreCase(occurrenceFields)) {
			Zoology zoology = new Zoology();
			zoology.addZoologyOccurrencefield(listZoology, filewriter, MAPPING_FILE_NAME);
		}
	}


	/* Create CRS Zoology Csv file. */
	private void writeCRSGeologyCsvFile(List<ESSpecimen> listGeology, String namecollectiontype, String occurrenceFields) throws IOException
	{
		if (MAPPING_FILE_NAME.equalsIgnoreCase(occurrenceFields)) {
			Geology geo = new Geology();
			geo.addGeologyOccurrencefield(listGeology, filewriter, MAPPING_FILE_NAME);
		}
	}


	/* Create Brahms CSV file */
	private void writeBrahmsCsvHeader(List<ESSpecimen> listBrahms, String namecollectiontype, String occurrenceFields) throws IOException
	{
		/* BRAHMS Occurrence */
		if (MAPPING_FILE_NAME.equalsIgnoreCase(occurrenceFields)) {
			Brahms brahms = new Brahms();
			if (brahms != null) {
				try {
					brahms.addBrahmsOccurrencefield(listBrahms, filewriter, MAPPING_FILE_NAME);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}


	public static String getNameCollectiontypeAnd()
	{
		return nameCollectiontypeAnd;
	}


	public static void setNameCollectiontypeAnd(String nameCollectiontypeAnd)
	{
		DwCAExporter.nameCollectiontypeAnd = nameCollectiontypeAnd;
	}


	@SuppressWarnings("unchecked")
	public <T> void getResultsList(String type, String namecollectiontype, String sourcesystemcode, int size, Class<T> targetClass)
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

				builder = QueryBuilders.filteredQuery(
						QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("sourceSystem.code.raw", sourcesystemcode)),
						FilterBuilders.orFilter(FilterBuilders.termFilter("collectionType.raw", nameCollectionType1),
								FilterBuilders.termFilter("collectionType.raw", nameCollectionType2)));
			}
			else {
				if (namecollectiontype != null) {
					builder = QueryBuilders.filteredQuery(
							QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("sourceSystem.code.raw", sourcesystemcode))
									.must(QueryBuilders.matchQuery("collectionType.raw", namecollectiontype)), null);
				}
			}

			if (builder != null) {
				logger.info(builder.toString());
			}

			if (size <= 0) {
				CountResponse res = eslasticClient.prepareCount().setQuery(builder).execute().actionGet();
				size = (int) res.getCount();
			}

			if (size > 0) {
				SortOrder order = SortOrder.ASC;
				searchRequestBuilder = eslasticClient.prepareSearch().setVersion(true).setQuery(builder)
						.addSort(SortBuilders.fieldSort("sourceSystemId").order(order).missing("_last")).setSearchType(SearchType.SCAN)
						.setExplain(true).setTypes("best_fields").setScroll(new TimeValue(60000)).setIndices(indexname).setTypes(type).setSize(size);
			}
		}

		/* BRAHMS */
		SearchResponse response = null;
		if (sourcesystemcode.equalsIgnoreCase("Brahms")) {
			FilteredQueryBuilder brahmsBuilder = QueryBuilders.filteredQuery(
					QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("sourceSystem.code.raw", sourcesystemcode.toUpperCase())), null);

			logger.info("Querying the data for BRAHMS");
			if (brahmsBuilder != null) {
				logger.info(brahmsBuilder.toString());
			}

			if (size <= 0) {
				CountResponse res = eslasticClient.prepareCount().setQuery(brahmsBuilder).execute().actionGet();
				size = (int) res.getCount();
			}

			if (size > 0) {
				SortOrder order = SortOrder.ASC;
				searchRequestBuilder = eslasticClient.prepareSearch().setVersion(true).setQuery(brahmsBuilder)
						.addSort(SortBuilders.fieldSort("sourceSystemId").order(order).missing("_last")).setSearchType(SearchType.SCAN)
						.setExplain(true).setScroll(TimeValue.timeValueMinutes(60000)).setIndices(indexname).setTypes(type).setSize(size);

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
		logger.info("Start writing data to occurrence file.");
		int count = response.getSuccessfulShards();
		int restvalue = 0;
		int resultrecord = 0;
		
		//int count = (int) totalHitCount / 1000;//(int) (totalHitCount / response.getSuccessfulShards());
		while (true) {
			try {
				List<T> list = new ArrayList<T>();

				for (SearchHit hit : response.getHits()) {
					T result = objectMapper.convertValue(hit.getSource(), targetClass);
					list.add(result);
				}

				response = eslasticClient.prepareSearchScroll(response.getScrollId()).setScrollId(response.getScrollId())
						.setScroll(TimeValue.timeValueMinutes(60000)).execute().actionGet();
				//count = count + 1000;
				//count++;
				restvalue = (int) (totalHitCount / count);
				resultrecord = restvalue - resultrecord;
				logger.info("Shard hit.'" + count++ + "' successful");
				//logger.info("Number of records '"+ resultrecord + "' process per shard");
				//logger.info(Integer.toString(response.getHits()));


				if (sourcesystemcode.equalsIgnoreCase("CRS") && MAPPING_FILE_NAME.equalsIgnoreCase("Zoology")) {
					writeCRSZoologyCsvFile((List<ESSpecimen>) list, namecollectiontype.toUpperCase(), "Zoology");
				}

				if (sourcesystemcode.equalsIgnoreCase("CRS") && MAPPING_FILE_NAME.equalsIgnoreCase("Geology")) {
					writeCRSGeologyCsvFile((List<ESSpecimen>) list, namecollectiontype.toUpperCase(), "Geology");
				}

				if (sourcesystemcode.equalsIgnoreCase("BRAHMS")) {
					writeBrahmsCsvHeader((List<ESSpecimen>) list, sourcesystemcode.toLowerCase(), "botany");
				}
				Requests.flushRequest(indexname);
				Requests.refreshRequest(indexname);

				// Break condition: No hits are returned
				if (response.getHits().hits().length == 0) {
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
