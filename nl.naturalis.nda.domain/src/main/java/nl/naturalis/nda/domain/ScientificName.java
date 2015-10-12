package nl.naturalis.nda.domain;

import java.util.List;

/**
 * This class encapsulates a taxon's full scientific name and the components it
 * is composed of. Only the full scientific name will always be set. Individual
 * name components will only be set if they were provided separately by the
 * source system.
 */
public class ScientificName {

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

	private List<Reference> references;
	private List<Expert> experts;

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

	public String getNamePartForRank(TaxonomicRank rank)
	{
		switch (rank) {
			case GENUS:
				return genusOrMonomial;
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

	public List<Reference> getReferences()
	{
		return references;
	}

	public void setReferences(List<Reference> references)
	{
		this.references = references;
	}

	public List<Expert> getExperts()
	{
		return experts;
	}

	public void setExperts(List<Expert> experts)
	{
		this.experts = experts;
	}

	/**
	 * Check if the given scientific name is the same as this one. An object is
	 * equal if the following fields have the same value:
	 * <ul>
	 * <li>genusOrMonomial</li>
	 * <li>specificEpithet</li>
	 * <li>infraspecificEpithet</li>
	 * </ul>
	 *
	 * @param scientificName
	 *            the scientificName to check
	 * @return true if fields have the same value
	 */
	public boolean isSameScientificName(ScientificName scientificName)
	{
		if (this == scientificName)
			return true;
		if (scientificName == null) {
			return false;
		}

		if (genusOrMonomial != null ? !genusOrMonomial.equals(scientificName.genusOrMonomial)
				: scientificName.genusOrMonomial != null)
			return false;
		if (infraspecificEpithet != null ? !infraspecificEpithet.equals(scientificName.infraspecificEpithet)
				: scientificName.infraspecificEpithet != null)
			return false;
		if (specificEpithet != null ? !specificEpithet.equals(scientificName.specificEpithet)
				: scientificName.specificEpithet != null)
			return false;

		return true;
	}

}
