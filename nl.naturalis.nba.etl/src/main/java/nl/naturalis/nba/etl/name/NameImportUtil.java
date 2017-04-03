package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.dao.DocumentType.SCIENTIFIC_NAME_GROUP;
import static nl.naturalis.nba.dao.DocumentType.TAXON;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.api.model.GatheringEvent;
import nl.naturalis.nba.api.model.GatheringSiteCoordinates;
import nl.naturalis.nba.api.model.Organization;
import nl.naturalis.nba.api.model.Person;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.ScientificNameGroup;
import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.summary.SummaryGatheringEvent;
import nl.naturalis.nba.api.model.summary.SummaryGatheringSiteCoordinates;
import nl.naturalis.nba.api.model.summary.SummaryOrganization;
import nl.naturalis.nba.api.model.summary.SummaryPerson;
import nl.naturalis.nba.api.model.summary.SummaryScientificName;
import nl.naturalis.nba.api.model.summary.SummarySourceSystem;
import nl.naturalis.nba.api.model.summary.SummarySpecimen;
import nl.naturalis.nba.api.model.summary.SummarySpecimenIdentification;
import nl.naturalis.nba.api.model.summary.SummaryTaxon;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.util.es.ESUtil;

public class NameImportUtil {

	private static final Logger logger = getLogger(NameImportUtil.class);

	static long longHashCode(String s)
	{
		long h = 0;
		for (int i = 0; i < s.length(); i++) {
			h = 31 * h + s.charAt(i);
		}
		return h;
	}

	static SummarySpecimen copySpecimen(Specimen specimen, String nameGroup)
	{
		SummarySpecimen summary = new SummarySpecimen();
		summary.setId(specimen.getId());
		for (SpecimenIdentification si : specimen.getIdentifications()) {
			if (si.getScientificNameGroup().equals(nameGroup)) {
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

	static SummaryTaxon copyTaxon(Taxon taxon)
	{
		SummaryTaxon summary = new SummaryTaxon();
		summary.setId(taxon.getId());
		summary.setAcceptedName(copyScientificName(taxon.getAcceptedName()));
		summary.setDefaultClassification(taxon.getDefaultClassification());
		summary.setSystemClassification(taxon.getSystemClassification());
		summary.setSourceSystem(copySourceSystem(taxon.getSourceSystem()));
		return summary;
	}

	static List<ScientificNameGroup> loadNameGroupsById(Collection<String> names)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Loading ScientificNameGroup documents for {} names", names.size());
		}
		DocumentType<ScientificNameGroup> dt = SCIENTIFIC_NAME_GROUP;
		SearchRequestBuilder request = ESUtil.newSearchRequest(dt);
		IdsQueryBuilder query = QueryBuilders.idsQuery(dt.getName());
		query.addIds(names.toArray(new String[names.size()]));
		request.setQuery(query);
		request.setSize(names.size());
		SearchResponse response = ESUtil.executeSearchRequest(request);
		SearchHit[] hits = response.getHits().getHits();
		if (hits.length == 0) {
			return Collections.emptyList();
		}
		List<ScientificNameGroup> result = new ArrayList<>(hits.length);
		ObjectMapper om = dt.getObjectMapper();
		for (SearchHit hit : hits) {
			ScientificNameGroup sns = om.convertValue(hit.getSource(), dt.getJavaType());
			sns.setId(hit.getId());
			result.add(sns);
		}
		return result;
	}

	static List<ScientificNameGroup> loadNameGroupsByName(Collection<String> names)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Loading ScientificNameGroup documents for {} names", names.size());
		}
		DocumentType<ScientificNameGroup> dt = SCIENTIFIC_NAME_GROUP;
		SearchRequestBuilder request = ESUtil.newSearchRequest(dt);
		TermsQueryBuilder query = QueryBuilders.termsQuery("name", names);
		request.setQuery(QueryBuilders.constantScoreQuery(query));
		request.setSize(names.size());
		SearchResponse response = ESUtil.executeSearchRequest(request);
		SearchHit[] hits = response.getHits().getHits();
		if (hits.length == 0) {
			return Collections.emptyList();
		}
		List<ScientificNameGroup> result = new ArrayList<>(hits.length);
		ObjectMapper om = dt.getObjectMapper();
		for (SearchHit hit : hits) {
			ScientificNameGroup sns = om.convertValue(hit.getSource(), dt.getJavaType());
			sns.setId(hit.getId());
			result.add(sns);
		}
		return result;
	}

	static List<Taxon> loadTaxa(Collection<String> names)
	{
		DocumentType<Taxon> dt = TAXON;
		SearchRequestBuilder request = ESUtil.newSearchRequest(dt);
		TermsQueryBuilder query = termsQuery("acceptedName.fullScientificName", names);
		request.setQuery(query);
		request.setSize(names.size());
		SearchResponse response = ESUtil.executeSearchRequest(request);
		SearchHit[] hits = response.getHits().getHits();
		if (hits.length == 0) {
			return Collections.emptyList();
		}
		List<Taxon> result = new ArrayList<>(hits.length);
		ObjectMapper om = dt.getObjectMapper();
		for (SearchHit hit : hits) {
			Taxon taxon = om.convertValue(hit.getSource(), dt.getJavaType());
			taxon.setId(hit.getId());
			result.add(taxon);
		}
		return result;
	}

	private static SummaryGatheringEvent copyGatheringEvent(GatheringEvent ge)
	{
		if (ge == null) {
			return null;
		}
		SummaryGatheringEvent summary = new SummaryGatheringEvent();
		summary.setDateTimeBegin(ge.getDateTimeBegin());
		summary.setDateTimeEnd(ge.getDateTimeEnd());
		summary.setGatheringOrganizations(ge.getGatheringOrganizations());
		summary.setGatheringPersons(copyGatheringPersons(ge.getGatheringPersons()));
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
			Double lat = coord.getLatitudeDecimal();
			Double lon = coord.getLongitudeDecimal();
			summary = new SummaryGatheringSiteCoordinates(lat, lon);
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

	private static SummaryScientificName copyScientificName(ScientificName sn)
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

	private static List<SummaryPerson> copyGatheringPersons(List<Person> persons)
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

	private static SummaryOrganization copyOrganization(Organization organization)
	{
		if (organization == null) {
			return null;
		}
		return new SummaryOrganization(organization.getName());
	}

	private static SummarySourceSystem copySourceSystem(SourceSystem ss)
	{
		return new SummarySourceSystem(ss.getCode());
	}

	private NameImportUtil()
	{
	}

}
