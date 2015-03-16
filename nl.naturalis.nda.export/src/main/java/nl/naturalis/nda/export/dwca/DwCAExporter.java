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
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import nl.naturalis.nda.export.dwca.FindFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.naturalis.nda.export.dwca.StringUtilities;
import nl.naturalis.nda.export.dwca.Zoology;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class DwCAExporter
{
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
	private static String sourcesystemcode = null;
	private static String zipoutputdirectory = null;
	private static String propertyName = null;
	private static String propertyValue = null;
	private static String result = null;
	private static String namecollectiontypecrs = null;
	private static String namecollectiontypebrahms = null;
	List<ESSpecimen> list;
	private static String ziparchivedirectory = null;
	private static String emldirectory = null;
	private static File destinationpatheml = null;
	private static String namecollectiontypeand = null;
	private static String collectionname = null;
	

	/**
	 * @param args
	 * @throws Exception
	 */

	public static void main(String[] args) throws Exception
	{

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
		emldirectory = StringUtilities.readPropertyvalue(args[0], "EMLDirectory");
		
		/* Output directory for the files EML.xml, Meta.xml and Ocurrence.txt */
		outputDirectory = StringUtilities.readPropertyvalue(args[0], "Directory") + "\\";

		collectionname = StringUtilities.readPropertyvalue(args[0], "Collectionname");
		
		
		/* Get the SourceSystem: CRS or BRAHMS, COL etc. */
		if (collectionname != null)
		{
			sourcesystemcode = StringUtilities.readPropertyvalue(collectionname, "sourceSystemcode");
			/* Get the Ocurrencefields value */
			MAPPING_FILE_NAME = StringUtilities.readPropertyvalue(collectionname, "Ocurrencefields");
		}

		/* args[2] Get the Collectiontype */
		try
		{
			if (sourcesystemcode.equals("CRS"))
			{
				namecollectiontypecrs = StringUtilities.readPropertyvalue(collectionname, "collectionType");
				
				if (namecollectiontypecrs.contains(","))
				{
					String resultcoltype = namecollectiontypecrs;
					int index = resultcoltype.indexOf(",");
					int collength = resultcoltype.length();
					namecollectiontypecrs = namecollectiontypecrs.substring(0, index);
					namecollectiontypeand = resultcoltype.substring(index + 2, collength);
				}
			}
			if (sourcesystemcode.toUpperCase().equals("BRAHMS"))
			{
				collectionname = StringUtilities.readPropertyvalue(collectionname, "Collectionname");
				namecollectiontypebrahms = collectionname;
			}
			
		} catch (Exception ex)
		{
			logger.info(collectionname + " properties filename is not correct.");
		}
		

		/* Directoy where zipfile will be created */
		zipoutputdirectory = StringUtilities.readPropertyvalue(args[0], "ZipDirectory") + "\\";
		/* Copy the DwCAZip file to the DwCAZipArchive directory. */
		ziparchivedirectory = StringUtilities.readPropertyvalue(args[0], "ZipArchiveDirectory") + "\\";

		/* Get the directory and zipfilename */
		String zipfilename = null;
		if (sourcesystemcode.toUpperCase().equals("CRS") || sourcesystemcode.toUpperCase().equals("BRAHMS"))
		{
		  zipfilename = zipoutputdirectory + collectionname;
		}
		

		logger.info("Loading Elastcisearch");
		IndexNative index = new IndexNative(LoadUtil.getESClient(), LoadUtil.getConfig().required("elasticsearch.index.name"));
		try
		{
			DwCAExporter exp = new DwCAExporter(index);
			/* Delete the CSV file if Exists */
			boolean success = (new File(outputDirectory + csvOutPutFile)).delete();
			if (success)
			{
				logger.info("The file " + csvOutPutFile + " has been successfully deleted");
			}
			if (sourcesystemcode.toUpperCase().equals("CRS"))
			{
				exp.ExportDwca(zipfilename, namecollectiontypecrs, totalsize);
			}
			if (sourcesystemcode.toUpperCase().equals("BRAHMS"))
			{
				exp.ExportDwca(zipfilename, namecollectiontypebrahms, totalsize);
			}
		} finally
		{
			index.getClient().close();
		}

		logger.info("Ready");
	}

	private static String GetEmlFileName(String emlDir, String emlfilename)
	{
		String filename = null;
		File[] filelist = FindFile.getFileList(emlDir);
		for (File file : filelist)
		{
			filename = file.getName();
			if (filename.toLowerCase().contains(emlfilename.toLowerCase()))
			{
				result = file.getName();
				break;
			}
		}
		return result;
	}

	public DwCAExporter(IndexNative index)
	{
		this.index = index;
	}

	private final IndexNative index;

	public void ExportDwca(String zipFileName, String namecollectiontype, String totalsize) throws Exception
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
			Field field = new Field(cnt.toString(), dwcUrlTdwgOrg + fieldIter.next());
			cores.addField(field);

		}

		/* Create Meta.xml file for NBA */
		Meta xmlspecimen = new Meta();
		xmlspecimen.setMetadata("eml.xml");
		xmlspecimen.setXmlnsxsi("http://www.w3.org/2001/XMLSchema-instance");
		xmlspecimen.setXmlnstdwg("http://rs.tdwg.org/dwc/text/");
		xmlspecimen.add(cores);
		DwCAObjectToXML(xmlspecimen);

		/* Create "EML" xml file */
		// logger.info("Creating the EML.xml file.");
		// CreateEmlObjectToXML();
		
		String emlfilefromdir = null;
		if (sourcesystemcode.equals("CRS"))
		{
			emlfilefromdir = GetEmlFileName(emldirectory, collectionname); 
			logger.info("Reading the file from: '" + emldirectory + "\\" + emlfilefromdir + "'.");
			FILE_NAME_EML = new File(emldirectory + "\\" + emlfilefromdir);
			destinationpatheml = new File(outputDirectory + "\\" + emlfilefromdir);
			logger.info("Copy the file to: '" + outputDirectory + emlfilefromdir + "'.");
		}
		if (sourcesystemcode.toUpperCase().equals("BRAHMS"))
		{
			namecollectiontypebrahms = sourcesystemcode;
			emlfilefromdir = GetEmlFileName(emldirectory, MAPPING_FILE_NAME.toLowerCase());
			logger.info("Reading the file from: '" + emldirectory + "\\" + emlfilefromdir + "'.");
			FILE_NAME_EML = new File(emldirectory + "\\" + emlfilefromdir);
			destinationpatheml = new File(outputDirectory + "\\" + emlfilefromdir);
			logger.info("Copy the file to: '" + outputDirectory + emlfilefromdir + "'.");
		}
		StringUtilities.CopyAFile(FILE_NAME_EML, destinationpatheml);
		StringUtilities.renameDwCAEMLFile(destinationpatheml);
		

		/* Create the zipfile with a given filename */
		createZipFiles(zipFileName);

		File source = new File(zipFileName + zipExtension);
		File destination = null;
		if (sourcesystemcode.equals("CRS") || sourcesystemcode.toUpperCase().equals("BRAHMS"))
		{
			destination = new File(ziparchivedirectory + collectionname + zipExtension);
		}
		
		/* Backup the zipfile */
		StringUtilities.CopyAFile(source, destination);
		/* Renamed the zip file into bak in de Archive map */
		StringUtilities.renameDwCAZipFile(destination);
	}

	/* Creating the Meta.xml */
	private static void DwCAObjectToXML(Meta meta)
	{
		try
		{
			JAXBContext context = JAXBContext.newInstance(Meta.class);
			Marshaller m = context.createMarshaller();
			// for pretty-print XML in JAXB
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			// Write to File
			m.marshal(meta, new File(outputDirectory + FILE_NAME_META));
			logger.info("Saved '" + FILE_NAME_META + "' to '" + outputDirectory + "'");
			m.marshal(meta, System.out);

		} catch (JAXBException e)
		{
			e.printStackTrace();
		}
	}

    /* Creating the zip file */
	public static void createZipFiles(String zipFileName)
	{
		ZipDwCA zip = new ZipDwCA();
		try
		{
			logger.info("Creating the zipfile: '" + zipFileName + zipExtension + "'");
			zip.zipDirectory(outputDirectory, zipFileName + zipExtension);
			logger.info("Zipfile '" + zipFileName + zipExtension + "' created successfull.");
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/* Printing tghe records to a CSV file named: "Occurrence.txt" */
	private void printHeaderRowAndDataForCSV(String namecollectiontype, String totalsize)
	{
		//String [] searchvalue = {namecollectiontypecrs, namecollectiontypeand}; 
		CsvFileWriter filewriter = null;
		try
		{ /* Create new CSV File object and output File */
			filewriter = new CsvFileWriter(outputDirectory + csvOutPutFile);
			/* Get the result from ElasticSearch */
			if (sourcesystemcode.equals("CRS"))
			{
				list = index.getResultsList(LUCENE_TYPE_SPECIMEN, namecollectiontype, sourcesystemcode,
						Integer.parseInt(totalsize), ESSpecimen.class);
			}
			if (sourcesystemcode.toUpperCase().equals("BRAHMS"))
			{
				list = index.getResultsList(LUCENE_TYPE_SPECIMEN, null, sourcesystemcode,
						Integer.parseInt(totalsize), ESSpecimen.class);
			}

			headerRow = filewriter.new CsvRow();

			Properties configFile = new Properties();
			try
			{ /* load the values from the properties file */
				logger.info("Load '" + MAPPING_FILE_NAME + propertiesExtension + "' Ocurrencefields.");
				configFile.load(getClass().getClassLoader().getResourceAsStream(
						MAPPING_FILE_NAME + propertiesExtension));
			} catch (IOException e)
			{
				logger.info("Fault: property file '" + MAPPING_FILE_NAME + "' not found in the classpath");
				// System.out.println("property file '" + MAPPING_FILE_NAME +
				// "' not found in the classpath");
				return;
			}
			/* Sort the value from the properties file when loaded */
			SortedMap<Object, Object> sortedSystemProperties = new TreeMap<Object, Object>(configFile);
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

			/* Zoology Occurrence */
			if (MAPPING_FILE_NAME.equals("Zoology"))
			{
				/* Add the value from ElasticSearch to the CSV File */
				logger.info("Writing values from ElasticSearch to the '" + namecollectiontype + "' '"
						+ MAPPING_FILE_NAME + "' Occurence.txt file.");
				Zoology zoology = new Zoology();
				zoology.addZoologyOccurrencefield(list, filewriter, MAPPING_FILE_NAME);
			}

			/* Geology Occurrence */
			if (MAPPING_FILE_NAME.equals("Geology"))
			{
				logger.info("Writing values from ElasticSearch to the '" + namecollectiontype + "' '"
						+ MAPPING_FILE_NAME + "' Occurence.txt file.");
				Geology geo = new Geology();
				geo.addGeologyOccurrencefield(list, filewriter, MAPPING_FILE_NAME);
			}
			/* BRAHMS Occurrence */
			if (MAPPING_FILE_NAME.equals("Botany"))
			{
				logger.info("Writing values from ElasticSearch to the '" + namecollectiontype + "' '"
						+ MAPPING_FILE_NAME + "' Occurence.txt file.");
				Brahms brahms = new Brahms();
				brahms.addBrahmsOccurrencefield(list, filewriter, MAPPING_FILE_NAME);
			}

		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{ /* Close the filewriter */
			if (filewriter != null)
			{
				try
				{
					filewriter.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}

		}
	}
}
