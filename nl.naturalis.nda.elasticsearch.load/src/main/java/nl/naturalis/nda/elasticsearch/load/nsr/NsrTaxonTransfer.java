package nl.naturalis.nda.elasticsearch.load.nsr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nl.naturalis.nda.domain.DefaultClassification;
import nl.naturalis.nda.domain.Monomial;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.domain.TaxonDescription;
import nl.naturalis.nda.domain.systypes.NsrCommonName;
import nl.naturalis.nda.domain.systypes.NsrScientificName;
import nl.naturalis.nda.domain.systypes.NsrTaxonDescription;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;

import org.domainobject.util.DOMUtil;
import org.w3c.dom.Element;

class NsrTaxonTransfer {

	static ESTaxon transfer(Element taxonElement)
	{

		final ESTaxon taxon = new ESTaxon();
		taxon.setSourceSystem(SourceSystem.NSR);
		taxon.setSourceSystemId(nl(DOMUtil.getValue(taxonElement, "nsr_id")));
		// TODO: Moet parent_nsr_id zijn ipv parent_id, maar dat zit nog niet in XML
		taxon.setSourceSystemParentId(nl(DOMUtil.getValue(taxonElement, "parent_id")));
		taxon.setTaxonRank(nl(DOMUtil.getValue(taxonElement, "rank")));

		List<Monomial> monomials = getMonomials(taxonElement);
		taxon.setMonomials(monomials);

		DefaultClassification dc = getDefaultClassification(monomials);
		taxon.setDefaultClassification(dc);

		for (NsrScientificName sn : getScientificNames(taxonElement)) {
			// BeanPrinter.out(sn);
			if (sn.getStatus().equals(ScientificName.TaxonomicStatus.ACCEPTED_NAME.toString())) {
				taxon.setAcceptedName(transfer(sn));
			}
			else {
				taxon.addSynonym(transfer(sn));
			}
		}

		for (NsrCommonName cn : getCommonNames(taxonElement)) {
			taxon.addVernacularName(cn.getName());
		}

		addDescriptions(taxon, getTaxonDescriptions(taxonElement));

		return taxon;
	}


	private static void addDescriptions(ESTaxon taxon, List<NsrTaxonDescription> descriptions)
	{
		for (NsrTaxonDescription d : descriptions) {
			TaxonDescription td = new TaxonDescription();
			td.setCategory(d.getCategory());
			td.setDescription(d.getDescription());
			taxon.addDescription(td);
		}
	}


	private static ScientificName transfer(NsrScientificName sn)
	{
		final ScientificName scientificName = new ScientificName();
		scientificName.setFullScientificName(sn.getScientificName());
		scientificName.setAuthor(sn.getAuthorName());
		scientificName.setAuthorshipVerbatim(sn.getAuthorship());
		scientificName.setGenusOrMonomial(sn.getGenusOrMonomial());
		scientificName.setSpecificEpithet(sn.getSpecificEpithet());
		scientificName.setInfraspecificEpithet(sn.getInfraSpecificEpithet());
		return scientificName;
	}


	private static List<NsrScientificName> getScientificNames(Element taxonElement)
	{
		Element namesElement = DOMUtil.getChild(taxonElement, "names");
		List<Element> nameElements = DOMUtil.getChildren(namesElement);
		List<NsrScientificName> names = new ArrayList<NsrScientificName>();
		for (Element e : nameElements) {
			if (isScientificNameElement(e)) {
				names.add(getScientificName(e));
			}
		}
		return names;
	}


	private static List<NsrCommonName> getCommonNames(Element taxonElement)
	{
		Element namesElement = DOMUtil.getChild(taxonElement, "names");
		List<Element> nameElements = DOMUtil.getChildren(namesElement);
		List<NsrCommonName> names = new ArrayList<NsrCommonName>();
		for (Element e : nameElements) {
			if (!isScientificNameElement(e)) {
				names.add(getCommonName(e));
			}
		}
		return names;
	}


