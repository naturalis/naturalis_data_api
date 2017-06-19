package nl.naturalis.nba.api.model.summary;

import static nl.naturalis.nba.api.annotations.Analyzer.CASE_INSENSITIVE;
import static nl.naturalis.nba.api.annotations.Analyzer.DEFAULT;
import static nl.naturalis.nba.api.annotations.Analyzer.LIKE;

import java.util.Objects;

import nl.naturalis.nba.api.annotations.Analyzers;
import nl.naturalis.nba.api.model.INbaModelObject;
import nl.naturalis.nba.api.model.Person;

public class SummaryPerson implements INbaModelObject {

	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String fullName;
	private SummaryOrganization organization;

	public SummaryPerson()
	{
	}

	/**
	 * Determines whether this object is the summary of a given
	 * {@code Person} object, i.e. if the (nested) fields of
	 * the  {@code SummaryPerson} object all match the given 
	 * {@code Person} object.
	 * 
	 * @param p the {@code Person} object to compare to
	 * @return true of this object is a summary of the object given in argument 
	 */
	public boolean isSummaryOf(Person p) 
	{
	    	boolean result = true;
	    	result &= Objects.equals(this.getFullName(), p.getFullName());	    		    		    	
	    	result &= this.getOrganization() == null ? p.getOrganization() == null : this.getOrganization().isSummaryOf(p.getOrganization());	    	

	    	return result;
	}
	
	public String getFullName()
	{
		return fullName;
	}

	public void setFullName(String fullName)
	{
		this.fullName = fullName;
	}

	public SummaryOrganization getOrganization()
	{
		return organization;
	}

	public void setOrganization(SummaryOrganization organization)
	{
		this.organization = organization;
	}
}
