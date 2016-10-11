package nl.naturalis.nba.etl.nsr;

import static nl.naturalis.nba.api.model.SourceSystem.NSR;
import static nl.naturalis.nba.api.model.TaxonomicStatus.ACCEPTED_NAME;
import static nl.naturalis.nba.api.model.TaxonomicStatus.BASIONYM;
import static nl.naturalis.nba.api.model.TaxonomicStatus.HOMONYM;
import static nl.naturalis.nba.api.model.TaxonomicStatus.MISSPELLED_NAME;
import static nl.naturalis.nba.api.model.TaxonomicStatus.SYNONYM;
import static nl.naturalis.nba.etl.TransformUtil.parseDate;
import static nl.naturalis.nba.etl.nsr.NsrImportUtil.val;
import static org.domainobject.util.DOMUtil.getChild;
import static org.domainobject.util.DOMUtil.getChildren;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.domainobject.util.DOMUtil;
import org.w3c.dom.Element;

import nl.naturalis.nba.api.model.DefaultClassification;
import nl.naturalis.nba.api.model.Expert;
import nl.naturalis.nba.api.model.Monomial;
import nl.naturalis.nba.api.model.Organization;
import nl.naturalis.nba.api.model.Person;
import nl.naturalis.nba.api.model.Reference;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.TaxonDescription;
import nl.naturalis.nba.api.model.TaxonomicStatus;
import nl.naturalis.nba.api.model.VernacularName;
import nl.naturalis.nba.dao.types.ESTaxon;
import nl.naturalis.nba.etl.AbstractXMLTransformer;
import nl.naturalis.nba.etl.ETLStatistics;

/**
 * The transformer component in the NSR ETL cycle for taxa.
 * 
 * @author Ayco Holleman
 *
 */
class NsrTaxonTransformer extends AbstractXMLTransformer<ESTaxon> {

	private static final HashMap<String, TaxonomicStatus> translations = new HashMap<>();

	static {
		translations.put("isValidNameOf", ACCEPTED_NAME);
		translations.put("isSynonymOf", SYNONYM);
		translations.put("isSynonymSLOf", SYNONYM);
		translations.put("isBasionymOf", BASIONYM);
		translations.put("isHomonymOf", HOMONYM);
		translations.put("isMisspelledNameOf", MISSPELLED_NAME);
		translations.put("isInvalidNameOf", SYNONYM);
	}

	private static final List<String> allowedTaxonRanks = Arrays.asList("species", "subspecies",
			"varietas", "cultivar", "forma_specialis", "forma", "nothospecies", "nothosubspecies",
			"nothovarietas", "subforma");

