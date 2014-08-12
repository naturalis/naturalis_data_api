package nl.naturalis.nda.elasticsearch.load.nsr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nl.naturalis.nda.domain.DefaultClassification;
import nl.naturalis.nda.domain.Monomial;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.systypes.NsrCommonName;
import nl.naturalis.nda.domain.systypes.NsrScientificName;
import nl.naturalis.nda.domain.systypes.NsrTaxonDescription;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESNsrTaxon;

import org.domainobject.util.DOMUtil;
import org.w3c.dom.Element;

class NsrTaxonTransfer {

	static ESNsrTaxon transfer(Element taxonElement)
	{
		ESNsrTaxon taxon = new ESNsrTaxon();

		taxon.setId(DOMUtil.getIntValue(taxonElement, "id"));
		taxon.setParentId(DOMUtil.getIntValue(taxonElement, "parent_id"));
		taxon.setNsrId(DOMUtil.getValue(taxonElement, "nsr_id"));
		taxon.setUrl(DOMUtil.getValue(taxonElement, "url"));
		taxon.setRank(DOMUtil.getValue(taxonElement, "rank"));

		List<Monomial> monomials = getMonomials(taxonElement);
		taxon.setNumMonomials(monomials.size());
		setClassification(taxon, monomials);

		taxon.setDefaultClassification(getDefaultClassification(monomials));

		List<NsrTaxonDescription> descriptions = getTaxonDescriptions(taxonElement);
		taxon.setNumDescriptions(descriptions.size());
		setDescriptions(taxon, descriptions);

		List<String> synonyms = new ArrayList<String>();
		taxon.setSynonyms(synonyms);
		for (NsrScientificName sn : getScientificNames(taxonElement)) {
			// BeanPrinter.out(sn);
			if (sn.getStatus().equals(ScientificName.Status.ACCEPTED_NAME.toString())) {
				taxon.setScientificName(sn);
			}
			else {
				synonyms.add(sn.getScientificName());
			}
		}

		List<String> commonNames = new ArrayList<String>();
		taxon.setCommonNames(commonNames);
		for (NsrCommonName cn : getCommonNames(taxonElement)) {
			commonNames.add(cn.getName());
		}

		return taxon;
	}


	static void setClassification(ESNsrTaxon taxon, List<Monomial> monomials)
	{
		taxon.setMonomial00(monomials.size() > 0 ? monomials.get(0) : null);
		taxon.setMonomial01(monomials.size() > 1 ? monomials.get(1) : null);
		taxon.setMonomial02(monomials.size() > 2 ? monomials.get(2) : null);
		taxon.setMonomial03(monomials.size() > 3 ? monomials.get(3) : null);
		taxon.setMonomial04(monomials.size() > 4 ? monomials.get(4) : null);
		taxon.setMonomial05(monomials.size() > 5 ? monomials.get(5) : null);
		taxon.setMonomial06(monomials.size() > 6 ? monomials.get(6) : null);
		taxon.setMonomial07(monomials.size() > 7 ? monomials.get(7) : null);
		taxon.setMonomial08(monomials.size() > 8 ? monomials.get(8) : null);
		taxon.setMonomial09(monomials.size() > 9 ? monomials.get(9) : null);
		taxon.setMonomial10(monomials.size() > 10 ? monomials.get(10) : null);
		taxon.setMonomial11(monomials.size() > 11 ? monomials.get(11) : null);
	}


	static void setDescriptions(ESNsrTaxon taxon, List<NsrTaxonDescription> descriptions)
	{
		taxon.setDescription00(descriptions.size() > 0 ? descriptions.get(0) : null);
		taxon.setDescription01(descriptions.size() > 1 ? descriptions.get(1) : null);
		taxon.setDescription02(descriptions.size() > 2 ? descriptions.get(2) : null);
		taxon.setDescription03(descriptions.size() > 3 ? descriptions.get(3) : null);
		taxon.setDescription04(descriptions.size() > 4 ? descriptions.get(4) : null);
		taxon.setDescription05(descriptions.size() > 5 ? descriptions.get(5) : null);
		taxon.setDescription06(descriptions.size() > 6 ? descriptions.get(6) : null);
		taxon.setDescription07(descriptions.size() > 7 ? descriptions.get(7) : null);
		taxon.setDescription08(descriptions.size() > 8 ? descriptions.get(8) : null);
		taxon.setDescription09(descriptions.size() > 9 ? descriptions.get(9) : null);
	}


