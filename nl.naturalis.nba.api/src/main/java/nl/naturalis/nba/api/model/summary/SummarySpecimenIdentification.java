package nl.naturalis.nba.api.model.summary;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import nl.naturalis.nba.api.model.DefaultClassification;
import nl.naturalis.nba.api.model.INbaModelObject;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.api.model.SpecimenTypeStatus;
import nl.naturalis.nba.api.model.TaxonomicEnrichment;
import nl.naturalis.nba.api.model.VernacularName;

/**
 * A miniature version of {@link SpecimenIdentification}.
 * 
 * @author Ayco Holleman
 *
 */
public class SummarySpecimenIdentification implements INbaModelObject {

	private boolean preferred;
	private SpecimenTypeStatus typeStatus;
	private SummaryScientificName scientificName;
	private DefaultClassification defaultClassification;
	private List<SummaryVernacularName> vernacularNames;
	private List<TaxonomicEnrichment> taxonomicEnrichments;

	/**
	 * Determines whether this object is the summary of a given
	 * {@code SpecimenIdentification} object, i.e. if the (nested) fields of
	 * the  {@code SummarySpecimenIdentification} object all match the given 
	 * {@code SpecimenIdentification} object.
	 * 
	 * @param sp the {@code SpecimenIdentification} object to compare to
	 * @return true of this object is a summary of the object given in argument 
	 */
	public boolean isSummaryOf(SpecimenIdentification id) 
	{
	    	boolean result = true;
		result &= this.isPreferred() == id.isPreferred();
		result &= this.getScientificName().isSummaryOf(id.getScientificName());
		result &= Objects.equals(this.getTypeStatus(), id.getTypeStatus());

		// compare enrichments
		List<TaxonomicEnrichment> summaryEnrichments = this.getTaxonomicEnrichments();
		List<TaxonomicEnrichment> enrichments = id.getTaxonomicEnrichments();
		if (summaryEnrichments == null ^ enrichments == null) {
	    		return false;
	    	}
	    	else if (summaryEnrichments != null && enrichments != null) {
        		// TODO: Could comparing of enrichments be done with the PathValue comparator?		
        		Comparator<TaxonomicEnrichment> enrichCompare = new Comparator<TaxonomicEnrichment>(){
        		    	@Override
        	    		public int compare(TaxonomicEnrichment t1, TaxonomicEnrichment t2) {
        		    	    return t1.toString().compareTo(t2.toString());
        		    	}};
        		summaryEnrichments.sort(enrichCompare);
        		enrichments.sort(enrichCompare);
        	    	
        		result &= summaryEnrichments.size() == enrichments.size();    	    
        	    	for (int i=0; i<summaryEnrichments.size(); i++){
        	    	    result &= summaryEnrichments.get(i).equals(enrichments.get(i));
        	    	}
	    	}
		// compare vernacular names		
		List<SummaryVernacularName> summaryNames = this.getVernacularNames();
	    	List<VernacularName> names = id.getVernacularNames();
	    	if (summaryNames == null ^ names == null) {
	    	    	return false;
	    	}
	    	else if (summaryNames != null && names != null) {
			Collections.sort(summaryNames, new Comparator<SummaryVernacularName>(){
		    		public int compare (SummaryVernacularName n1, SummaryVernacularName n2){ 		    
		    		    return (n1.getName().compareTo(n2.getName()));
		    		}});

			Collections.sort(names, new Comparator<VernacularName>(){
        	    		public int compare (VernacularName n1, VernacularName n2){ 		    
        	    		    return (n1.getName().compareTo(n2.getName()));
        	    		}});
        		result &= summaryNames.size() == names.size();    	    
        	    	for (int i=0; i<summaryNames.size(); i++) {
        	    	    result &= summaryNames.get(i).isSummaryOf(names.get(i));
        	    	}
	    	}	    	
		return result;
	}
	
	public boolean isPreferred()
	{
		return preferred;
	}

	public void setPreferred(boolean preferred)
	{
		this.preferred = preferred;
	}

	public SpecimenTypeStatus getTypeStatus()
	{
		return typeStatus;
	}

	public void setTypeStatus(SpecimenTypeStatus typeStatus)
	{
		this.typeStatus = typeStatus;
	}

	public SummaryScientificName getScientificName()
	{
		return scientificName;
	}

	public void setScientificName(SummaryScientificName scientificName)
	{
		this.scientificName = scientificName;
	}

	public DefaultClassification getDefaultClassification()
	{
		return defaultClassification;
	}

	public void setDefaultClassification(DefaultClassification defaultClassification)
	{
		this.defaultClassification = defaultClassification;
	}

	public List<SummaryVernacularName> getVernacularNames()
	{
		return vernacularNames;
	}

	public void setVernacularNames(List<SummaryVernacularName> vernacularNames)
	{
		this.vernacularNames = vernacularNames;
	}

	public List<TaxonomicEnrichment> getTaxonomicEnrichments()
	{
		return taxonomicEnrichments;
	}

	public void setTaxonomicEnrichments(List<TaxonomicEnrichment> taxonomicEnrichments)
	{
		this.taxonomicEnrichments = taxonomicEnrichments;
	}

}
