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
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;

import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringEvent;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import nl.naturalis.nda.export.dwca.EMLS;

import org.domainobject.util.debug.BeanPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class DwCAExporter
{
	static final Logger logger = LoggerFactory.getLogger(DwCAExporter.class);
	public static final String CSVComma = "\t";
	private static final String csvOutPutFile = "specimen.txt";
	private static final String FILE_NAME = "meta.xml";
	private static final String FILE_NAME_EML = "eml.xml";

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{

		logger.info("-----------------------------------------------------------------");
		logger.info("Start");
		IndexNative index = new IndexNative(LoadUtil.getESClient(), LoadUtil.getConfig().required(
				"elasticsearch.index.name"));
		try
		{
			DwCAExporter exp = new DwCAExporter(index);
			boolean success = (new File(csvOutPutFile)).delete();
			if (success)
			{
				System.out.println("The file has been successfully deleted");
			}
			exp.ExportDwca();
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

	public void ExportDwca() throws Exception
	{
		// before we open the file check to see if it already exists
		// boolean alreadyExists = new File(csvOutPutFile).exists();
		CsvFileWriter fileWriter = new CsvFileWriter(csvOutPutFile);
		List<ESSpecimen> list = index.getResultsList(LUCENE_TYPE_SPECIMEN, 10, ESSpecimen.class);
		BeanPrinter.out(list);

		CsvFileWriter.CsvRow headerRow = fileWriter.new CsvRow();
		headerRow.add("UnitID");
		headerRow.add("UnitGUID");
		headerRow.add("CollectorsFieldNumber");
		headerRow.add("AssemblageID");
		headerRow.add("SourceInstitutionID");
		headerRow.add("SourceID");
		headerRow.add("Owner");
		headerRow.add("LicenceType");
		headerRow.add("Licence");
		headerRow.add("RecordBasis");
		headerRow.add("KindOfUnit");
		headerRow.add("CollectionType");
		headerRow.add("TypeStatus");
		headerRow.add("Sex");
		headerRow.add("PhaseOrStage");
		headerRow.add("Titles");
		headerRow.add("Notes");
		headerRow.add("PreparationType");
		headerRow.add("NumberOfSpecimen");
		headerRow.add("FromCaptivity");
		headerRow.add("ObjectPublic");
		headerRow.add("MultiMediaPublic");
		headerRow.add("AcquiredFrom");
		headerRow.add("ProjectTitle");
		headerRow.add("WorldRegion");
		headerRow.add("Continent");
		headerRow.add("Country");
		/**
		 * adding header row
		 */
		fileWriter.WriteRow(headerRow);

		for (ESSpecimen specimen : list)
		{
			// pw.println(getCSVRecordFromSpecimen(specimen));

			CsvFileWriter.CsvRow dataRow = fileWriter.new CsvRow();
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
			dataRow.add(specimen.getGatheringEvent().getProjectTitle());
			dataRow.add(specimen.getGatheringEvent().getWorldRegion());
			dataRow.add(specimen.getGatheringEvent().getContinent());
			dataRow.add(specimen.getGatheringEvent().getCountry());

			/**
			 * adding data row
			 */
			fileWriter.WriteRow(dataRow);

			XMLSpecimen xmlspecimen = new XMLSpecimen();
			xmlspecimen.setKindOfUnit(specimen.getKindOfUnit());
			xmlspecimen.setOwner(specimen.getOwner());
			DwCAObjectToXML(xmlspecimen);

			XMLSpecimen specFromFile = DwCAXMLToObject();
			System.out.println(specFromFile.toString());

			CreateEmlObjectToXML();
		}

		/* always close the csv writer object after use */
		fileWriter.close();
		// pw.close();

	}

	private static XMLSpecimen DwCAXMLToObject()
	{
		try
		{
			JAXBContext context = JAXBContext.newInstance(XMLSpecimen.class);
			Unmarshaller un = context.createUnmarshaller();
			XMLSpecimen xmlspecimen = (XMLSpecimen) un.unmarshal(new File(FILE_NAME));
			return xmlspecimen;
		} catch (JAXBException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private static void DwCAObjectToXML(XMLSpecimen specimen)
	{
		try
		{
			JAXBContext context = JAXBContext.newInstance(XMLSpecimen.class);
			Marshaller m = context.createMarshaller();
			// for pretty-print XML in JAXB
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			// Write to File
			m.marshal(specimen, new File(FILE_NAME));
		} catch (JAXBException e)
		{
			e.printStackTrace();
		}
	}


	private static void CreateEmlObjectToXML()
	{
		try
		{
			/* Role info for Contact */
			Role role = new Role();
			role.setPosition("Application Developer");

			/* Name info for Contact */
			IndividualName indi = new IndividualName();
			indi.setGivenname("Reinier");
			indi.setSurname("Kartowikromo");
			
			/* Role info for Creator */
			Role rolecreator = new Role();
			rolecreator.setPosition("Analist");

			/* Name info for Creator */
			IndividualName individual = new IndividualName();
			individual.setGivenname("Wilfred");
			individual.setSurname("Gerritsen");
			
			/* Role info for Provider */
			Role roleprovider = new Role();
			roleprovider.setPosition("Lead Software Developer");

			/* Name info for Provider */
			IndividualName individualprovider = new IndividualName();
			individualprovider.setGivenname("Ayco");
			individualprovider.setSurname("Holleman");
			
			Address address = new Address();
			address.setDeliveryPoint("Darwinweg 2");
			address.setCity("Leiden");
			address.setStateProvince("South Holland");
			address.setCountry("Netherlands");
			address.setPostalCode("NL-2300RA");
			
			Contact contact = new Contact();
			contact.setOrganisation("Naturalis Biodiversity Center");
			contact.setPhone("0102150587");
			contact.setEmailAddress("contact@naturalis.nl");
			contact.setOnlineUrl("http://www.naturalis.nl");
			contact.setRole(role);
			contact.setIndividualName(indi);
			contact.setAddress(address);
			
			Creator creator = new Creator();
			creator.setOrganisation("Naturalis Biodiversity Center");
			creator.setPhone("0102151088");
			creator.setEmailAddress("contact@naturalis.nl");
			creator.setOnlineUrl("http://www.naturalis.nl");
			creator.setRole(rolecreator);
			creator.setIndividualName(individual);
			creator.setAddress(address);
			
			Provider provider = new Provider();
			provider.setOrganisation("Naturalis Biodiversity Center");
			provider.setPhone("0102151088");
			provider.setEmailAddress("contact@naturalis.nl");
			provider.setOnlineUrl("http://www.naturalis.nl");
			provider.setRole(roleprovider);
			provider.setIndividualName(individualprovider);
			provider.setAddress(address);
			
			
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
			
			
			EMLS eml = new EMLS();
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
			
			JAXBContext jaxbContext = JAXBContext.newInstance(EMLS.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			/* set this flag to true to format the output */
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			/*
			 * marshaling of java objects in xml (output to file and standard
			 * output)
			 */
			jaxbMarshaller.marshal(eml, new File("eml.xml"));
			jaxbMarshaller.marshal(eml, System.out);

		} catch (JAXBException e)
		{
			e.printStackTrace();
		}

	}

}
