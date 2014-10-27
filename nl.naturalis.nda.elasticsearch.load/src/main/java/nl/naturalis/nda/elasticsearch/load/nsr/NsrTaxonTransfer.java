package nl.naturalis.nda.elasticsearch.load.nsr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import nl.naturalis.nda.domain.DefaultClassification;
import nl.naturalis.nda.domain.Expert;
import nl.naturalis.nda.domain.Monomial;
import nl.naturalis.nda.domain.Organization;
import nl.naturalis.nda.domain.Person;
import nl.naturalis.nda.domain.Reference;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.domain.TaxonDescription;
import nl.naturalis.nda.domain.TaxonomicStatus;
import nl.naturalis.nda.domain.VernacularName;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.load.TransferUtil;

import org.domainobject.util.DOMUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

class NsrTaxonTransfer {

	private static final Logger logger = LoggerFactory.getLogger(NsrTaxonTransfer.class);
	private static final HashMap<String, TaxonomicStatus> translations = new HashMap<String, TaxonomicStatus>();

	static {
		translations.put("isValidNameOf", TaxonomicStatus.ACCEPTED_NAME);
		translations.put("isSynonymOf", TaxonomicStatus.SYNONYM);
		translations.put("isSynonymSLOf", TaxonomicStatus.SYNONYM);
		translations.put("isBasionymOf", TaxonomicStatus.BASIONYM);
		translations.put("isHomonymOf", TaxonomicStatus.HOMONYM);
		translations.put("isMisspelledNameOf", TaxonomicStatus.MISSPELLED_NAME);
		translations.put("isInvalidNameOf", TaxonomicStatus.MISAPPLIED_NAME);
	}


	static ESTaxon transfer(Element taxonElement) throws Exception
	{
		ESTaxon taxon = new ESTaxon();
		taxon.setSourceSystem(SourceSystem.NSR);
		taxon.setSourceSystemId(nl(DOMUtil.getValue(taxonElement, "nsr_id")));
		taxon.setSourceSystemParentId(nl(DOMUtil.getValue(taxonElement, "nsr_id_parent")));
		taxon.setTaxonRank(nl(DOMUtil.getValue(taxonElement, "rank")));
		List<Monomial> monomials = getMonomials(taxonElement);
		if (monomials != null) {
			taxon.setSystemClassification(monomials);
			DefaultClassification dc = getDefaultClassification(monomials);
			taxon.setDefaultClassification(dc);
		}
		for (ScientificName sn : getScientificNames(taxonElement)) {
			if (sn.getTaxonomicStatus() == TaxonomicStatus.ACCEPTED_NAME) {
				if (taxon.getAcceptedName() != null) {
					throw new Exception("Only one accepted name per taxon allowed");
				}
				taxon.setAcceptedName(sn);
			}
			else {
				taxon.addSynonym(sn);
			}
		}
		if (taxon.getAcceptedName() == null) {
			throw new Exception("Missing accepted name for taxon");
		}
		taxon.setVernacularNames(getVernacularNames(taxonElement));
		taxon.setDescriptions(getTaxonDescriptions(taxonElement));
		return taxon;
	}

	private static List<ScientificName> getScientificNames(Element taxonElement) throws Exception
	{
		Element namesElement = DOMUtil.getChild(taxonElement, "names");
		if (namesElement == null) {
			throw new Exception("Missing <names> element under <taxon> element");
		}
		List<Element> nameElements = DOMUtil.getChildren(namesElement);
		if (nameElements == null) {
			throw new Exception("There must be at least one <name> element under a <names> element (for the accepted name)");
		}
		List<ScientificName> names = new ArrayList<ScientificName>();
		for (Element e : nameElements) {
			if (isScientificNameElement(e)) {
				names.add(getScientificName(e));
			}
		}
		return names;
	}


	private static List<VernacularName> getVernacularNames(Element taxonElement) throws Exception
	{
		Element namesElement = DOMUtil.getChild(taxonElement, "names");
		if (namesElement == null) {
			return null;
		}
		List<Element> nameElements = DOMUtil.getChildren(namesElement);
		if (nameElements == null) {
			return null;
		}
		List<VernacularName> names = new ArrayList<VernacularName>();
		for (Element e : nameElements) {
			if (!isScientificNameElement(e)) {
				names.add(getVernacularName(e));
			}
		}
		return names;
	}


	private static boolean isScientificNameElement(Element nameElement) throws Exception
	{
		String nameType = nl(DOMUtil.getValue(nameElement, "nametype"));
		if (nameType == null) {
			throw new Exception("Missing <nametype> element under <name> element");
		}
		return (!nameType.equals("isPreferredNameOf")) && (!nameType.equals("isAlternativeNameOf"));
	}


	private static DefaultClassification getDefaultClassification(List<Monomial> monomials)
	{
		DefaultClassification dc = new DefaultClassification();
		for (Monomial monomial : monomials) {
			if (monomial.getRank().equals("regnum")) {
				dc.setKingdom(monomial.getName());
			}
			else if (monomial.getRank().equals("phylum")) {
				dc.setPhylum(monomial.getName());
			}
			else if (monomial.getRank().equals("classis")) {
				dc.setClassName(monomial.getName());
			}
			else if (monomial.getRank().equals("ordo")) {
				dc.setOrder(monomial.getName());
			}
			else if (monomial.getRank().equals("familia")) {
				dc.setFamily(monomial.getName());
			}
			else if (monomial.getRank().equals("genus")) {
				dc.setGenus(monomial.getName());
			}
		}
		return dc;
	}


