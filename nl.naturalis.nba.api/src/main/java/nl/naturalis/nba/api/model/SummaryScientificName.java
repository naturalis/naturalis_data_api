package nl.naturalis.nba.api.model;

import static nl.naturalis.nba.api.annotations.Analyzer.CASE_INSENSITIVE;
import static nl.naturalis.nba.api.annotations.Analyzer.DEFAULT;
import static nl.naturalis.nba.api.annotations.Analyzer.LIKE;

import nl.naturalis.nba.api.annotations.Analyzers;

/**
 * Encapsulates a taxon's full scientific name and the components it is composed
 * of. Only the full scientific name will always be set. Individual name
 * components will only be set if they were provided separately by the source
 * system.
 * 
 * @author Ayco Holleman
 */
public class SummaryScientificName implements INbaModelObject {

	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String fullScientificName;
	private TaxonomicStatus taxonomicStatus;
	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String genusOrMonomial;
	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String subgenus;
	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String specificEpithet;
	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String infraspecificEpithet;
	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String authorshipVerbatim;

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

	public String getAuthorshipVerbatim()
	{
		return authorshipVerbatim;
	}

	public void setAuthorshipVerbatim(String authorshipVerbatim)
	{
		this.authorshipVerbatim = authorshipVerbatim;
	}

}
