package nl.naturalis.nba.api.model;

public enum TaxonomicRank implements INbaModelObject
{

  DOMAIN("domain", "regio"),
	KINGDOM("kingdom", "regnum"),
	SUBKINGDOM("subkingdom"),
	PHYLUM("phylum"),
	SUBPHYLUM("subphylum"),
	SUPERCLASS("superclass"),
	CLASS("class", "classis"),
	SUBCLASS("subclass"),
	SUPERORDER("superorder"),
	ORDER("order", "ordo"),
	SUBORDER("suborder"),
	INFRA_ORDER("infraorder"),
	SUPER_FAMILY("superfamily", "suprafamilia"),
	FAMILY("family", "familia"),
	SUBFAMILY("subfamily"),
	TRIBE("tribe", "tribus"),
	SUBTRIBE("subtribe"),
	GENUS("genus"),
	SUBGENUS("subgenus"),
	SPECIES("species"),
	SUBSPECIES("subspecies");

	private final String englishName;
	private final String latinName;

	private TaxonomicRank(String englishName, String latinName)
	{
		this.englishName = englishName;
		this.latinName = latinName;
	}

	private TaxonomicRank(String latinName)
	{
		this.englishName = latinName;
		this.latinName = latinName;
	}

	public String getEnglishName()
	{
		return englishName;
	}

	public String getLatinName()
	{
		return latinName;
	}

	public String toString()
	{
		return englishName;
	}

	public static TaxonomicRank parse(String name)
	{
		if (name == null) {
			return null;
		}
		name = name.toLowerCase();
		if (name.equals(KINGDOM.englishName) || name.equals(KINGDOM.latinName)) {
			return KINGDOM;
		}
    if (name.equals(SUBKINGDOM.englishName)) {
      return SUBKINGDOM;
    }
		if (name.equals(PHYLUM.latinName)) {
			return PHYLUM;
		}
    if (name.equals(SUBPHYLUM.englishName)) {
      return SUBPHYLUM;
    }
    if (name.equals(SUPERCLASS.englishName)) {
      return SUPERCLASS;
    }
		if (name.equals(CLASS.englishName) || name.equals(CLASS.latinName)) {
			return CLASS;
		}
    if (name.equals(SUBCLASS.englishName)) {
      return SUBCLASS;
    }
    if (name.equals(SUPERORDER.englishName)) {
      return SUPERORDER;
    }
		if (name.equals(ORDER.englishName) || name.equals(ORDER.latinName)) {
			return ORDER;
		}
    if (name.equals(SUBORDER.englishName)) {
      return SUBORDER;
    }
    if (name.equals(INFRA_ORDER.englishName)) {
      return INFRA_ORDER;
    }
    if (name.equals(SUPER_FAMILY.englishName) || name.equals(SUPER_FAMILY.latinName)) {
			return SUPER_FAMILY;
		}
		if (name.equals(FAMILY.englishName) || name.equals(FAMILY.latinName)) {
			return FAMILY;
		}
    if (name.equals(SUBFAMILY.englishName)) {
      return SUBFAMILY;
    }
		if (name.equals(TRIBE.englishName) || name.equals(TRIBE.latinName)) {
			return TRIBE;
		}
    if (name.equals(SUBTRIBE.englishName)) {
      return SUBTRIBE;
    }
		if (name.equals(GENUS.latinName)) {
			return GENUS;
		}
		if (name.equals(SUBGENUS.latinName)) {
			return SUBGENUS;
		}
		if (name.equals(SPECIES.latinName)) {
			return SPECIES;
		}
		if (name.equals("species epithet") || name.equals("speciesepithet")) {
			return SPECIES;
		}
		if (name.equals("specific epithet") || name.equals("specificepithet")) {
			return SPECIES;
		}
		if (name.equals(SUBSPECIES.latinName)) {
			return SUBSPECIES;
		}
		if (name.equals("subspecies epithet") || name.equals("subspeciesepithet")) {
			return SUBSPECIES;
		}
		if (name.equals("infraspecific epithet") || name.equals("infraspecificepithet")) {
			return SUBSPECIES;
		}
		return null;
	}

}