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
 * @see http://rs.tdwg.org/dwc/terms/#taxonindex).
 * 
 * @author ayco_holleman
 * 
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
	public static DefaultClassification fromSystemClassification(
			List<Monomial> systemClassification)
	{
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

	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String kingdom;
	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String phylum;
	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String className;
	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String order;
	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String superFamily;
	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String family;
	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String genus;
	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String subgenus;
	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String specificEpithet;
	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String infraspecificEpithet;
	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String infraspecificRank;

	/**
	 * Sets the rank corresponding to the specified monomial's rank <i>iff</i>
	 * the monomial's rank can be mapped to a predefined {@link TaxonomicRank}
	 * (using {@link TaxonomicRank#parse(String)}). If not, this method does
	 * nothing (it will not throw an exception if the monomial's rank is not one
	 * of the predefined taxonomic ranks).
	 * 
	 * @param monomial
	 */
	public void set(Monomial monomial)
	{
		TaxonomicRank rank = TaxonomicRank.parse(monomial.getRank());
		if (rank != null) {
			set(rank, monomial.getName());
		}
	}

	public void set(TaxonomicRank rank, String name)
	{
		switch (rank) {
			case KINGDOM:
				kingdom = name;
				break;
			case PHYLUM:
				phylum = name;
				break;
			case CLASS:
				className = name;
				break;
			case ORDER:
				order = name;
				break;
			case SUPER_FAMILY:
				superFamily = name;
				break;
			case FAMILY:
				family = name;
				break;
			case TRIBE:
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

	public String get(TaxonomicRank rank)
	{
		switch (rank) {
			case KINGDOM:
				return kingdom;
			case PHYLUM:
				return phylum;
			case CLASS:
				return className;
			case ORDER:
				return order;
			case SUPER_FAMILY:
				return superFamily;
			case FAMILY:
				return family;
			case TRIBE:
				return null;
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

	public String getKingdom()
	{
		return kingdom;
	}

	public void setKingdom(String kingdom)
	{
		this.kingdom = kingdom;
	}

	public String getPhylum()
	{
		return phylum;
	}

	public void setPhylum(String phylum)
	{
		this.phylum = phylum;
	}

	public String getClassName()
	{
		return className;
	}

	public void setClassName(String className)
	{
		this.className = className;
	}

	public String getOrder()
	{
		return order;
	}

	public void setOrder(String order)
	{
		this.order = order;
	}

	public String getSuperFamily()
	{
		return superFamily;
	}

	public void setSuperFamily(String superFamily)
	{
		this.superFamily = superFamily;
	}

	public String getFamily()
	{
		return family;
	}

	public void setFamily(String family)
	{
		this.family = family;
	}

	// public String getTribe()
	// {
	// return tribe;
	// }
	//
	//
	// public void setTribe(String tribe)
	// {
	// this.tribe = tribe;
	// }

	public String getGenus()
	{
		return genus;
	}

	public void setGenus(String genus)
	{
		this.genus = genus;
	}

	public String getSubgenus()
	{
		return subgenus;
	}

	public void setSubgenus(String subgenus)
	{
		this.subgenus = subgenus;
	}

	public String getSpecificEpithet()
	{
		return specificEpithet;
	}

	public void setSpecificEpithet(String specificEpithet)
	{
		this.specificEpithet = specificEpithet;
	}

	public String getInfraspecificEpithet()
	{
		return infraspecificEpithet;
	}

	public void setInfraspecificEpithet(String infraspecificEpithet)
	{
		this.infraspecificEpithet = infraspecificEpithet;
	}

	public String getInfraspecificRank()
	{
		return infraspecificRank;
	}

	public void setInfraspecificRank(String infraspecificRank)
	{
		this.infraspecificRank = infraspecificRank;
	}

}