	private static boolean isScientificNameElement(Element e)
	{
		String nameType = DOMUtil.getValue(e, "nametype");
		return nameType != null && (!nameType.equals("isPreferredNameOf")) && (!nameType.equals("isAlternativeNameOf"));
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


	private static List<NsrTaxonDescription> getTaxonDescriptions(Element taxonElement)
	{
		List<NsrTaxonDescription> descriptions = new ArrayList<NsrTaxonDescription>();
		Element descriptionElement = DOMUtil.getChild(taxonElement, "description");
		if (descriptionElement != null) {
			List<Element> pageElements = DOMUtil.getChildren(descriptionElement);
			for (Element pageElement : pageElements) {
				NsrTaxonDescription taxonDescription = new NsrTaxonDescription();
				taxonDescription.setCategory(DOMUtil.getValue(pageElement, "title"));
				taxonDescription.setDescription(DOMUtil.getValue(pageElement, "text"));
				descriptions.add(taxonDescription);
			}
		}
		return descriptions;
	}


	private static List<Monomial> getMonomials(Element taxonElement)
	{
		Element classificationElement = DOMUtil.getChild(taxonElement, "classification");
		List<Element> taxonElements = DOMUtil.getChildren(classificationElement);
		List<Monomial> monomials = new ArrayList<Monomial>(taxonElements.size());
		for (Element e : taxonElements) {
			monomials.add(new Monomial(DOMUtil.getValue(e, "rank"), DOMUtil.getValue(e, "name")));
		}
		return monomials;
	}


	private static NsrCommonName getCommonName(Element nameElement)
	{
		NsrCommonName name = new NsrCommonName();
		String expert = nl(DOMUtil.getValue(nameElement, "expert_name"));
		if (expert == null) {
			expert = nl(DOMUtil.getValue(nameElement, "expert"));
		}
		name.setExpert(expert);
		String organisation = nl(DOMUtil.getValue(nameElement, "organization_name"));
		if (organisation == null) {
			organisation = nl(DOMUtil.getValue(nameElement, "organization"));
		}
		name.setOrganisation(organisation);
		name.setLanguage(nl(DOMUtil.getValue(nameElement, "language")));
		String nameType = nl(DOMUtil.getValue(nameElement, "nametype"));
		name.setPreferred(nameType.equals("isPreferredNameOf"));
		name.setReferenceAuthor(nl(DOMUtil.getValue(nameElement, "reference_author")));
		name.setReferenceDate(nl(DOMUtil.getValue(nameElement, "reference_date")));
		name.setReferenceTitle(nl(DOMUtil.getValue(nameElement, "reference_title")));
		return name;
	}


	private static NsrScientificName getScientificName(Element nameElement)
	{
		NsrScientificName name = new NsrScientificName();
		name.setAuthorName(nl(DOMUtil.getValue(nameElement, "name_author")));
		name.setAuthorYear(nl(DOMUtil.getValue(nameElement, "authorship_year")));
		name.setExpert(nl(DOMUtil.getValue(nameElement, "expert")));
		name.setGenusOrMonomial(nl(DOMUtil.getValue(nameElement, "uninomial")));
		name.setInfraSpecificEpithet(nl(DOMUtil.getValue(nameElement, "infra_specific_epithet")));
		name.setScientificName(nl(DOMUtil.getValue(nameElement, "fullname")));
		name.setReferenceAuthor(nl(DOMUtil.getValue(nameElement, "reference_author")));
		name.setReferenceDate(nl(DOMUtil.getValue(nameElement, "reference_date")));
		name.setReferenceTitle(nl(DOMUtil.getValue(nameElement, "reference_title")));
		name.setStatus(getNameStatus(nameElement));
		String expert = nl(DOMUtil.getValue(nameElement, "expert_name"));
		if (expert == null) {
			expert = DOMUtil.getValue(nameElement, "expert");
		}
		name.setExpert(expert);
		String organisation = nl(DOMUtil.getValue(nameElement, "organization_name"));
		if (organisation == null) {
			organisation = DOMUtil.getValue(nameElement, "organization");
		}
		name.setOrganisation(organisation);
		return name;
	}

	private static final HashMap<String, String> translations = new HashMap<String, String>();

	static {
		translations.put("isValidNameOf", ScientificName.TaxonomicStatus.ACCEPTED_NAME.toString());
		translations.put("isSynonymOf", ScientificName.TaxonomicStatus.SYNONYM.toString());
		translations.put("isSynonymSLOf", ScientificName.TaxonomicStatus.SYNONYM.toString());
		translations.put("isBasionymOf", ScientificName.TaxonomicStatus.BASIONYM.toString());
		translations.put("isHomonymOf", ScientificName.TaxonomicStatus.HOMONYM.toString());
		translations.put("isMisspelledNameOf", ScientificName.TaxonomicStatus.MISSPELLED_NAME.toString());
		translations.put("isInvalidNameOf", ScientificName.TaxonomicStatus.MISAPPLIED_NAME.toString());
	}


	private static String getNameStatus(Element nameElement)
	{
		String untranslated = DOMUtil.getValue(nameElement, "nametype");
		String translated = translations.get(untranslated);
		if (translated == null) {
			throw new RuntimeException("Unaccounted for name status: " + untranslated);
		}
		return translated;
	}


	private static String nl(String in)
	{
		return ((in == null || in.trim().length() == 0) ? null : in);
	}

}
