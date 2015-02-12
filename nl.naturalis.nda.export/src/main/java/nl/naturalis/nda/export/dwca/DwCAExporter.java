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
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringEvent;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;

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

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
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

	public void ExportDwca() throws IOException
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

	// private String getCSVRecordFromSpecimen(ESSpecimen specimen) throws
	// IOException
	// {
	// String result = null;
	// result = specimen.getUnitID() + CSVComma + specimen.getUnitGUID() +
	// CSVComma
	// + specimen.getCollectorsFieldNumber() + CSVComma +
	// specimen.getAssemblageID() + CSVComma
	// + specimen.getSourceInstitutionID() + CSVComma + specimen.getSourceID() +
	// CSVComma
	// + specimen.getOwner() + CSVComma + specimen.getLicenceType() + CSVComma
	// + specimen.getLicence() + CSVComma + specimen.getRecordBasis() + CSVComma
	// + specimen.getKindOfUnit() + CSVComma + specimen.getCollectionType() +
	// CSVComma
	// + specimen.getTypeStatus() + CSVComma + specimen.getSex() + CSVComma
	// + specimen.getPhaseOrStage() + CSVComma + specimen.getTitle() + CSVComma
	// + specimen.getNotes() + CSVComma + specimen.getPreparationType() +
	// CSVComma
	// + specimen.getNumberOfSpecimen() + CSVComma +
	// String.valueOf(specimen.isFromCaptivity())
	// + CSVComma + Boolean.toString(specimen.isObjectPublic()) + CSVComma
	// + Boolean.toString(specimen.isMultiMediaPublic()) + CSVComma
	// + specimen.getGatheringEvent().getProjectTitle();
	//
	// // ArrayList<String> list = new ArrayList<String>();
	// // list.add(ESGatheringEvent.class.toString());
	// // for (String each : list)
	// // {
	// // resGathering = each + "\t";
	// //
	// // }
	// return result;
	// }

}
