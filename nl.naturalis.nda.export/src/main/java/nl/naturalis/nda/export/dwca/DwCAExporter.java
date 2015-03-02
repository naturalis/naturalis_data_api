/*
 *  Description: Create a new DwCA export tool for the NBA
 *  Date: January 28th 2015
 *  Developer: Reinier Kartowikromo
 *  
 */
package nl.naturalis.nda.export.dwca;

import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_SPECIMEN;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.zip.ZipOutputStream;

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

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{

		logger.info("-----------------------------------------------------------------");
		logger.info("Start");

		String zipfilename = args[0];
		String namecollectiontype = args[1];
		String totalsize = args[2];

		IndexNative index = new IndexNative(LoadUtil.getESClient(), LoadUtil.getConfig().required(
				"elasticsearch.index.name"));
		try
		{
			DwCAExporter exp = new DwCAExporter(index);
			boolean success = (new File(csvOutPutFile)).delete();
			if (success)
			{
				System.out.println("The file " + csvOutPutFile + " has been successfully deleted");
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
		// before we open the file check to see if it already exists
		// boolean alreadyExists = new File(csvOutPutFile).exists();

		// CsvFileWriter fileWriter = new CsvFileWriter(csvOutPutFile);
		// List<ESSpecimen> list = index.getResultsList(LUCENE_TYPE_SPECIMEN,
		// namecollectiontype,
		// Integer.parseInt(totalsize), ESSpecimen.class);
		//
		// /* Create the CSV file */
		// CsvFileWriter.CsvRow headerRow = fileWriter.new CsvRow();
		// headerRow.add("UnitID");
		// headerRow.add("UnitGUID");
		// headerRow.add("CollectorsFieldNumber");
		// headerRow.add("AssemblageID");
		// headerRow.add("SourceInstitutionID");
		// headerRow.add("SourceID");
		// headerRow.add("Owner");
		// headerRow.add("LicenceType");
		// headerRow.add("Licence");
		// headerRow.add("RecordBasis");
		// headerRow.add("KindOfUnit");
		// headerRow.add("CollectionType");
		// headerRow.add("TypeStatus");
		// headerRow.add("Sex");
		// headerRow.add("PhaseOrStage");
		// headerRow.add("Titles");
		// headerRow.add("Notes");
		// headerRow.add("PreparationType");
		// headerRow.add("NumberOfSpecimen");
		// headerRow.add("FromCaptivity");
		// headerRow.add("ObjectPublic");
		// headerRow.add("MultiMediaPublic");
		// headerRow.add("AcquiredFrom"); // ToDo
		// headerRow.add("ProjectTitle");
		// headerRow.add("WorldRegion");
		// headerRow.add("Continent");
		// headerRow.add("Country");
		// headerRow.add("ISO3166Code");
		// headerRow.add("ProvinceState");
		// headerRow.add("Island");
		// headerRow.add("Locality");
		// headerRow.add("City");
		// headerRow.add("Sublocality");
		// headerRow.add("LocalityText");
		// headerRow.add("DateTimeBegin");
		// headerRow.add("DateTimeEnd");
		// headerRow.add("Method");
		// headerRow.add("Altitude");
		// headerRow.add("AltitudeUnifOfMeasurement");
		// headerRow.add("Depth");
		// headerRow.add("depthUnitOfMeasurement");
		// headerRow.add("FullName");
		// headerRow.add("GatheringOrganizations");
		// headerRow.add("LongitudeDecimal");
		// headerRow.add("LatitudeDecimal");
		//
		// /**
		// * adding header row
		// */
		// fileWriter.WriteRow(headerRow);
		//
		// for (ESSpecimen specimen : list)
		// {
		// CsvFileWriter.CsvRow dataRow = fileWriter.new CsvRow();
		// dataRow.add(specimen.getUnitID());
		// dataRow.add(specimen.getUnitGUID());
		// dataRow.add(specimen.getCollectorsFieldNumber());
		// dataRow.add(specimen.getAssemblageID());
		// dataRow.add(specimen.getSourceInstitutionID());
		// dataRow.add(specimen.getSourceID());
		// dataRow.add(specimen.getOwner());
		// dataRow.add(specimen.getLicenceType());
		// dataRow.add(specimen.getLicence());
		// dataRow.add(specimen.getRecordBasis());
		// dataRow.add(specimen.getKindOfUnit());
		// dataRow.add(specimen.getCollectionType());
		// dataRow.add(specimen.getTypeStatus());
		// dataRow.add(specimen.getSex());
		// dataRow.add(specimen.getPhaseOrStage());
		// dataRow.add(specimen.getTitle());
		// dataRow.add(specimen.getNotes());
		// dataRow.add(specimen.getPreparationType());
		// dataRow.add(Integer.toString(specimen.getNumberOfSpecimen()));
		// dataRow.add(String.valueOf(specimen.isFromCaptivity()));
		// dataRow.add(Boolean.toString(specimen.isObjectPublic()));
		// dataRow.add(Boolean.toString(specimen.isMultiMediaPublic()));
		// if (specimen.getAcquiredFrom() != null)
		// {
		// dataRow.add(specimen.getAcquiredFrom().getAgentText());
		// }
		// dataRow.add(specimen.getGatheringEvent().getProjectTitle());
		// dataRow.add(specimen.getGatheringEvent().getWorldRegion());
		// dataRow.add(specimen.getGatheringEvent().getContinent());
		// dataRow.add(specimen.getGatheringEvent().getCountry());
		// dataRow.add(specimen.getGatheringEvent().getIso3166Code());
		// dataRow.add(specimen.getGatheringEvent().getProvinceState());
		// dataRow.add(specimen.getGatheringEvent().getIsland());
		// dataRow.add(specimen.getGatheringEvent().getLocality());
		// dataRow.add(specimen.getGatheringEvent().getCity());
		// dataRow.add(specimen.getGatheringEvent().getSublocality());
		// dataRow.add(specimen.getGatheringEvent().getLocalityText());
		// if (specimen.getGatheringEvent().getDateTimeBegin() != null)
		// {
		// SimpleDateFormat datetimebegin = new SimpleDateFormat("yyyy-MM-dd");
		// String datebegin =
		// datetimebegin.format(specimen.getGatheringEvent().getDateTimeBegin());
		// dataRow.add(datebegin);
		// }
		// if (specimen.getGatheringEvent().getDateTimeEnd() != null)
		// {
		// SimpleDateFormat datetimenend = new SimpleDateFormat("yyyy-MM-dd");
		// String dateEnd =
		// datetimenend.format(specimen.getGatheringEvent().getDateTimeEnd());
		// dataRow.add(dateEnd);
		// }
		// dataRow.add(specimen.getGatheringEvent().getMethod());
		// dataRow.add(specimen.getGatheringEvent().getAltitude());
		// dataRow.add(specimen.getGatheringEvent().getAltitudeUnifOfMeasurement());
		// dataRow.add(specimen.getGatheringEvent().getDepth());
		// dataRow.add(specimen.getGatheringEvent().getDepthUnitOfMeasurement());
		// if (specimen.getGatheringEvent().getGatheringPersons() != null)
		// {
		// dataRow.add(specimen.getGatheringEvent().getGatheringPersons().iterator().next()
		// .getFullName());
		// }
		// if (specimen.getGatheringEvent().getGatheringOrganizations() != null)
		// {
		// dataRow.add(specimen.getGatheringEvent().getGatheringOrganizations().iterator().next()
		// .getName());
		// }
		// if (specimen.getGatheringEvent().getSiteCoordinates() != null)
		// {
		// if
		// (specimen.getGatheringEvent().getSiteCoordinates().iterator().next().getLatitudeDecimal()
		// != null)
		// {
		// dataRow.add(Double.toString(specimen.getGatheringEvent().getSiteCoordinates().iterator()
		// .next().getLatitudeDecimal()));
		// }
		// if
		// (specimen.getGatheringEvent().getSiteCoordinates().iterator().next().getLongitudeDecimal()
		// != null)
		// {
		// dataRow.add(Double.toString(specimen.getGatheringEvent().getSiteCoordinates().iterator()
		// .next().getLongitudeDecimal()));
		// }
		// }
		//
		// /**
		// * adding data row
		// */
		// fileWriter.WriteRow(dataRow);
		// }
		// fileWriter.close();

		printHeaderRowForCSV(namecollectiontype, totalsize);

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
		// Integer cnt = new Integer(0);
		// Iterator<String> fieldIter = headerRow.iterator();
		// while (fieldIter.hasNext())
		// {
		// cnt = Integer.valueOf(cnt.intValue() + 1);
		// Field field = new Field(cnt.toString(), dwcUrlTdwgOrg +
		// fieldIter.next());
		// cores.addField(field);
		//
		// }

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
			Meta xmlspecimen = (Meta) un.unmarshal(new File(FILE_NAME_META));
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
			m.marshal(meta, new File(FILE_NAME_META));
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
			jaxbMarshaller.marshal(eml, new File(FILE_NAME_EML));
			jaxbMarshaller.marshal(eml, System.out);

		} catch (JAXBException e)
		{
			e.printStackTrace();
		}

	}

	public static void createZipFiles(String zipFileName) throws FileNotFoundException
	{
		try
		{
			String zipfilename = zipFileName + zipExtension;
			FileOutputStream fos = new FileOutputStream(zipfilename);
			ZipOutputStream zos = new ZipOutputStream(fos);

			StringUtilities.addToZipFile(FILE_NAME_META, zos);
			StringUtilities.addToZipFile(FILE_NAME_EML, zos);
			StringUtilities.addToZipFile(csvOutPutFile, zos);

			zos.close();
			fos.close();
			System.out.println("Zipfile '" + zipfilename + "' created successfull.");

		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void printHeaderRowForCSV(String namecollectiontype, String totalsize)
	{
		CsvFileWriter filewriter;
		Properties prop = new Properties();
		InputStream inputStream = null;
		try
		{
			inputStream = getClass().getClassLoader().getResourceAsStream(MAPPING_FILE_NAME);
			filewriter = new CsvFileWriter(csvOutPutFile);
			List<ESSpecimen> list = index.getResultsList(LUCENE_TYPE_SPECIMEN, namecollectiontype,
					Integer.parseInt(totalsize), ESSpecimen.class);
	
			CsvFileWriter.CsvRow headerRow = filewriter.new CsvRow();
			if (inputStream == null)
			{
				System.out.println("property file '" + MAPPING_FILE_NAME + "' not found in the classpath");
				return;
			}
			prop.load(inputStream);
			
			Enumeration<?> enuKeys = prop.keys();
			while (enuKeys.hasMoreElements())
			{
				String key = (String) enuKeys.nextElement();
				String value = prop.getProperty(key);
				headerRow.add(value);
				Collections.sort(headerRow);
			}
			
			Properties configFile  = new Properties();
			try 
			{
				configFile.load(getClass().getClassLoader().getResourceAsStream(MAPPING_FILE_NAME));
				Enumeration<?> e  = configFile.keys();
				while (e.hasMoreElements())
				{
					String key = (String) e.nextElement();
					System.out.println(key);
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			Properties systemProperties = new Properties();
			systemProperties.load(getClass().getClassLoader().getResourceAsStream(MAPPING_FILE_NAME));
			SortedMap sortedSystemProperties = new TreeMap(systemProperties);
			Set keySet = sortedSystemProperties.keySet();
			Iterator iterator = keySet.iterator();
			while (iterator.hasNext()) {
				String propertyName = (String) iterator.next();
				String propertyValue = systemProperties.getProperty(propertyName);
				System.out.println(propertyName + ": " + propertyValue);
			}
			

			filewriter.WriteRow(headerRow);

			for (ESSpecimen specimen : list)
			{
				CsvFileWriter.CsvRow dataRow = filewriter.new CsvRow();
				dataRow.add(specimen.getUnitID());
				dataRow.add(specimen.getUnitGUID());
				dataRow.add(specimen.getCollectorsFieldNumber());
				dataRow.add(specimen.getAssemblageID());
				dataRow.add(specimen.getSourceInstitutionID());
				dataRow.add(specimen.getSourceID());
				dataRow.add(specimen.getOwner());
				dataRow.add(specimen.getLicenceType());
				dataRow.add(specimen.getLicence());
				dataRow.add(specimen.getRecordBasis());
				dataRow.add(specimen.getKindOfUnit());
				dataRow.add(specimen.getCollectionType());
				dataRow.add(specimen.getTypeStatus());
				dataRow.add(specimen.getSex());
				dataRow.add(specimen.getPhaseOrStage());
				dataRow.add(specimen.getTitle());
				dataRow.add(specimen.getNotes());
				dataRow.add(specimen.getPreparationType());
				dataRow.add(Integer.toString(specimen.getNumberOfSpecimen()));
				dataRow.add(String.valueOf(specimen.isFromCaptivity()));
				dataRow.add(Boolean.toString(specimen.isObjectPublic()));
				dataRow.add(Boolean.toString(specimen.isMultiMediaPublic()));
				if (specimen.getAcquiredFrom() != null)
				{
					dataRow.add(specimen.getAcquiredFrom().getAgentText());
				}
				dataRow.add(specimen.getGatheringEvent().getProjectTitle());
				dataRow.add(specimen.getGatheringEvent().getWorldRegion());
				dataRow.add(specimen.getGatheringEvent().getContinent());
				dataRow.add(specimen.getGatheringEvent().getCountry());
				dataRow.add(specimen.getGatheringEvent().getIso3166Code());
				dataRow.add(specimen.getGatheringEvent().getProvinceState());
				dataRow.add(specimen.getGatheringEvent().getIsland());
				dataRow.add(specimen.getGatheringEvent().getLocality());
				dataRow.add(specimen.getGatheringEvent().getCity());
				dataRow.add(specimen.getGatheringEvent().getSublocality());
				dataRow.add(specimen.getGatheringEvent().getLocalityText());
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
				dataRow.add(specimen.getGatheringEvent().getMethod());
				dataRow.add(specimen.getGatheringEvent().getAltitude());
				dataRow.add(specimen.getGatheringEvent().getAltitudeUnifOfMeasurement());
				dataRow.add(specimen.getGatheringEvent().getDepth());
				dataRow.add(specimen.getGatheringEvent().getDepthUnitOfMeasurement());
				if (specimen.getGatheringEvent().getGatheringPersons() != null)
				{
					dataRow.add(specimen.getGatheringEvent().getGatheringPersons().iterator().next()
							.getFullName());
				}
				if (specimen.getGatheringEvent().getGatheringOrganizations() != null)
				{
					dataRow.add(specimen.getGatheringEvent().getGatheringOrganizations().iterator().next()
							.getName());
				}
				if (specimen.getGatheringEvent().getSiteCoordinates() != null)
				{
					if (specimen.getGatheringEvent().getSiteCoordinates().iterator().next()
							.getLatitudeDecimal() != null)
					{
						dataRow.add(Double.toString(specimen.getGatheringEvent().getSiteCoordinates()
								.iterator().next().getLatitudeDecimal()));
					}
					if (specimen.getGatheringEvent().getSiteCoordinates().iterator().next()
							.getLongitudeDecimal() != null)
					{
						dataRow.add(Double.toString(specimen.getGatheringEvent().getSiteCoordinates()
								.iterator().next().getLongitudeDecimal()));
					}
				}

				/**
				 * adding data row
				 */
				filewriter.WriteRow(dataRow);
			}
			filewriter.close();


		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally
		{
			if (inputStream != null)
			{
				try
				{
					inputStream.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}

		}
	}

}
