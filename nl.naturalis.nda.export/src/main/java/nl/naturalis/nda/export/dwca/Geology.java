package nl.naturalis.nda.export.dwca;

import java.util.List;

import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;

public class Geology
{
	
	public Geology()
	{
		// TODO Auto-generated constructor stub
	}

	public static void addGeologyOccurrencefield(List<ESSpecimen> list, CsvFileWriter filewriter,
			String MAPPING_FILE_NAME) throws Exception
	{
		for (ESSpecimen specimen : list)
		{
			CsvFileWriter.CsvRow dataRow = filewriter.new CsvRow();

			/* 00_Dummy0 is ID */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "00_Dummy0"))
			{
				Fieldmapping.setDummyValue(MAPPING_FILE_NAME, specimen, dataRow);
			}
				
			/* 01_RecordBasis is basisOfRecord */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "01_RecordBasis"))
			{
				Fieldmapping.setBasisOfRecord(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 02_SourceSystemId is catalogNumber */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "02_SourceSystemId"))
			{
				Fieldmapping.setCatalogNumber(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 03_ClassName is class */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "03_ClassName"))
			{
				Fieldmapping.setClassName(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 04_CollectionType is collectionCode */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "04_CollectionType"))
			{
				Fieldmapping.setCollectionType(MAPPING_FILE_NAME, specimen, dataRow);
			}	
			
			/* 05_Continent is continent */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "05_Continent"))
			{
				Fieldmapping.setContinent(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 06_Country is country */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "06_Country"))
			{
				Fieldmapping.setCountry(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 07_County is county */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "07_County"))
			{
				Fieldmapping.setCounty(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 08_DateIdentified is dateIdentified */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "08_DateIdentified"))
			{
				Fieldmapping.setDateIndentified(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 09_LatitudeDecimal is decimalLatitude */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "09_LatitudeDecimal"))
			{
				Fieldmapping.setLatitudeDecimal(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 10_LongitudeDecimal is decimalLongitude */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "10_LongitudeDecimal"))
			{
				Fieldmapping.setLongitudeDecimal(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 11_Dummy1 is eventDate DateTimeBegin en EndTimeEnd */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "11_Dummy1"))
			{
				Fieldmapping.setEvendate(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 12_Family is family */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "12_Family"))
			{
				Fieldmapping.setFamily(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 13_GenusOrMonomial is genus */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "13_GenusOrMonomial"))
			{
				Fieldmapping.setGenus(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 14_DummyDefault is geodeticDatum */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "14_DummyDefault"))
			{
				Fieldmapping.setGeodeticDatum(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 15_DummyDefault is habitat */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "15_DummyDefault"))
			{
				Fieldmapping.setHabitat(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 16_Dummy2 is higherClassification */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "16_Dummy2"))
			{
				Fieldmapping.setHigherClassification(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 17_identifications_identifiers_fullName is identifiedBy | BRAHMS ONLY ?? */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "17_identifications_identifiers_fullName"))
			{
				Fieldmapping.setIdentifiersFullName(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 18_NumberOfSpecimen is individualCount */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "18_NumberOfSpecimen"))
			{
				Fieldmapping.setNumberOfSpecimen(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 19_DummyDefault is informationWithheld */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "19_DummyDefault"))
			{
				Fieldmapping.setInformationWithHeld(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 20_InfraspecificEpithet is infraspecificEpithet */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "20_InfraspecificEpithet"))
			{
				Fieldmapping.setInfraspecificEpithet(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 21_Island is island */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "21_Island"))
			{
				Fieldmapping.setIsland(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 22_DummyDefault is institutionCode */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "22_DummyDefault"))
			{
				Fieldmapping.setInstitudeCode(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 23_Kingdom is kingdom */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "23_Kingdom"))
			{
				Fieldmapping.setKingdom(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 24_PhaseOrStage is lifeStage */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "24_PhaseOrStage"))
			{
				Fieldmapping.setPhaseOrStage(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 25_Locality is locality */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "25_Locality"))
			{
				Fieldmapping.setLocality(MAPPING_FILE_NAME, specimen, dataRow);
			}
				
			/* 26_DummyDefault is maximumElevationInMeters */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "26_DummyDefault"))
			{
				Fieldmapping.setMaximumElevationInMeters(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 27_DummyDefault is minimumElevationInMeters */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "27_DummyDefault"))
			{
				Fieldmapping.setMinimumElevationInMeters(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 28_DummyDefault is nomenclaturalCode */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "28_DummyDefault"))
			{
				Fieldmapping.setNomenclaturalCode_Geology(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 29_DummyDefault is occurrenceID */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "29_DummyDefault"))
			{
				Fieldmapping.setOccurrenceID(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 30_Order is order */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "30_Order"))
			{
				Fieldmapping.setOrder(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 31_Phylum is phylum */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "31_Phylum"))
			{
				Fieldmapping.setPhylum(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 32_PreparationType is preparations */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "32_PreparationType"))
			{
				Fieldmapping.setPreparationType(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 33_gatheringEvent_gatheringAgents_fullName IS recordedBy */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "33_gatheringEvent_gatheringAgents_fullName"))
			{
				Fieldmapping.setGatheringAgents_FullName(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 34_FullScientificName IS scientificName */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "34_FullScientificName"))
			{
				Fieldmapping.setFullScientificName(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 35_AuthorshipVerbatim IS scientificNameAuthorship */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "35_AuthorshipVerbatim"))
			{
				Fieldmapping.setAuthorshipVerbatim(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 36_Sex is sex */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "36_Sex"))
			{
				Fieldmapping.setSex(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 37_SpecificEpithet is specificEpithet */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "37_SpecificEpithet"))
			{
				Fieldmapping.setSpecificEpithet(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 38_ProvinceState is stateProvince */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "38_ProvinceState"))
			{
				Fieldmapping.setProvinceState(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 39_Subgenus is subGenus */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "39_Subgenus"))
			{
				Fieldmapping.setSubGenus(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 40_TaxonRank is taxonRank */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "40_TaxonRank"))
			{
				Fieldmapping.setTaxonrank(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 41_Remarks is taxonRemarks */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "41_Remarks"))
			{
				Fieldmapping.setTaxonRemarks(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 42_TypeStatus is typeStatus */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "42_TypeStatus"))
			{
				Fieldmapping.setTypeStatus(MAPPING_FILE_NAME, specimen, dataRow);	
			}
			
			/* 43_Dummy3 is verbatimCoordinates */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "43_Dummy3"))
			{
				Fieldmapping.setVerbatimCoordinates(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 44_Depth is verbatimDepth */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "44_Depth"))
			{
				Fieldmapping.setVerbatimDepth(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 45_AltitudeUnifOfMeasurement is verbatimElevation */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "45_AltitudeUnifOfMeasurement"))
			{
				Fieldmapping.setAltitudeUnifOfMeasurement(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 46_Dummy4 is verbatimEventDate */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "46_Dummy4"))
			{
				Fieldmapping.setVerbatimEventDate(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/* 47_TaxonRank is verbatimTaxonRank */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "47_TaxonRank"))
			{
				Fieldmapping.setTaxonRank_Is_VerbatimTaxonRank(MAPPING_FILE_NAME, specimen, dataRow);
			}
			
			/**
			 * adding data row
			 */
     		filewriter.WriteRow(dataRow);
		}
	}
}