package nl.naturalis.nba.api.model.summary;

import static nl.naturalis.nba.api.annotations.Analyzer.CASE_INSENSITIVE;
import static nl.naturalis.nba.api.annotations.Analyzer.DEFAULT;
import static nl.naturalis.nba.api.annotations.Analyzer.LIKE;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import nl.naturalis.nba.api.annotations.Analyzers;
import nl.naturalis.nba.api.model.GatheringEvent;
import nl.naturalis.nba.api.model.GatheringSiteCoordinates;
import nl.naturalis.nba.api.model.INbaModelObject;
import nl.naturalis.nba.api.model.Organization;
import nl.naturalis.nba.api.model.Person;

/**
 * A miniature version of {@link GatheringEvent}.
 * 
 * @author Ayco Holleman
 *
 */
public class SummaryGatheringEvent implements INbaModelObject {

    @Analyzers({ DEFAULT, CASE_INSENSITIVE, LIKE })
    private String localityText;
    private Date dateTimeBegin;
    private List<SummaryPerson> gatheringPersons;
    private List<SummaryOrganization> gatheringOrganizations;
    private List<SummaryGatheringSiteCoordinates> siteCoordinates;

    /**
	 * Determines whether this object is the summary of a given
	 * {@code GatheringEvent} object, i.e. if the (nested) fields of
	 * the  {@code SummaryGatheringEvent} object all match the given 
	 * {@code GatheringEvent} object.
	 * 
	 * @param sp the {@code GatheringEvent} object to compare to
	 * @return true of this object is a summary of the object given in argument 
	 */
	public boolean isSummaryOf(GatheringEvent ge)
	{	    	    
    	    boolean result = true;
    	    result &= Objects.equals(this.getDateTimeBegin(), ge.getDateTimeBegin());
    	    result &= Objects.equals(this.getLocalityText(), ge.getLocalityText());
        	        	        	   
    	    // compare gathering persons
    	    List<SummaryPerson> summaryPersons = this.getGatheringPersons();    	    
    	    Collections.sort(summaryPersons, new Comparator<SummaryPerson>(){
    		@Override
    		public int compare (SummaryPerson p1, SummaryPerson p2){ 		    
    		    return p1.getFullName().compareTo(p2.getFullName());
    		}});
    	    
    	    List<Person> persons = ge.getGatheringPersons();
    	    Collections.sort(persons, new Comparator<Person>(){
    		public int compare (Person p1, Person p2){ 		    
    		    return p1.getFullName().compareTo(p2.getFullName());
    		}});
    	    result &= summaryPersons.size() == persons.size();    	    
    	    for (int i=0; i<summaryPersons.size(); i++){
    		result &= summaryPersons.get(i).isSummaryOf(persons.get(i));
    	    }
    	    
    	    // compare gathering organizations    	    
    	    List<SummaryOrganization> summaryOrganizations = this.getGatheringOrganizations();
    	    List<Organization> organizations = ge.getGatheringOrganizations();
    	    if (summaryOrganizations == null ^ organizations == null) {
    		return false;
    	    }
    	    else if (summaryOrganizations != null && organizations != null) {
        	    Collections.sort(summaryOrganizations, new Comparator<SummaryOrganization>(){
        		@Override
        		public int compare (SummaryOrganization o1, SummaryOrganization o2){ 		    
        		    return o1.getName().compareTo(o2.getName());
        		}});
        	    
        	    Collections.sort(organizations, new Comparator<Organization>(){
        		@Override
        		public int compare (Organization o1, Organization o2){ 		    
        		    return o1.getName().compareTo(o2.getName());
        		}});
        	    result &= summaryOrganizations.size() == organizations.size();    	    
        	    for (int i=0; i<summaryOrganizations.size(); i++){
        		result &= Objects.equals(summaryOrganizations.get(i).getName(), organizations.get(i).getName());
        	    }
    	    }
    	    
    	    // compare site coordinates
    	    List<SummaryGatheringSiteCoordinates> summaryCoordinates = this.getSiteCoordinates();
    	    List<GatheringSiteCoordinates> coordinates = ge.getSiteCoordinates();
    	    if (summaryCoordinates == null ^ coordinates == null) {
    		return false;
    	    }
    	    else if (summaryCoordinates != null && coordinates != null) {
        	    Collections.sort(summaryCoordinates, new Comparator<SummaryGatheringSiteCoordinates>(){
        		@Override
        		public int compare (SummaryGatheringSiteCoordinates s1, SummaryGatheringSiteCoordinates s2){ 		    
        		    return s1.getGeoShape().toString().compareTo(s2.getGeoShape().toString());
        		}});
        	    
        	    Collections.sort(coordinates, new Comparator<GatheringSiteCoordinates>(){
        		@Override
    		public int compare(GatheringSiteCoordinates s1, GatheringSiteCoordinates s2)
    		{
    		    return s1.getGeoShape().toString().compareTo(s2.getGeoShape().toString());
    		}});
        	    result &= summaryCoordinates.size() == coordinates.size();    	    
        	    for (int i=0; i<summaryCoordinates.size(); i++){
        		result &= summaryCoordinates.get(i).isSummaryOf(coordinates.get(i));
        	    }
    	    }    	        	    
    	    return result;
	}

    public String getLocalityText()
    {
	return localityText;
    }

    public void setLocalityText(String localityText)
    {
	this.localityText = localityText;
    }

    public Date getDateTimeBegin()
    {
	return dateTimeBegin;
    }

    public void setDateTimeBegin(Date dateTimeBegin)
    {
	this.dateTimeBegin = dateTimeBegin;
    }

    public List<SummaryPerson> getGatheringPersons()
    {
	return gatheringPersons;
    }

    public void setGatheringPersons(List<SummaryPerson> gatheringPersons)
    {
	this.gatheringPersons = gatheringPersons;
    }

    public List<SummaryOrganization> getGatheringOrganizations()
    {
	return gatheringOrganizations;
    }

    public void setGatheringOrganizations(List<SummaryOrganization> gatheringOrganizations)
    {
	this.gatheringOrganizations = gatheringOrganizations;
    }

    public List<SummaryGatheringSiteCoordinates> getSiteCoordinates()
    {
	return siteCoordinates;
    }

    public void setSiteCoordinates(List<SummaryGatheringSiteCoordinates> siteCoordinates)
    {
	this.siteCoordinates = siteCoordinates;
    }

}
