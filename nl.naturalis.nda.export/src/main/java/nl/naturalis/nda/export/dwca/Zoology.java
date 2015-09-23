package nl.naturalis.nda.export.dwca;

import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import java.util.List;


/**
 * <h1>ZOOLOGY</h1>
  *  Description: Match the fields what is described in the zoology.properties<br>
 *               and set the value to the CSV datarow 
 *  
 *  @version	1.0
 *  @author 	Reinier.Kartowikromo 
 *  @since		01-07-2015
 *  
 * */
public class Zoology
{
	
	public Zoology()
	{
		// TODO Auto-generated constructor stub
	}
	/**
	 * This method will add all values to a list.
	 * Used in the class DwCAExporter method
	 * private static void writeCRSZoologyCsvFile(List<ESSpecimen> listZoology, String occurrenceFields) throws Exception {
	 * @param list
	 * @param filewriter
	 * @param MAPPING_FILE_NAME
	 * @throws Exception
	 */
  
	public static void addZoologyOccurrencefield(List<ESSpecimen> list, CsvFileWriter filewriter,
			String MAPPING_FILE_NAME) throws Exception
	{
		/*
		 *  ALL Mapping fields is set default at '1' */
		
		for (ESSpecimen specimen : list)
		{
			CsvFileWriter.CsvRow dataRow = filewriter.new CsvRow();
			
			/* 00_Dummy0 is ID */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "00_Dummy0"))
			{
				Fieldmapping.setDummyValue(specimen, dataRow);
			}
			
