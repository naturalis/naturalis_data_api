package nl.naturalis.nba.api.model;

public enum TaxonomicRank implements INbaModelObject
{

	KINGDOM("kingdom", "regnum"),
	PHYLUM("phylum"),
	CLASS("class", "classis"),
	ORDER("order", "ordo"),
	SUPER_FAMILY("superfamily", "suprafamilia"),
	FAMILY("family", "familia"),
	TRIBE("tribe", "tribus"),
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
		if (name.equals(PHYLUM.latinName)) {
			return PHYLUM;
		}
		if (name.equals(CLASS.englishName) || name.equals(CLASS.latinName)) {
			return CLASS;
		}
		if (name.equals(ORDER.englishName) || name.equals(ORDER.latinName)) {
			return ORDER;
		}
		if (name.equals(SUPER_FAMILY.englishName) || name.equals(SUPER_FAMILY.latinName)) {
			return SUPER_FAMILY;
		}
		if (name.equals(FAMILY.englishName) || name.equals(FAMILY.latinName)) {
			return FAMILY;
		}
		if (name.equals(TRIBE.englishName) || name.equals(TRIBE.latinName)) {
			return TRIBE;
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