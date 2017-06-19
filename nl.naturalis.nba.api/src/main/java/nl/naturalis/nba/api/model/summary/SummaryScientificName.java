package nl.naturalis.nba.api.model.summary;

import static nl.naturalis.nba.api.annotations.Analyzer.CASE_INSENSITIVE;
import static nl.naturalis.nba.api.annotations.Analyzer.DEFAULT;
import static nl.naturalis.nba.api.annotations.Analyzer.LIKE;

import java.util.Objects;

import nl.naturalis.nba.api.annotations.Analyzers;
import nl.naturalis.nba.api.model.INbaModelObject;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.TaxonomicStatus;

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

	/**
	 * Determines whether this object is the summary of a given
	 * {@code ScientificName} object, i.e. if the (nested) fields of
	 * the  {@code SummaryScientificName} object all match the given 
	 * {@code ScientificName} object.
	 * 
	 * @param sn the {@code ScientificName} object to compare to
	 * @return true of this object is a summary of the object given in argument 
	 */
	public boolean isSummaryOf(ScientificName sn)
	{
	    	boolean result = true;
	    	result &= Objects.equals(this.getAuthorshipVerbatim(), sn.getAuthorshipVerbatim());
	    	result &= Objects.equals(this.getFullScientificName(), sn.getFullScientificName());
	    	result &= Objects.equals(this.getGenusOrMonomial(), sn.getGenusOrMonomial());
	    	result &= Objects.equals(this.getInfraspecificEpithet(), sn.getInfraspecificEpithet());
	    	result &= Objects.equals(this.getSpecificEpithet(), sn.getSpecificEpithet());
	    	result &= Objects.equals(this.getSubgenus(), sn.getSubgenus());
	    	result &= this.getTaxonomicStatus().equals(sn.getTaxonomicStatus());

	    	return result;
	}
	
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
