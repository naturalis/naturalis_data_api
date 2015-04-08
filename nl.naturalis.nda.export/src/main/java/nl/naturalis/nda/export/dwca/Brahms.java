package nl.naturalis.nda.export.dwca;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import nl.naturalis.nda.domain.Agent;
import nl.naturalis.nda.domain.Person;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringSiteCoordinates;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.export.dwca.StringUtilities;

public class Brahms
{

	public Brahms()
	{
		// TODO Auto-generated constructor stub
	}

	public void addBrahmsOccurrencefield(List<ESSpecimen> list, CsvFileWriter filewriter,
			String MAPPING_FILE_NAME) throws Exception 
	{
		SimpleDateFormat datetimebegin = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat datetimenend = new SimpleDateFormat("yyyy-MM-dd");
		StringUtilities strutil = new StringUtilities();
		
		for (ESSpecimen specimen : list)
		{
			CsvFileWriter.CsvRow dataRow = filewriter.new CsvRow();

			/* 01_Dummy1 is basisOfRecord */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "01_Dummy1"))
			{
				if (specimen.getRecordBasis().contains("Herbarium sheet"))
				{
					dataRow.add("PreservedSpecimen");
				}
				else if (specimen.getRecordBasis().contains("Extra sheet"))
				{
					dataRow.add("PreservedSpecimen");
				}
				else if (specimen.getRecordBasis().contains("Wood sample"))
				{
					dataRow.add("PreservedSpecimen");
				}
				else if (specimen.getRecordBasis() != null &&
						 !specimen.getRecordBasis().contains("Herbarium sheet") &&
						 !specimen.getRecordBasis().contains("Extra sheet") &&
						 !specimen.getRecordBasis().contains("Wood sample"))
				{
					dataRow.add(specimen.getRecordBasis());
				}
				else
				{
					dataRow.add(null);
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
					dataRow.add(null);
				}
			}

