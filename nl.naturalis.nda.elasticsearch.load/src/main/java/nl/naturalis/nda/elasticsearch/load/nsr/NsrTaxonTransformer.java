package nl.naturalis.nda.elasticsearch.load.nsr;

import static nl.naturalis.nda.domain.SourceSystem.NSR;
import static nl.naturalis.nda.domain.TaxonomicStatus.ACCEPTED_NAME;
import static nl.naturalis.nda.domain.TaxonomicStatus.BASIONYM;
import static nl.naturalis.nda.domain.TaxonomicStatus.HOMONYM;
import static nl.naturalis.nda.domain.TaxonomicStatus.SYNONYM;
import static nl.naturalis.nda.elasticsearch.load.TransformUtil.equalizeNameComponents;
import static nl.naturalis.nda.elasticsearch.load.TransformUtil.parseDate;
import static nl.naturalis.nda.elasticsearch.load.nsr.NsrImportUtil.val;
import static org.domainobject.util.DOMUtil.getChild;
import static org.domainobject.util.DOMUtil.getChildren;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import nl.naturalis.nda.domain.*;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.load.AbstractXMLTransformer;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;

import org.w3c.dom.Element;

public class NsrTaxonTransformer extends AbstractXMLTransformer<ESTaxon> {

	private static final HashMap<String, TaxonomicStatus> translations = new HashMap<>();

	static {
		translations.put("isValidNameOf", ACCEPTED_NAME);
		translations.put("isSynonymOf", SYNONYM);
		translations.put("isSynonymSLOf", SYNONYM);
		translations.put("isBasionymOf", BASIONYM);
		translations.put("isHomonymOf", HOMONYM);
		translations.put("isMisspelledNameOf", SYNONYM);
		translations.put("isInvalidNameOf", SYNONYM);
	}

	private static final List<String> allowedTaxonRanks = Arrays.asList("species", "subspecies",
			"varietas", "cultivar", "forma_specialis", "forma");

	public NsrTaxonTransformer(ETLStatistics stats)
	{
		super(stats);
	}
	
	@Override
	protected String getObjectID()
	{
		return val(input.getRecord(), "nsr_id");
	}

	@Override
	protected List<ESTaxon> doTransform()
	{
		try {
			Element taxonElem = input.getRecord();
			String rank = val(taxonElem, "rank");
			if (invalidRank(rank))
				return null;
			ESTaxon taxon = new ESTaxon();
			if (!addScientificNames(taxon))
				return null;
			addSystemClassification(taxon);
			addDefaultClassification(taxon);
			equalizeNameComponents(taxon);
			taxon.setSourceSystem(NSR);
			taxon.setSourceSystemId(objectID);
			taxon.setTaxonRank(rank);
			taxon.setSourceSystemParentId(val(taxonElem, "nsr_id_parent"));
			setRecordURI(taxon);
			addVernacularNames(taxon);
			addDescriptions(taxon);
			stats.recordsAccepted++;
			stats.objectsProcessed++;
			stats.objectsAccepted++;
			return Arrays.asList(taxon);
		}
		catch (Throwable t) {
			stats.recordsRejected++;
			if (!suppressErrors)
				error(t.getMessage());
			return null;
		}
	}

	private boolean invalidRank(String rank)
	{
		if (rank == null) {
			stats.recordsRejected++;
			if (!suppressErrors)
				error("Missing taxonomic rank");
			return true;
		}
		if (!allowedTaxonRanks.contains(rank)) {
			stats.recordsSkipped++;
			if (!suppressErrors)
				warn("Ignoring higher taxon: \"%s\"", rank);
			return true;
		}
		return false;
	}

	private boolean addScientificNames(ESTaxon taxon)
	{
		List<Element> nameElems = getNameElements();
		if (nameElems == null)
			return false;
		for (Element e : nameElems) {
			String nametype = val(e, "nametype");
			if (nametype == null) {
				stats.recordsRejected++;
				if (!suppressErrors)
					error("Missing <nametype> element under <name> element");
				return false;
			}
			if (!isVernacularName(nametype)) {
				if (!add(taxon, getScientificName(e)))
					return false;
			}
		}
		if (taxon.getAcceptedName() == null) {
			stats.recordsRejected++;
			if (!suppressErrors)
				error("Missing accepted name for taxon");
			return false;
		}
		return true;
	}

