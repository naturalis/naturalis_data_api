package nl.naturalis.nda.export.dwca;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.naturalis.nda.domain.Agent;
import nl.naturalis.nda.domain.Person;
import nl.naturalis.nda.domain.SpecimenIdentification;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringSiteCoordinates;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;

public class Geology
{
	private static final String EMPTY_STRING = "";
	
	public Geology()
	{
		// TODO Auto-generated constructor stub
	}

	public void addGeologyOccurrencefield(List<ESSpecimen> list, CsvFileWriter filewriter,
			String MAPPING_FILE_NAME)
	{
		SimpleDateFormat datetimebegin = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat datetimenend = new SimpleDateFormat("yyyy-MM-dd");
		StringUtilities strutil = new StringUtilities();
		
		for (ESSpecimen specimen : list)
		{
			CsvFileWriter.CsvRow dataRow = filewriter.new CsvRow();

			/* 00_Dummy0 is ID */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "00_Dummy0"))
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
			
			/* 01_RecordBasis is basisOfRecord */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "01_RecordBasis"))
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

			/* 02_SourceSystemId is catalogNumber */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "02_SourceSystemId"))
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
			
			/* 03_ClassName is class */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "03_ClassName"))
			{
				if(specimen.getIdentifications().iterator().next().getDefaultClassification() != null &&
				   specimen.getIdentifications().iterator().next().isPreferred() == true)
				{
					dataRow.add(specimen.getIdentifications().iterator().next().getDefaultClassification().getClassName());
				}
				else
				{
					dataRow.add(EMPTY_STRING);
				}
			}

			/* 04_CollectionType is collectionCode */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "04_CollectionType"))
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
				
			/* 05_Continent is continent */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "05_Continent"))
			{
				if (specimen.getGatheringEvent().getWorldRegion() != null)
				{
					/* NDA-303/372
					dataRow.add(strutil.convertStringToUTF8(specimen.getGatheringEvent().getWorldRegion()));
					*/
					dataRow.add(specimen.getGatheringEvent().getWorldRegion());
				}
				else
				{
					dataRow.add(EMPTY_STRING);
				}
			}
				
			/* 06_Country is country */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "06_Country"))
			{
				if(specimen.getGatheringEvent().getCountry() != null)
				{
					/* NDA-303/372
					dataRow.add(strutil.convertStringToUTF8(specimen.getGatheringEvent().getCountry()));
					*/
					dataRow.add(specimen.getGatheringEvent().getCountry());
				}
				else
				{
					dataRow.add(EMPTY_STRING);
				}
			}

			/* 07_County is county */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "07_County"))
			{
				if(specimen.getGatheringEvent().getCity() != null)
				{
					/* NDA-303/372
					dataRow.add(strutil.convertStringToUTF8(specimen.getGatheringEvent().getCity()));
					*/
					dataRow.add(specimen.getGatheringEvent().getCity());
				}
				else
				{
					dataRow.add(EMPTY_STRING);
				}
			}

			/* 08_DateIdentified is dateIdentified */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "08_DateIdentified"))
			{
				if (specimen.getIdentifications().iterator().next().getDateIdentified() != null &&
					specimen.getIdentifications().iterator().next().isPreferred() == true)
				{
					SimpleDateFormat dateidentified = new SimpleDateFormat("yyyy-MM-dd");
					String dateiden = dateidentified.format(specimen.getIdentifications().iterator().next()
							.getDateIdentified());
					dataRow.add(dateiden);
				}
				else
				{
					dataRow.add(EMPTY_STRING);
				}
			}

			
			/* 09_LatitudeDecimal is decimalLatitude */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "09_LatitudeDecimal"))
			{
				if (specimen.getGatheringEvent().getSiteCoordinates() != null)
				{
					if (specimen.getGatheringEvent().getSiteCoordinates().iterator().next()
							.getLatitudeDecimal() != null)
					{
						dataRow.add(String.format("%s",specimen.getGatheringEvent().getSiteCoordinates().iterator().next().getLatitudeDecimal()));
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

			
			/* 10_LongitudeDecimal is decimalLongitude */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "10_LongitudeDecimal"))
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
			
			
			/* 11_Dummy1 is eventDate DateTimeBegin en EndTimeEnd */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "11_Dummy1"))
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

			/* 12_Family is family */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "12_Family"))
			{
				if(specimen.getIdentifications().iterator().next().getDefaultClassification().getFamily() != null &&
				   specimen.getIdentifications().iterator().next().isPreferred() == true)
				{
					dataRow.add(specimen.getIdentifications().iterator().next().getDefaultClassification().getFamily());
				}
				else
				{
					dataRow.add(EMPTY_STRING);
				}
			}
			
			/* 13_GenusOrMonomial is genus */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "13_GenusOrMonomial"))
			{
				if (specimen.getIdentifications().iterator().next().getScientificName().getGenusOrMonomial() != null &&  
					specimen.getIdentifications().iterator().next().isPreferred() == true)
				{
					/* NDA-303/372
					dataRow.add(strutil.convertStringToUTF8(specimen.getIdentifications().iterator().next().getScientificName().getGenusOrMonomial()));
					*/
					dataRow.add(specimen.getIdentifications().iterator().next().getScientificName().getGenusOrMonomial());
				}
				else
				{
					dataRow.add(EMPTY_STRING);
				}
			}

			/* 14_DummyDefault is geodeticDatum */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "14_DummyDefault"))
			{
				dataRow.add(EMPTY_STRING);
			}

			/* 15_DummyDefault is habitat */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "15_DummyDefault"))
			{
				dataRow.add(EMPTY_STRING);
			}

			/* 16_Dummy2 is higherClassification */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "16_Dummy2"))
			{
				dataRow.add(EMPTY_STRING);
			}

			/* 17_identifications_identifiers_fullName is identifiedBy | BRAHMS ONLY ?? */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "17_identifications_identifiers_fullName"))
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
					    	/* NDA-303/372
					    	listAgentFullname.add(strutil.convertStringToUTF8(per.getFullName()));
					    	*/
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
					    /* NDA-303/372
					    dataRow.add(strutil.convertStringToUTF8(resultAgentFullName));*/
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

			/* 18_NumberOfSpecimen is individualCount */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "18_NumberOfSpecimen"))
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

			/* 19_DummyDefault is informationWithheld */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "19_DummyDefault"))
			{
				dataRow.add(EMPTY_STRING);
			}

			/* 20_InfraspecificEpithet is infraspecificEpithet */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "20_InfraspecificEpithet"))
			{
				if (specimen.getIdentifications().iterator().next().getScientificName().getInfraspecificEpithet() != null &&
					specimen.getIdentifications().iterator().next().isPreferred() == true)
				{
					dataRow.add(specimen.getIdentifications().iterator().next().getScientificName().getInfraspecificEpithet());
				}
				else
				{
					dataRow.add(EMPTY_STRING);
				}
			}

			/* 21_Island is island */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "21_Island"))
			{
				if (specimen.getGatheringEvent().getIsland() != null)
				{
					/* NDA-303/372
					dataRow.add(strutil.convertStringToUTF8(specimen.getGatheringEvent().getIsland()));
					*/
					dataRow.add(specimen.getGatheringEvent().getIsland());
				}
				else
				{
					dataRow.add(EMPTY_STRING);
				}
			}

			/* 22_DummyDefault is institutionCode */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "22_DummyDefault"))
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

			/* 23_Kingdom is kingdom */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "23_Kingdom"))
			{
				if (specimen.getIdentifications().iterator().next().getDefaultClassification() != null &&
					specimen.getIdentifications().iterator().next().isPreferred() == true)
				{
					dataRow.add(specimen.getIdentifications().iterator().next().getDefaultClassification()
							.getKingdom());
				}
				else
				{
					dataRow.add(EMPTY_STRING);
				}
			}

			/* 24_PhaseOrStage is lifeStage */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "24_PhaseOrStage"))
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

			/* 25_Locality is locality */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "25_Locality"))
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
					/* NDA-303/372
					dataRow.add(strutil.convertStringToUTF8(localityResult));
					*/
				}
				else
				{
					dataRow.add(EMPTY_STRING);
				}
			}

			/* 26_DummyDefault is maximumElevationInMeters */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "26_DummyDefault"))
			{
				dataRow.add(EMPTY_STRING);
			}

			/* 27_DummyDefault is minimumElevationInMeters */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "27_DummyDefault"))
			{
				dataRow.add(EMPTY_STRING);
			}

			/* 28_DummyDefault is nomenclaturalCode */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "28_DummyDefault"))
			{
				dataRow.add(EMPTY_STRING);
			}

			/* 29_DummyDefault is occurrenceID */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "29_DummyDefault"))
			{
				String institutionCode = null;
				String objectType = "specimen";
				if (specimen.getSourceInstitutionID().contains("Naturalis"))
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

			/* 30_Order is order */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "30_Order"))
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

			/* 31_Phylum is phylum */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "31_Phylum"))
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

			/* 32_PreparationType is preparations */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "32_PreparationType"))
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

			/* 33_gatheringEvent_gatheringAgents_fullName IS recordedBy */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "33_gatheringEvent_gatheringAgents_fullName"))
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
					    /* NDA-303/372
					    dataRow.add(strutil.convertStringToUTF8(resultFullName));
					    */
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

			/* 34_FullScientificName IS scientificName */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "34_FullScientificName"))
			{
				if (specimen.getIdentifications().iterator().next().getScientificName().getFullScientificName() != null  &&
					specimen.getIdentifications().iterator().next().isPreferred() == true)
				{
					/* NDA-303/372
					dataRow.add(strutil.convertStringToUTF8(specimen.getIdentifications().iterator().next().getScientificName().getFullScientificName()));
					*/
					dataRow.add(specimen.getIdentifications().iterator().next().getScientificName().getFullScientificName());
				}
				else
				{
					dataRow.add(EMPTY_STRING);
				}
			}

			/* 35_AuthorshipVerbatim IS scientificNameAuthorship */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "35_AuthorshipVerbatim"))
			{
				if (specimen.getIdentifications().iterator().next().getScientificName().getAuthorshipVerbatim() != null  &&
					specimen.getIdentifications().iterator().next().isPreferred() == true)
				{
					/* NDA-303/372
					dataRow.add(strutil.convertStringToUTF8(specimen.getIdentifications().iterator().next().getScientificName().getAuthorshipVerbatim()));
					*/
					dataRow.add(specimen.getIdentifications().iterator().next().getScientificName().getAuthorshipVerbatim());
				}
				else
				{
					dataRow.add(EMPTY_STRING);
				}
			}

			/* 36_Sex is sex */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "36_Sex"))
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

			/* 37_SpecificEpithet is specificEpithet */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "37_SpecificEpithet"))
			{
				if (specimen.getIdentifications().iterator().next().getScientificName().getSpecificEpithet() != null &&
						specimen.getIdentifications().iterator().next().isPreferred() == true)
				{
					/* NDA-303/372
					dataRow.add(strutil.convertStringToUTF8(specimen.getIdentifications().iterator().next().getScientificName().getSpecificEpithet()));
					*/
					dataRow.add(specimen.getIdentifications().iterator().next().getScientificName().getSpecificEpithet());
				}
				else
				{
					dataRow.add(EMPTY_STRING);
				}
			}

			/* 38_ProvinceState is stateProvince */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "38_ProvinceState"))
			{
				if (specimen.getGatheringEvent().getProvinceState() != null)
				{
					/* NDA-303/372
					dataRow.add(strutil.convertStringToUTF8(specimen.getGatheringEvent().getProvinceState()));
					*/
					dataRow.add(specimen.getGatheringEvent().getProvinceState());
				}
				else
				{
					dataRow.add(EMPTY_STRING);
				}
			}

			/* 39_Subgenus is subGenus */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "39_Subgenus"))
			{
				if (specimen.getIdentifications().iterator().next().getScientificName() != null &&
					specimen.getIdentifications().iterator().next().isPreferred() == true)
				{
					dataRow.add(specimen.getIdentifications().iterator().next().getScientificName().getSubgenus());
				}
				else
				{
					dataRow.add(EMPTY_STRING);
				}
			}

			/* 40_TaxonRank is taxonRank */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "40_TaxonRank"))
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

			/* 41_Remarks is taxonRemarks */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "41_Remarks"))
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
					/* NDA-303/372
					dataRow.add(strutil.convertStringToUTF8(resultFullName));
					*/
					listFullname.clear();
				}
				else
				{
					dataRow.add(EMPTY_STRING);
				}
			}

			/* 42_TypeStatus is typeStatus */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "42_TypeStatus"))
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
			
			/* 43_Dummy3 is verbatimCoordinates */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "43_Dummy3"))
			{
				if (specimen.getGatheringEvent().getSiteCoordinates() != null)
				{
					String latitudeDecimal1 = null;
					String longitudeDecimal1 = null;
					String latitudeDecimal2 = null;
					String longitudeDecimal2 = null;
					int record1 = 1;
					int record2 = 2;
					int count = 0;
					
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
			
			/* 44_Depth is verbatimDepth */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "44_Depth"))
			{
				if (specimen.getGatheringEvent() != null)
				{
					dataRow.add(specimen.getGatheringEvent().getDepth());
				}
				else
				{
					dataRow.add(EMPTY_STRING);
				}
			}

			/* 45_AltitudeUnifOfMeasurement is verbatimElevation */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "45_AltitudeUnifOfMeasurement"))
			{
				if (specimen.getGatheringEvent() != null)
				{
					dataRow.add(specimen.getGatheringEvent().getAltitudeUnifOfMeasurement());
				}
				else
				{
					dataRow.add(EMPTY_STRING);
				}
			}

			/* 46_Dummy4 is verbatimEventDate */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "46_Dummy4"))
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

			/* 47_TaxonRank is verbatimTaxonRank */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "47_TaxonRank"))
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

			/**
			 * adding data row
			 */
			try
			{
				filewriter.WriteRow(dataRow);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}

	}

}