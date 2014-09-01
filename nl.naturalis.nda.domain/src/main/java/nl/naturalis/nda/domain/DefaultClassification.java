package nl.naturalis.nda.domain;

/**
 * A {@code DefaultClassification} classifies a specimen or species according to
 * the ranks explicitly listed as a term the Darwin Core specification (see
 * http://rs.tdwg.org/dwc/terms/#taxonindex).
 */
public class DefaultClassification {

	//@formatter:off
	public static enum Rank
	{
		
		KINGDOM("kingdom"),
		PHYLUM("phylum"),
		CLASS("class"),
		ORDER("order"),
		SUPER_FAMILY("superfamily"),
		FAMILY("family"),
		GENUS("genus"),
		SUBGENUS("subgenus"),
		SPECIFIC_EPITHET("specificEpithet"),
		INFRASPECIFIC_EPITHET("infraspecificEpithet");
		
		private String name;	
		private Rank(String name) { this.name = name; }
		public String toString() { return name; }
		
	}
	//@formatter:on

	private String kingdom;
	private String phylum;
	private String className;
	private String order;
	private String superFamily;
	private String family;
	private String genus;
	private String subgenus;
	private String specificEpithet;
	private String infraspecificEpithet;
	private String infraspecificRank;


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
