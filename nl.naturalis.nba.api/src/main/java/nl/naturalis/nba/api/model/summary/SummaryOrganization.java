package nl.naturalis.nba.api.model.summary;

import static nl.naturalis.nba.api.annotations.Analyzer.CASE_INSENSITIVE;
import static nl.naturalis.nba.api.annotations.Analyzer.DEFAULT;
import static nl.naturalis.nba.api.annotations.Analyzer.LIKE;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import nl.naturalis.nba.api.annotations.Analyzers;
import nl.naturalis.nba.api.model.INbaModelObject;
import nl.naturalis.nba.api.model.Organization;

/**
 * A miniature version of {@link Organization}.
 * 
 * @author Ayco Holleman
 *
 */
public class SummaryOrganization implements INbaModelObject {

	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String name;

	@JsonCreator
	public SummaryOrganization(@JsonProperty("name") String name)
	{
		this.name = name;
	}

	/**
	 * Determines whether this object is the summary of a given
	 * {@code Organization} object, i.e. if the (nested) fields of
	 * the  {@code SummaryOrganization} object all match the given 
	 * {@code Organization} object.
	 * 
	 * @param sp the {@code Organization} object to compare to
	 * @return true of this object is a summary of the object given in argument 
	 */
	public boolean isSummaryOf(Organization o) 
	{		    
	    return Objects.equals(this.getName(), o.getName());
	}
	
	public String getName()
	{
	    return name;
	}

}