			/* 01_RecordBasis is basisOfRecord */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "01_RecordBasis"))
			{
				Fieldmapping.setBasisOfRecord(specimen, dataRow);
			}
			
			/* 02_SourceSystemId is catalogNumber */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "02_SourceSystemId"))
			{
				Fieldmapping.setCatalogNumber(specimen, dataRow);
			}
			
			/* 03_ClassName is class */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "03_ClassName"))
			{
				Fieldmapping.setClassName(specimen, dataRow);
			}
			
			/* 04_CollectionType is collectionCode */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "04_CollectionType"))
			{
				Fieldmapping.setCollectionType(specimen, dataRow);
			}	
			
			/* 05_Continent is continent */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "05_Continent"))
			{
				Fieldmapping.setContinent(specimen, dataRow);
			}
			
			/* 06_Country is country */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "06_Country"))
			{
				Fieldmapping.setCountry(specimen, dataRow);
			}
			
			/* 07_County is county */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "07_County"))
			{
				Fieldmapping.setCounty(specimen, dataRow);
			}
			
			/* 08_DateIdentified is dateIdentified */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "08_DateIdentified"))
			{
				Fieldmapping.setDateIndentified(specimen, dataRow);
			}
			
			/* 09_LatitudeDecimal is decimalLatitude */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "09_LatitudeDecimal"))
			{
				Fieldmapping.setLatitudeDecimal(specimen, dataRow);
			}
			
			/* 10_LongitudeDecimal is decimalLongitude */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "10_LongitudeDecimal"))
			{
				Fieldmapping.setLongitudeDecimal(specimen, dataRow);
			}
			
			/* 11_Dummy1 is eventDate  DateTimeBegin en EndTimeEnd */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "11_Dummy1"))
			{
				Fieldmapping.setEvendate(specimen, dataRow);
			}
			
			/* 12_Family is family */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "12_Family"))
			{
				Fieldmapping.setFamily(specimen, dataRow);
			}
			
			/* 13_GenusOrMonomial is genus */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "13_GenusOrMonomial"))
			{
				Fieldmapping.setGenus(specimen, dataRow);
			}
			
			/* 14_DummyDefault is geodeticDatum */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "14_DummyDefault"))
			{
				Fieldmapping.setGeodeticDatum(specimen, dataRow);
			}
			
			/* 15_DummyDefault is habitat */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "15_DummyDefault"))
			{
				Fieldmapping.setHabitat(specimen, dataRow);
			}
			
			/* 16_Dummy2 is higherClassification */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "16_Dummy2"))
			{
				Fieldmapping.setHigherClassification(specimen, dataRow);
			}
			
			/* 17_identifications_identifiers_fullName is identifiedBy */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "17_identifications_identifiers_fullName"))
			{
				Fieldmapping.setIdentifiersFullName(specimen, dataRow);
			}
			
			/* 18_NumberOfSpecimen is individualCount */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "18_NumberOfSpecimen"))
			{
				Fieldmapping.setNumberOfSpecimen(specimen, dataRow);
			}
			
			/* 19_DummyDefault is informationWithheld */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "19_DummyDefault"))
			{
				Fieldmapping.setInformationWithHeld(specimen, dataRow);
			}
				
			/* 20_InfraspecificEpithet is infraspecificEpithet */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "20_InfraspecificEpithet"))
			{
				Fieldmapping.setInfraspecificEpithet(specimen, dataRow);
			}
			
			/* 21_Island is island */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "21_Island"))
			{
				Fieldmapping.setIsland(specimen, dataRow);
			}
			
			/* 22_DummyDefault is institutionCode */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "22_DummyDefault"))
			{
				Fieldmapping.setInstitudeCode(specimen, dataRow);
			}
			
			/* 23_Kingdom is kingdom */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "23_Kingdom"))
			{
				Fieldmapping.setKingdom(specimen, dataRow);
			}
			
			/* 24_PhaseOrStage is lifeStage */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "24_PhaseOrStage"))
			{
				Fieldmapping.setPhaseOrStage(specimen, dataRow);
			}
			
			/* 25_Locality is locality */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "25_Locality"))
			{
				Fieldmapping.setLocality(specimen, dataRow);
			}
			
            /* 26_DummyDefault is maximumElevationInMeters */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "26_DummyDefault"))
			{
				Fieldmapping.setMaximumElevationInMeters(specimen, dataRow);
			}
			
			/* 27_DummyDefault is minimumElevationInMeters */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "27_DummyDefault"))
			{
				Fieldmapping.setMinimumElevationInMeters(specimen, dataRow);
			}
			
			/* 28_DummyDefault is nomenclaturalCode */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "28_DummyDefault"))
			{
				Fieldmapping.setNomenclaturalCode_Zoology(specimen, dataRow);
			}
			
			/* 29_unitGUID is occurrenceID */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "29_unitGUID"))
			{
				Fieldmapping.setOccurrenceID(specimen, dataRow);
			}
			
			/* 30_Order is order */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "30_Order"))
			{
				Fieldmapping.setOrder(specimen, dataRow);
			}
			
			/* 31_Phylum is phylum */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "31_Phylum"))
			{
				Fieldmapping.setPhylum(specimen, dataRow);
			}
			
			/* 32_PreparationType is preparations */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "32_PreparationType"))
			{
				Fieldmapping.setPreparationType(specimen, dataRow);
			}
			
			/* 33_gatheringEvent_gatheringAgents_fullName is recordedBy */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "33_gatheringEvent_gatheringAgents_fullName"))
			{
				Fieldmapping.setGatheringAgents_FullName(specimen, dataRow);
			}
			
			/* 34_FullScientificName is scientificName */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "34_FullScientificName"))
			{
				Fieldmapping.setFullScientificName(specimen, dataRow);
			}
			
			/* 35_AuthorshipVerbatim is scientificNameAuthorship */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "35_AuthorshipVerbatim"))
			{
				Fieldmapping.setAuthorshipVerbatim(specimen, dataRow);
			}
			
			/* 36_Sex is sex */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "36_Sex"))
			{
				Fieldmapping.setSex(specimen, dataRow);
			}
			
			/* 37_SpecificEpithet is specificEpithet */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "37_SpecificEpithet"))
			{
				Fieldmapping.setSpecificEpithet(specimen, dataRow);
			}
			
			/* 38_ProvinceState is stateProvince */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "38_ProvinceState"))
			{
				Fieldmapping.setProvinceState(specimen, dataRow);
			}
			
			/* 39_Subgenus is subGenus */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "39_Subgenus"))
			{
				Fieldmapping.setSubGenus(specimen, dataRow);
			}
			
			/* 40_TaxonRank is taxonRank */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "40_TaxonRank"))
			{
				Fieldmapping.setTaxonrank(specimen, dataRow);
			}
			
			/* 41_Remarks is taxonRemarks */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "41_Remarks"))
			{
				Fieldmapping.setTaxonRemarks(specimen, dataRow);
			}
			
			/* 42_TypeStatus is typeStatus */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "42_TypeStatus"))
			{
				Fieldmapping.setTypeStatus(specimen, dataRow);	
			}
			
			/* 43_Dummy3 is verbatimCoordinates */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "43_Dummy3"))
			{
				Fieldmapping.setVerbatimCoordinates(specimen, dataRow);
			}
			
			/* 44_Depth is verbatimDepth */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "44_Depth"))
			{
				Fieldmapping.setVerbatimDepth(specimen, dataRow);
			}
			
			/* 45_AltitudeUnifOfMeasurement is verbatimElevation */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "45_AltitudeUnifOfMeasurement"))
			{
				Fieldmapping.setAltitudeUnifOfMeasurement(specimen, dataRow);
			}
			
			/* 46_Dummy4 is verbatimEventDate */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "46_Dummy4"))
			{
				Fieldmapping.setVerbatimEventDate(specimen, dataRow);
			}
			
			/* 47_TaxonRank is verbatimTaxonRank */
			if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME, "47_TaxonRank"))
			{
				Fieldmapping.setTaxonRank_Is_VerbatimTaxonRank(specimen, dataRow);
			}
			
			/**
			 * adding data row
			 */
			filewriter.WriteRow(dataRow);
		}
	 }

}
