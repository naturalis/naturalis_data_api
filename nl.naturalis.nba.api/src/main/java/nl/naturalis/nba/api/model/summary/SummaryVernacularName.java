package nl.naturalis.nba.api.model.summary;

import static nl.naturalis.nba.api.annotations.Analyzer.CASE_INSENSITIVE;
import static nl.naturalis.nba.api.annotations.Analyzer.DEFAULT;
import static nl.naturalis.nba.api.annotations.Analyzer.LIKE;

import java.util.Objects;

import nl.naturalis.nba.api.annotations.Analyzers;
import nl.naturalis.nba.api.model.INbaModelObject;
import nl.naturalis.nba.api.model.VernacularName;

/**
 * A miniature version of {@link VernacularName}.
 * 
 * @author Ayco Holleman
 *
 */
public class SummaryVernacularName implements INbaModelObject {

	@Analyzers({ DEFAULT, CASE_INSENSITIVE, LIKE })
	private String name;
	private String language;

	public SummaryVernacularName()
	{
	}

	/**
	 * Determines whether this object is the summary of a given
	 * {@code VernacularName} object, i.e. if the (nested) fields of
	 * the  {@code SummaryVernacularName} object all match the given 
	 * {@code VernacularName} object.
	 * 
	 * @param sp the {@code VernacularName} object to compare to
	 * @return 
	 * @return true of this object is a summary of the object given in argument 
	 */
	public boolean isSummaryOf(VernacularName n) 
	{
	    return Objects.equals(this.getName(), n.getName()) && Objects.equals(this.getLanguage(), n.getLanguage());
	    
	}
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getLanguage()
	{
		return language;
	}

	public void setLanguage(String language)
	{
		this.language = language;
	}

}
