package nl.naturalis.nda.elasticsearch.dao.transfer;

import nl.naturalis.nda.domain.PhaseOrStage;
import nl.naturalis.nda.domain.Sex;
import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.domain.SpecimenTypeStatus;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;

public class SpecimenTransfer {

	private SpecimenTransfer()
	{
		// Only static method in transfer objects
	}

	public static Specimen transfer(ESSpecimen esSpecimen)
	{
		Specimen specimen = new Specimen();
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
		specimen.setLicenceType(esSpecimen.getLicenceType());
		// TODO: Change licence to license in ESSpecimen and ES mapping!!
		specimen.setLicense(esSpecimen.getLicence());
		specimen.setRecordBasis(esSpecimen.getRecordBasis());
		specimen.setKindOfUnit(esSpecimen.getKindOfUnit());
		specimen.setCollectionType(esSpecimen.getCollectionType());
		specimen.setSex(Sex.forName(esSpecimen.getSex()));
		specimen.setPhaseOrStage(PhaseOrStage.forName(esSpecimen.getPhaseOrStage()));
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
