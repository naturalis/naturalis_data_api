package nl.naturalis.nda.elasticsearch.dao.transfer;

import java.util.List;

import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.domain.SpecimenIdentification;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESCrsDetermination;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESCrsSpecimen;

public class SpecimenTransfer {

	public static Specimen transfer(ESCrsSpecimen crsSpecimen, List<ESCrsDetermination> extraDeterminations)
	{
		Specimen specimenUnit = new Specimen();
		return specimenUnit;
	}


	public static SpecimenIdentification transfer(ESCrsDetermination crsDetermination)
	{
		final SpecimenIdentification specimenIdentification = new SpecimenIdentification();
		
//		identification.setAuthorTeamOriginalAndYear(crsDetermination.getAuthorTeamOriginalAndYear());
//		identification.setGenusOrMonomial(crsDetermination.getGenusOrMonomial());
//		identification.setHigherTaxonRank(crsDetermination.getHigherTaxonRank());
//		identification.setQualifier1(crsDetermination.getIdentificationQualifier1());
//		identification.setQualifier2(crsDetermination.getIdentificationQualifier2());
//		identification.setQualifier3(crsDetermination.getIdentificationQualifier3());
//		identification.setInfraSubspecificName(crsDetermination.getInfraSubspecificName());
//		identification.setInfraSubspecificRank(crsDetermination.getInfraSubspecificRank());
//		identification.setNameAddendum(crsDetermination.getNameAddendum());
//		identification.setPreferred(crsDetermination.isPreferred());
//		identification.setScientificName(crsDetermination.getScientificName());
//		identification.setSpeciesEpithet(crsDetermination.getSpeciesEpithet());
//		identification.setSubgenus(crsDetermination.getSubgenus());
//		identification.setTypeStatus(crsDetermination.getTypeStatus());
		return specimenIdentification;
	}
}
