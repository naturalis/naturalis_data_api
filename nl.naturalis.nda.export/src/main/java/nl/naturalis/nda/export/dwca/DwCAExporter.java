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
import java.util.ArrayList;
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
import nl.naturalis.nda.export.dwca.Eml;

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
	private static final String FILE_NAME_EML = "eml.xml";
	private static final String dwcUrlTdwgOrg = "http://rs.tdwg.org/dwc/terms/";
	private static final String zipExtension = ".zip";
	private static final String propertiesExtension = ".properties";
	private static String MAPPING_FILE_NAME = null;
	CsvFileWriter.CsvRow headerRow = null;
	static String outputdirectory = null;
	static String sourcesystemcode = null;
	static String zipoutputdirectory = null;
	static String propertyName = null;
	static String propertyValue = null;
	List<String> listfield = new ArrayList<>();
	static String result = null;
	static String resultprop = null;
	static String namecollectiontype = null;
	static String collectionname = null;
	List<ESSpecimen> list;
	static String ziparchivedirectory = null;

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

		/* Get the SourceSystem: CRS or BRAHMS, COL etc. */
		if (args[2] != null)
		{
			sourcesystemcode = StringUtilities.readPropertyvalue(args[2], "sourceSystemcode");
		}

		/* args[2] Get the Collectiontype */
		try
		{
			if (sourcesystemcode.equals("CRS"))
			{
				namecollectiontype = StringUtilities.readPropertyvalue(args[2], "collectionType");
			}
			/*
			 * else { namecollectiontype = "BRAHMS"; }
			 */
		} catch (Exception ex)
		{
			logger.info(args[2] + " properties filename is not correct.");
		}

		/* Get the Ocurrencefields value */
		MAPPING_FILE_NAME = StringUtilities.readPropertyvalue(args[2], "Ocurrencefields");

		/* Output directory for the files EML.xml, Meta.xml and Ocurrence.txt */
		outputdirectory = StringUtilities.readPropertyvalue(args[0], "Directory") + "\\";
		/* Directoy where zipfile will be created */
		zipoutputdirectory = StringUtilities.readPropertyvalue(args[0], "ZipDirectory") + "\\";
		/* Copy the DwCAZip file to the DwCAZipArchive directory. */
		ziparchivedirectory = StringUtilities.readPropertyvalue(args[0], "ZipArchiveDirectory") + "\\";

		/* Get the directory and zipfilename */
		String zipfilename = zipoutputdirectory + namecollectiontype;

		logger.info("Loading Elastcisearch");
		IndexNative index = new IndexNative(LoadUtil.getESClient(), LoadUtil.getConfig().required(
				"elasticsearch.index.name"));
		try
		{
			DwCAExporter exp = new DwCAExporter(index);
			/* Delete the CSV file if Exists */
			boolean success = (new File(outputdirectory + csvOutPutFile)).delete();
			if (success)
			{
				logger.info("The file " + csvOutPutFile + " has been successfully deleted");
			}
			exp.ExportDwca(zipfilename, namecollectiontype, totalsize);
		} finally
		{
			index.getClient().close();
		}

		logger.info("Ready");
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
		logger.info("Creating the EML.xml file.");
		CreateEmlObjectToXML();

		/* Create the zipfile with a given filename */
		createZipFiles(zipFileName);

		File source = new File(zipFileName + zipExtension);
		File destination = null;
		if (sourcesystemcode.equals("CRS"))
		{
			destination = new File(ziparchivedirectory + namecollectiontype + zipExtension);
		}
		if (sourcesystemcode.equals("BRAHMS"))
		{
			destination = new File(ziparchivedirectory + sourcesystemcode + zipExtension);
		}
		/* Backup the zipfile */
		StringUtilities.backupZipFile(source, destination);

	}

	private static void DwCAObjectToXML(Meta meta)
	{
		try
		{
			JAXBContext context = JAXBContext.newInstance(Meta.class);
			Marshaller m = context.createMarshaller();
			// for pretty-print XML in JAXB
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			// Write to File
			m.marshal(meta, new File(outputdirectory + FILE_NAME_META));
			logger.info("Saved '" + FILE_NAME_META + "' to '" + outputdirectory + "'");
			m.marshal(meta, System.out);

		} catch (JAXBException e)
		{
			e.printStackTrace();
		}
	}

	private static void CreateEmlObjectToXML()
	{
		try
		{
			/* Element Role info for Contact */
			Role role = new Role();
			role.setPosition("Application Developer");

			/* Element Name info for Contact */
			IndividualName indi = new IndividualName();
			indi.setGivenname("Reinier");
			indi.setSurname("Kartowikromo");

			/* Element Role info for Creator */
			Role rolecreator = new Role();
			rolecreator.setPosition("Analist");

			/* Element Name info for Creator */
			IndividualName individual = new IndividualName();
			individual.setGivenname("Wilfred");
			individual.setSurname("Gerritsen");

			/* Element Role info for Provider */
			Role roleprovider = new Role();
			roleprovider.setPosition("Lead Software Developer");

			/* Element Name info for Provider */
			IndividualName individualprovider = new IndividualName();
			individualprovider.setGivenname("Ayco");
			individualprovider.setSurname("Holleman");

			/* Element info "address" */
			Address address = new Address();
			address.setDeliveryPoint("Darwinweg 2");
			address.setCity("Leiden");
			address.setStateProvince("South Holland");
			address.setCountry("Netherlands");
			address.setPostalCode("NL-2300RA");

			/* Element info "contact" */
			Contact contact = new Contact();
			contact.setOrganisation("Naturalis Biodiversity Center");
			contact.setPhone("0102150587");
			contact.setEmailAddress("contact@naturalis.nl");
			contact.setOnlineUrl("http://www.naturalis.nl");
			contact.setRole(role);
			contact.setIndividualName(indi);
			contact.setAddress(address);

			/* Element info "creator" */
			Creator creator = new Creator();
			creator.setOrganisation("Naturalis Biodiversity Center");
			creator.setPhone("0102151088");
			creator.setEmailAddress("contact@naturalis.nl");
			creator.setOnlineUrl("http://www.naturalis.nl");
			creator.setRole(rolecreator);
			creator.setIndividualName(individual);
			creator.setAddress(address);

			/* Element info "provider" */
			Provider provider = new Provider();
			provider.setOrganisation("Naturalis Biodiversity Center");
			provider.setPhone("0102151088");
			provider.setEmailAddress("contact@naturalis.nl");
			provider.setOnlineUrl("http://www.naturalis.nl");
			provider.setRole(roleprovider);
			provider.setIndividualName(individualprovider);
			provider.setAddress(address);

			/* Element info "dataset" */
			Dataset ds = new Dataset();
			ds.setTitle("Naturalis Biodiversity Center(NL)");
			ds.setDescription("Test description");
			ds.setMetadatalanguage("English");
			ds.setResourcelanguage("Multiple language");
			ds.setType("Occurence");
			ds.setSubtype("Specimen");
			ds.setContacts(contact);
			ds.setCreator(creator);
			ds.setProvider(provider);

			/* Header info Element "eml" */
			Eml eml = new Eml();
			eml.add(ds);
			eml.setEmlxmlns("eml://ecoinformatics.org/eml-2.1.1");
			eml.setXmlnsmd("eml://ecoinformatics.org/methods-2.1.1");
			eml.setXmlnsproj("eml://ecoinformatics.org/project-2.1.1");
			eml.setXmlnsd("eml://ecoinformatics.org/dataset-2.1.1");
			eml.setXmlnsres("eml://ecoinformatics.org/resource-2.1.1");
			eml.setXmlnsdc("http://purl.org/dc/terms/");
			eml.setXmlnsxsi("http://www.w3.org/2001/XMLSchema-instance");
			eml.setXsischemaLocation("eml://ecoinformatics.org/eml-2.1.1 http://rs.gbif.org/schema/eml-gbif-profile/1.0.2/eml.xsd");
			eml.setPackageId("2015-02-18");
			eml.setSystem("NBA 1.0");
			eml.setScope("system");
			eml.setXmllang("eng");
			eml.setXmlns("eml");

			JAXBContext jaxbContext = JAXBContext.newInstance(Eml.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			/* set this flag to true to format the output */
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			/*
			 * marshaling of java objects in xml (output to file and standard
			 * output)
			 */
			jaxbMarshaller.marshal(eml, new File(outputdirectory + FILE_NAME_EML));
			jaxbMarshaller.marshal(eml, System.out);

		} catch (JAXBException e)
		{
			e.printStackTrace();
		}

	}

	public static void createZipFiles(String zipFileName)
	{
		ZipDwCA zip = new ZipDwCA();
		try
		{
			logger.info("Creating the zipfile: '" + zipFileName + zipExtension + "'");
			zip.zipDirectory(outputdirectory, zipFileName + zipExtension);
			logger.info("Zipfile '" + zipFileName + zipExtension + "' created successfull.");
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void printHeaderRowAndDataForCSV(String namecollectiontype, String totalsize)
	{
		CsvFileWriter filewriter = null;
		try
		{ /* Create new CSV File object and output File */
			filewriter = new CsvFileWriter(outputdirectory + csvOutPutFile);
			/* Get the result from ElasticSearch */
			if (sourcesystemcode.equals("CRS"))
			{
				list = index.getResultsList(LUCENE_TYPE_SPECIMEN, namecollectiontype, sourcesystemcode,
						Integer.parseInt(totalsize), ESSpecimen.class);
			}
			if (sourcesystemcode.equals("BRAHMS"))
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
					headerRow.add(propertyValue.substring(0, propertyValue.length() - 2));
				listfield.add(propertyName);
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
