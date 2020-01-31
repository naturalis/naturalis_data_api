package nl.naturalis.nba.etl.nsr;

import static nl.naturalis.nba.api.model.SourceSystem.NSR;
import static nl.naturalis.nba.api.model.TaxonomicStatus.ACCEPTED_NAME;
import static nl.naturalis.nba.api.model.TaxonomicStatus.ALTERNATIVE_NAME;
import static nl.naturalis.nba.api.model.TaxonomicStatus.BASIONYM;
import static nl.naturalis.nba.api.model.TaxonomicStatus.HOMONYM;
import static nl.naturalis.nba.api.model.TaxonomicStatus.INVALID_NAME;
import static nl.naturalis.nba.api.model.TaxonomicStatus.MISIDENTIFICATION;
import static nl.naturalis.nba.api.model.TaxonomicStatus.MISSPELLED_NAME;
import static nl.naturalis.nba.api.model.TaxonomicStatus.NOMEN_NUDUM;
import static nl.naturalis.nba.api.model.TaxonomicStatus.PREFERRED_NAME;
import static nl.naturalis.nba.api.model.TaxonomicStatus.SYNONYM;
import static nl.naturalis.nba.dao.util.es.ESUtil.getElasticsearchId;
import static nl.naturalis.nba.etl.ETLUtil.getTestGenera;
import static nl.naturalis.nba.etl.TransformUtil.setScientificNameGroup;
import static nl.naturalis.nba.etl.nsr.NsrImportUtil.val;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.api.model.*;
import nl.naturalis.nba.common.es.ESDateInput;
import nl.naturalis.nba.etl.AbstractJSONTransformer;
import nl.naturalis.nba.etl.nsr.model.*;
import nl.naturalis.nba.etl.ETLStatistics;

/**
 * The transformer component in the NSR ETL cycle for taxa.
 *
 * @author Ayco Holleman
 * @author Tom Gilissen
 */
class NsrTaxonTransformer extends AbstractJSONTransformer<Taxon> {

    private static final HashMap<String, TaxonomicStatus> translations = new HashMap<>();

    static {
        translations.put("isBasionymOf", BASIONYM);
        translations.put("isHomonymOf", HOMONYM);
        translations.put("isMisidentificationOf", MISIDENTIFICATION);
        translations.put("isMisspelledNameOf", MISSPELLED_NAME);
        translations.put("isNomenNudumOf", NOMEN_NUDUM);
        translations.put("isNomenNudemOf", NOMEN_NUDUM); // TODO: this should be deleted after the NSR source has been cleaned of this misspelling
        translations.put("isPreferredNameOf", PREFERRED_NAME);
        translations.put("isSynonymOf", SYNONYM);
        translations.put("isSynonymSLOf", SYNONYM);
        translations.put("isInvalidNameOf", INVALID_NAME);
        translations.put("isValidNameOf", ACCEPTED_NAME);
        translations.put("isAlternativeNameOf", ALTERNATIVE_NAME);
    }

    private static final List<String> allowedTaxonRanks = Arrays.asList(
            "species",
            "subspecies",
            "varietas",
            "cultivar",
            "forma_specialis",
            "forma",
            "nothospecies",
            "nothosubspecies",
            "nothovarietas",
            "subforma");

    private String[] testGenera;

    private static ObjectMapper objectMapper = new ObjectMapper();
    private NsrTaxon nsrTaxon;

    NsrTaxonTransformer(ETLStatistics stats) throws JsonProcessingException {
        super(stats);
        testGenera = getTestGenera();
    }

    @Override
    protected String getObjectID() {
        try {
            nsrTaxon = objectMapper.readValue(input, NsrTaxon.class);
            return nsrTaxon.getNsr_id();
        } catch (JsonProcessingException e) {
            stats.recordsRejected++;
            if (!suppressErrors) {
                error("Record rejected! Missing nsr_id");
            }
            return null;
        }
    }