	private static List<TaxonDescription> getTaxonDescriptions(Element taxonElement)
	{
		Element descriptionElement = DOMUtil.getChild(taxonElement, "description");
		if (descriptionElement == null) {
			return null;
		}
		List<Element> pageElements = DOMUtil.getChildren(descriptionElement);
		if (pageElements == null) {
			return null;
		}
		List<TaxonDescription> descriptions = new ArrayList<TaxonDescription>(pageElements.size());
		for (Element pageElement : pageElements) {
			TaxonDescription taxonDescription = new TaxonDescription();
			taxonDescription.setCategory(DOMUtil.getValue(pageElement, "title"));
			taxonDescription.setDescription(DOMUtil.getValue(pageElement, "text"));
			descriptions.add(taxonDescription);
		}
		return descriptions;
	}


	private static List<Monomial> getMonomials(Element taxonElement)
	{
		Element classificationElement = DOMUtil.getChild(taxonElement, "classification");
		if (classificationElement == null) {
			String name = nl(DOMUtil.getValue(taxonElement, "name"));
			logger.warn(String.format("No classification for taxon \"%s\"", name));
			return null;
		}
		List<Element> taxonElements = DOMUtil.getChildren(classificationElement);
		if(taxonElements == null) {
			String name = nl(DOMUtil.getValue(taxonElement, "name"));
			logger.warn(String.format("No classification for taxon \"%s\"", name));
			return null;
		}
		List<Monomial> monomials = new ArrayList<Monomial>(taxonElements.size());
		for (Element e : taxonElements) {
			monomials.add(new Monomial(DOMUtil.getValue(e, "rank"), DOMUtil.getValue(e, "name")));
		}
		return monomials;
	}


	private static VernacularName getVernacularName(Element nameElement)
	{
		VernacularName name = new VernacularName();
		name.setLanguage(nl(DOMUtil.getValue(nameElement, "language")));
		name.setName(nl(DOMUtil.getValue(nameElement, "fullname")));
		String nameType = nl(DOMUtil.getValue(nameElement, "nametype"));
		name.setPreferred(nameType.equals("isPreferredNameOf"));
		String expertName = nl(DOMUtil.getValue(nameElement, "expert_name"));
		if (expertName == null) {
			expertName = nl(DOMUtil.getValue(nameElement, "expert"));
		}
		String organization = nl(DOMUtil.getValue(nameElement, "organization_name"));
		if (organization == null) {
			organization = nl(DOMUtil.getValue(nameElement, "organization"));
		}
		if (expertName != null || organization != null) {
			Expert expert = new Expert();
			expert.setFullName(expertName);
			expert.setOrganization(new Organization(organization));
			name.setExperts(Arrays.asList(expert));
		}
		String referenceAuthor = nl(DOMUtil.getValue(nameElement, "reference_author"));
		String referenceTitle = nl(DOMUtil.getValue(nameElement, "reference_title"));
		if (referenceAuthor != null || referenceTitle != null) {
			Reference reference = new Reference();
			reference.setTitleCitation(referenceTitle);
			reference.setAuthor(new Person(referenceAuthor));
			reference.setPublicationDate(TransferUtil.parseDate(nl(DOMUtil.getValue(nameElement, "reference_date"))));
			name.setReferences(Arrays.asList(reference));
		}
		return name;
	}


	private static ScientificName getScientificName(Element nameElement)
	{
		ScientificName sn = new ScientificName();
		sn.setFullScientificName(nl(DOMUtil.getValue(nameElement, "fullname")));
		sn.setAuthor(nl(DOMUtil.getValue(nameElement, "name_author")));
		sn.setGenusOrMonomial(DOMUtil.getValue(nameElement, "uninomial"));
		sn.setSpecificEpithet(nl(DOMUtil.getValue(nameElement, "specific_epithet")));
		sn.setInfraspecificEpithet(nl(DOMUtil.getValue(nameElement, "infra_specific_epithet")));
		sn.setTaxonomicStatus(getTaxonomicStatus(nameElement));
		String expertName = nl(DOMUtil.getValue(nameElement, "expert_name"));
		if (expertName == null) {
			expertName = nl(DOMUtil.getValue(nameElement, "expert"));
		}
		String organization = nl(DOMUtil.getValue(nameElement, "organization_name"));
		if (organization == null) {
			organization = nl(DOMUtil.getValue(nameElement, "organization"));
		}
		if (expertName != null || organization != null) {
			Expert expert = new Expert();
			expert.setFullName(expertName);
			expert.setOrganization(new Organization(organization));
			sn.setExperts(Arrays.asList(expert));
		}
		String referenceAuthor = nl(DOMUtil.getValue(nameElement, "reference_author"));
		String referenceTitle = nl(DOMUtil.getValue(nameElement, "reference_title"));
		if (referenceAuthor != null || referenceTitle != null) {
			Reference reference = new Reference();
			reference.setTitleCitation(referenceTitle);
			reference.setAuthor(new Person(referenceAuthor));
			reference.setPublicationDate(TransferUtil.parseDate(nl(DOMUtil.getValue(nameElement, "reference_date"))));
			sn.setReferences(Arrays.asList(reference));
		}
		return sn;
	}


	private static TaxonomicStatus getTaxonomicStatus(Element nameElement)
	{
		String raw = nl(DOMUtil.getValue(nameElement, "nametype"));
		if (raw == null) {
			logger.error("Missing or empty nametype element for name: " + DOMUtil.getValue(nameElement, "fullname"));
			return null;
		}
		TaxonomicStatus status = translations.get(raw);
		if (status == null) {
			logger.error("Unknown taxonomic status: " + raw);
			return null;
		}
		return status;
	}


	private static String nl(String in)
	{
		return ((in == null || in.trim().length() == 0) ? null : in);
	}

}
