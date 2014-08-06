package nl.naturalis.nda.elasticsearch.dao.transfer;

import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nda.domain.Determination;
import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESCrsDetermination;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESCrsSpecimen;

public class SpecimenTransfer {

	public static Specimen transfer(ESCrsSpecimen crsSpecimen, List<ESCrsDetermination> extraDeterminations)
	{
		Specimen specimen = new Specimen();
		specimen.setAccessionSpecimenNumbers(crsSpecimen.getAccessionSpecimenNumbers());
		specimen.setAltitude(crsSpecimen.getAltitude());
		specimen.setAltitudeUnit(crsSpecimen.getAltitudeUnit());
		specimen.setCollectingEndDate(crsSpecimen.getCollectingEndDate());
		specimen.setCollectingStartDate(crsSpecimen.getCollectingStartDate());
		specimen.setCollectionType(crsSpecimen.getCollectionType());
		specimen.setCountry(crsSpecimen.getCountry());
		specimen.setDepth(crsSpecimen.getDepth());
		specimen.setDepthUnit(crsSpecimen.getDepthUnit());
		specimen.setGatheringAgent(crsSpecimen.getGatheringAgent());
		specimen.setGeodeticDatum(crsSpecimen.getGeodeticDatum());
		specimen.setKindOfUnit(crsSpecimen.getKindOfUnit());
		specimen.setLatitudeDecimal(crsSpecimen.getLatitudeDecimal());
		specimen.setLocality(crsSpecimen.getLocality());
		specimen.setLongitudeDecimal(crsSpecimen.getLongitudeDecimal());
		specimen.setMultiMediaPublic(crsSpecimen.getMultiMediaPublic());
		specimen.setObjectPublic(crsSpecimen.isObjectPublic());
		specimen.setPhaseOrStage(crsSpecimen.getPhaseOrStage());
		specimen.setProvinceState(crsSpecimen.getPhaseOrStage());
		specimen.setRecordBasis(crsSpecimen.getRecordBasis());
		specimen.setSex(crsSpecimen.getSex());
		specimen.setSourceInstitutionID(crsSpecimen.getSourceInstitutionID());
		specimen.setTaxonCoverage(crsSpecimen.getTaxonCoverage());
		specimen.setTitle(crsSpecimen.getTitle());
		specimen.setUnitGUID(crsSpecimen.getUnitGUID());
		specimen.setUnitID(crsSpecimen.getUnitID());
		specimen.setUrl(crsSpecimen.getUrl());
		specimen.setWorldRegion(crsSpecimen.getWorldRegion());
		List<Determination> determinations = new ArrayList<Determination>(crsSpecimen.getNumDeterminations());
		specimen.setDeterminations(determinations);
		if (crsSpecimen.getNumDeterminations() > 0) {
			determinations.add(transfer(crsSpecimen.getDetermination0()));
			if (crsSpecimen.getNumDeterminations() > 1) {
				determinations.add(transfer(crsSpecimen.getDetermination1()));
				if (crsSpecimen.getNumDeterminations() > 2) {
					determinations.add(transfer(crsSpecimen.getDetermination2()));
					if (extraDeterminations != null) {
						for (ESCrsDetermination d : extraDeterminations) {
							determinations.add(transfer(d));
						}
					}
				}
			}
		}
		return specimen;
	}


	public static Determination transfer(ESCrsDetermination crsDetermination)
	{
		final Determination determination = new Determination();
		determination.setAuthorTeamOriginalAndYear(crsDetermination.getAuthorTeamOriginalAndYear());
		determination.setGenusOrMonomial(crsDetermination.getGenusOrMonomial());
		determination.setHigherTaxonRank(crsDetermination.getHigherTaxonRank());
		determination.setIdentificationQualifier1(crsDetermination.getIdentificationQualifier1());
		determination.setIdentificationQualifier2(crsDetermination.getIdentificationQualifier2());
		determination.setIdentificationQualifier3(crsDetermination.getIdentificationQualifier3());
		determination.setInfraSubspecificName(crsDetermination.getInfraSubspecificName());
		determination.setInfraSubspecificRank(crsDetermination.getInfraSubspecificRank());
		determination.setNameAddendum(crsDetermination.getNameAddendum());
		determination.setPreferred(crsDetermination.isPreferred());
		determination.setScientificName(crsDetermination.getScientificName());
		determination.setSpeciesEpithet(crsDetermination.getSpeciesEpithet());
		determination.setSubgenus(crsDetermination.getSubgenus());
		determination.setTypeStatus(crsDetermination.getTypeStatus());
		return determination;
	}
}
