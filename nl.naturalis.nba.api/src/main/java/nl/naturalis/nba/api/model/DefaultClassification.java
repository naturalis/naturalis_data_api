package nl.naturalis.nba.api.model;

import static nl.naturalis.nba.api.annotations.Analyzer.CASE_INSENSITIVE;
import static nl.naturalis.nba.api.annotations.Analyzer.DEFAULT;
import static nl.naturalis.nba.api.annotations.Analyzer.LIKE;

import java.util.List;

import nl.naturalis.nba.api.annotations.Analyzers;

/**
 * A {@code DefaultClassification} classifies a specimen or species according to
 * the ranks explicitly listed in the Darwin Core specification plus two extra
 * higher ranks commonly used within the Naturalis specimen registration
 * systems: super family and tribe.
 *
 * @author ayco_holleman
 * @url http://rs.tdwg.org/dwc/terms/#taxonindex.
 */
public class DefaultClassification implements INbaModelObject {

    /**
     * Extract's the NBA's default taxonomic classification from a provided
     * classification (i.e. a classification as provided by one of the NBA's
     * source systems).
     *
     * @param systemClassification
     * @return
     */
    public static DefaultClassification fromSystemClassification(List<Monomial> systemClassification) {
        if (systemClassification == null) {
            return null;
        }
        DefaultClassification dc = null;
        for (Monomial monomial : systemClassification) {
            TaxonomicRank rank = TaxonomicRank.parse(monomial.getRank());
            if (rank != null) {
                if (dc == null) {
                    dc = new DefaultClassification();
                }
                dc.set(rank, monomial.getName());
            }
        }
        return dc;
    }

    @Analyzers({CASE_INSENSITIVE, DEFAULT, LIKE})
    private String domain;
    @Analyzers({CASE_INSENSITIVE, DEFAULT, LIKE})
    private String subKingdom;
    @Analyzers({CASE_INSENSITIVE, DEFAULT, LIKE})
    private String kingdom;
    @Analyzers({CASE_INSENSITIVE, DEFAULT, LIKE})
    private String phylum;
    @Analyzers({CASE_INSENSITIVE, DEFAULT, LIKE})
    private String subPhylum;
    @Analyzers({CASE_INSENSITIVE, DEFAULT, LIKE})
    private String superClass;
    @Analyzers({CASE_INSENSITIVE, DEFAULT, LIKE})
    private String className;
    @Analyzers({CASE_INSENSITIVE, DEFAULT, LIKE})
    private String subClass;
    @Analyzers({CASE_INSENSITIVE, DEFAULT, LIKE})
    private String superOrder;
    @Analyzers({CASE_INSENSITIVE, DEFAULT, LIKE})
    private String order;
    @Analyzers({CASE_INSENSITIVE, DEFAULT, LIKE})
    private String subOrder;
    @Analyzers({CASE_INSENSITIVE, DEFAULT, LIKE})
    private String infraOrder;
    @Analyzers({CASE_INSENSITIVE, DEFAULT, LIKE})
    private String superFamily;
    @Analyzers({CASE_INSENSITIVE, DEFAULT, LIKE})
    private String family;
    @Analyzers({CASE_INSENSITIVE, DEFAULT, LIKE})
    private String subFamily;
    @Analyzers({CASE_INSENSITIVE, DEFAULT, LIKE})
    private String tribe;
    @Analyzers({CASE_INSENSITIVE, DEFAULT, LIKE})
    private String subTribe;
    @Analyzers({CASE_INSENSITIVE, DEFAULT, LIKE})
    private String genus;
    @Analyzers({CASE_INSENSITIVE, DEFAULT, LIKE})
    private String subgenus;
    @Analyzers({CASE_INSENSITIVE, DEFAULT, LIKE})
    private String specificEpithet;
    @Analyzers({CASE_INSENSITIVE, DEFAULT, LIKE})
    private String infraspecificEpithet;
    @Analyzers({CASE_INSENSITIVE, DEFAULT, LIKE})
    private String infraspecificRank;

    /**
     * Sets the rank corresponding to the specified monomial's rank <i>if</i>
     * the monomial's rank can be mapped to a predefined {@link TaxonomicRank}
     * (using {@link TaxonomicRank#parse(String)}). If not, this method does
     * nothing (it will not throw an exception if the monomial's rank is not one
     * of the predefined taxonomic ranks).
     *
     * @param monomial : The Monomial
     */
    public void set(Monomial monomial) {
        TaxonomicRank rank = TaxonomicRank.parse(monomial.getRank());
        if (rank != null) {
            set(rank, monomial.getName());
        }
    }

