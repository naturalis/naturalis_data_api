package nl.naturalis.nda.export.dwca;

import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;


import java.io.IOException;
import java.util.List;


public class Zoology
{
	
	public Zoology()
	{
		// TODO Auto-generated constructor stub
	}
  
	public static void addZoologyOccurrencefield(List<ESSpecimen> list, CsvFileWriter filewriter,
			String MAPPING_FILE_NAME) throws IOException
	{
		/* ALL Mapping fields is set default at '1' */
		
		for (ESSpecimen specimen : list)
		{
			CsvFileWriter.CsvRow dataRow = filewriter.new CsvRow();
			
			/* 00_Dummy0 is ID */
			Fieldmapping.setDummyValue(MAPPING_FILE_NAME, specimen, dataRow);
			
			/* 01_RecordBasis is basisOfRecord */
			Fieldmapping.setBasisOfRecord(MAPPING_FILE_NAME, specimen, dataRow);
			
			/* 02_SourceSystemId is catalogNumber */
			Fieldmapping.setCatalogNumber(MAPPING_FILE_NAME, specimen, dataRow);
			
			/* 03_ClassName is class */
			Fieldmapping.setClassName(MAPPING_FILE_NAME, specimen, dataRow);
			
			/* 04_CollectionType is collectionCode */
			Fieldmapping.setCollectionType(MAPPING_FILE_NAME, specimen, dataRow);
			
			/* 05_Continent is continent */
			Fieldmapping.setContinent(MAPPING_FILE_NAME, specimen, dataRow);
			
			/* 06_Country is country */
			Fieldmapping.setCountry(MAPPING_FILE_NAME, specimen, dataRow);
			
			/* 07_County is county */
			Fieldmapping.setCounty(MAPPING_FILE_NAME, specimen, dataRow);

			/* 08_DateIdentified is dateIdentified */
			Fieldmapping.setDateIndentified(MAPPING_FILE_NAME, specimen, dataRow);
			
			/* 09_LatitudeDecimal is decimalLatitude */
			Fieldmapping.setLatitudeDecimal(MAPPING_FILE_NAME, specimen, dataRow);
			
			/* 10_LongitudeDecimal is decimalLongitude */
			Fieldmapping.setLongitudeDecimal(MAPPING_FILE_NAME, specimen, dataRow);
			
			/* 11_Dummy1 is eventDate  DateTimeBegin en EndTimeEnd */
			Fieldmapping.setEvendate(MAPPING_FILE_NAME, specimen, dataRow);
			
			/* 12_Family is family */
			Fieldmapping.setFamily(MAPPING_FILE_NAME, specimen, dataRow);

			/* 13_GenusOrMonomial is genus */
			Fieldmapping.setGenus(MAPPING_FILE_NAME, specimen, dataRow);

			/* 14_DummyDefault is geodeticDatum */
			Fieldmapping.setGeodeticDatum(MAPPING_FILE_NAME, specimen, dataRow);

			/* 15_DummyDefault is habitat */
			Fieldmapping.setHabitat(MAPPING_FILE_NAME, specimen, dataRow);

			/* 16_Dummy2 is higherClassification */
			Fieldmapping.setHigherClassification(MAPPING_FILE_NAME, specimen, dataRow);

			/* 17_identifications_identifiers_fullName is identifiedBy */
			Fieldmapping.setIdentifiersFullName(MAPPING_FILE_NAME, specimen, dataRow);

			/* 18_NumberOfSpecimen is individualCount */
			Fieldmapping.setNumberOfSpecimen(MAPPING_FILE_NAME, specimen, dataRow);

			/* 19_DummyDefault is informationWithheld */
			Fieldmapping.setInformationWithHeld(MAPPING_FILE_NAME, specimen, dataRow);

			/* 20_InfraspecificEpithet is infraspecificEpithet */
			Fieldmapping.setInfraspecificEpithet(MAPPING_FILE_NAME, specimen, dataRow);

			/* 21_Island is island */
            Fieldmapping.setIsland(MAPPING_FILE_NAME, specimen, dataRow);
            
			/* 22_DummyDefault is institutionCode */
			Fieldmapping.setInstitudeCode(MAPPING_FILE_NAME, specimen, dataRow);

			/* 23_Kingdom is kingdom */
			Fieldmapping.setKingdom(MAPPING_FILE_NAME, specimen, dataRow);
		
			/* 24_PhaseOrStage is lifeStage */
			Fieldmapping.setPhaseOrStage(MAPPING_FILE_NAME, specimen, dataRow);

			/* 25_Locality is locality */
            Fieldmapping.setLocality(MAPPING_FILE_NAME, specimen, dataRow);

            /* 26_DummyDefault is maximumElevationInMeters */
			Fieldmapping.setMaximumElevationInMeters(MAPPING_FILE_NAME, specimen, dataRow);

			/* 27_DummyDefault is minimumElevationInMeters */
			Fieldmapping.setMinimumElevationInMeters(MAPPING_FILE_NAME, specimen, dataRow);

			/* 28_DummyDefault is nomenclaturalCode */
			Fieldmapping.setNomenclaturalCode_Zoology(MAPPING_FILE_NAME, specimen, dataRow);

			/* 29_DummyDefault is occurrenceID */
			Fieldmapping.setOccurrenceID(MAPPING_FILE_NAME, specimen, dataRow);
			
			/* 30_Order is order */
            Fieldmapping.setOrder(MAPPING_FILE_NAME, specimen, dataRow);
			
			/* 31_Phylum is phylum */
			Fieldmapping.setPhylum(MAPPING_FILE_NAME, specimen, dataRow);

			/* 32_PreparationType is preparations */
			Fieldmapping.setPreparationType(MAPPING_FILE_NAME, specimen, dataRow);

			/* 33_gatheringEvent_gatheringAgents_fullName is recordedBy */
			Fieldmapping.setGatheringAgents_FullName(MAPPING_FILE_NAME, specimen, dataRow);
			
			/* 34_FullScientificName is scientificName */
			Fieldmapping.setFullScientificName(MAPPING_FILE_NAME, specimen, dataRow);

			/* 35_AuthorshipVerbatim is scientificNameAuthorship */
			Fieldmapping.setAuthorshipVerbatim(MAPPING_FILE_NAME, specimen, dataRow);

			/* 36_Sex is sex */
			Fieldmapping.setSex(MAPPING_FILE_NAME, specimen, dataRow);

			/* 37_SpecificEpithet is specificEpithet */
			Fieldmapping.setSpecificEpithet(MAPPING_FILE_NAME, specimen, dataRow);

			/* 38_ProvinceState is stateProvince */
			Fieldmapping.setProvinceState(MAPPING_FILE_NAME, specimen, dataRow);
			
			/* 39_Subgenus is subGenus */
			Fieldmapping.setSubGenus(MAPPING_FILE_NAME, specimen, dataRow);

			/* 40_TaxonRank is taxonRank */
			Fieldmapping.setTaxonrank(MAPPING_FILE_NAME, specimen, dataRow);

			/* 41_Remarks is taxonRemarks */
			Fieldmapping.setTaxonRemarks(MAPPING_FILE_NAME, specimen, dataRow);

			/* 42_TypeStatus is typeStatus */
            Fieldmapping.setTypeStatus(MAPPING_FILE_NAME, specimen, dataRow);	
            
			/* 43_Dummy3 is verbatimCoordinates */
			Fieldmapping.setVerbatimCoordinates(MAPPING_FILE_NAME, specimen, dataRow);

			/* 44_Depth is verbatimDepth */
			Fieldmapping.setVerbatimDepth(MAPPING_FILE_NAME, specimen, dataRow);
			
			/* 45_AltitudeUnifOfMeasurement is verbatimElevation */
			Fieldmapping.setAltitudeUnifOfMeasurement(MAPPING_FILE_NAME, specimen, dataRow);
			
			/* 46_Dummy4 is verbatimEventDate */
			Fieldmapping.setVerbatimEventDate(MAPPING_FILE_NAME, specimen, dataRow);

			/* 47_TaxonRank is verbatimTaxonRank */
			Fieldmapping.setTaxonRank_Is_VerbatimTaxonRank(MAPPING_FILE_NAME, specimen, dataRow);

			/**
			 * adding data row
			 */
			filewriter.WriteRow(dataRow);
		}
	 }

}
