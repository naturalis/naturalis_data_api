package nl.naturalis.nba.etl;

import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nba.api.model.GatheringEvent;
import nl.naturalis.nba.api.model.GatheringSiteCoordinates;
import nl.naturalis.nba.api.model.Organization;
import nl.naturalis.nba.api.model.Person;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.VernacularName;
import nl.naturalis.nba.api.model.summary.SummaryGatheringEvent;
import nl.naturalis.nba.api.model.summary.SummaryGatheringSiteCoordinates;
import nl.naturalis.nba.api.model.summary.SummaryOrganization;
import nl.naturalis.nba.api.model.summary.SummaryPerson;
import nl.naturalis.nba.api.model.summary.SummaryScientificName;
import nl.naturalis.nba.api.model.summary.SummarySourceSystem;
import nl.naturalis.nba.api.model.summary.SummarySpecimen;
import nl.naturalis.nba.api.model.summary.SummarySpecimenIdentification;
import nl.naturalis.nba.api.model.summary.SummaryTaxon;
import nl.naturalis.nba.api.model.summary.SummaryVernacularName;

public class SummaryObjectUtil {

	private SummaryObjectUtil()
	{
	}

	public static SummarySpecimen copySpecimen(Specimen specimen, String nameGroup)
	{
		SummarySpecimen summary = new SummarySpecimen();
		summary.setId(specimen.getId());
		for (SpecimenIdentification si : specimen.getIdentifications()) {
			if (si.getScientificName().getScientificNameGroup().equals(nameGroup)) {
				summary.addMatchingIdentification(copyIdentification(si));
			}
			else {
				summary.addOtherIdentification(copyIdentification(si));
			}
		}
		summary.setCollectorsFieldNumber(specimen.getCollectorsFieldNumber());
		summary.setPhaseOrStage(specimen.getPhaseOrStage());
		summary.setSex(specimen.getSex());
		summary.setSourceSystem(copySourceSystem(specimen.getSourceSystem()));
		summary.setGatheringEvent(copyGatheringEvent(specimen.getGatheringEvent()));
		summary.setUnitID(specimen.getUnitID());
		return summary;
	}

	public static SummaryTaxon copyTaxon(Taxon taxon)
	{
		SummaryTaxon summary = new SummaryTaxon();
		summary.setId(taxon.getId());
		summary.setAcceptedName(copyScientificName(taxon.getAcceptedName()));
		summary.setDefaultClassification(taxon.getDefaultClassification());
		summary.setSystemClassification(taxon.getSystemClassification());
		summary.setSourceSystem(copySourceSystem(taxon.getSourceSystem()));
		return summary;
	}

	public static SummaryScientificName copyScientificName(ScientificName sn)
	{
		SummaryScientificName ssn = new SummaryScientificName();
		ssn.setAuthorshipVerbatim(sn.getAuthorshipVerbatim());
		ssn.setFullScientificName(sn.getFullScientificName());
		ssn.setGenusOrMonomial(sn.getGenusOrMonomial());
		ssn.setInfraspecificEpithet(sn.getInfraspecificEpithet());
		ssn.setSpecificEpithet(sn.getSpecificEpithet());
		ssn.setSubgenus(sn.getSubgenus());
		ssn.setTaxonomicStatus(sn.getTaxonomicStatus());
		return ssn;
	}

	public static SummaryVernacularName copySummaryVernacularName(VernacularName vn)
	{
		SummaryVernacularName svn = new SummaryVernacularName();
		svn.setName(vn.getName());
		svn.setLanguage(vn.getLanguage());
		return svn;
	}

	public static SummarySourceSystem copySourceSystem(SourceSystem ss)
	{
		return new SummarySourceSystem(ss.getCode());
	}

	private static SummaryGatheringEvent copyGatheringEvent(GatheringEvent ge)
	{
		if (ge == null) {
			return null;
		}
		SummaryGatheringEvent summary = new SummaryGatheringEvent();
		summary.setDateTimeBegin(ge.getDateTimeBegin());
		summary.setGatheringOrganizations(copyOrganizations(ge.getGatheringOrganizations()));
		summary.setGatheringPersons(copyPersons(ge.getGatheringPersons()));
		summary.setLocalityText(ge.getLocalityText());
		summary.setSiteCoordinates(copySiteCoordinates(ge.getSiteCoordinates()));
		return summary;
	}

	private static List<SummaryGatheringSiteCoordinates> copySiteCoordinates(
			List<GatheringSiteCoordinates> coords)
	{
		if (coords == null) {
			return null;
		}
		List<SummaryGatheringSiteCoordinates> summaries = new ArrayList<>(coords.size());
		SummaryGatheringSiteCoordinates summary;
		for (GatheringSiteCoordinates coord : coords) {
			summary = new SummaryGatheringSiteCoordinates(coord.getGeoShape());
			summaries.add(summary);
		}
		return summaries;
	}

	private static SummarySpecimenIdentification copyIdentification(SpecimenIdentification si)
	{
		SummarySpecimenIdentification ssi = new SummarySpecimenIdentification();
		ssi.setTypeStatus(si.getTypeStatus());
		ssi.setDefaultClassification(si.getDefaultClassification());
		ssi.setScientificName(copyScientificName(si.getScientificName()));
		ssi.setTaxonomicEnrichments(si.getTaxonomicEnrichments());
		return ssi;
	}

	private static List<SummaryPerson> copyPersons(List<Person> persons)
	{
		if (persons == null) {
			return null;
		}
		List<SummaryPerson> summaries = new ArrayList<>(persons.size());
		for (Person p : persons) {
			SummaryPerson sp = new SummaryPerson();
			sp.setFullName(p.getFullName());
			sp.setOrganization(copyOrganization(p.getOrganization()));
			summaries.add(sp);
		}
		return summaries;
	}

	private static List<SummaryOrganization> copyOrganizations(List<Organization> organizations)
	{
		if (organizations == null) {
			return null;
		}
		List<SummaryOrganization> summaries = new ArrayList<>(organizations.size());
		for (Organization o : organizations) {
			summaries.add(new SummaryOrganization(o.getName()));
		}
		return summaries;
	}

	private static SummaryOrganization copyOrganization(Organization organization)
	{
		if (organization == null) {
			return null;
		}
		return new SummaryOrganization(organization.getName());
	}

}