    public void set(TaxonomicRank rank, String name) {
        switch (rank) {
            case DOMAIN:
                domain = name;
                break;
            case KINGDOM:
                kingdom = name;
                break;
            case SUBKINGDOM:
                subKingdom = name;
                break;
            case PHYLUM:
                phylum = name;
                break;
            case SUBPHYLUM:
                subPhylum = name;
                break;
            case SUPERCLASS:
                superClass = name;
                break;
            case CLASS:
                className = name;
                break;
            case SUBCLASS:
                subClass = name;
                break;
            case SUPERORDER:
                superOrder = name;
                break;
            case ORDER:
                order = name;
                break;
            case SUBORDER:
                subOrder = name;
                break;
            case INFRA_ORDER:
                infraOrder = name;
                break;
            case SUPERFAMILY:
                superFamily = name;
                break;
            case FAMILY:
                family = name;
                break;
            case SUBFAMILY:
                subFamily = name;
                break;
            case TRIBE:
                tribe = name;
                break;
            case SUBTRIBE:
                subTribe = name;
                break;
            case GENUS:
                genus = name;
                break;
            case SUBGENUS:
                subgenus = name;
                break;
            case SPECIES:
                specificEpithet = name;
                break;
            case SUBSPECIES:
                infraspecificEpithet = name;
                break;
            // TODO: TaxonomicRank has been extended and contains now more ranks
            // than Darwin Core does: http://rs.tdwg.org/dwc/terms/#taxon
            // Should we update this?
            default:
                break;
        }
    }

    public String get(TaxonomicRank rank) {
        switch (rank) {
            case DOMAIN:
                return domain;
            case KINGDOM:
                return kingdom;
            case SUBKINGDOM:
                return subKingdom;
            case PHYLUM:
                return phylum;
            case SUBPHYLUM:
                return subPhylum;
            case SUPERCLASS:
                return superClass;
            case CLASS:
                return className;
            case SUBCLASS:
                return subClass;
            case SUPERORDER:
                return superOrder;
            case ORDER:
                return order;
            case SUBORDER:
                return subOrder;
            case INFRA_ORDER:
                return infraOrder;
            case SUPERFAMILY:
                return superFamily;
            case FAMILY:
                return family;
            case SUBFAMILY:
                return subFamily;
            case TRIBE:
                return tribe;
            case SUBTRIBE:
                return subTribe;
            case GENUS:
                return genus;
            case SUBGENUS:
                return subgenus;
            case SPECIES:
                return specificEpithet;
            case SUBSPECIES:
                return infraspecificEpithet;
            default:
                return null;
        }
    }

    public String getDomain() { return domain; }

    public void setDomain(String domain) { this.domain = domain; }

    public String getKingdom() {
        return kingdom;
    }

    public void setKingdom(String kingdom) {
        this.kingdom = kingdom;
    }

    public String getSubKingdom() {
        return subKingdom;
    }

    public void setSubKingdom(String subKingdom) {
        this.subKingdom = subKingdom;
    }

    public String getPhylum() {
        return phylum;
    }

    public void setPhylum(String phylum) {
        this.phylum = phylum;
    }

    public String getSubPhylum() {
        return subPhylum;
    }

    public void setSubPhylum(String subPhylum) {
        this.subPhylum = subPhylum;
    }

    public String getSuperClass() {
        return superClass;
    }

    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSubClass() {
        return subClass;
    }

    public void setSubClass(String subClass) {
        this.subClass = subClass;
    }

    public String getSuperOrder() {
        return order;
    }

    public void setSuperOrder(String superOrder) { this.superOrder = superOrder; }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getSubOrder() {
        return subOrder;
    }

    public void setSubOrder(String subOrder) {
        this.subOrder = subOrder;
    }

    public String getInfraOrder() {
        return infraOrder;
    }

    public void setInfraOrder(String infraOrder) {
        this.infraOrder = infraOrder;
    }

    public String getSuperFamily() {
        return superFamily;
    }

    public void setSuperFamily(String superFamily) {
        this.superFamily = superFamily;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getSubFamily() {
        return subFamily;
    }

    public void setSubFamily(String subFamily) {
        this.subFamily = subFamily;
    }

    public String getTribe() { return tribe; }

    public void setTribe(String tribe) { this.tribe = tribe; }

    public String getSubTribe() { return subTribe; }

    public void setSubTribe(String subTribe) { this.subTribe = subTribe; }

    public String getGenus() { return genus; }

    public void setGenus(String genus) { this.genus = genus; }

    public String getSubgenus() { return subgenus; }

    public void setSubgenus(String subgenus) { this.subgenus = subgenus; }

    public String getSpecificEpithet() { return specificEpithet; }

    public void setSpecificEpithet(String specificEpithet) { this.specificEpithet = specificEpithet; }

    public String getInfraspecificEpithet() { return infraspecificEpithet; }

    public void setInfraspecificEpithet(String infraspecificEpithet) { this.infraspecificEpithet = infraspecificEpithet; }

    public String getInfraspecificRank() { return infraspecificRank; }

    public void setInfraspecificRank(String infraspecificRank) { this.infraspecificRank = infraspecificRank; }

}