	NsrTaxonTransformer(ETLStatistics stats)
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
			if (invalidRank(rank)) {
				return null;
			}
			ESTaxon taxon = new ESTaxon();
			if (!addScientificNames(taxon)) {
				return null;
			}
			addSystemClassification(taxon);
			addDefaultClassification(taxon);
			//equalizeNameComponents(taxon);
			taxon.setSourceSystem(NSR);
			taxon.setSourceSystemId(objectID);
			taxon.setTaxonRank(rank);
			String s = getOccurrenceStatusVerbatim(taxonElem);
			taxon.setOccurrenceStatusVerbatim(s);
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
			if (!suppressErrors) {
				error("Missing taxonomic rank");
			}
			return true;
		}
		if (!allowedTaxonRanks.contains(rank)) {
			stats.recordsSkipped++;
			if (logger.isDebugEnabled()) {
				debug("Ignoring higher taxon: \"%s\"", rank);
			}
			return true;
		}
		return false;
	}

	private boolean addScientificNames(ESTaxon taxon)
	{
		List<Element> nameElems = getNameElements();
		if (nameElems == null) {
			return false;
		}
		for (Element e : nameElems) {
			String nametype = val(e, "nametype");
			if (nametype == null) {
				stats.recordsRejected++;
				if (!suppressErrors)
					error("Missing <nametype> element under <name> element");
				return false;
			}
			if (!isVernacularName(nametype)) {
				if (!add(taxon, getScientificName(e))) {
					return false;
				}
			}
		}
		if (taxon.getAcceptedName() == null) {
			stats.recordsRejected++;
			if (!suppressErrors) {
				error("Missing accepted name for taxon");
			}
			return false;
		}
		return true;
	}

	private List<Element> getNameElements()
	{
		Element namesElem = getChild(input.getRecord(), "names");
		if (namesElem == null) {
			stats.recordsRejected++;
			if (!suppressErrors) {
				error("Missing <names> element under <taxon> element");
			}
			return null;
		}
		List<Element> nameElems = getChildren(namesElem);
		if (nameElems == null) {
			stats.recordsRejected++;
			if (!suppressErrors) {
				error("Missing accepted name (zero <name> elements under <names> element)");
			}
			return null;
		}
		return nameElems;
	}

	private boolean add(ESTaxon taxon, ScientificName sn)
	{
		if (sn.getTaxonomicStatus() == ACCEPTED_NAME) {
			if (taxon.getAcceptedName() != null) {
				stats.recordsRejected++;
				if (!suppressErrors) {
					error("Only one accepted name per taxon allowed");
				}
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
				if (isVernacularName(nameType)) {
					taxon.addVernacularName(getVernacularName(e));
				}
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
			if (!suppressErrors) {
				warn("Missing URL for taxon with id \"%s\"", taxon.getSourceSystemId());
			}
		}
		else {
			try {
				taxon.setRecordURI(new URI(uri));
			}
			catch (URISyntaxException e) {
				if (!suppressErrors) {
					warn("Invalid URL: \"%s\"", uri);
				}
			}
		}
	}

	private void addDescriptions(ESTaxon taxon)
	{
		Element e = getChild(input.getRecord(), "description");
		if (e == null) {
			return;
		}
		List<Element> pageElems = getChildren(e);
		if (pageElems == null) {
			return;
		}
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
		if (monomials.size() != 0) {
			taxon.setSystemClassification(monomials);
		}
	}

	private static String getOccurrenceStatusVerbatim(Element taxonElem)
	{
		/* Get content of status element within status element */
		Element statusElement = DOMUtil.getChild(taxonElem, "status");
		return statusElement == null ? null : val(statusElement, "status");
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
				switch (m.getRank()) {
					case "regnum":
						dc.setKingdom(m.getName());
						break;
					case "phylum":
						dc.setPhylum(m.getName());
						break;
					case "classis":
						dc.setClassName(m.getName());
						break;
					case "ordo":
						dc.setOrder(m.getName());
						break;
					case "superfamilia":
						dc.setSuperFamily(m.getName());
						break;
					case "familia":
						dc.setFamily(m.getName());
						break;
					case "genus":
						dc.setGenus(m.getName());
						break;
					case "subgenus":
						dc.setSubgenus(m.getName());
						break;
				}
			}
		}
	}

	private ScientificName getScientificName(Element nameElem)
	{
		ScientificName sn = new ScientificName();
		sn.setFullScientificName(val(nameElem, "fullname"));
		sn.setAuthor(val(nameElem, "name_author"));
		sn.setYear(val(nameElem, "authorship_year"));
		sn.setAuthorshipVerbatim(val(nameElem, "authorship"));
		sn.setGenusOrMonomial(val(nameElem, "uninomial"));
		sn.setSpecificEpithet(val(nameElem, "specific_epithet"));
		sn.setInfraspecificEpithet(val(nameElem, "infra_specific_epithet"));
		sn.setTaxonomicStatus(getTaxonomicStatus(nameElem));
		String expertName = val(nameElem, "expert_name");
		if (expertName == null) {
			expertName = val(nameElem, "expert");
		}
		String organization = val(nameElem, "organisation_name");
		if (organization == null) {
			organization = val(nameElem, "organisation");
		}
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
			ref.setPublicationDate(getReferenceDate(nameElem));
			sn.setReferences(Arrays.asList(ref));
		}
		return sn;
	}

	private VernacularName getVernacularName(Element e)
	{
		VernacularName vn = new VernacularName();
		vn.setLanguage(val(e, "language"));
		vn.setName(val(e, "fullname"));
		String nameType = val(e, "nametype");
		vn.setPreferred(nameType.equals("isPreferredNameOf"));
		String expert = val(e, "expert_name");
		if (expert == null) {
			expert = val(e, "expert");
		}
		String organization = val(e, "organization_name");
		if (organization == null) {
			organization = val(e, "organization");
		}
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
			ref.setPublicationDate(getReferenceDate(e));
			vn.setReferences(Arrays.asList(ref));
		}
		return vn;
	}

	private Date getReferenceDate(Element e)
	{
		String date = val(e, "reference_date");
		if (date == null) {
			return null;
		}
		if (date.toLowerCase().startsWith("in prep")) {
			if (logger.isDebugEnabled()) {
				logger.debug("Invalid date: \"{}\"", date);
			}
			return null;
		}
		return parseDate(date);
	}

	private TaxonomicStatus getTaxonomicStatus(Element nameElem)
	{
		String raw = val(nameElem, "nametype");
		if (raw == null) {
			stats.recordsRejected++;
			if (!suppressErrors) {
				error("Missing or empty <nametype> for name: " + val(nameElem, "fullname"));
			}
			return null;
		}
		TaxonomicStatus status = translations.get(raw);
		if (status == null) {
			stats.recordsRejected++;
			if (!suppressErrors) {
				error("Invalid taxonomic status: " + raw);
			}
			return null;
		}
		return status;
	}

}
