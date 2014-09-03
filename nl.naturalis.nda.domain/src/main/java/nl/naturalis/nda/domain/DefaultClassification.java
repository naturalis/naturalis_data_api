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
		
		public static Rank forName(String name) {
			if(name.equalsIgnoreCase(KINGDOM.name)) return KINGDOM;
			if(name.equalsIgnoreCase(PHYLUM.name)) return PHYLUM;
			if(name.equalsIgnoreCase(CLASS.name)) return CLASS;
			if(name.equalsIgnoreCase(ORDER.name)) return ORDER;
			if(name.equalsIgnoreCase(SUPER_FAMILY.name)) return SUPER_FAMILY;
			if(name.equalsIgnoreCase(FAMILY.name)) return FAMILY;
			if(name.equalsIgnoreCase(GENUS.name)) return GENUS;
			if(name.equalsIgnoreCase(SUBGENUS.name)) return SUBGENUS;
			if(name.equalsIgnoreCase(SPECIFIC_EPITHET.name)) return SPECIFIC_EPITHET;
			if(name.equalsIgnoreCase(INFRASPECIFIC_EPITHET.name)) return INFRASPECIFIC_EPITHET;
			return null;
		}
		
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


	public void set(Rank rank, String name)
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
			case GENUS:
				this.genus = name;
				break;
			case SUBGENUS:
				this.subgenus = name;
				break;
			case SPECIFIC_EPITHET:
				this.specificEpithet = name;
				break;
			case INFRASPECIFIC_EPITHET:
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
