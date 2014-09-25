package nl.naturalis.nda.elasticsearch.dao.dao;

public enum LuceneType
{
	TAXON("Taxon"), SPECIMEN("Specimen"), MULTIMEDIA_OBJECT("MultiMediaObject");

	private final String name;


	private LuceneType(String name)
	{
		this.name = name;
	}


	public String toString()
	{
		return name;
	}

}