	private List<Element> getNameElements()
	{
		Element namesElem = getChild(input.getRecord(), "names");
		if (namesElem == null) {
			stats.recordsRejected++;
			if (!suppressErrors)
				error("Missing <names> element under <taxon> element");
			return null;
		}
		List<Element> nameElems = getChildren(namesElem);
		if (nameElems == null) {
			stats.recordsRejected++;
			if (!suppressErrors)
				error("Missing accepted name (zero <name> elements under <names> element)");
			return null;
		}
		return nameElems;
	}

	private boolean add(ESTaxon taxon, ScientificName sn)
	{
		if (sn.getTaxonomicStatus() == ACCEPTED_NAME) {
			if (taxon.getAcceptedName() != null) {
				stats.recordsRejected++;
				if (!suppressErrors)
					error("Only one accepted name per taxon allowed");
				return false;
			}
			taxon.setAcceptedName(sn);
		}
		else {
			taxon.addSynonym(sn);
		}
		return true;
	}

	/*
	 * This method MUST be called after addScientificNames(), because it relies
	 * on checks that are done in that method.
	 */
	private void addVernacularNames(ESTaxon taxon)
	{
		Element namesElem = getChild(input.getRecord(), "names");
		List<Element> nameElems = getChildren(namesElem);
		if (nameElems != null) {
			for (Element e : nameElems) {
				String nameType = val(e, "nametype");
				if (isVernacularName(nameType))
					taxon.addVernacularName(getVernacularName(e));
			}
		}
	}

	private static boolean isVernacularName(String nameType)
	{
		return (nameType.equals("isPreferredNameOf") || nameType.equals("isAlternativeNameOf"));
	}

	private void setRecordURI(ESTaxon taxon)
	{
		String uri = val(input.getRecord(), "url");
		if (uri == null) {
			if (!suppressErrors)
				warn("Missing URL for taxon with id \"%s\"", taxon.getSourceSystemId());
		}
		else {
			try {
				taxon.setRecordURI(new URI(uri));
			}
			catch (URISyntaxException e) {
				if (!suppressErrors)
					warn("Invalid URL: \"%s\"", uri);
			}
		}
	}

	private void addDescriptions(ESTaxon taxon)
	{
		Element e = getChild(input.getRecord(), "description");
		if (e == null)
			return;
		List<Element> pageElems = getChildren(e);
		if (pageElems == null)
			return;
		for (Element pageElem : pageElems) {
			TaxonDescription descr = new TaxonDescription();
			descr.setCategory(val(pageElem, "title"));
			descr.setDescription(val(pageElem, "text"));
			taxon.addDescription(descr);
		}
	}

	private void addSystemClassification(ESTaxon taxon)
	{
		Element ce = getChild(input.getRecord(), "classification");
		// Confusingly, the elements under <classification> are again <taxon>
		// elements.
		List<Element> taxonElems;
		if (ce == null || (taxonElems = getChildren(ce)) == null) {
			String name = val(input.getRecord(), "name");
			warn("No classification for taxon \"%s\"", name);
			return;
		}
		List<Monomial> monomials = new ArrayList<>(taxonElems.size());
		for (Element e : taxonElems) {
			String rank = val(e, "rank");
			if (rank == null) {
				String name = val(input.getRecord(), "name");
				warn("Empty <rank> element for \"%s\" (monomial discarded)", name);
				continue;
			}
			String epithet = val(e, "name");
			if (epithet == null) {
				String name = val(input.getRecord(), "name");
				warn("Empty <name> element for \"%s\" (monomial discarded)", name);
				continue;
			}
			monomials.add(new Monomial(rank, epithet));
		}
		if (monomials.size() != 0)
			taxon.setSystemClassification(monomials);
	}

