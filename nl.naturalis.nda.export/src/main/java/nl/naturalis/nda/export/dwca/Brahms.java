package nl.naturalis.nda.export.dwca;

import java.util.List;

import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;

public class Brahms
{
	public Brahms()
	{
		// TODO Auto-generated constructor stub
	}

	public static void addBrahmsOccurrencefield(List<ESSpecimen> list, CsvFileWriter filewriter,
			String MAPPING_FILE_NAME) throws Exception 
	{
		for (ESSpecimen specimen : list)
		{
			CsvFileWriter.CsvRow dataRow = filewriter.new CsvRow();

			/* 00_Dummy0 is ID */
			Fieldmapping.setDummyValue(MAPPING_FILE_NAME, specimen, dataRow);
			
			/* 01_Dummy1 is basisOfRecord */
            Fieldmapping.setBasisOfRecord_Brahms(MAPPING_FILE_NAME, specimen, dataRow);

			/* 02_SourceSystemId is catalogNumber */
            Fieldmapping.setCatalogNumber(MAPPING_FILE_NAME, specimen, dataRow);

			/* 03_DummyDefault class */
            Fieldmapping.setClassName_Brahms(MAPPING_FILE_NAME, specimen, dataRow);
            
			/* 04_DummyDefault is collectionCode */
            Fieldmapping.setCollectionCode_Brahms(MAPPING_FILE_NAME, specimen, dataRow);
            	
			/* 05_Continent is continent */
            Fieldmapping.setContinent(MAPPING_FILE_NAME, specimen, dataRow);
			
			/* 06_Country is country */
            Fieldmapping.setCountry(MAPPING_FILE_NAME, specimen, dataRow);

			/* 07_County is county*/
            Fieldmapping.setCounty(MAPPING_FILE_NAME, specimen, dataRow);

			/* 08_DateIdentified is dateIdentified */
            Fieldmapping.setDateIndentified_Brahms(MAPPING_FILE_NAME, specimen, dataRow);
            
			/* 09_LatitudeDecimal is decimalLatitude */
            Fieldmapping.setLatitudeDecimal(MAPPING_FILE_NAME, specimen, dataRow);
				
			/* 10_LongitudeDecimal is decimalLongitude */
            Fieldmapping.setLongitudeDecimal(MAPPING_FILE_NAME, specimen, dataRow);
			
			/* 11_Dummy2 is eventDate DateTimeBegin en EndTimeEnd */
            Fieldmapping.setEvendate_Brahms(MAPPING_FILE_NAME, specimen, dataRow);

			/* 12_DummyDefault is family */
            Fieldmapping.setFamily_Brahms(MAPPING_FILE_NAME, specimen, dataRow);
            	
			/* 13_GenusOrMonomial is genus */
            Fieldmapping.setGenus_Brahms(MAPPING_FILE_NAME, specimen, dataRow);
			
			/* 14_DummyDefault is geodeticDatum */
            Fieldmapping.setGeodeticDatum(MAPPING_FILE_NAME, specimen, dataRow);

			/* 15_DummyDefault is  habitat */
            Fieldmapping.setHabitat(MAPPING_FILE_NAME, specimen, dataRow);
		
			/* 16_Dummy3 is higherClassification */
            Fieldmapping.setHigherClassification_Brahms(MAPPING_FILE_NAME, specimen, dataRow);
            
			/* 17_identifications_identifiers_fullName is identifiedBy */
            Fieldmapping.setIdentifiersFullName_Brahms(MAPPING_FILE_NAME, specimen, dataRow);

            /* 18_NumberOfSpecimen is individualCount */
            Fieldmapping.setNumberOfSpecimen(MAPPING_FILE_NAME, specimen, dataRow);

			/* 19_DummyDefault is informationWithheld */
            Fieldmapping.setInformationWithHeld(MAPPING_FILE_NAME, specimen, dataRow);
            
			/* 20_InfraspecificEpithet is infraSpecificEpithet */
			Fieldmapping.setInfraspecificEpithet_Brahms(MAPPING_FILE_NAME, specimen, dataRow);

			/* 21_Island is island */
			Fieldmapping.setIsland(MAPPING_FILE_NAME, specimen, dataRow);
			
			/* 22_DummyDefault is institutionCode */
			Fieldmapping.setInstitudeCode(MAPPING_FILE_NAME, specimen, dataRow);

			/* 23_DummyDefault is kingdom */
			Fieldmapping.setKingdom_Brahms(MAPPING_FILE_NAME, specimen, dataRow);
				
			/* 24_PhaseOrStage is lifeStage */
			Fieldmapping.setPhaseOrStage(MAPPING_FILE_NAME, specimen, dataRow);
			
			/* 25_Locality is locality */
			Fieldmapping.setLocality(MAPPING_FILE_NAME, specimen, dataRow);
			
			/* 26_DummyDefault maximumElevationInMeters */
			Fieldmapping.setMaximumElevationInMeters(MAPPING_FILE_NAME, specimen, dataRow);

			/* 27_DummyDefault is minimumElevationInMeters */
			Fieldmapping.setMinimumElevationInMeters(MAPPING_FILE_NAME, specimen, dataRow);

			/* 28_DummyDefault is nomenclaturalCode */
			Fieldmapping.setNomenclaturalCode_Brahms(MAPPING_FILE_NAME, specimen, dataRow);
			
			/* 29_DummyDefault is occurrenceID */
			Fieldmapping.setOccurrenceID(MAPPING_FILE_NAME, specimen, dataRow);

			/* 30_Order is order */
			Fieldmapping.setOrder_Brahms(MAPPING_FILE_NAME, specimen, dataRow);

			/* 31_Phylum is Phylum */
			Fieldmapping.setPhylum(MAPPING_FILE_NAME, specimen, dataRow);

			/* 32_PreparationType is preparations */
			Fieldmapping.setPreparationType(MAPPING_FILE_NAME, specimen, dataRow);

			/* 33_gatheringEvent.gatheringAgents.fullName is recordedBy */
			Fieldmapping.setGatheringAgents_FullName(MAPPING_FILE_NAME, specimen, dataRow);
			
			/* 34_FullScientificName is scientificName */
			Fieldmapping.setFullScientificName_Brahms(MAPPING_FILE_NAME, specimen, dataRow);
			
			/* 35_AuthorshipVerbatim is scientificNameAuthorship */
			Fieldmapping.setAuthorshipVerbatim_Brahms(MAPPING_FILE_NAME, specimen, dataRow);

			/* 36_Sex is sex */
			Fieldmapping.setSex(MAPPING_FILE_NAME, specimen, dataRow);
			
			/* 37_SpecificEpithet is specificEpithet */
			Fieldmapping.setSpecificEpithet_Brahms(MAPPING_FILE_NAME, specimen, dataRow);

			/* 38_ProvinceState is stateProvince */
			Fieldmapping.setProvinceState(MAPPING_FILE_NAME, specimen, dataRow);

			/* 39_Subgenus is subGenus */
			Fieldmapping.setSubGenus_Brahms(MAPPING_FILE_NAME, specimen, dataRow);

			/* 40_Dummy4 is taxonRank */
			Fieldmapping.setTaxonrank_Brahms(MAPPING_FILE_NAME, specimen, dataRow);

			/* 41_Remarks is taxonRemarks */
			Fieldmapping.setTaxonRemarks(MAPPING_FILE_NAME, specimen, dataRow);

			/* 42_TypeStatus is typeStatus */
			Fieldmapping.setTypeStatus(MAPPING_FILE_NAME, specimen, dataRow);
			
			/* 43_Dummy5 is verbatimCoordinates */
			Fieldmapping.setVerbatimCoordinates_Brahms(MAPPING_FILE_NAME, specimen, dataRow);
			
			/* 44_Depth is verbatimDepth */
			Fieldmapping.setVerbatimDepth(MAPPING_FILE_NAME, specimen, dataRow);

			/* 45_AltitudeUnifOfMeasurement is verbatimElevation */
			Fieldmapping.setAltitudeUnifOfMeasurement(MAPPING_FILE_NAME, specimen, dataRow);

			/* 46_Dummy6 is verbatimEventDate */
			Fieldmapping.setVerbatimEventDate_Brahms(MAPPING_FILE_NAME, specimen, dataRow);

			/* 47_TaxonRank is verbatimTaxonRank */
			Fieldmapping.setTaxonRank_Is_VerbatimTaxonRank(MAPPING_FILE_NAME, specimen, dataRow);

			/**
			 * adding data row
			 */
			filewriter.WriteRow(dataRow);
		}
	}
	
	
}
