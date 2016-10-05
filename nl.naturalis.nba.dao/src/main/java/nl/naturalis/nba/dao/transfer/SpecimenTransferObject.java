package nl.naturalis.nba.dao.transfer;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.types.ESSpecimen;

public class SpecimenTransferObject implements ITransferObject<Specimen, ESSpecimen> {

	public SpecimenTransferObject()
	{
	}

	@Override
	public Specimen getApiObject(ESSpecimen in, String elasticsearchId)
	{
		Specimen out = new Specimen();
		out.setId(elasticsearchId);
		out.setCollectorsFieldNumber(in.getCollectorsFieldNumber());
		out.setSourceSystem(in.getSourceSystem());
		out.setSourceSystemId(in.getSourceSystemId());
		out.setUnitID(in.getUnitID());
		out.setUnitGUID(in.getUnitGUID());
		out.setAssemblageID(in.getAssemblageID());
		out.setSourceInstitutionID(in.getSourceInstitutionID());
		out.setSourceID(in.getSourceID());
		out.setOwner(in.getOwner());
		// TODO: Change licenceType to licenseType in ESSpecimen and ES mapping!!
		out.setLicenseType(in.getLicenceType());
		// TODO: Change licence to license in ESSpecimen and ES mapping!!
		out.setLicense(in.getLicence());
		out.setRecordBasis(in.getRecordBasis());
		out.setKindOfUnit(in.getKindOfUnit());
		out.setCollectionType(in.getCollectionType());
		out.setSex(in.getSex());
		out.setPhaseOrStage(in.getPhaseOrStage());
		out.setTitle(in.getTitle());
		out.setNotes(in.getNotes());
		out.setPreparationType(in.getPreparationType());
		out.setTypeStatus(in.getTypeStatus());
		out.setNumberOfSpecimen(in.getNumberOfSpecimen());
		out.setFromCaptivity(in.isFromCaptivity());
		out.setObjectPublic(in.isObjectPublic());
		out.setMultiMediaPublic(in.isMultiMediaPublic());
		out.setAcquiredFrom(in.getAcquiredFrom());
		out.setIdentifications(in.getIdentifications());
		out.setGatheringEvent(GatheringEventTransfer.load(in.getGatheringEvent()));
		return out;
	}

	@Override
	public ESSpecimen getEsObject(Specimen in)
	{
		ESSpecimen out = new ESSpecimen();
		out.setCollectorsFieldNumber(in.getCollectorsFieldNumber());
		out.setSourceSystem(in.getSourceSystem());
		out.setSourceSystemId(in.getSourceSystemId());
		out.setUnitID(in.getUnitID());
		out.setUnitGUID(in.getUnitGUID());
		out.setAssemblageID(in.getAssemblageID());
		out.setSourceInstitutionID(in.getSourceInstitutionID());
		out.setSourceID(in.getSourceID());
		out.setOwner(in.getOwner());
		out.setLicenceType(in.getLicenseType());
		out.setLicence(in.getLicense());
		out.setRecordBasis(in.getRecordBasis());
		out.setKindOfUnit(in.getKindOfUnit());
		out.setCollectionType(in.getCollectionType());
		out.setSex(in.getSex());
		out.setPhaseOrStage(in.getPhaseOrStage());
		out.setTitle(in.getTitle());
		out.setNotes(in.getNotes());
		out.setPreparationType(in.getPreparationType());
		out.setTypeStatus(in.getTypeStatus());
		out.setNumberOfSpecimen(in.getNumberOfSpecimen());
		out.setFromCaptivity(in.isFromCaptivity());
		out.setObjectPublic(in.isObjectPublic());
		out.setMultiMediaPublic(in.isMultiMediaPublic());
		out.setAcquiredFrom(in.getAcquiredFrom());
		out.setIdentifications(in.getIdentifications());
		out.setGatheringEvent(GatheringEventTransfer.save(in.getGatheringEvent()));
		return out;
	}

}
