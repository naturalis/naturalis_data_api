package nl.naturalis.nda.export.dwca;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.naturalis.nda.domain.Agent;
import nl.naturalis.nda.domain.Person;
import nl.naturalis.nda.domain.SpecimenIdentification;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringSiteCoordinates;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow;

@SuppressWarnings("unused")
public class Fieldmapping {
	
	static final Logger logger = LoggerFactory.getLogger(DwCAExporter.class);
	private static final String EMPTY_STRING = "";
	
	static SimpleDateFormat datetimebegin = new SimpleDateFormat("yyyy-MM-dd");
	static SimpleDateFormat datetimenend = new SimpleDateFormat("yyyy-MM-dd");
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	/* Get ID value */
	public static void setDummyValue(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		/* 00_Dummy0 is ID */
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "00_Dummy0"))
		{
			if (specimen.getSourceSystemId() != null)
			{
				dataRow.add(specimen.getSourceSystemId());  
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
		
	}
	
	/* Get RecordBasis value for Zoology and Geology */
	public static void setBasisOfRecord(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "01_RecordBasis"))
		{
			if (specimen.getRecordBasis() != null) 
			{
				dataRow.add(specimen.getRecordBasis());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get BasisOf record value For BRAHMS */
	public static void setBasisOfRecord_Brahms(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "01_Dummy1"))
		{
			if (specimen.getRecordBasis() != null &&
				specimen.getRecordBasis().contains("photo(copy) of herbarium sheet") && 
				specimen.getRecordBasis().contains("Illustration") &&
				specimen.getRecordBasis().contains("Photographs, negatives") &&
				specimen.getRecordBasis().contains("DNA sample from sheet") &&
				specimen.getRecordBasis().contains("Slides") &&
				specimen.getRecordBasis().contains("Observation"))
			{
				System.out.println("Not included in Brahms CSV: " + specimen.getRecordBasis());
				logger.info("Not included in Brahms CSV: " + specimen.getRecordBasis());
				return;
			}
			else
			if (specimen.getRecordBasis() != null)
			{
				dataRow.add("PreservedSpecimen");
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	
	/*Get catalogNumber value */
	public static void setCatalogNumber(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "02_SourceSystemId"))
		{
			if (specimen.getSourceSystemId() != null)
			{
				dataRow.add(specimen.getSourceSystemId());  
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get ClassName value for Zoology and Geology */
	public static void setClassName(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "03_ClassName"))
		{
			if (specimen.getIdentifications().iterator().next().getDefaultClassification() != null &&
				specimen.getIdentifications().iterator().next().isPreferred() == true)
			{
				dataRow.add(specimen.getIdentifications().iterator().next().getDefaultClassification().getClassName());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get Classname for BRAHMS */
	public static void setClassName_Brahms(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		/* 03_DummyDefault class */
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "03_DummyDefault"))
		{
			dataRow.add(EMPTY_STRING);
		}
	}
	
	/* Get CollectionType value for Zoology and Geology */
	public static void setCollectionType(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "04_CollectionType"))
		{
			if (specimen.getCollectionType() != null)
			{
				dataRow.add(specimen.getCollectionType());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get CollectionCode value for Brahms */
	public static void setCollectionCode_Brahms(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "04_DummyDefault"))
		{
			dataRow.add("Botany");
		}
	}
	
	/* Get Continent value */
	public static void setContinent(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "05_Continent"))
		{
			if (specimen.getGatheringEvent().getWorldRegion() != null)
			{
				/* NDA-303/372
				 * dataRow.add(strutil.convertStringToUTF8(specimen.getGatheringEvent().getWorldRegion()));
				 */
				dataRow.add(specimen.getGatheringEvent().getWorldRegion());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get Country value */
	public static void setCountry(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "06_Country"))
		{
			if(specimen.getGatheringEvent().getCountry() != null)
			{
				/*NDA-303/372
				dataRow.add(strutil.convertStringToUTF8(specimen.getGatheringEvent().getCountry()));
				*/
				dataRow.add(specimen.getGatheringEvent().getCountry());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get County value */
	public static void setCounty(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "07_County"))
		{
			if(specimen.getGatheringEvent().getCity() != null)
			{
				/*NDA-303/372
				dataRow.add(strutil.convertStringToUTF8(specimen.getGatheringEvent().getCity()));
				*/
				dataRow.add(specimen.getGatheringEvent().getCity());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get DateIndentified value for Zoology and Geology */
	public static void setDateIndentified(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "08_DateIdentified"))
		{
			if (specimen.getIdentifications().iterator().next().getDateIdentified() != null &&
				specimen.getIdentifications().iterator().next().isPreferred() == true)
			{
				SimpleDateFormat dateidentified = new SimpleDateFormat("yyyy-MM-dd");
				String dateiden = dateidentified.format(specimen.getIdentifications().iterator().next().getDateIdentified());
				dataRow.add(dateiden);
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get DateIndentified value for Brahms */
	public static void setDateIndentified_Brahms(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "08_DateIdentified"))
		{
			if (specimen.getIdentifications().iterator().next().getDateIdentified() != null)
			{
				SimpleDateFormat dateidentified = new SimpleDateFormat("yyyy-MM-dd");
				String dateiden = dateidentified.format(specimen.getIdentifications().iterator().next().getDateIdentified());
				dataRow.add(dateiden);
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get LatitudeDecimal value */
	public static void setLatitudeDecimal(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "09_LatitudeDecimal"))
		{
			if (specimen.getGatheringEvent().getSiteCoordinates() != null)
			{
				if (specimen.getGatheringEvent().getSiteCoordinates().iterator().next().getLatitudeDecimal() != null)
				{
					dataRow.add(String.format("%s", specimen.getGatheringEvent().getSiteCoordinates()
						.iterator().next().getLatitudeDecimal()));
				}
				else
				{
					dataRow.add(EMPTY_STRING);
				}
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get LongituDecimal value */
	public static void setLongitudeDecimal(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "10_LongitudeDecimal"))
		{
			if (specimen.getGatheringEvent().getSiteCoordinates() != null)
			{
				if (specimen.getGatheringEvent().getSiteCoordinates().iterator().next().getLongitudeDecimal() != null)
				{
					dataRow.add(String.format("%s", specimen.getGatheringEvent().getSiteCoordinates()
							.iterator().next().getLongitudeDecimal()));
				}
				else
				{
					dataRow.add(EMPTY_STRING);
				}
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get Eventdate value for Zoology and Geology */
	public static void setEvendate(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "11_Dummy1"))
		{
			/* if BeginDate and EndDate both has values and not equal then get the value of the BeginDate and EndDate */
			if (specimen.getGatheringEvent().getDateTimeBegin() != null && specimen.getGatheringEvent().getDateTimeEnd() != null &&
				!specimen.getGatheringEvent().getDateTimeBegin().equals(specimen.getGatheringEvent().getDateTimeEnd()))
			{
				String dateBegin = datetimebegin.format(specimen.getGatheringEvent().getDateTimeBegin());
				String dateEnd = datetimenend.format(specimen.getGatheringEvent().getDateTimeEnd());
				dataRow.add(dateBegin + " | " + dateEnd);
			}
			/* if BeginDate is equal to EndDate then only the value of BeginDate */
			else if (specimen.getGatheringEvent().getDateTimeBegin() != null && 
					specimen.getGatheringEvent().getDateTimeBegin() == specimen.getGatheringEvent().getDateTimeEnd())
			{
				String dateBegin = datetimebegin.format(specimen.getGatheringEvent().getDateTimeBegin());
				dataRow.add(dateBegin);
			}
			/* if only begindate has a value then get the value of begindate */
			else if(specimen.getGatheringEvent().getDateTimeBegin() != null && specimen.getGatheringEvent().getDateTimeEnd() == null)
			{
				String dateBegin = datetimebegin.format(specimen.getGatheringEvent().getDateTimeBegin());
				dataRow.add(dateBegin);
			}
			/* if EndDate has a value and Begindate has no value set the value of null for Enddate */
			else if (specimen.getGatheringEvent().getDateTimeEnd() != null && specimen.getGatheringEvent().getDateTimeBegin() == null)
			{
				dataRow.add(EMPTY_STRING);
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	
	/* Get Eventdate value for Brahms */
	public static void setEvendate_Brahms(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "11_Dummy2"))
		{
			/* if BeginDate and EndDate both has values and not equal then get the value of the BeginDate and EndDate */
			if (specimen.getGatheringEvent().getDateTimeBegin() != null && specimen.getGatheringEvent().getDateTimeEnd() != null &&
				!specimen.getGatheringEvent().getDateTimeBegin().equals(specimen.getGatheringEvent().getDateTimeEnd()))
			{
				String dateBegin = datetimebegin.format(specimen.getGatheringEvent().getDateTimeBegin());
				String dateEnd = datetimenend.format(specimen.getGatheringEvent().getDateTimeEnd());
				dataRow.add(dateBegin + " | " + dateEnd);
			}
			/* if BeginDate is equal to EndDate then only the value of BeginDate */
			else if (specimen.getGatheringEvent().getDateTimeBegin() != null && 
					specimen.getGatheringEvent().getDateTimeBegin() == specimen.getGatheringEvent().getDateTimeEnd())
			{
				String dateBegin = datetimebegin.format(specimen.getGatheringEvent().getDateTimeBegin());
				dataRow.add(dateBegin);
			}
			/* if only begindate has a value then get the value of begindate */
			else if(specimen.getGatheringEvent().getDateTimeBegin() != null && specimen.getGatheringEvent().getDateTimeEnd() == null)
			{
				String dateBegin = datetimebegin.format(specimen.getGatheringEvent().getDateTimeBegin());
				dataRow.add(dateBegin);
			}
			/* if EndDate has a value and Begindate has no value set the value of null for Enddate */
			else if (specimen.getGatheringEvent().getDateTimeEnd() != null && specimen.getGatheringEvent().getDateTimeBegin() == null)
			{
				dataRow.add(EMPTY_STRING);
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}

	/* Get Family value for Zoology and Geology */
	public static void setFamily(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "12_Family"))
		{
			if (specimen.getIdentifications().iterator().next().getDefaultClassification().getFamily() != null &&
				specimen.getIdentifications().iterator().next().isPreferred() == true)
			{
				dataRow.add(specimen.getIdentifications().iterator().next().getDefaultClassification().getFamily());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get Family value for Brahms */
	public static void setFamily_Brahms(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "12_DummyDefault"))
		{
			if(specimen.getIdentifications().iterator().next().getDefaultClassification().getFamily() != null)
			{
				dataRow.add(specimen.getIdentifications().iterator().next().getDefaultClassification().getFamily());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}

	/* Get Genus value for Zoology and Geology  */
	public static void setGenus(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "13_GenusOrMonomial"))
		{
			if(specimen.getIdentifications().iterator().next().getScientificName().getGenusOrMonomial() != null &&
			   specimen.getIdentifications().iterator().next().isPreferred() == true)
			{
				dataRow.add(specimen.getIdentifications().iterator().next().getScientificName().getGenusOrMonomial());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get Genus value for Brahms */
	public static void setGenus_Brahms(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "13_GenusOrMonomial"))
		{
			if(specimen.getIdentifications().iterator().next().getScientificName().getGenusOrMonomial() != null)
			{
				dataRow.add(specimen.getIdentifications().iterator().next().getScientificName().getGenusOrMonomial());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get Geodeticdatum value */
	public static void setGeodeticDatum(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "14_DummyDefault"))
		{
			dataRow.add(EMPTY_STRING);
		}
	}
	
	/* Get Habitat value */
	public static void setHabitat(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "15_DummyDefault"))
		{
			dataRow.add(EMPTY_STRING);
		}
	}
	
	/* Get HigherClassification value for Zoology and Geology */
	public static void setHigherClassification(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "16_Dummy2"))
		{
			dataRow.add(EMPTY_STRING);
		}
	}
	
	/* Get HigherClassification value for Brahms */
	public static void setHigherClassification_Brahms(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "16_Dummy3"))
		{
			if (specimen.getIdentifications().iterator().next().getDefaultClassification() != null)
			{
				String kingdom = specimen.getIdentifications().iterator().next().getDefaultClassification().getKingdom();
				String classname = specimen.getIdentifications().iterator().next().getDefaultClassification().getClassName();
				String order = specimen.getIdentifications().iterator().next().getDefaultClassification().getOrder();
				String family = specimen.getIdentifications().iterator().next().getDefaultClassification().getFamily();
				if (kingdom != null && classname != null && order != null && family != null)
				{
					dataRow.add(kingdom + "|" + classname + "|" + order + "|" + family);
				}
				else if (kingdom == null)
				{
					dataRow.add(classname + "|" + order + "|" + family);
				}
				else if (classname == null)
				{
					dataRow.add(kingdom + "|" + order + "|" + family);
				}	
				else if (order == null)
				{
					dataRow.add(kingdom + "|" + classname + "|" + family);
				}
				else if (family == null)
				{
					dataRow.add(kingdom + "|" + classname + "|" + order);
				}
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}

	/* Get IndentifiersFullname value for Zoology and Geology */
	public static void setIdentifiersFullName(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "17_identifications_identifiers_fullName"))
		{
			List<String> listAgentFullname = new ArrayList<String>();
			if (specimen.getIdentifications().iterator().next().getIdentifiers() != null)
			{
				Iterator<Agent> identifiedByIterator = specimen.getIdentifications().iterator().next().getIdentifiers().iterator();
				while(identifiedByIterator.hasNext())
				{	
					Agent ag = identifiedByIterator.next();
				    if (ag instanceof Person)
				    {
				    	Person per = (Person) ag;
				    	/*NDA-303/372*/
				    	listAgentFullname.add(per.getFullName()); 
				    }
					
				    if (listAgentFullname.size() > 1)
					{	
						listAgentFullname.add(" | ");
					}
				}
				
				if (listAgentFullname.size() > 0)
				{
				    String resultAgentFullName = listAgentFullname.toString()
				    .replace(",", " ")
				    .replace("[", " ")
				    .replace("]", " ")
				    .trim();
				    dataRow.add(resultAgentFullName);
				    /*NDA-303/372*/
				}
				else
				{
					dataRow.add(EMPTY_STRING);
				}
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get IndentifiersFullname value for Brahms */
	public static void setIdentifiersFullName_Brahms(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "17_identifications_identifiers_fullName"))
		{
			if (specimen.getIdentifications().iterator().next().getIdentifiers() != null)
			{
				List<String> listAgentFullname = new ArrayList<String>();
				Iterator<Agent> identifiedByIterator = specimen.getIdentifications().iterator().next().getIdentifiers().iterator();
				while(identifiedByIterator.hasNext())
				{	
					Agent ag = identifiedByIterator.next();
				    if (ag instanceof Person)
				    {
				    	Person per = (Person) ag;
				    	listAgentFullname.add(per.getFullName()); 
				    }
					
				    if (listAgentFullname.size() > 1)
					{	
						listAgentFullname.add(" | ");
					}
				}
				
				if (listAgentFullname.size() > 0)
				{
				    String resultAgentFullName = listAgentFullname.toString()
				    .replace(",", " ")
				    .replace("[", " ")
				    .replace("]", " ")
				    .trim();
				    dataRow.add(resultAgentFullName);
				}
				else
				{
					dataRow.add(EMPTY_STRING);
				}
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
			
		}
	}
	
	/* Get NumberOfSpecimen value */
	public static void setNumberOfSpecimen(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "18_NumberOfSpecimen"))
		{
			if (Integer.toString(specimen.getNumberOfSpecimen()) != null && specimen.getNumberOfSpecimen() > 0)
			{
				dataRow.add(Integer.toString(specimen.getNumberOfSpecimen()));
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get InformationWithHeld value */
	public static void setInformationWithHeld(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "19_DummyDefault"))
		{
			dataRow.add(EMPTY_STRING);
		}
	}
	
	/* Get InfraspecificEpithet value for Zoology and Geology */
	public static void setInfraspecificEpithet(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "20_InfraspecificEpithet"))
		{
			if(specimen.getIdentifications().iterator().next().getScientificName().getInfraspecificEpithet() != null &&
			   specimen.getIdentifications().iterator().next().isPreferred() == true)
			{
				dataRow.add(specimen.getIdentifications().iterator().next().getScientificName().getInfraspecificEpithet());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}	
		}	
	}
	
	/* Get InfraspecificEpithet value for Brahms */
	public static void setInfraspecificEpithet_Brahms(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "20_InfraspecificEpithet"))
		{
			if (specimen.getIdentifications().iterator().next().getScientificName().getInfraspecificEpithet() != null)
			{
				dataRow.add(specimen.getIdentifications().iterator().next().getScientificName().getInfraspecificEpithet());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get Island value */
	public static void setIsland(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "21_Island"))
		{
			if (specimen.getGatheringEvent().getIsland() != null)
			{
				/*NDA-303/372*/
				dataRow.add(specimen.getGatheringEvent().getIsland());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get InstitudeCode value */
	public static void setInstitudeCode(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "22_DummyDefault"))
		{
			if(specimen.getSourceInstitutionID().contains("Naturalis"))
			{
			  dataRow.add(specimen.getSourceInstitutionID().substring(0, 9));
			}
			else if(specimen.getSourceInstitutionID() != null)
			{
				dataRow.add(specimen.getSourceInstitutionID());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get Kingdom value for Zoology and Geology  */
	public static void setKingdom(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "23_Kingdom"))
		{
			if (specimen.getIdentifications().iterator().next().getDefaultClassification() != null &&
				specimen.getIdentifications().iterator().next().isPreferred() == true)
			{
				dataRow.add(specimen.getIdentifications().iterator().next().getDefaultClassification().getKingdom());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get Kingdom value for Brahms  */
	public static void setKingdom_Brahms(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "23_DummyDefault"))
		{
			if (specimen.getIdentifications().iterator().next().getDefaultClassification() != null)
			{
				String family = specimen.getIdentifications().iterator().next().getDefaultClassification().getFamily();
				if (family != null)
				{
					if (family.contains("Fungi"))
					{
						dataRow.add("fungi");
					}
					else
					{
						dataRow.add("Plantae");
						//dataRow.add(specimen.getIdentifications().iterator().next().getDefaultClassification().getKingdom());
					}
				}
				else
				{
					dataRow.add(EMPTY_STRING);
				}
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get PhaseOrStage value */
	public static void setPhaseOrStage(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "24_PhaseOrStage"))
		{
			if (specimen.getPhaseOrStage() != null)
			{
				dataRow.add(specimen.getPhaseOrStage());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}

	/* Get Locality value */
	public static void setLocality(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "25_Locality"))
		{
			if (specimen.getGatheringEvent().getLocality() != null)
			{
				String localityResult = specimen.getGatheringEvent().getLocality()
						.replace('"', ' ')
						.replace(' ', ' ')
						.replace('\t', ' ')
						.replace("\r", "")
		           		.replace("\n", "")
		           		.trim();
				dataRow.add(localityResult);
				/*NDA-303/372*/
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}

	/* Get MaximumElevationInMeters value */
	public static void setMaximumElevationInMeters(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "26_DummyDefault"))
		{
			dataRow.add(EMPTY_STRING);
		}
	}
	
	/* Get MinimumElevationInMeters value */
	public static void setMinimumElevationInMeters(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "27_DummyDefault"))
		{
			dataRow.add(EMPTY_STRING);
		}
	}
	
	/* Get NomenclaturalCode value for Zoology */
	public static void setNomenclaturalCode_Zoology(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "28_DummyDefault"))
		{
			dataRow.add("ICZN");
		}
	}
	
	/* Get NomenclaturalCode value Geology */
	public static void setNomenclaturalCode_Geology(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "28_DummyDefault"))
		{
			dataRow.add(EMPTY_STRING);
		}
	}
	
	/* Get NomenclaturalCode value for Brahms */
	public static void setNomenclaturalCode_Brahms(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "28_DummyDefault"))
		{
			dataRow.add("ICN");
		}
	}
	
	/* Get OccurrenceID value */
	public static void setOccurrenceID(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "29_DummyDefault"))
		{
			String institutionCode = null;
			String objectType = "specimen";
			if(specimen.getSourceInstitutionID().contains("Naturalis"))
			{
			   institutionCode = specimen.getSourceInstitutionID().substring(0, 9);
			}
			else if (!specimen.getSourceInstitutionID().contains("Naturalis"))
			{
				institutionCode = specimen.getSourceInstitutionID();	
			}
			/* PersistentID is: Example: occurrence id = http://data.biodiversitydata.nl/naturalis/specimen/RMNH.MAM.40012 */
			dataRow.add(CsvFileWriter.httpUrl + institutionCode + "/" + objectType + "/" + specimen.getSourceSystemId());
		}
	}
	
	/* Get Order value for Zoology and Geology */
	public static void setOrder(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "30_Order"))
		{
			if (specimen.getIdentifications().iterator().next().getDefaultClassification().getOrder() != null &&
				specimen.getIdentifications().iterator().next().isPreferred() == true)
			{
				dataRow.add(specimen.getIdentifications().iterator().next().getDefaultClassification().getOrder());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get Order value for Brahms */
	public static void setOrder_Brahms(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "30_Order"))
		{
			if (specimen.getIdentifications().iterator().next().getDefaultClassification().getOrder() != null)
			{
				dataRow.add(specimen.getIdentifications().iterator().next().getDefaultClassification().getOrder());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get Phylum value */
	public static void setPhylum(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "31_Phylum"))
		{
			if (specimen.getIdentifications().iterator().next().getDefaultClassification().getPhylum() != null)
			{
				dataRow.add(specimen.getIdentifications().iterator().next().getDefaultClassification().getPhylum());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get PreparationType value */
	public static void setPreparationType(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "32_PreparationType"))
		{
			if (specimen.getPreparationType() != null)
			{	
				dataRow.add(specimen.getPreparationType());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get GatheringAgents_FullName value */
	public static void setGatheringAgents_FullName(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "33_gatheringEvent_gatheringAgents_fullName"))
		{
			List<String> listFullname = new ArrayList<String>();
			if (specimen.getGatheringEvent().getGatheringPersons() != null)
			{
				Iterator<Person> fullnameIterator = specimen.getGatheringEvent().getGatheringPersons().iterator();
				while(fullnameIterator.hasNext())
				{	
					listFullname.add(fullnameIterator.next().getFullName()); 
					
					if (specimen.getGatheringEvent().getGatheringPersons().size() > 1)
					{	
						listFullname.add(" | ");
					}
				}
				
				if (listFullname.size() > 0)
				{
				    String resultFullName = listFullname.toString()
				    .replace(",", " ")
				    .replace("[", " ")
				    .replace("]", " ")
				    .trim();
				    dataRow.add(resultFullName);
				}
				else
				{
					dataRow.add(EMPTY_STRING);
				}
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get FullScientificName value for Zoology and Geology */
	public static void setFullScientificName(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "34_FullScientificName"))
		{
			if (specimen.getIdentifications().iterator().next().getScientificName().getFullScientificName() != null &&
				specimen.getIdentifications().iterator().next().isPreferred() == true)
			{
				dataRow.add(specimen.getIdentifications().iterator().next().getScientificName().getFullScientificName());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get FullScientificName value for Brahms */
	public static void setFullScientificName_Brahms(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "34_FullScientificName"))
		{
			if (specimen.getIdentifications().iterator().next().getScientificName().getFullScientificName() != null)
			{
				dataRow.add(specimen.getIdentifications().iterator().next().getScientificName().getFullScientificName());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}	
	}
	
	/* Get AuthorshipVerbatim value for Zoology and Geology */
	public static void setAuthorshipVerbatim(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "35_AuthorshipVerbatim"))
		{
			if (specimen.getIdentifications().iterator().next().getScientificName().getAuthorshipVerbatim() != null &&
				specimen.getIdentifications().iterator().next().isPreferred() == true)
			{
				/*NDA-303/372*/
				dataRow.add(specimen.getIdentifications().iterator().next().getScientificName().getAuthorshipVerbatim());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get AuthorshipVerbatim value for Brahms */
	public static void setAuthorshipVerbatim_Brahms(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "35_AuthorshipVerbatim"))
		{
			if (specimen.getIdentifications().iterator().next().getScientificName().getAuthorshipVerbatim() != null)
			{
				dataRow.add(specimen.getIdentifications().iterator().next().getScientificName().getAuthorshipVerbatim());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}

	}
	
	/* Get Sex value */
	public static void setSex(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "36_Sex"))
		{
			if (specimen.getSex() != null)
			{
				dataRow.add(specimen.getSex());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get SpecificEpithet value for Zoology and Geology */
	public static void setSpecificEpithet(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "37_SpecificEpithet"))
		{
			if (specimen.getIdentifications().iterator().next().getScientificName().getSpecificEpithet() != null &&
				specimen.getIdentifications().iterator().next().isPreferred() == true)
			{
				dataRow.add(specimen.getIdentifications().iterator().next().getScientificName().getSpecificEpithet());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get SpecificEpithet value for Brahms */
	public static void setSpecificEpithet_Brahms(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "37_SpecificEpithet"))
		{
			if (specimen.getIdentifications().iterator().next().getScientificName().getSpecificEpithet() != null)
			{
				dataRow.add(specimen.getIdentifications().iterator().next().getScientificName().getSpecificEpithet());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	

	/* Get ProvinceState value */
	public static void setProvinceState(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "38_ProvinceState"))
		{
			if (specimen.getGatheringEvent().getProvinceState() != null)
			{
				/*NDA-303/372*/
				dataRow.add(specimen.getGatheringEvent().getProvinceState());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}

	/* Get SubGenus value for Zoology and Geology */
	public static void setSubGenus(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "39_Subgenus"))
		{
			if (specimen.getIdentifications().iterator().next().getScientificName().getSubgenus() != null &&
				specimen.getIdentifications().iterator().next().isPreferred() == true)
			{
				dataRow.add(specimen.getIdentifications().iterator().next().getScientificName().getSubgenus());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get SubGenus value for Brahms */
	public static void setSubGenus_Brahms(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "39_Subgenus"))
		{
			if (specimen.getIdentifications().iterator().next().getScientificName().getSubgenus() != null)
			{
				dataRow.add(specimen.getIdentifications().iterator().next().getScientificName().getSubgenus());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get Taxonrank value for Zoology and Geology */
	public static void setTaxonrank(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "40_TaxonRank"))
		{
			if (specimen.getIdentifications().iterator().next().getTaxonRank() != null &&
				specimen.getIdentifications().iterator().next().isPreferred() == true)
			{
				dataRow.add(specimen.getIdentifications().iterator().next().getTaxonRank());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get Taxonrank value for Brahms */
	public static void setTaxonrank_Brahms(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "40_Dummy4"))
		{
			if (specimen.getIdentifications().iterator().next().getTaxonRank() != null)
			{
				if (specimen.getIdentifications().iterator().next().getTaxonRank().contains("subsp."))
				{
					dataRow.add("subspecies");
				}	
				else if (specimen.getIdentifications().iterator().next().getTaxonRank().contains("var."))
				{
					dataRow.add("variety");
				}
				else if (specimen.getIdentifications().iterator().next().getTaxonRank().contains("f."))
				{
					dataRow.add("form");
				}	
				else if (specimen.getIdentifications().iterator().next().getTaxonRank() != null &&
						!specimen.getIdentifications().iterator().next().getTaxonRank().contains("f.") &&
						!specimen.getIdentifications().iterator().next().getTaxonRank().contains("var.") &&
						!specimen.getIdentifications().iterator().next().getTaxonRank().contains("subsp."))
				{
					dataRow.add(specimen.getIdentifications().iterator().next().getTaxonRank());
				}
				else
				{
					dataRow.add(EMPTY_STRING);
				}
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}

	}
	
	/* Get TaxonRemarks value */
	public static void setTaxonRemarks(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "41_Remarks"))
		{
			if(specimen.getIdentifications() != null)
			{
				List<String> listFullname = new ArrayList<String>();
				Iterator<SpecimenIdentification> identIterator = specimen.getIdentifications().iterator();
				while(identIterator.hasNext())
				{	
					listFullname.add(identIterator.next().getScientificName().getFullScientificName()); 
					if (specimen.getIdentifications().size() > 1)
					{	
						listFullname.add(" | ");
					}
				}

				if (listFullname.size() > 1)
				{
					String resultFullName = listFullname.toString()
							.replace(",", "")
							.replace("[", "")
							.replace("]", "")
							.trim();
					dataRow.add(resultFullName);
					listFullname.clear();
				}
				else
				{
					dataRow.add(EMPTY_STRING);
				}
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}			
		}
	}
	
	/*Get TypeStatus value */
	public static void setTypeStatus(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "42_TypeStatus"))
		{
			if (specimen.getTypeStatus() != null)
			{
				dataRow.add(specimen.getTypeStatus());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	static String latitudeDecimal1 = null;
	static String longitudeDecimal1 = null;
	static String latitudeDecimal2 = null;
	static String longitudeDecimal2 = null;
	static int record1 = 1;
	static int record2 = 2;
	static int count = 0;
	
	/*Get VerbatimCoordinates value for Zoology and Geology */
	public static void setVerbatimCoordinates(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "43_Dummy3"))
		{
			if (specimen.getGatheringEvent().getSiteCoordinates() != null)
			{
				if (specimen.getGatheringEvent().getSiteCoordinates().size() > 1)
				{	
					Iterator<ESGatheringSiteCoordinates> iterator = specimen.getGatheringEvent().getSiteCoordinates().iterator();
				
					while(iterator.hasNext())
					{
						count++;
						if (count == record1)
						{
								latitudeDecimal1 = Double.toString(iterator.next().getLatitudeDecimal());
								longitudeDecimal1 = Double.toString(iterator.next().getLongitudeDecimal());
						}
						
						if (count == record2)
						{
								latitudeDecimal2 = Double.toString(iterator.next().getLatitudeDecimal());
								longitudeDecimal2 = Double.toString(iterator.next().getLongitudeDecimal());
						}
					}
					if (latitudeDecimal1 != null && longitudeDecimal1 != null && latitudeDecimal2 != null && longitudeDecimal2 != null)
					{
							dataRow.add(latitudeDecimal1 + ", " + latitudeDecimal2 + " | " + longitudeDecimal1 + ", "+ longitudeDecimal2);
						}
				    }
				else
				{
					dataRow.add(EMPTY_STRING);
				}
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/*Get VerbatimCoordinates value for Brahms */
	public static void setVerbatimCoordinates_Brahms(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "43_Dummy5"))
		{
			if (specimen.getGatheringEvent().getSiteCoordinates() != null)
			{
				if (specimen.getGatheringEvent().getSiteCoordinates().size() > 1)
				{	
					Iterator<ESGatheringSiteCoordinates> iterator = specimen.getGatheringEvent().getSiteCoordinates().iterator();
				
					while(iterator.hasNext())
					{
						count++;
						if (count == record1)
						{
								latitudeDecimal1 = Double.toString(iterator.next().getLatitudeDecimal());
								longitudeDecimal1 = Double.toString(iterator.next().getLongitudeDecimal());
						}
						
						if (count == record2)
						{
								latitudeDecimal2 = Double.toString(iterator.next().getLatitudeDecimal());
								longitudeDecimal2 = Double.toString(iterator.next().getLongitudeDecimal());
						}
						if (latitudeDecimal1 != null && longitudeDecimal1 != null && latitudeDecimal2 != null && longitudeDecimal2 != null)
						{
  							dataRow.add(latitudeDecimal1 + ", " + latitudeDecimal2 + " | " + longitudeDecimal1 + ", "+ longitudeDecimal2);
  						}
					}
					
				    }
				else
				{
					dataRow.add(EMPTY_STRING);
				}
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get VerbatimDepth value */
	public static void setVerbatimDepth(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "44_Depth"))
		{
			if (specimen.getGatheringEvent().getDepth() != null)
			{
				dataRow.add(specimen.getGatheringEvent().getDepth());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get AltitudeUnifOfMeasurement value */
	public static void setAltitudeUnifOfMeasurement(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "45_AltitudeUnifOfMeasurement"))
		{
			if (specimen.getGatheringEvent().getAltitudeUnifOfMeasurement() != null)
			{
				dataRow.add(specimen.getGatheringEvent().getAltitudeUnifOfMeasurement());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get VerbatimEventDate value for Zoology and Geology */
	public static void setVerbatimEventDate(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "46_Dummy4"))
		{
			/* if BeginDate and EndDate both has values and not equal then get the value of the BeginDate and EndDate */
			if (specimen.getGatheringEvent().getDateTimeBegin() != null && specimen.getGatheringEvent().getDateTimeEnd() != null &&
				!specimen.getGatheringEvent().getDateTimeBegin().equals(specimen.getGatheringEvent().getDateTimeEnd()))
			{
				String dateBegin = datetimebegin.format(specimen.getGatheringEvent().getDateTimeBegin());
				String dateEnd = datetimenend.format(specimen.getGatheringEvent().getDateTimeEnd());
				dataRow.add(dateBegin + " | " + dateEnd);
			}
			/* if BeginDate is equal to EndDate then only the value of BeginDate */
			else if (specimen.getGatheringEvent().getDateTimeBegin() != null && 
					specimen.getGatheringEvent().getDateTimeBegin() == specimen.getGatheringEvent().getDateTimeEnd())
			{
				String dateBegin = datetimebegin.format(specimen.getGatheringEvent().getDateTimeBegin());
				dataRow.add(dateBegin);
			}
			/* if only begindate has a value then get the value of begindate */
			else if(specimen.getGatheringEvent().getDateTimeBegin() != null && specimen.getGatheringEvent().getDateTimeEnd() == null)
			{
				String dateBegin = datetimebegin.format(specimen.getGatheringEvent().getDateTimeBegin());
				dataRow.add(dateBegin);
			}
			/* if EndDate has a value and Begindate has no value set the value of null for Enddate */
			else if (specimen.getGatheringEvent().getDateTimeEnd() != null && specimen.getGatheringEvent().getDateTimeBegin() == null)
			{
				dataRow.add(EMPTY_STRING);
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get VerbatimEventDate value for Brahms*/
	public static void setVerbatimEventDate_Brahms(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "46_Dummy6"))
		{
			/* if BeginDate and EndDate both has values and not equal then get the value of the BeginDate and EndDate */
			if (specimen.getGatheringEvent().getDateTimeBegin() != null && specimen.getGatheringEvent().getDateTimeEnd() != null &&
				!specimen.getGatheringEvent().getDateTimeBegin().equals(specimen.getGatheringEvent().getDateTimeEnd()))
			{
				String dateBegin = datetimebegin.format(specimen.getGatheringEvent().getDateTimeBegin());
				String dateEnd = datetimenend.format(specimen.getGatheringEvent().getDateTimeEnd());
				dataRow.add(dateBegin + " | " + dateEnd);
			}
			/* if BeginDate is equal to EndDate then only the value of BeginDate */
			else if (specimen.getGatheringEvent().getDateTimeBegin() != null && 
					specimen.getGatheringEvent().getDateTimeBegin() == specimen.getGatheringEvent().getDateTimeEnd())
			{
				String dateBegin = datetimebegin.format(specimen.getGatheringEvent().getDateTimeBegin());
				dataRow.add(dateBegin);
			}
			/* if only begindate has a value then get the value of begindate */
			else if(specimen.getGatheringEvent().getDateTimeBegin() != null && specimen.getGatheringEvent().getDateTimeEnd() == null)
			{
				String dateBegin = datetimebegin.format(specimen.getGatheringEvent().getDateTimeBegin());
				dataRow.add(dateBegin);
			}
			/* if EndDate has a value and Begindate has no value set the value of null for Enddate */
			else if (specimen.getGatheringEvent().getDateTimeEnd() != null && specimen.getGatheringEvent().getDateTimeBegin() == null)
			{
				dataRow.add(EMPTY_STRING);
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
	/* Get TaxonRank Is VerbatimTaxonRank value */
	public static void setTaxonRank_Is_VerbatimTaxonRank(String MAPPING_FILE_NAME, ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
	{
		if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "47_TaxonRank"))
		{
			if (specimen.getIdentifications().iterator().next().getTaxonRank() != null)
			{
				dataRow.add(specimen.getIdentifications().iterator().next().getTaxonRank());
			}
			else
			{
				dataRow.add(EMPTY_STRING);
			}
		}
	}
	
}
