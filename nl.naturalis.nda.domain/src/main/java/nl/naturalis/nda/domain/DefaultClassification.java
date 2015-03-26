package nl.naturalis.nda.domain;

import java.util.List;

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
public class DefaultClassification {

	/**
	 * Extract's the NBA's default taxonomic classification from a provided
	 * classification (i.e. a classification as provided by one of the NBA's
	 * source systems).
	 * 
	 * @param systemClassification
	 * @return
	 */
	public static DefaultClassification fromSystemClassification(List<Monomial> systemClassification)
	{
		if (systemClassification == null) {
			return null;
		}
		DefaultClassification dc = null;
		for (Monomial monomial : systemClassification) {
			TaxonomicRank rank = TaxonomicRank.forName(monomial.getRank());
			if (rank != null) {
				if (dc == null) {
					dc = new DefaultClassification();
				}
				dc.set(rank, monomial.getName());
			}
		}
		return dc;
	}

	private String kingdom;
	private String phylum;
	private String className;
	private String order;
	private String superFamily;
	private String family;
//	private String tribe;
	private String genus;
	private String subgenus;
	private String specificEpithet;
	private String infraspecificEpithet;
	private String infraspecificRank;


	/**
	 * Sets the rank corresponding to the specified monomial's rank <i>iff</i>
	 * the monomial's rank can be mapped to a predefined {@link TaxonomicRank}
	 * (using {@link TaxonomicRank#forName(String)}). If not, this method does
	 * nothing (it will not throw an exception if the monomial's rank is not one
	 * of the predefined taxonomic ranks).
	 * 
	 * @param monomial
	 */
	public void set(Monomial monomial)
	{
		TaxonomicRank rank = TaxonomicRank.forName(monomial.getRank());
		if (rank != null) {
			set(rank, monomial.getName());
		}
	}


	public void set(TaxonomicRank rank, String name)
	{
		switch (rank) {
			case KINGDOM:
				this.kingdom = name;
				break;
			case PHYLUM:
				this.phylum = name;
				break;
			case CLASS:
				this.className = name;
				break;
			case ORDER:
				this.order = name;
				break;
			case SUPER_FAMILY:
				this.superFamily = name;
				break;
			case FAMILY:
				this.family = name;
				break;
//			case TRIBE:
//				this.tribe = name;
//				break;
			case GENUS:
				this.genus = name;
				break;
			case SUBGENUS:
				this.subgenus = name;
				break;
			case SPECIES:
				this.specificEpithet = name;
				break;
			case SUBSPECIES:
				this.infraspecificEpithet = name;
				break;
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


//	public String getTribe()
//	{
//		return tribe;
//	}
//
//
//	public void setTribe(String tribe)
//	{
//		this.tribe = tribe;
//	}


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
