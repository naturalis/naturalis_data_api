package nl.naturalis.nda.elasticsearch.dao.transfer;

import nl.naturalis.nda.domain.MultiMediaGatheringEvent;
import nl.naturalis.nda.domain.MultiMediaObject;
import nl.naturalis.nda.domain.ServiceAccessPoint;
import nl.naturalis.nda.domain.SpecimenTypeStatus;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringEvent;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;

import java.util.*;

public class MultiMediaObjectTransfer {

	private MultiMediaObjectTransfer()
	{
		// Only static method in transfer objects
	}


	public static MultiMediaObject transfer(ESMultiMediaObject esMmo)
	{
		MultiMediaObject mmo = new MultiMediaObject();
		mmo.setSourceSystem(esMmo.getSourceSystem());
		mmo.setSourceSystemId(esMmo.getSourceSystemId());
		mmo.setSourceInstitutionID(esMmo.getSourceInstitutionID());
		mmo.setSourceID(esMmo.getSourceID());
		mmo.setOwner(esMmo.getOwner());
		// TODO: Change licenceType to licenseType in ESMultiMediaObject and ES mapping!!
		mmo.setLicenceType(esMmo.getLicenceType());
		// TODO: Change licence to license in ESMultiMediaObject and ES mapping!!
		mmo.setLicense(esMmo.getLicence());
		mmo.setUnitID(esMmo.getUnitID());
		mmo.setIdentifications(esMmo.getIdentifications());
		mmo.setAssociatedTaxonReference(esMmo.getAssociatedTaxonReference());
		mmo.setAssociatedSpecimenReference(esMmo.getAssociatedSpecimenReference());
		mmo.setCaption(esMmo.getCaption());
		mmo.setCopyrightText(esMmo.getCopyrightText());
		mmo.setCreator(esMmo.getCreator());
		mmo.setDescription(esMmo.getDescription());

		List<ESGatheringEvent> esGatheringEvents = esMmo.getGatheringEvents();
		if (esGatheringEvents != null) {
			List<MultiMediaGatheringEvent> gatheringEvents = new ArrayList<>();
			for (ESGatheringEvent gatheringEvent : esGatheringEvents) {
				gatheringEvents.add(GatheringEventTransfer.transferToMultiMedia(gatheringEvent));
			}
			mmo.setGatheringEvents(gatheringEvents);
		}

		mmo.setMultimediaPublic(esMmo.isMultiMediaPublic());
		mmo.setPhasesOrStages(esMmo.getPhasesOrStages());
		List<ServiceAccessPoint> serviceAccessPoints = esMmo.getServiceAccessPoints();

		if (serviceAccessPoints != null) {
			LinkedHashMap<ServiceAccessPoint.Variant, ServiceAccessPoint> accessPointMap = new LinkedHashMap<>();
			for (ServiceAccessPoint serviceAccessPoint : serviceAccessPoints) {
				accessPointMap.put(serviceAccessPoint.getVariant(), serviceAccessPoint);
			}
			mmo.setServiceAccessPoints(accessPointMap);
		}

		mmo.setPhasesOrStages(esMmo.getPhasesOrStages());
		mmo.setSexes(esMmo.getSexes());
		mmo.setSpecimenTypeStatus(SpecimenTypeStatus.forName(esMmo.getSpecimenTypeStatus()));
		mmo.setSubjectOrientations(esMmo.getSubjectOrientations());
		mmo.setSubjectParts(esMmo.getSubjectParts());
		mmo.setTaxonCount(esMmo.getTaxonCount());
		mmo.setTitle(esMmo.getTitle());
		mmo.setType(esMmo.getType());

		return mmo;
	}
}
