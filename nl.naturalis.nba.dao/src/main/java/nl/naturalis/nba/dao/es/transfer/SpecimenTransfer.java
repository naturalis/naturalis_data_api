package nl.naturalis.nba.dao.es.transfer;

import nl.naturalis.nba.api.model.PhaseOrStage;
import nl.naturalis.nba.api.model.Sex;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenTypeStatus;
import nl.naturalis.nba.dao.es.types.ESSpecimen;

public class SpecimenTransfer {

	private SpecimenTransfer()
	{
		// Only static method in transfer objects
	}

	public static Specimen transfer(ESSpecimen esSpecimen, String elasticsearchId)
	{
		Specimen specimen = new Specimen();
		specimen.setId(elasticsearchId);
		specimen.setCollectorsFieldNumber(esSpecimen.getCollectorsFieldNumber());
		specimen.setSourceSystem(esSpecimen.getSourceSystem());
		specimen.setSourceSystemId(esSpecimen.getSourceSystemId());
		specimen.setUnitID(esSpecimen.getUnitID());
		specimen.setUnitGUID(esSpecimen.getUnitGUID());
		specimen.setAssemblageID(esSpecimen.getAssemblageID());
		specimen.setSourceInstitutionID(esSpecimen.getSourceInstitutionID());
		specimen.setSourceID(esSpecimen.getSourceID());
		specimen.setOwner(esSpecimen.getOwner());
		// TODO: Change licenceType to licenseType in ESSpecimen and ES mapping!!
		specimen.setLicenseType(esSpecimen.getLicenceType());
		// TODO: Change licence to license in ESSpecimen and ES mapping!!
		specimen.setLicense(esSpecimen.getLicence());
		specimen.setRecordBasis(esSpecimen.getRecordBasis());
		specimen.setKindOfUnit(esSpecimen.getKindOfUnit());
		specimen.setCollectionType(esSpecimen.getCollectionType());
		specimen.setSex(Sex.parse(esSpecimen.getSex()));
		specimen.setPhaseOrStage(PhaseOrStage.parse(esSpecimen.getPhaseOrStage()));
		specimen.setTitle(esSpecimen.getTitle());
		specimen.setNotes(esSpecimen.getNotes());
		specimen.setPreparationType(esSpecimen.getPreparationType());
		specimen.setTypeStatus(SpecimenTypeStatus.forName(esSpecimen.getTypeStatus()));
		specimen.setNumberOfSpecimen(esSpecimen.getNumberOfSpecimen());
		specimen.setFromCaptivity(esSpecimen.isFromCaptivity());
		specimen.setObjectPublic(esSpecimen.isObjectPublic());
		specimen.setMultiMediaPublic(esSpecimen.isMultiMediaPublic());
		specimen.setAcquiredFrom(esSpecimen.getAcquiredFrom());
		specimen.setIdentifications(esSpecimen.getIdentifications());
		specimen.setGatheringEvent(GatheringEventTransfer.transfer(esSpecimen.getGatheringEvent()));
		return specimen;
	}
	
}
