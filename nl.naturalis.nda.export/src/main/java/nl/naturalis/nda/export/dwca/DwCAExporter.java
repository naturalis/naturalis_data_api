/*
 *  Description: Create a new DwCA export tool for the NBA
 *  Date: January 28th 2015
 *  Developer: Reinier Kartowikromo
 *  
 */
package nl.naturalis.nda.export.dwca;

import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_SPECIMEN;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow;
import nl.naturalis.nda.export.dwca.Eml;

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
	private static final String dwcUrlTdwgOrg = "http://rs.tdwg.org/dwc/terms/";

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

		/* Create the CSV file */
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

			Files files = new Files();
			files.setLocation("occurence.txt");
			Id id = new Id();
			id.setIndex(0);

			// field.setTerm(dwcUrlTdwgOrg + headerRow.toString());//
			// specimen.getTitle());

			// for (ListIterator<Integer> iter = numbers.listIterator();
			// iter.hasNext(); ) {
			// Integer number = iter.next();
			// iter.add(number+1); // insert a number right before this
			// }
			//

	
			/*
			 * Iterator<String> fieldIterator = headerRow.iterator(); while
			 * (fieldIterator.hasNext()) { // field.add(fieldIterator.next());
			 * field.setTerm(dwcUrlTdwgOrg + fieldIterator.next());
			 * field.setFields("2", headerRow); arglist.add(dwcUrlTdwgOrg +
			 * fieldIterator.next()); }
			 */

			List<String> arg = new ArrayList<String>();
			Iterator<String> fieldIterator = headerRow.iterator();
			while (fieldIterator.hasNext())
			{
				arg.add(dwcUrlTdwgOrg + fieldIterator.next());
			}
			/*Fields fld = new Fields("123", arg);
			JAXBContext jc = JAXBContext.newInstance(Fields.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(fld, new File("test.xml"));
			marshaller.marshal(fld, System.out);
           */
			
			
			Core cores = new Core("2", arg);
			cores.setEncoding("UTF-8");
			cores.setFieldsEnclosedBy("'");
			cores.setFieldsTerminatedBy("\t");
			cores.setLinesTerminatedBy("\r\n");
			cores.setIgnoreHeaderLines("1");
			cores.setRowtype("http://rs.tdwg.org/dwc/terms/Occurrence");
			cores.setFiles(files);
			cores.setId(id);
//			cores.setField(field);
			
			/*Iterator<String> fieldIter = headerRow.iterator();
			while (fieldIter.hasNext())
			{
				Field field = new Field();
				field.setIndex("0");

				field.setFields("2", dwcUrlTdwgOrg + fieldIter.next());
				cores.setField(field);
			}*/

			// List<String> fields = new ArrayList<String>();
			// for (CsvRow i : Arrays.asList(headerRow))
			// fields.addAll(i);

			// Iterator<String> fieldIterator = headerRow.iterator();
			// while (fieldIterator.hasNext())
			// {
			// fields.add(fieldIterator.next());
			// field.setTerm(dwcUrlTdwgOrg + fieldIterator.next());
			// System.out.println(fieldIterator.next());
			//
			// }

			// StringBuilder builder = new StringBuilder();
			// for (ListIterator<String> iter = headerRow.listIterator();
			// iter.hasNext();)
			// {
			//
			// String column = iter.next();
			// if (column != null)
			// {
			// builder.append(column);
			// field.setTerm(dwcUrlTdwgOrg + builder.toString());//
			// column.toString());
			// System.out.println(column);
			// }
			//
			// cores.setField(field);
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
		}

		/* always close the csv writer object after use */
		fileWriter.close();
	}

	private static Meta DwCAXMLToObject()
	{
		try
		{
			JAXBContext context = JAXBContext.newInstance(Meta.class);
			Unmarshaller un = context.createUnmarshaller();
			Meta xmlspecimen = (Meta) un.unmarshal(new File(FILE_NAME));
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
			m.marshal(meta, new File(FILE_NAME));
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

}