	/*
	 * Does not set lower ranks, therefore does not cause discrepancies between
	 * DefaultClassification and ScientificName.
	 */
	private static void addDefaultClassification(ESTaxon taxon)
	{
		DefaultClassification dc = new DefaultClassification();
		taxon.setDefaultClassification(dc);
		List<Monomial> monomials = taxon.getSystemClassification();
		if (monomials != null) {
			for (Monomial m : monomials) {
				if (m.getRank().equals("regnum"))
					dc.setKingdom(m.getName());
				else if (m.getRank().equals("phylum"))
					dc.setPhylum(m.getName());
				else if (m.getRank().equals("classis"))
					dc.setClassName(m.getName());
				else if (m.getRank().equals("ordo"))
					dc.setOrder(m.getName());
				else if (m.getRank().equals("familia"))
					dc.setFamily(m.getName());
				else if (m.getRank().equals("genus"))
					dc.setGenus(m.getName());
			}
		}
	}

	private ScientificName getScientificName(Element nameElem)
	{
		ScientificName sn = new ScientificName();
		sn.setFullScientificName(val(nameElem, "fullname"));
		sn.setAuthor(val(nameElem, "name_author"));
		sn.setGenusOrMonomial(val(nameElem, "uninomial"));
		sn.setSpecificEpithet(val(nameElem, "specific_epithet"));
		sn.setInfraspecificEpithet(val(nameElem, "infra_specific_epithet"));
		sn.setTaxonomicStatus(getTaxonomicStatus(nameElem));
		String expertName = val(nameElem, "expert_name");
		if (expertName == null)
			expertName = val(nameElem, "expert");
		String organization = val(nameElem, "organization_name");
		if (organization == null)
			organization = val(nameElem, "organization");
		if (expertName != null || organization != null) {
			Expert expert = new Expert();
			expert.setFullName(expertName);
			expert.setOrganization(new Organization(organization));
			sn.setExperts(Arrays.asList(expert));
		}
		String author = val(nameElem, "reference_author");
		String title = val(nameElem, "reference_title");
		if (author != null || title != null) {
			Reference ref = new Reference();
			ref.setTitleCitation(title);
			ref.setAuthor(new Person(author));
			ref.setPublicationDate(parseDate(val(nameElem, "reference_date")));
			sn.setReferences(Arrays.asList(ref));
		}
		return sn;
	}

	private static VernacularName getVernacularName(Element e)
	{
		VernacularName vn = new VernacularName();
		vn.setLanguage(val(e, "language"));
		vn.setName(val(e, "fullname"));
		String nameType = val(e, "nametype");
		vn.setPreferred(nameType.equals("isPreferredNameOf"));
		String expert = val(e, "expert_name");
		if (expert == null)
			expert = val(e, "expert");
		String organization = val(e, "organization_name");
		if (organization == null)
			organization = val(e, "organization");
		if (expert != null || organization != null) {
			Expert exp = new Expert();
			exp.setFullName(expert);
			exp.setOrganization(new Organization(organization));
			vn.setExperts(Arrays.asList(exp));
		}
		String author = val(e, "reference_author");
		String title = val(e, "reference_title");
		if (author != null || title != null) {
			Reference ref = new Reference();
			ref.setTitleCitation(title);
			ref.setAuthor(new Person(author));
			ref.setPublicationDate(parseDate(val(e, "reference_date")));
			vn.setReferences(Arrays.asList(ref));
		}
		return vn;
	}

	private TaxonomicStatus getTaxonomicStatus(Element nameElem)
	{
		String raw = val(nameElem, "nametype");
		if (raw == null) {
			stats.recordsRejected++;
			if (!suppressErrors)
				error("Missing or empty <nametype> for name: " + val(nameElem, "fullname"));
			return null;
		}
		TaxonomicStatus status = translations.get(raw);
		if (status == null) {
			stats.recordsRejected++;
			if (!suppressErrors)
				error("Invalid taxonomic status: " + raw);
			return null;
		}
		return status;
	}

}
