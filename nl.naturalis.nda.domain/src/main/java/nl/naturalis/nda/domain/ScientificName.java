package nl.naturalis.nda.domain;

/**
 * This class encapsulates a taxon's full scientific name and the components it
 * is composed of. Only the full scientific name will always be set. Individual
 * name components will only be set if they were provided separately by the
 * source system.
 * 
 */
public class ScientificName {

	//@formatter:off
	public static enum TaxonomicStatus {
		ACCEPTED_NAME,
		AMBIGUOUS_SYNONYM,
		MISAPPLIED_NAME,
		MISSPELLED_NAME,
		PROVISIONALLY_ACCEPTED,
		SYNONYM,
		BASIONYM,
		HOMONYM
	}
	//@formatter:on

	private String fullScientificName;
	private TaxonomicStatus taxonomicStatus;
	private String genusOrMonomial;
	private String subgenus;
	private String specificEpithet;
	private String infraspecificEpithet;
	private String infraspecificMarker;
	private String nameAddendum;
	private String authorshipVerbatim;
	private String author;
	private String year;

	private Reference reference;
	private Expert expert;


	public String getFullScientificName()
	{
		return fullScientificName;
	}


	public void setFullScientificName(String fullScientificName)
	{
		this.fullScientificName = fullScientificName;
	}


	public TaxonomicStatus getTaxonomicStatus()
	{
		return taxonomicStatus;
	}


	public void setTaxonomicStatus(TaxonomicStatus taxonomicStatus)
	{
		this.taxonomicStatus = taxonomicStatus;
	}


	public String getGenusOrMonomial()
	{
		return genusOrMonomial;
	}


	public void setGenusOrMonomial(String genusOrMonomial)
	{
		this.genusOrMonomial = genusOrMonomial;
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


	public String getInfraspecificMarker()
	{
		return infraspecificMarker;
	}


	public void setInfraspecificMarker(String infraspecificMarker)
	{
		this.infraspecificMarker = infraspecificMarker;
	}


	public String getNameAddendum()
	{
		return nameAddendum;
	}


	public void setNameAddendum(String nameAddendum)
	{
		this.nameAddendum = nameAddendum;
	}


	public String getAuthorshipVerbatim()
	{
		return authorshipVerbatim;
	}


	public void setAuthorshipVerbatim(String authorshipVerbatim)
	{
		this.authorshipVerbatim = authorshipVerbatim;
	}


	public String getAuthor()
	{
		return author;
	}


	public void setAuthor(String author)
	{
		this.author = author;
	}


	public String getYear()
	{
		return year;
	}


	public void setYear(String year)
	{
		this.year = year;
	}


	public Reference getReference()
	{
		return reference;
	}


	public void setReference(Reference reference)
	{
		this.reference = reference;
	}


	public Expert getExpert()
	{
		return expert;
	}


	public void setExpert(Expert expert)
	{
		this.expert = expert;
	}

}
