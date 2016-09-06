package nl.naturalis.nba.dao.es.transfer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import nl.naturalis.nba.api.model.MultiMediaGatheringEvent;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.api.model.ServiceAccessPoint;
import nl.naturalis.nba.api.model.SpecimenTypeStatus;
import nl.naturalis.nba.dao.es.types.ESGatheringEvent;
import nl.naturalis.nba.dao.es.types.ESMultiMediaObject;

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
		mmo.setLicenseType(esMmo.getLicenceType());
		// TODO: Change licence to license in ESMultiMediaObject and ES mapping!!
		mmo.setLicense(esMmo.getLicence());
		mmo.setUnitID(esMmo.getUnitID());
		mmo.setCollectionType(esMmo.getCollectionType());
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
				gatheringEvents
						.add(GatheringEventTransfer.loadMultiMediaGatheringEvent(gatheringEvent));
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
		mmo.setSpecimenTypeStatus(SpecimenTypeStatus.parse(esMmo.getSpecimenTypeStatus()));
		mmo.setSubjectOrientations(esMmo.getSubjectOrientations());
		mmo.setSubjectParts(esMmo.getSubjectParts());
		mmo.setTaxonCount(esMmo.getTaxonCount());
		mmo.setTitle(esMmo.getTitle());
		mmo.setType(esMmo.getType());

		return mmo;
	}
}