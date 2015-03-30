package nl.naturalis.nda.export.dwca;

import nl.naturalis.nda.domain.Agent;
import nl.naturalis.nda.domain.Person;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringSiteCoordinates;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

public class Zoology
{

	
	public Zoology()
	{
		// TODO Auto-generated constructor stub
	}

	public void addZoologyOccurrencefield(List<ESSpecimen> list, CsvFileWriter filewriter,
			String MAPPING_FILE_NAME)
	{
		SimpleDateFormat datetimebegin = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat datetimenend = new SimpleDateFormat("yyyy-MM-dd");
		
		/* ALL Mapping fields is set default at '1' */
		
		for (ESSpecimen specimen : list)
		{
			CsvFileWriter.CsvRow dataRow = filewriter.new CsvRow();

			/* 01_RecordBasis */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "basisOfRecord,1"))
			{
				if (specimen.getRecordBasis() != null)
				{
					dataRow.add(specimen.getRecordBasis());
				}
				else
				{
					dataRow.add(null);
				}
			}

			/* 02_CatalogNumber */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "catalogNumber,1"))
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

			/* 03_ClassName */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "class,1"))
			{
				if (specimen.getIdentifications().iterator().next().getDefaultClassification() != null)
				{
					dataRow.add(specimen.getIdentifications().iterator().next().getDefaultClassification().getClassName());
				}
				else
				{
					dataRow.add(null);
				}
			}

			/* 04_CollectionType */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "collectionCode,1"))
			{
				if (specimen.getCollectionType() != null)
				{
					dataRow.add(specimen.getCollectionType());
				}
				else
				{
					dataRow.add(null);
				}
			}

			/* 05_Continent */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "continent,1"))
			{
				if (specimen.getGatheringEvent().getWorldRegion() != null)
				{
					dataRow.add(specimen.getGatheringEvent().getWorldRegion());
				}
				else
				{
					dataRow.add(null);
				}
			}

			/* 06_Country */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "country,1"))
			{
				if(specimen.getGatheringEvent().getCountry() != null)
				{
					dataRow.add(specimen.getGatheringEvent().getCountry());
				}
				else
				{
					dataRow.add(null);
				}
			}

			/* ToDo: GetCity moet County worden. */
			/* 07_County */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "county,1"))
			{
				if(specimen.getGatheringEvent().getCity() != null)
				{
					dataRow.add(specimen.getGatheringEvent().getCity());
				}
				else
				{
					dataRow.add(null);
				}
			}

			/* 08_DateIdentified */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "dateIdentified,1"))
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

			
			/* 09_LatitudeDecimal */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "decimalLatitude,1"))
			{
				if (specimen.getGatheringEvent().getSiteCoordinates() != null)
				{
					if (specimen.getGatheringEvent().getSiteCoordinates().iterator().next().getLatitudeDecimal() != null)
					{
						dataRow.add(Double.toString(specimen.getGatheringEvent().getSiteCoordinates()
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
				   

			/* 10_LongitudeDecimal */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "decimalLongitude,1"))
			{
				if (specimen.getGatheringEvent().getSiteCoordinates() != null)
				{
					if (specimen.getGatheringEvent().getSiteCoordinates().iterator().next().getLongitudeDecimal() != null)
					{
						dataRow.add(Double.toString(specimen.getGatheringEvent().getSiteCoordinates()
								.iterator().next().getLongitudeDecimal()));
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
			
			/* 11_verbatimCoordinates */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "verbatimCoordinates,1"))
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
				}
				else
				{
					dataRow.add(null);
				}
			}
			
			/* 12_DateTimeBegin en EndTimeEnd */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "eventDate,1"))
			{
				/* if BeginDate and EndDate both has values get the value of the BeginDate and EndDate */
				if (specimen.getGatheringEvent().getDateTimeBegin() != null && specimen.getGatheringEvent().getDateTimeEnd() != null )
				{
					String dateBegin = datetimebegin.format(specimen.getGatheringEvent().getDateTimeBegin());
					String dateEnd = datetimenend.format(specimen.getGatheringEvent().getDateTimeEnd());
					dataRow.add(dateBegin + " / " + dateEnd);
				}
				/* if BeginDate has a value and EndDate has no value get the value of the BeginDate */
				else if (specimen.getGatheringEvent().getDateTimeBegin() != null && specimen.getGatheringEvent().getDateTimeEnd() == null )
				{
					String dateBegin = datetimebegin.format(specimen.getGatheringEvent().getDateTimeBegin());
					dataRow.add(dateBegin);
				}
				/* if EndDate has a value and Begindate has no value get the value of the Enddate */
				else if(specimen.getGatheringEvent().getDateTimeEnd() != null && specimen.getGatheringEvent().getDateTimeBegin() == null)
				{
					String dateEnd = datetimenend.format(specimen.getGatheringEvent().getDateTimeEnd());
					dataRow.add(dateEnd);
				}
				else
				{
					dataRow.add(null);
				}
			}

			
			/* 13_Family */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "family,1"))
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

			/* 14_GenusOrMonomial */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "genus,1"))
			{
				if(specimen.getIdentifications().iterator().next().getScientificName().getGenusOrMonomial() != null)
				{
					dataRow.add(specimen.getIdentifications().iterator().next().getScientificName().getGenusOrMonomial());
				}
				else
				{
					dataRow.add(null);
				}
			}

			/* 15_Dummy1 */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "geodeticDatum,1"))
			{
				dataRow.add(null);
			}

			/* 16_Dummy2 */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "habitat,1"))
			{
				dataRow.add(null);
			}

			/* 17_Dummy3 */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "higherClassification,1"))
			{
				dataRow.add(null);
			}

			/* 18_identifications.identifiers.fullName | BRAHMS ONLY ?? */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "identifiedBy,1"))
			{
				if (specimen.getIdentifications().iterator().next().getIdentifiers() != null)
				{
					Agent ag = specimen.getIdentifications().iterator().next().getIdentifiers().iterator().next();
					if (ag instanceof Person)
					{
						Person per = (Person) ag;
						dataRow.add(per.getFullName());
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

			/* 19_NumberOfSpecimen */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "individualCount,1"))
			{
				if (Integer.toString(specimen.getNumberOfSpecimen()) != null)
				{
					dataRow.add(Integer.toString(specimen.getNumberOfSpecimen()));
				}
				else
				{
					dataRow.add(null);
				}
			}

			/* 20_DummyDefault ToDO */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "informationWithheld,1"))
			{
				dataRow.add(" ");
			}

			/* 22_InfraspecificEpithet */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "infraspecificEpithet,1"))
			{
				if(specimen.getIdentifications().iterator().next().getScientificName().getInfraspecificEpithet() != null)
				{
					dataRow.add(specimen.getIdentifications().iterator().next().getScientificName().getInfraspecificEpithet());
				}
				else
				{
					dataRow.add(null);
				}	
			}

			/* 22_Island */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "island,1"))
			{
				if (specimen.getGatheringEvent().getIsland() != null)
				{
					dataRow.add(specimen.getGatheringEvent().getIsland());
				}
				else
				{
					dataRow.add(null);
				}
			}

			/* 23_SourceInstitutionID */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "institutionCode,1"))
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

			/* 24_Kingdom */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "kingdom,1"))
			{
				if (specimen.getIdentifications().iterator().next().getDefaultClassification() != null)
				{
					dataRow.add(specimen.getIdentifications().iterator().next().getDefaultClassification().getKingdom());
				}
				else
				{
					dataRow.add(null);
				}
			}
		
			/* 25_PhaseOrStage */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "lifeStage,1"))
			{
				if (specimen.getPhaseOrStage() != null)
				{
					dataRow.add(specimen.getPhaseOrStage());
				}
				else
				{
					dataRow.add(null);
				}
			}

			/* 26_Locality */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "locality,1"))
			{
				if (specimen.getGatheringEvent().getLocality() != null)
				{
					dataRow.add(specimen.getGatheringEvent().getLocality());
				}
				else
				{
					dataRow.add(null);
				}
			}

			/* 27_Dummy4 */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "maximumElevationInMeters,1"))
			{
				dataRow.add(null);
			}

			/* 28_Dummy5 */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "minimumElevationInMeters,1"))
			{
				dataRow.add(null);
			}

			/* 29_Dummy6 ToDO ? */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "nomenclaturalCode,1"))
			{
				dataRow.add("ICZN");
			}

			/* 30_DummyDefault */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "occurrenceID,1"))
			{
				String institutionCode = null;
				String objectType = "specimen";
				if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "institutionCode,1"))
				{
					if(specimen.getSourceInstitutionID().contains("Naturalis"))
					{
	    			   institutionCode = specimen.getSourceInstitutionID().substring(0, 9);
					}
					else
					{
						institutionCode = specimen.getSourceInstitutionID();	
					}
				}
				/* PersitentID is: Example: occurrence id = http://data.biodiversitydata.nl/naturalis/specimen/RMNH.MAM.40012 */
				dataRow.add(CsvFileWriter.httpUrl + institutionCode + "/" + objectType + "/" + specimen.getSourceSystemId());
			}

			/* 31_Order */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "order,1"))
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

			/* 32_Phylum */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "phylum,1"))
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

			/* 33_PreparationType */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "preparations,1"))
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

			/* 34_gatheringEvent.gatheringAgents.fullName */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "recordedBy,1"))
			{
				if (specimen.getGatheringEvent().getGatheringPersons() != null)
				{
					dataRow.add(specimen.getGatheringEvent().getGatheringPersons().iterator().next()
							.getFullName());
				}
				else
				{
					dataRow.add(null);
				}
			}

			/* 35_FullScientificName */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "scientificName,1"))
			{
				if (specimen.getIdentifications().iterator().next().getScientificName().getFullScientificName() != null)
				{
					dataRow.add(specimen.getIdentifications().iterator().next().getScientificName().getFullScientificName());
				}
				else
				{
					dataRow.add(null);
				}
			}

			/* 36_AuthorshipVerbatim */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "scientificNameAuthorship,1"))
			{
				if (specimen.getIdentifications().iterator().next().getScientificName().getAuthorshipVerbatim() != null)
				{
					dataRow.add(specimen.getIdentifications().iterator().next().getScientificName().getAuthorshipVerbatim());
				}
				else
				{
					dataRow.add(null);
				}
			}

			/* 37_Sex */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "sex,1"))
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

			/* 38_SpecificEpithet */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "specificEpithet,1"))
			{
				if (specimen.getIdentifications().iterator().next().getScientificName().getSpecificEpithet() != null)
				{
					dataRow.add(specimen.getIdentifications().iterator().next().getScientificName().getSpecificEpithet());
				}
				else
				{
					dataRow.add(null);
				}
			}

			/* 39_ProvinceState */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "stateProvince,1"))
			{
				if (specimen.getGatheringEvent().getProvinceState() != null)
				{
					dataRow.add(specimen.getGatheringEvent().getProvinceState());
				}
				else
				{
					dataRow.add(null);
				}
			}

			/* 40_Subgenus */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "subGenus,1"))
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

			/* 41_TaxonRank */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "taxonRank,1"))
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

			/* 42_Remarks */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "taxonRemarks,1"))
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

			/* 43_TypeStatus */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "typeStatus,1"))
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
				
			/* 44_Depth */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "verbatimDepth,1"))
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
			
			/* 45_AltitudeUnifOfMeasurement */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "verbatimElevation,1"))
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
			
			/* 46_Dummy7 */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "verbatimEventDate,1"))
			{
				/* if BeginDate and EndDate both has values get the value of the BeginDate and EndDate */
				if (specimen.getGatheringEvent().getDateTimeBegin() != null && specimen.getGatheringEvent().getDateTimeEnd() != null )
				{
					String dateBegin = datetimebegin.format(specimen.getGatheringEvent().getDateTimeBegin());
					String dateEnd = datetimenend.format(specimen.getGatheringEvent().getDateTimeEnd());
					dataRow.add(dateBegin + " / " + dateEnd);
				}
				/* if BeginDate has a value and EndDate has no value get the value of the BeginDate */
				else if (specimen.getGatheringEvent().getDateTimeBegin() != null && specimen.getGatheringEvent().getDateTimeEnd() == null )
				{
					String dateBegin = datetimebegin.format(specimen.getGatheringEvent().getDateTimeBegin());
					dataRow.add(dateBegin);
				}
				/* if EndDate has a value and Begindate has no value get the value of the Enddate */
				else if(specimen.getGatheringEvent().getDateTimeEnd() != null && specimen.getGatheringEvent().getDateTimeBegin() == null)
				{
					String dateEnd = datetimenend.format(specimen.getGatheringEvent().getDateTimeEnd());
					dataRow.add(dateEnd);
				}
				else
				{
					dataRow.add(null);
				}
			}

			/* 47_TaxonRank */
			if (StringUtilities.isFieldChecked(MAPPING_FILE_NAME, "verbatimTaxonRank,1"))
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