    @Override
    protected List<Taxon> doTransform() {
        try {
            String rank = val(nsrTaxon.getRank());
            if (invalidRank(rank)) {
                return null;
            }
            Taxon taxon = new Taxon();
            taxon.setId(getElasticsearchId(NSR, objectID));
            if (!addScientificNames(taxon)) {
                return null;
            }
            if (testGenera != null && !hasTestGenus(taxon)) {
                stats.recordsSkipped++;
                return null;
            }
            addSystemClassification(taxon);
            addDefaultClassification(taxon);
            taxon.setSourceSystem(NSR);
            taxon.setSourceSystemId(objectID);
            taxon.setTaxonRank(rank);
            String s = getOccurrenceStatusVerbatim(nsrTaxon.getStatus());
            taxon.setOccurrenceStatusVerbatim(s);
            taxon.setSourceSystemParentId(val(nsrTaxon.getNsr_id_parent()));
            setRecordURI(taxon);
            addVernacularNames(taxon);
            addDescriptions(taxon);
            stats.recordsAccepted++;
            stats.objectsProcessed++;
            stats.objectsAccepted++;
            return Collections.singletonList(taxon);
        } catch (Throwable t) {
            stats.recordsRejected++;
            if (!suppressErrors)
                error("Record rejected! {}", t.getMessage());
            return null;
        }
    }

    private boolean invalidRank(String rank) {
        if (rank == null) {
            stats.recordsRejected++;
            if (!suppressErrors)
                error("Record rejected! Missing taxonomic rank");
            return true;
        }
        if (!allowedTaxonRanks.contains(rank)) {
            stats.recordsSkipped++;
            error("Record skipped. Ignoring higher taxon: \"%s\"", rank);
            return true;
        }
        return false;
    }

    private boolean addScientificNames(Taxon taxon) {
        Name[] names = getNameElements();
        if (names == null)
            return false;
        for (Name name : names) {
            String nametype = val(name.getNametype());
            if (nametype == null) {
                stats.recordsRejected++;
                if (!suppressErrors)
                    error("Record rejected! Missing <nametype> element under <name> element");
                return false;
            }
            if (!isVernacularName(nametype)) {
                if (!add(taxon, getScientificName(name))) {
                    return false;
                }
            }
        }
        if (taxon.getAcceptedName() == null) {
            stats.recordsRejected++;
            if (!suppressErrors)
                error("Record rejected! Missing accepted name for taxon");
            return false;
        }
        return true;
    }

    private Name[] getNameElements() {
		Name[] names = nsrTaxon.getNames();
		if (names == null) {
			stats.recordsRejected++;
			if (!suppressErrors) error("Record rejected! Missing <names> element under <taxon> element");
			return null;
		}
		if (names.length == 0) {
			stats.recordsRejected++;
			if (!suppressErrors) error("Record rejected! Missing accepted name (zero <name> elements under <names> element)");
			return null;
		}
		return names;
    }

    private boolean add(Taxon taxon, ScientificName sn) {
        if (sn.getTaxonomicStatus() == ACCEPTED_NAME) {
            if (taxon.getAcceptedName() != null) {
                stats.recordsRejected++;
                if (!suppressErrors)
                    error("Record rejected! Only one accepted name per taxon allowed");
                return false;
            }
            taxon.setAcceptedName(sn);
        } else {
            taxon.addSynonym(sn);
        }
        return true;
    }

    /*
     * This method MUST be called after addScientificNames(), because it relies
     * on checks that are done in that method.
     */
    private void addVernacularNames(Taxon taxon) {
		Name[] names = nsrTaxon.getNames();
		if (names == null) return;
		for (Name name : names) {
			String nameType = name.getNametype();
			if (isVernacularName(nameType)) {
				taxon.addVernacularName(getVernacularName(name));
			}
		}
    }

    private static boolean isVernacularName(String nameType) {
        return (nameType.equals("isPreferredNameOf") || nameType.equals("isAlternativeNameOf"));
    }

    private void setRecordURI(Taxon taxon) {
        String uri = val(nsrTaxon.getUrl());
        if (uri == null) {
            if (!suppressErrors) {
                warn("Missing URL for taxon with id \"%s\"", taxon.getSourceSystemId());
            }
        } else {
            try {
                taxon.setRecordURI(new URI(uri));
            } catch (URISyntaxException e) {
                if (!suppressErrors)
					warn("Invalid URL: \"%s\"", uri);
            }
        }
    }

    private void addDescriptions(Taxon taxon) {
		Description[] descriptions = nsrTaxon.getDescription();
		if (descriptions == null || descriptions.length == 0) return;
		for (Description description : descriptions) {
			TaxonDescription descr = new TaxonDescription();
			descr.setCategory(val(description.getTitle()));
			descr.setDescription(val(description.getText()));
			descr.setLanguage(val(description.getLanguage()));
			taxon.addDescription(descr);
		}
    }