			/* 03_DummyDefault class */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "03_DummyDefault"))
			{
				/*if(specimen.getIdentifications().iterator().next().getDefaultClassification() != null)
				{
					dataRow.add(specimen.getIdentifications().iterator().next().getDefaultClassification().getClassName());
				}
				else*/
				{
					dataRow.add(null);
				}
			}

			/* 04_DummyDefault is collectionCode */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "04_DummyDefault"))
			{
					dataRow.add("Botany");
			}
				
			/* 05_Continent is continent */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "05_Continent"))
			{
				if (specimen.getGatheringEvent().getWorldRegion() != null)
				{
					dataRow.add(strutil.convertStringToUTF8(specimen.getGatheringEvent().getWorldRegion()));
				}
				else
				{
					dataRow.add(null);
				}
			}
			
			/* 06_Country is country */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "06_Country"))
			{
				if(specimen.getGatheringEvent().getCountry() != null)
				{
					dataRow.add(strutil.convertStringToUTF8(specimen.getGatheringEvent().getCountry()));
				}
				else
				{
					dataRow.add(null);
				}
			}

			/* 07_County is county*/
			if (strutil.isEnabled(MAPPING_FILE_NAME, "07_County"))
			{
				if(specimen.getGatheringEvent().getCity() != null)
				{
					dataRow.add(strutil.convertStringToUTF8(specimen.getGatheringEvent().getCity()));
				}
				else
				{
					dataRow.add(null);
				}
			}


			/* 08_DateIdentified is dateIdentified */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "08_DateIdentified"))
			{
				if (specimen.getIdentifications().iterator().next().getDateIdentified() != null)
				{
					SimpleDateFormat dateidentified = new SimpleDateFormat("yyyy-MM-dd");
					String dateiden = dateidentified.format(specimen.getIdentifications().iterator().next().getDateIdentified());
					dataRow.add(dateiden);
				}
				else
				{
					dataRow.add(null);
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
						dataRow.add(String.format("%s", specimen.getGatheringEvent().getSiteCoordinates()
								.iterator().next().getLatitudeDecimal()));
					}
					else
					{
						dataRow.add(null);
					}
				}
				else
				{
					dataRow.add(null);
				}
			}
				
			/* 10_LongitudeDecimal is decimalLongitude */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "10_LongitudeDecimal"))
			{
				if (specimen.getGatheringEvent().getSiteCoordinates() != null)
				{
					if (specimen.getGatheringEvent().getSiteCoordinates().iterator().next().getLongitudeDecimal() != null)
					{
						dataRow.add(String.format("%s", specimen.getGatheringEvent().getSiteCoordinates().iterator().next().getLongitudeDecimal()));
					}
					else
					{
						dataRow.add(null);
					}
				}
				else
				{
					dataRow.add(null);
				}
			}
			
			
			/* 11_Dummy2 is eventDate DateTimeBegin en EndTimeEnd */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "11_Dummy2"))
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
					dataRow.add(null);
				}
				else
				{
					dataRow.add(null);
				}
			}


			/* 12_DummyDefault is family */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "12_DummyDefault"))
			{
				if(specimen.getIdentifications().iterator().next().getDefaultClassification().getFamily() != null)
				{
					dataRow.add(specimen.getIdentifications().iterator().next().getDefaultClassification().getFamily());
				}
				else
				{
					dataRow.add(null);
				}
			}
			
				
			/* 13_GenusOrMonomial is genus */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "13_GenusOrMonomial"))
			{
				if(specimen.getIdentifications().iterator().next().getScientificName().getGenusOrMonomial() != null)
				{
					dataRow.add(strutil.convertStringToUTF8(specimen.getIdentifications().iterator().next().getScientificName().getGenusOrMonomial()));
				}
				else
				{
					dataRow.add(null);
				}
			}
			
			/* 14_DummyDefault is geodeticDatum */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "14_DummyDefault"))
			{
				dataRow.add(null);
			}
			

			/* 15_DummyDefault is  habitat */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "15_DummyDefault"))
			{
				dataRow.add(null);
			}
			

			/* 16_Dummy3 is higherClassification */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "16_Dummy3"))
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
					dataRow.add(null);
				}
			}
			
			/* 17_identifications_identifiers_fullName is identifiedBy */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "17_identifications_identifiers_fullName"))
			{
				if (specimen.getIdentifications().iterator().next().getIdentifiers() != null)
				{
					Agent ag = specimen.getIdentifications().iterator().next().getIdentifiers().iterator()
							.next();
					if (ag instanceof Person)
					{
						Person per = (Person) ag;
						dataRow.add(strutil.convertStringToUTF8(per.getFullName()));
					} else
					{
						dataRow.add(null);
					}
				}
				else
				{
					dataRow.add(null);
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
					dataRow.add(null);
				}
			}
			

			/* 19_DummyDefault is informationWithheld */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "19_DummyDefault"))
			{
				dataRow.add(null);
			}
			
			/* 20_InfraspecificEpithet is infraSpecificEpithet */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "20_InfraspecificEpithet"))
			{
				if (specimen.getIdentifications().iterator().next().getScientificName().getInfraspecificEpithet() != null)
				{
					dataRow.add(specimen.getIdentifications().iterator().next().getScientificName().getInfraspecificEpithet());
				}
				else
				{
					dataRow.add(null);
				}
			}

			/* 21_Island is island */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "21_Island"))
			{
				if (specimen.getGatheringEvent().getIsland() != null)
				{
					dataRow.add(strutil.convertStringToUTF8(specimen.getGatheringEvent().getIsland()));
				}
				else
				{
					dataRow.add(null);
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
					dataRow.add(null);
				}
			}

			/* 23_DummyDefault is kingdom */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "23_DummyDefault"))
			{
				if (specimen.getIdentifications().iterator().next().getDefaultClassification() != null)
				/*{
					dataRow.add(specimen.getIdentifications().iterator().next().getDefaultClassification().getKingdom());
				}
				else*/
				{
					dataRow.add(null);
				}
			}
				
			/* 24_PhaseOrStage is lifeStage */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "24_PhaseOrStage"))
			{
				if(specimen.getPhaseOrStage() != null)
				{
					dataRow.add(specimen.getPhaseOrStage());
				}
				else
				{
					dataRow.add(null);
				}
			}

			/* 25_Locality is locality */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "25_Locality"))
			{
				if (specimen.getGatheringEvent().getLocality() != null)
				{
					dataRow.add(strutil.convertStringToUTF8(specimen.getGatheringEvent().getLocality()));
				}
				else
				{
					dataRow.add(null);
				}
			}

			/* 26_DummyDefault maximumElevationInMeters */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "26_DummyDefault"))
			{
    			dataRow.add(null);
			}

			/* 27_DummyDefault is minimumElevationInMeters */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "27_DummyDefault"))
			{
				dataRow.add(null);
			}

			/* 28_DummyDefault is nomenclaturalCode */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "28_DummyDefault"))
			{
				dataRow.add("ICN");
			}

			/* 29_DummyDefault is occurrenceID */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "29_DummyDefault"))
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
				/* PersitentID is: Example: occurrence id = http://data.biodiversitydata.nl/naturalis/specimen/RMNH.MAM.40012 */
				dataRow.add(CsvFileWriter.httpUrl + institutionCode + "/" + objectType + "/" + specimen.getSourceSystemId());
			}

			/* 30_Order is order */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "30_Order"))
			{
				if (specimen.getIdentifications().iterator().next().getDefaultClassification().getOrder() != null)
				{
					dataRow.add(specimen.getIdentifications().iterator().next().getDefaultClassification().getOrder());
				}
				else
				{
					dataRow.add(null);
				}
			}

			/* 31_Phylum is Phylum */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "31_Phylum"))
			{
				if (specimen.getIdentifications().iterator().next().getDefaultClassification().getPhylum() != null)
				{
					dataRow.add(specimen.getIdentifications().iterator().next().getDefaultClassification().getPhylum());
				}
				else
				{
					dataRow.add(null);
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
					dataRow.add(null);
				}
			}

			/* 33_gatheringEvent.gatheringAgents.fullName is recordedBy */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "33_gatheringEvent.gatheringAgents.fullName"))
			{
				if (specimen.getGatheringEvent().getGatheringPersons() != null)
				{
					dataRow.add(strutil.convertStringToUTF8(specimen.getGatheringEvent().getGatheringPersons().iterator().next().getFullName()));
				}
				else
				{
					dataRow.add(null);
				}
			}

			/* 34_FullScientificName is scientificName */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "34_FullScientificName"))
			{
				if (specimen.getIdentifications().iterator().next().getScientificName().getFullScientificName() != null)
				{
					dataRow.add(strutil.convertStringToUTF8(specimen.getIdentifications().iterator().next().getScientificName().getFullScientificName()));
				}
				else
				{
					dataRow.add(null);
				}
			}	
			
			/* 35_AuthorshipVerbatim is scientificNameAuthorship */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "35_AuthorshipVerbatim"))
			{
				if (specimen.getIdentifications().iterator().next().getScientificName().getAuthorshipVerbatim() != null)
				{
					dataRow.add(strutil.convertStringToUTF8(specimen.getIdentifications().iterator().next().getScientificName().getAuthorshipVerbatim()));
				}
				else
				{
					dataRow.add(null);
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
					dataRow.add(null);
				}
			}

			/* 37_SpecificEpithet is specificEpithet */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "37_SpecificEpithet"))
			{
				if (specimen.getIdentifications().iterator().next().getScientificName().getSpecificEpithet() != null)
				{
					dataRow.add(strutil.convertStringToUTF8(specimen.getIdentifications().iterator().next().getScientificName().getSpecificEpithet()));
				}
				else
				{
					dataRow.add(null);
				}
			}

			/* 38_ProvinceState is stateProvince */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "38_ProvinceState"))
			{
				if (specimen.getGatheringEvent().getProvinceState() != null)
				{
					dataRow.add(strutil.convertStringToUTF8(specimen.getGatheringEvent().getProvinceState()));
				}
				else
				{
					dataRow.add(null);
				}
			}

			/* 39_Subgenus is subGenus */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "39_Subgenus"))
			{
				if (specimen.getIdentifications().iterator().next().getScientificName().getSubgenus() != null)
				{
					dataRow.add(specimen.getIdentifications().iterator().next().getScientificName().getSubgenus());
				}
				else
				{
					dataRow.add(null);
				}
			}

			/* 40_Dummy4 is taxonRank */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "40_Dummy4"))
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
						dataRow.add(null);
					}
				}
				else
				{
					dataRow.add(null);
				}
			}

			/* 41_Remarks is taxonRemarks */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "41_Remarks"))
			{
				if (specimen.getIdentifications().iterator().next().getRemarks() != null)
				{
					dataRow.add(specimen.getIdentifications().iterator().next().getRemarks());
				}
				else
				{
					dataRow.add(null);
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
					dataRow.add(null);
				}
			}
			
			/* 43_Dummy5 is verbatimCoordinates */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "43_Dummy5"))
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
							if (latitudeDecimal1 != null && longitudeDecimal1 != null && latitudeDecimal2 != null && longitudeDecimal2 != null)
							{
	  							dataRow.add(latitudeDecimal1 + ", " + latitudeDecimal2 + " | " + longitudeDecimal1 + ", "+ longitudeDecimal2);
	  						}
						}
						
 				    }
					else
					{
						dataRow.add(null);
					}
				}
				else
				{
					dataRow.add(null);
				}
			}
			
			/* 44_Depth is verbatimDepth */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "44_Depth"))
			{
				if (specimen.getGatheringEvent().getDepth() != null)
				{
					dataRow.add(specimen.getGatheringEvent().getDepth());
				}
				else
				{
					dataRow.add(null);
				}
			}

			/* 45_AltitudeUnifOfMeasurement is verbatimElevation */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "45_AltitudeUnifOfMeasurement"))
			{
				if (specimen.getGatheringEvent().getAltitudeUnifOfMeasurement() != null)
				{
					dataRow.add(specimen.getGatheringEvent().getAltitudeUnifOfMeasurement());
				}
				else
				{
					dataRow.add(null);
				}
			}

			/* 46_Dummy6 is verbatimEventDate */
			if (strutil.isEnabled(MAPPING_FILE_NAME, "46_Dummy6"))
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
					dataRow.add(null);
				}
				else
				{
					dataRow.add(null);
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
					dataRow.add(null);
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
