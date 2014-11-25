package nl.naturalis.nda.elasticsearch.load.nsr;

import java.net.URI;
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

	/*
	 * static { translations.put("isValidNameOf",
	 * TaxonomicStatus.ACCEPTED_NAME); translations.put("isSynonymOf",
	 * TaxonomicStatus.SYNONYM); translations.put("isSynonymSLOf",
	 * TaxonomicStatus.SYNONYM); translations.put("isBasionymOf",
	 * TaxonomicStatus.BASIONYM); translations.put("isHomonymOf",
	 * TaxonomicStatus.HOMONYM); translations.put("isMisspelledNameOf",
	 * TaxonomicStatus.MISSPELLED_NAME); translations.put("isInvalidNameOf",
	 * TaxonomicStatus.MISAPPLIED_NAME); }
	 * 
	 * TaxonomicStatus has a richer set of possible statuses than actually used
	 * when importing (misspelled names and invalid names are simply mapped to
	 * synonym)
	 */
	static {
		translations.put("isValidNameOf", TaxonomicStatus.ACCEPTED_NAME);
		translations.put("isSynonymOf", TaxonomicStatus.SYNONYM);
		translations.put("isSynonymSLOf", TaxonomicStatus.SYNONYM);
		translations.put("isBasionymOf", TaxonomicStatus.BASIONYM);
		translations.put("isHomonymOf", TaxonomicStatus.HOMONYM);
		translations.put("isMisspelledNameOf", TaxonomicStatus.SYNONYM);
		translations.put("isInvalidNameOf", TaxonomicStatus.SYNONYM);
	}

	private static final List<String> ALLOWED_TAXON_RANKS = Arrays.asList("species", "subspecies", "varietas", "cultivar", "forma_specialis");


	static ESTaxon transfer(Element taxonElement) throws Exception
	{
		String id = nl(DOMUtil.getValue(taxonElement, "nsr_id"));
		String rank = nl(DOMUtil.getValue(taxonElement, "rank"));
		if (rank == null) {
			logger.error(String.format("Missing taxonomic rank for taxon with id \"%s\"", id));
			return null;
		}
		if (!ALLOWED_TAXON_RANKS.contains(rank)) {
			logger.debug(String.format("Skipping taxon with id \"%s\" (higher rank not allowed: \"%s\")", id, rank));
			return null;
		}
		ESTaxon taxon = new ESTaxon();
		taxon.setSourceSystem(SourceSystem.NSR);
		taxon.setSourceSystemId(id);
		taxon.setTaxonRank(rank);
		taxon.setSourceSystemParentId(nl(DOMUtil.getValue(taxonElement, "nsr_id_parent")));
		String uriString = nl(DOMUtil.getValue(taxonElement, "url"));
		if (uriString == null) {
			logger.error(String.format("Missing URL for taxon with id \"%s\"", taxon.getSourceSystemId()));
		}
		else {
			try {
				taxon.setRecordURI(URI.create(uriString));
			}
			catch (IllegalArgumentException e) {
				logger.error(String.format("Invalid URL for taxon with id %s: \"%s\"", taxon.getSourceSystemId(), uriString));
			}
		}
		List<Monomial> monomials = getMonomials(taxonElement);
		if (monomials != null) {
			taxon.setSystemClassification(monomials);
			DefaultClassification dc = getDefaultClassification(monomials);
			//DefaultClassification dc = DefaultClassification.fromSystemClassification(monomials);
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
		TransferUtil.equalizeNameComponents(taxon);
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


	@SuppressWarnings("unused")
	// Does not retrieve lower ranks, therefore does not cause discrepancies between
	// DefaultClassification and ScientificName.
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
			taxonDescription.setCategory(nl(DOMUtil.getValue(pageElement, "title")));
			taxonDescription.setDescription(nl(DOMUtil.getValue(pageElement, "text")));
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
		if (taxonElements == null) {
			String name = nl(DOMUtil.getValue(taxonElement, "name"));
			logger.warn(String.format("No classification for taxon \"%s\"", name));
			return null;
		}
		List<Monomial> monomials = new ArrayList<Monomial>(taxonElements.size());
		for (Element e : taxonElements) {
			String rankIdentifier = nl(DOMUtil.getValue(e, "rank"));
			if (rankIdentifier == null) {
				String name = nl(DOMUtil.getValue(taxonElement, "name"));
				logger.error(String.format("Empty rank identifier (<rank>) for taxon \"%s\"", name));
				continue;
			}
			String rankValue = nl(DOMUtil.getValue(e, "name"));
			if (rankValue == null) {
				String name = nl(DOMUtil.getValue(taxonElement, "name"));
				logger.error(String.format("Empty rank value (<name>) for taxon \"%s\"", name));
				continue;
			}
			monomials.add(new Monomial(rankIdentifier, rankValue));
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
		sn.setGenusOrMonomial(nl(DOMUtil.getValue(nameElement, "uninomial")));
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


	static String nl(String in)
	{
		if (in == null) {
			return null;
		}
		in = in.trim();
		return in.length() == 0 ? null : in;
	}
}
