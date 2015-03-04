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
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import nl.naturalis.nda.export.dwca.Eml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.naturalis.nda.export.dwca.StringUtilities;

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
	private static final String MAPPING_FILE_NAME = "Specimen.properties";
	CsvFileWriter.CsvRow headerRow = null;
	static String outputdirectory = null;
	static String sourcesystemcode = null;
	static String zipoutputdirectory = null;

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

		/* Get the arguments: "OutPut", "Size", "Mammalia" */
		logger.info("Start reading properties value from OutPut.properties");
		/* args[1] get the total records from ElasticSearch */
		String totalsize = StringUtilities.readPropertyvalue(args[0], args[1]);
		/* args[2] Get the Collectionname */
		String namecollectiontype = StringUtilities.readPropertyvalue(args[2], "Collectiontype");
		/* Get the SourceSystem: CRS or BRAHMS, COL etc. */
		sourcesystemcode = StringUtilities.readPropertyvalue(args[2], "sourceSystem.code");

		/* Output directory for the files EML.xml, Meta.xml and Ocurrence.txt */
		outputdirectory = StringUtilities.readPropertyvalue(args[0], "Directory") + "\\";

		zipoutputdirectory = StringUtilities.readPropertyvalue(args[0], "ZipDirectory") + "\\";

		/* Get the directory and zipfilename */
		String zipfilename = zipoutputdirectory + namecollectiontype;

		logger.info("Loading Elastcisearch");
		IndexNative index = new IndexNative(LoadUtil.getESClient(), LoadUtil.getConfig().required(
				"elasticsearch.index.name"));
		try
		{
			DwCAExporter exp = new DwCAExporter(index);
			/* Delete the CSV file if Exists */
			logger.info("Delete file Occurence.txt");
			boolean success = (new File(outputdirectory + csvOutPutFile)).delete();
			if (success)
			{
				logger.info("The file " + csvOutPutFile + " has been successfully deleted");
//				System.out.println("The file " + csvOutPutFile + " has been successfully deleted");
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
		cores.setLinesTerminatedBy("\r\n");
		cores.setIgnoreHeaderLines("1");
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

		Meta specFromFile = DwCAXMLToObject();
		System.out.println(specFromFile.toString());

		/* Create "EML" xml file */
		logger.info("Creating the EML.xml file.");
		CreateEmlObjectToXML();

		/* Create the zipfile with a given filename */
		createZipFiles(zipFileName);
		/* always close the csv writer object after use */

	}

	private static Meta DwCAXMLToObject()
	{
		try
		{
			JAXBContext context = JAXBContext.newInstance(Meta.class);
			Unmarshaller un = context.createUnmarshaller();
			Meta xmlspecimen = (Meta) un.unmarshal(new File(outputdirectory + FILE_NAME_META));
			return xmlspecimen;
		} catch (JAXBException e)
		{
			e.printStackTrace();
		}
		return null;
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
			zip.zipDirectory(outputdirectory, zipFileName + zipExtension);
			System.out.println("Zipfile '" +  zipFileName + "' created successfull.");
		} catch (IOException e)
		{
			e.printStackTrace();
		}

//		try
//		{
//			String zipfilename = zipFileName + zipExtension;
//			FileOutputStream fos = new FileOutputStream(zipfilename);
//			ZipOutputStream zos = new ZipOutputStream(fos);
//
//			StringUtilities.addToZipFile(outputdirectory + FILE_NAME_META, zos);
//			StringUtilities.addToZipFile(outputdirectory + FILE_NAME_EML, zos);
//			StringUtilities.addToZipFile(outputdirectory + csvOutPutFile, zos);
//
//			zos.close();
//			fos.close();
//			System.out.println("Zipfile '" + zipfilename + "' created successfull.");
//
//		} catch (FileNotFoundException e)
//		{
//			e.printStackTrace();
//		} catch (IOException e)
//		{
//			e.printStackTrace();
//		}
	}

	private void printHeaderRowAndDataForCSV(String namecollectiontype, String totalsize)
	{
		CsvFileWriter filewriter = null;
		try
		{ /* Create new CSV File object and output File */
			filewriter = new CsvFileWriter(outputdirectory + csvOutPutFile);
			/* Get the result from ElasticSearch */
			List<ESSpecimen> list = index.getResultsList(LUCENE_TYPE_SPECIMEN, namecollectiontype,
					sourcesystemcode, Integer.parseInt(totalsize), ESSpecimen.class);

			headerRow = filewriter.new CsvRow();

			Properties configFile = new Properties();
			try
			{ /* load the values from the properties file */
				logger.info("Load '"+ MAPPING_FILE_NAME + "' mappingscheme.");
				configFile.load(getClass().getClassLoader().getResourceAsStream(MAPPING_FILE_NAME));
			} catch (IOException e)
			{
				logger.info("Fault: property file '" + MAPPING_FILE_NAME + "' not found in the classpath");
				//System.out.println("property file '" + MAPPING_FILE_NAME + "' not found in the classpath");
				return;
			}
			/* Sort the value from the properties file when loaded */
			SortedMap<Object, Object> sortedSystemProperties = new TreeMap<Object, Object>(configFile);
			Set<?> keySet = sortedSystemProperties.keySet();
			Iterator<?> iterator = keySet.iterator();
			while (iterator.hasNext())
			{
				String propertyName = (String) iterator.next();
				String propertyValue = configFile.getProperty(propertyName);
				/* Add the headers to the CSV File */
				headerRow.add(propertyValue);
				System.out.println(propertyName + ": " + propertyValue);
			}

			/* Write the headers columns */
			logger.info("Writing headers row to the Occurence.txt file.");
			filewriter.WriteRow(headerRow);

			/* Add the value from ElasticSearch to the CSV File */
			logger.info("Writing values from ElasticSearch to the Occurence.txt file.");
			for (ESSpecimen specimen : list)
			{
				CsvFileWriter.CsvRow dataRow = filewriter.new CsvRow();
				if (specimen.getUnitID() != null)
				{
					dataRow.add(specimen.getUnitID());
				}
				dataRow.add(specimen.getCollectionType());
				dataRow.add(specimen.getSourceInstitutionID());
				dataRow.add(specimen.getRecordBasis());
				dataRow.add(specimen.getPreparationType());
				dataRow.add(specimen.getTypeStatus());
				dataRow.add(specimen.getSex());
				dataRow.add(specimen.getPhaseOrStage());
				dataRow.add(Integer.toString(specimen.getNumberOfSpecimen()));
				dataRow.add(specimen.getGatheringEvent().getWorldRegion());
				dataRow.add(specimen.getGatheringEvent().getCountry());
				dataRow.add(specimen.getGatheringEvent().getLocality());
				dataRow.add(specimen.getGatheringEvent().getProvinceState());
				dataRow.add(specimen.getGatheringEvent().getIsland());
				/* ToDo: GetCity moet County worden. */
				dataRow.add(specimen.getGatheringEvent().getCity());
				if (specimen.getGatheringEvent().getDateTimeBegin() != null)
				{
					SimpleDateFormat datetimebegin = new SimpleDateFormat("yyyy-MM-dd");
					String datebegin = datetimebegin.format(specimen.getGatheringEvent().getDateTimeBegin());
					dataRow.add(datebegin);
				}
				if (specimen.getGatheringEvent().getDateTimeEnd() != null)
				{
					SimpleDateFormat datetimenend = new SimpleDateFormat("yyyy-MM-dd");
					String dateEnd = datetimenend.format(specimen.getGatheringEvent().getDateTimeEnd());
					dataRow.add(dateEnd);
				}
				dataRow.add(specimen.getGatheringEvent().getDepth());
				dataRow.add(specimen.getGatheringEvent().getAltitudeUnifOfMeasurement());
				if (specimen.getGatheringEvent().getGatheringPersons() != null)
				{
					dataRow.add(specimen.getGatheringEvent().getGatheringPersons().iterator().next()
							.getFullName());
				}
				if (specimen.getGatheringEvent().getSiteCoordinates() != null)
				{
					if (specimen.getGatheringEvent().getSiteCoordinates().iterator().next()
							.getLongitudeDecimal() != null)
					{
						dataRow.add(Double.toString(specimen.getGatheringEvent().getSiteCoordinates()
								.iterator().next().getLongitudeDecimal()));
					}
					if (specimen.getGatheringEvent().getSiteCoordinates().iterator().next()
							.getLatitudeDecimal() != null)
					{
						dataRow.add(Double.toString(specimen.getGatheringEvent().getSiteCoordinates()
								.iterator().next().getLatitudeDecimal()));
					}
				}
				dataRow.add(specimen.getIdentifications().iterator().next().getScientificName()
						.getAuthorshipVerbatim());
				dataRow.add(specimen.getIdentifications().iterator().next().getScientificName()
						.getSpecificEpithet());
				dataRow.add(specimen.getIdentifications().iterator().next().getScientificName()
						.getFullScientificName());
				dataRow.add(specimen.getIdentifications().iterator().next().getScientificName()
						.getAuthorshipVerbatim());
				if (specimen.getIdentifications().iterator().next().getDateIdentified() != null)
				{
					SimpleDateFormat dateidentified = new SimpleDateFormat("yyyy-MM-dd");
					String dateiden = dateidentified.format(specimen.getIdentifications().iterator().next()
							.getDateIdentified());
					dataRow.add(dateiden);
				}
				dataRow.add(specimen.getIdentifications().iterator().next().getScientificName()
						.getGenusOrMonomial());
				dataRow.add(specimen.getIdentifications().iterator().next().getScientificName()
						.getInfraspecificEpithet());
				if (specimen.getIdentifications().iterator().next().getTaxonRank() != null)
				{
					dataRow.add(specimen.getIdentifications().iterator().next().getTaxonRank());
				}
				dataRow.add(specimen.getIdentifications().iterator().next().getScientificName().getSubgenus());
				/**
				 * adding data row
				 */
				filewriter.WriteRow(dataRow);
			}
			// filewriter.close();
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
