package nl.naturalis.nda.elasticsearch.dao.transfer;

import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nda.domain.Identification;
import nl.naturalis.nda.domain.Occurrence;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESCrsDetermination;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESCrsSpecimen;

public class SpecimenTransfer {

	public static Occurrence transfer(ESCrsSpecimen crsSpecimen, List<ESCrsDetermination> extraDeterminations)
	{
		Occurrence specimen = new Occurrence();
//		specimen.setAccessionSpecimenNumbers(crsSpecimen.getAccessionSpecimenNumbers());
//		specimen.setAltitude(crsSpecimen.getAltitude());
//		specimen.setAltitudeUnit(crsSpecimen.getAltitudeUnit());
//		specimen.setCollectingEndDate(crsSpecimen.getCollectingEndDate());
//		specimen.setCollectingStartDate(crsSpecimen.getCollectingStartDate());
//		specimen.setCollectionType(crsSpecimen.getCollectionType());
//		specimen.setCountry(crsSpecimen.getCountry());
//		specimen.setDepth(crsSpecimen.getDepth());
//		specimen.setDepthUnit(crsSpecimen.getDepthUnit());
//		specimen.setGatheringAgent(crsSpecimen.getGatheringAgent());
//		specimen.setGeodeticDatum(crsSpecimen.getGeodeticDatum());
//		specimen.setKindOfUnit(crsSpecimen.getKindOfUnit());
//		specimen.setLatitudeDecimal(crsSpecimen.getLatitudeDecimal());
//		specimen.setLocality(crsSpecimen.getLocality());
//		specimen.setLongitudeDecimal(crsSpecimen.getLongitudeDecimal());
//		specimen.setMultiMediaPublic(crsSpecimen.getMultiMediaPublic());
//		specimen.setObjectPublic(crsSpecimen.isObjectPublic());
//		specimen.setPhaseOrStage(crsSpecimen.getPhaseOrStage());
//		specimen.setProvinceState(crsSpecimen.getPhaseOrStage());
//		specimen.setRecordBasis(crsSpecimen.getRecordBasis());
//		specimen.setSex(crsSpecimen.getSex());
//		specimen.setSourceInstitutionID(crsSpecimen.getSourceInstitutionID());
//		specimen.setTaxonCoverage(crsSpecimen.getTaxonCoverage());
//		specimen.setTitle(crsSpecimen.getTitle());
//		specimen.setUnitGUID(crsSpecimen.getUnitGUID());
//		specimen.setUnitID(crsSpecimen.getUnitID());
//		specimen.setUrl(crsSpecimen.getUrl());
//		specimen.setWorldRegion(crsSpecimen.getWorldRegion());
//		List<Identification> identifications = new ArrayList<Identification>(crsSpecimen.getNumDeterminations());
//		specimen.setDeterminations(identifications);
//		if (crsSpecimen.getNumDeterminations() > 0) {
//			identifications.add(transfer(crsSpecimen.getDetermination0()));
//			if (crsSpecimen.getNumDeterminations() > 1) {
//				identifications.add(transfer(crsSpecimen.getDetermination1()));
//				if (crsSpecimen.getNumDeterminations() > 2) {
//					identifications.add(transfer(crsSpecimen.getDetermination2()));
//					if (extraDeterminations != null) {
//						for (ESCrsDetermination d : extraDeterminations) {
//							identifications.add(transfer(d));
//						}
//					}
//				}
//			}
//		}
		return specimen;
	}


	public static Identification transfer(ESCrsDetermination crsDetermination)
	{
		final Identification identification = new Identification();
//		identification.setAuthorTeamOriginalAndYear(crsDetermination.getAuthorTeamOriginalAndYear());
//		identification.setGenusOrMonomial(crsDetermination.getGenusOrMonomial());
//		identification.setHigherTaxonRank(crsDetermination.getHigherTaxonRank());
//		identification.setIdentificationQualifier1(crsDetermination.getIdentificationQualifier1());
//		identification.setIdentificationQualifier2(crsDetermination.getIdentificationQualifier2());
//		identification.setIdentificationQualifier3(crsDetermination.getIdentificationQualifier3());
//		identification.setInfraSubspecificName(crsDetermination.getInfraSubspecificName());
//		identification.setInfraSubspecificRank(crsDetermination.getInfraSubspecificRank());
//		identification.setNameAddendum(crsDetermination.getNameAddendum());
//		identification.setPreferred(crsDetermination.isPreferred());
//		identification.setScientificName(crsDetermination.getScientificName());
//		identification.setSpeciesEpithet(crsDetermination.getSpeciesEpithet());
//		identification.setSubgenus(crsDetermination.getSubgenus());
//		identification.setTypeStatus(crsDetermination.getTypeStatus());
		return identification;
	}
}