	static List<NsrScientificName> getScientificNames(Element taxonElement)
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


	static List<NsrCommonName> getCommonNames(Element taxonElement)
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


	static DefaultClassification getDefaultClassification(List<Monomial> monomials)
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


	static List<NsrTaxonDescription> getTaxonDescriptions(Element taxonElement)
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


	static List<Monomial> getMonomials(Element taxonElement)
	{
		Element classificationElement = DOMUtil.getChild(taxonElement, "classification");
		List<Element> taxonElements = DOMUtil.getChildren(classificationElement);
		List<Monomial> monomials = new ArrayList<Monomial>(taxonElements.size());
		for (Element e : taxonElements) {
			monomials.add(new Monomial(DOMUtil.getValue(e, "rank"), DOMUtil.getValue(e, "name")));
		}
		return monomials;
	}


	static NsrCommonName getCommonName(Element nameElement)
	{
		NsrCommonName name = new NsrCommonName();
		String expert = DOMUtil.getValue(nameElement, "expert_name");
		if (expert == null || expert.trim().length() == 0) {
			expert = DOMUtil.getValue(nameElement, "expert");
		}
		name.setExpert(expert);
		String organisation = DOMUtil.getValue(nameElement, "organization_name");
		if (organisation == null || organisation.trim().length() == 0) {
			organisation = DOMUtil.getValue(nameElement, "organization");
		}
		name.setOrganisation(organisation);
		name.setLanguage(DOMUtil.getValue(nameElement, "language"));
		String nameType = DOMUtil.getValue(nameElement, "nametype");
		name.setPreferred(nameType.equals("isPreferredNameOf"));
		name.setReferenceAuthor(DOMUtil.getValue(nameElement, "reference_author"));
		name.setReferenceDate(DOMUtil.getValue(nameElement, "reference_date"));
		name.setReferenceTitle(DOMUtil.getValue(nameElement, "reference_title"));
		return name;
	}


	static NsrScientificName getScientificName(Element nameElement)
	{
		NsrScientificName name = new NsrScientificName();
		name.setAuthorName(DOMUtil.getValue(nameElement, "name_author"));
		name.setAuthorYear(DOMUtil.getValue(nameElement, "authorship_year"));
		name.setExpert(DOMUtil.getValue(nameElement, "expert"));
		name.setGenusOrMonomial(DOMUtil.getValue(nameElement, "uninomial"));
		name.setInfraSpecificEpithet(DOMUtil.getValue(nameElement, "infra_specific_epithet"));
		name.setScientificName(DOMUtil.getValue(nameElement, "fullname"));
		name.setReferenceAuthor(DOMUtil.getValue(nameElement, "reference_author"));
		name.setReferenceDate(DOMUtil.getValue(nameElement, "reference_date"));
		name.setReferenceTitle(DOMUtil.getValue(nameElement, "reference_title"));
		name.setStatus(getNameStatus(nameElement));
		String expert = DOMUtil.getValue(nameElement, "expert_name");
		if (expert == null || expert.trim().length() == 0) {
			expert = DOMUtil.getValue(nameElement, "expert");
		}
		name.setExpert(expert);
		String organisation = DOMUtil.getValue(nameElement, "organization_name");
		if (organisation == null || organisation.trim().length() == 0) {
			organisation = DOMUtil.getValue(nameElement, "organization");
		}
		name.setOrganisation(organisation);
		return name;
	}

	private static final HashMap<String, String> translations = new HashMap<String, String>();

	static {
		translations.put("isValidNameOf", ScientificName.Status.ACCEPTED_NAME.toString());
		translations.put("isSynonymOf", ScientificName.Status.SYNONYM.toString());
		translations.put("isSynonymSLOf", ScientificName.Status.SYNONYM.toString());
		translations.put("isBasionymOf", ScientificName.Status.BASIONYM.toString());
		translations.put("isHomonymOf", ScientificName.Status.HOMONYM.toString());
		translations.put("isMisspelledNameOf", ScientificName.Status.MISSPELLED_NAME.toString());
		translations.put("isInvalidNameOf", ScientificName.Status.MISAPPLIED_NAME.toString());
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

}
