package nl.naturalis.nda.elasticsearch.dao.transfer;

import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nda.domain.SpecimenIdentification;
import nl.naturalis.nda.domain.SpecimenUnit;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESCrsDetermination;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESCrsSpecimen;

public class SpecimenTransfer {

	public static SpecimenUnit transfer(ESCrsSpecimen crsSpecimen, List<ESCrsDetermination> extraDeterminations)
	{
		SpecimenUnit specimenUnit = new SpecimenUnit();
		specimenUnit.setAccessionSpecimenNumbers(crsSpecimen.getAccessionSpecimenNumbers());
		//specimen.setAltitude(crsSpecimen.getAltitude());
		specimenUnit.setAltitudeUnit(crsSpecimen.getAltitudeUnit());
		//specimen.setCollectingEndDate(crsSpecimen.getCollectingEndDate());
		//specimen.setCollectingStartDate(crsSpecimen.getCollectingStartDate());
		specimenUnit.setCollectionType(crsSpecimen.getCollectionType());
		specimenUnit.setCountry(crsSpecimen.getCountry());
		//specimen.setDepth(crsSpecimen.getDepth());
		specimenUnit.setDepthUnit(crsSpecimen.getDepthUnit());
		specimenUnit.setGatheringAgent(crsSpecimen.getGatheringAgent());
		specimenUnit.setGeodeticDatum(crsSpecimen.getGeodeticDatum());
		specimenUnit.setKindOfUnit(crsSpecimen.getKindOfUnit());
		specimenUnit.setLatitudeDecimal(crsSpecimen.getLatitudeDecimal());
		specimenUnit.setLocality(crsSpecimen.getLocality());
		specimenUnit.setLongitudeDecimal(crsSpecimen.getLongitudeDecimal());
		specimenUnit.setMultiMediaPublic(crsSpecimen.getMultiMediaPublic());
		specimenUnit.setObjectPublic(crsSpecimen.isObjectPublic());
		specimenUnit.setPhaseOrStage(crsSpecimen.getPhaseOrStage());
		specimenUnit.setProvinceState(crsSpecimen.getPhaseOrStage());
		specimenUnit.setRecordBasis(crsSpecimen.getRecordBasis());
		specimenUnit.setSex(crsSpecimen.getSex());
		specimenUnit.setSourceInstitutionID(crsSpecimen.getSourceInstitutionID());
		specimenUnit.setTaxonCoverage(crsSpecimen.getTaxonCoverage());
		specimenUnit.setTitle(crsSpecimen.getTitle());
		specimenUnit.setUnitGUID(crsSpecimen.getUnitGUID());
		specimenUnit.setUnitID(crsSpecimen.getUnitID());
		specimenUnit.setUrl(crsSpecimen.getUrl());
		specimenUnit.setWorldRegion(crsSpecimen.getWorldRegion());
		List<SpecimenIdentification> specimenIdentifications = new ArrayList<SpecimenIdentification>(crsSpecimen.getNumDeterminations());
		specimenUnit.setIdentifications(specimenIdentifications);
		if (crsSpecimen.getNumDeterminations() > 0) {
			specimenIdentifications.add(transfer(crsSpecimen.getDetermination0()));
			if (crsSpecimen.getNumDeterminations() > 1) {
				specimenIdentifications.add(transfer(crsSpecimen.getDetermination1()));
				if (crsSpecimen.getNumDeterminations() > 2) {
					specimenIdentifications.add(transfer(crsSpecimen.getDetermination2()));
					if (extraDeterminations != null) {
						for (ESCrsDetermination d : extraDeterminations) {
							specimenIdentifications.add(transfer(d));
						}
					}
				}
			}
		}
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
