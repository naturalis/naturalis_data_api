package nl.naturalis.nda.export.dwca;

import java.util.List;

import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;

public class Brahms
{
	static Fieldmapping fieldmap = new Fieldmapping();
	
	public Brahms()
	{
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("static-access")
	public static void addBrahmsOccurrencefield(List<ESSpecimen> list, CsvFileWriter filewriter,
			String MAPPING_FILE_NAME) throws Exception 
	{
		
		
		for (ESSpecimen specimen : list)
		{
			CsvFileWriter.CsvRow dataRow = filewriter.new CsvRow();

			/* 00_Dummy0 is ID */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "00_Dummy0"))
			{
				fieldmap.setDummyValue(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 01_Dummy1 is basisOfRecord */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "01_Dummy1"))
			{
				fieldmap.setBasisOfRecord_Brahms(MAPPING_FILE_NAME, specimen, dataRow);
			}

			/* 02_SourceSystemId is catalogNumber */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "02_SourceSystemId"))
			{
				fieldmap.setCatalogNumber(MAPPING_FILE_NAME, specimen, dataRow);
			}
            
			/* 03_DummyDefault class */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "03_DummyDefault"))
			{
				fieldmap.setClassName_Brahms(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 04_DummyDefault is collectionCode */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "04_DummyDefault"))
			{
				fieldmap.setCollectionCode_Brahms(MAPPING_FILE_NAME, specimen, dataRow);
			}
            
			/* 05_Continent is continent */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "05_Continent"))
			{
				fieldmap.setContinent(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 06_Country is country */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "06_Country"))
			{
				fieldmap.setCountry(MAPPING_FILE_NAME, specimen, dataRow);
			}
				
			/* 07_County is county*/
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "07_County"))
			{
				fieldmap.setCounty(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 08_DateIdentified is dateIdentified */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "08_DateIdentified"))
			{
				fieldmap.setDateIndentified_Brahms(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 09_LatitudeDecimal is decimalLatitude */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "09_LatitudeDecimal"))
			{
				fieldmap.setLatitudeDecimal(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 10_LongitudeDecimal is decimalLongitude */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "10_LongitudeDecimal"))
			{
				fieldmap.setLongitudeDecimal(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 11_Dummy2 is eventDate DateTimeBegin en EndTimeEnd */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "11_Dummy2"))
			{
				fieldmap.setEvendate_Brahms(MAPPING_FILE_NAME, specimen, dataRow);
			}
				
			/* 12_DummyDefault is family */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "12_DummyDefault"))
			{
				fieldmap.setFamily_Brahms(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 13_GenusOrMonomial is genus */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "13_GenusOrMonomial"))
			{
				fieldmap.setGenus_Brahms(MAPPING_FILE_NAME, specimen, dataRow);
			}
				
			/* 14_DummyDefault is geodeticDatum */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "14_DummyDefault"))
			{
				fieldmap.setGeodeticDatum(MAPPING_FILE_NAME, specimen, dataRow);
			}
				
			/* 15_DummyDefault is  habitat */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "15_DummyDefault"))
			{
				fieldmap.setHabitat(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 16_Dummy3 is higherClassification */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "16_Dummy3"))
			{
				fieldmap.setHigherClassification_Brahms(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 17_identifications_identifiers_fullName is identifiedBy */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "17_identifications_identifiers_fullName"))
			{
				fieldmap.setIdentifiersFullName_Brahms(MAPPING_FILE_NAME, specimen, dataRow);
			}
				
            /* 18_NumberOfSpecimen is individualCount */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "18_NumberOfSpecimen"))
			{
				fieldmap.setNumberOfSpecimen(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 19_DummyDefault is informationWithheld */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "19_DummyDefault"))
			{
				fieldmap.setInformationWithHeld(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 20_InfraspecificEpithet is infraSpecificEpithet */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "20_InfraspecificEpithet"))
			{
				fieldmap.setInfraspecificEpithet_Brahms(MAPPING_FILE_NAME, specimen, dataRow);
			}
				
			/* 21_Island is island */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "21_Island"))
			{
				fieldmap.setIsland(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 22_DummyDefault is institutionCode */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "22_DummyDefault"))
			{
				fieldmap.setInstitudeCode(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 23_DummyDefault is kingdom */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "23_DummyDefault"))
			{
				fieldmap.setKingdom_Brahms(MAPPING_FILE_NAME, specimen, dataRow);
			}
				
			/* 24_PhaseOrStage is lifeStage */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "24_PhaseOrStage"))
			{
				fieldmap.setPhaseOrStage(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 25_Locality is locality */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "25_Locality"))
			{
				fieldmap.setLocality(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 26_DummyDefault maximumElevationInMeters */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "26_DummyDefault"))
			{
				fieldmap.setMaximumElevationInMeters(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 27_DummyDefault is minimumElevationInMeters */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "27_DummyDefault"))
			{
				fieldmap.setMinimumElevationInMeters(MAPPING_FILE_NAME, specimen, dataRow);
			}
				
			/* 28_DummyDefault is nomenclaturalCode */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "28_DummyDefault"))
			{
				fieldmap.setNomenclaturalCode_Brahms(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 29_DummyDefault is occurrenceID */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "29_DummyDefault"))
			{
				fieldmap.setOccurrenceID(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 30_Order is order */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "30_Order"))
			{
				fieldmap.setOrder_Brahms(MAPPING_FILE_NAME, specimen, dataRow);
			}

			/* 31_Phylum is Phylum */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "31_Phylum"))
			{
				fieldmap.setPhylum(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 32_PreparationType is preparations */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "32_PreparationType"))
			{
				fieldmap.setPreparationType(MAPPING_FILE_NAME, specimen, dataRow);
			}
				
			/* 33_gatheringEvent.gatheringAgents.fullName is recordedBy */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "33_gatheringEvent_gatheringAgents_fullName"))
			{
				fieldmap.setGatheringAgents_FullName(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 34_FullScientificName is scientificName */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "34_FullScientificName"))
			{
				fieldmap.setFullScientificName_Brahms(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 35_AuthorshipVerbatim is scientificNameAuthorship */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "35_AuthorshipVerbatim"))
			{
				fieldmap.setAuthorshipVerbatim_Brahms(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 36_Sex is sex */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "36_Sex"))
			{
				fieldmap.setSex(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 37_SpecificEpithet is specificEpithet */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "37_SpecificEpithet"))
			{
				fieldmap.setSpecificEpithet_Brahms(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 38_ProvinceState is stateProvince */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "38_ProvinceState"))
			{
				fieldmap.setProvinceState(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 39_Subgenus is subGenus */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "39_Subgenus"))
			{
				fieldmap.setSubGenus_Brahms(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 40_Dummy4 is taxonRank */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "40_Dummy4"))
			{
				fieldmap.setTaxonrank_Brahms(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 41_Remarks is taxonRemarks */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "41_Remarks"))
			{
				fieldmap.setTaxonRemarks(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 42_TypeStatus is typeStatus */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "42_TypeStatus"))
			{
				fieldmap.setTypeStatus(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 43_Dummy5 is verbatimCoordinates */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "43_Dummy5"))
			{
				fieldmap.setVerbatimCoordinates_Brahms(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 44_Depth is verbatimDepth */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "44_Depth"))
			{
				fieldmap.setVerbatimDepth(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 45_AltitudeUnifOfMeasurement is verbatimElevation */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "45_AltitudeUnifOfMeasurement"))
			{
				fieldmap.setAltitudeUnifOfMeasurement(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 46_Dummy6 is verbatimEventDate */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "46_Dummy6"))
			{
				fieldmap.setVerbatimEventDate_Brahms(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 47_TaxonRank is verbatimTaxonRank */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "47_TaxonRank"))
			{
				fieldmap.setTaxonRank_Is_VerbatimTaxonRank(MAPPING_FILE_NAME, specimen, dataRow);
			}
			/**
			 * adding data row
			 */
			filewriter.WriteRow(dataRow);
		}
	}
	
	
}