	private void addSystemClassification(Taxon taxon) {

		Classification[] classifications = nsrTaxon.getClassification();
		if (classifications == null) {
			if (!suppressErrors) {
				warn("No classification for taxon \"%s\"", nsrTaxon.getName());
			}
			return;
		}
		List<Monomial> monomials = new ArrayList<>(classifications.length);
		for (Classification classification : classifications) {
			String rank = val(classification.getRank());
			if (rank == null) {
				if (!suppressErrors) {
					warn("Empty <rank> element for \"%s\" (monomial discarded)", nsrTaxon.getName());
				}
				continue;
			}
			String epithet = val(classification.getName());
			if (epithet == null) {
				if (!suppressErrors) {
					warn("Empty <name> element for \"%s\" (monomial discarded)", nsrTaxon.getName());
				}
				continue;
			}
			monomials.add(new Monomial(rank, epithet));
			if (monomials.size() > 0) {
				taxon.setSystemClassification(monomials);
			}
		}
	}

    private boolean hasTestGenus(Taxon taxon) {
        String genus = taxon.getAcceptedName().getGenusOrMonomial();
        if (genus == null) {
            return false;
        }
        genus = genus.toLowerCase();
        for (String testGenus : testGenera) {
            if (genus.equals(testGenus)) {
                return true;
            }
        }
        return false;
    }

    private static String getOccurrenceStatusVerbatim(Status status) {
        /* Get content of status element within status element */
        return status == null ? null : val(status.getStatus());
    }

    /*
     * Does not set lower ranks, therefore does not cause discrepancies between
     * DefaultClassification and ScientificName.
     */
    private static void addDefaultClassification(Taxon taxon) {
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

    private ScientificName getScientificName(Name name) {
        ScientificName sn = new ScientificName();
        sn.setFullScientificName(val(name.getFullname()));
        sn.setAuthor(val(name.getName_author()));
        sn.setYear(val(name.getAuthorship_year()));
        sn.setAuthorshipVerbatim(val(name.getAuthorship()));
        sn.setGenusOrMonomial(val(name.getUninomial()));
        sn.setSpecificEpithet(val(name.getSpecific_epithet()));
        sn.setInfraspecificEpithet(val(name.getInfra_specific_epithet()));
        sn.setTaxonomicStatus(getTaxonomicStatus(name));
        String author = val(name.getReference_author());
        String title = val(name.getReference_title());
        if (author != null || title != null) {
            Reference ref = new Reference();
            ref.setTitleCitation(title);
            if (author != null) {
                ref.setAuthor(new Person(author));
            }
            ref.setPublicationDate(getReferenceDate(name));
            sn.setReferences(Collections.singletonList(ref));
        }
        setScientificNameGroup(sn);
        return sn;
    }

    private VernacularName getVernacularName(Name name) {
		VernacularName vn = new VernacularName();
		vn.setLanguage(val(name.getLanguage()));
		vn.setName(val(name.getFullname()));
		String nameType = val(name.getNametype());
		vn.setPreferred(nameType.equals("isPreferredNameOf"));
		String author = val(name.getReference_author());
		String title = val(name.getReference_title());
		if (author != null || title != null) {
			Reference ref = new Reference();
			ref.setTitleCitation(title);
			if (author != null) {
				ref.setAuthor(new Person(author));
			}
			ref.setPublicationDate(getReferenceDate(name));
			vn.setReferences(Collections.singletonList(ref));
		}
		return vn;
    }

    private OffsetDateTime getReferenceDate(Name name) {
        String date = val(name.getReference_date());
        if (date == null) {
            return null;
        }
        if (date.toLowerCase().startsWith("in prep")) {
            if (logger.isDebugEnabled()) {
                logger.debug("Invalid input for <reference_date>: \"{}\"", date);
            }
            return null;
        }
        OffsetDateTime odt = new ESDateInput(date).parseAsYear();
        if (odt == null && !suppressErrors) {
            warn("Invalid input for <reference_date>:", date);
        }
        return odt;
    }

    private TaxonomicStatus getTaxonomicStatus(Name name) {
        String nameType = val(name.getNametype());
        if (nameType == null) {
            stats.recordsRejected++;
            if (!suppressErrors)
                error("Record rejected! Missing or empty <nametype> for name: " + val(name.getFullname()));
            return null;
        }
        TaxonomicStatus status = translations.get(nameType);
        if (status == null) {
            stats.recordsRejected++;
            if (!suppressErrors)
                error("Record rejected! Invalid taxonomic status: " + nameType);
            return null;
        }
        return status;
    }

}
