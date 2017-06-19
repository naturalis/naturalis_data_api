package nl.naturalis.nba.api.model.summary;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import nl.naturalis.nba.api.annotations.NotIndexed;
import nl.naturalis.nba.api.model.DefaultClassification;
import nl.naturalis.nba.api.model.INbaModelObject;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.VernacularName;

/**
 * 
 * @author Ayco Holleman
 *
 */
public class SummaryTaxon implements INbaModelObject {

	@NotIndexed
	private String id;
	private SummarySourceSystem sourceSystem;
	private SummaryScientificName acceptedName;
	private DefaultClassification defaultClassification;

	private List<SummaryScientificName> synonyms;
	private List<SummaryVernacularName> vernacularNames;

	/**
	 * Determines whether this object is the summary of a given
	 * {@code Taxon} object, i.e. if the (nested) fields of
	 * the  {@code SummaryTaxon} object all match the given 
	 * {@code Taxon} object.
	 * 
	 * @param taxon the {@code Taxon} object to compare to
	 * @return true of this object is a summary of the object given in argument 
	 */
	public boolean isSummaryOf(Taxon taxon) 
	{
	    boolean result = true;
	    result &= this.getAcceptedName().isSummaryOf(taxon.getAcceptedName());
	    this.getDefaultClassification().equals(taxon.getDefaultClassification());
	    Objects.equals(this.getId(), taxon.getId());
	    Objects.equals(this.getSourceSystem().getCode(), taxon.getSourceSystem().getCode());

	    // compare synonyms
	    List<SummaryScientificName> summarySynonyms = this.getSynonyms();
	    List<ScientificName> syns = taxon.getSynonyms();
	    if (summarySynonyms == null ^ synonyms == null) {
		return false;
	    }
	    else if (summarySynonyms != null && synonyms == null) {
		Collections.sort(summarySynonyms, new Comparator<SummaryScientificName>() {
		    public int compare(SummaryScientificName s1, SummaryScientificName s2 ) {
			return(s1.getFullScientificName().compareTo(s2.getFullScientificName()));
		    }
		});
		Collections.sort(syns, new Comparator<ScientificName>() {
		    public int compare(ScientificName s1, ScientificName s2 ) {
			return(s1.getFullScientificName().compareTo(s2.getFullScientificName()));
		    }
		});
		for (int i=0; i<summarySynonyms.size(); i++) {
		    result &= summarySynonyms.get(i).isSummaryOf(syns.get(i));
		}		
	    }

	    // compare vernacular names
	    List<SummaryVernacularName> summaryNames = this.getVernacularNames();
	    List<VernacularName> names = taxon.getVernacularNames();
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
		for (int i=0; i<summaryNames.size(); i++) {
		    result &= summaryNames.get(i).isSummaryOf(names.get(i));
		}
	    }	    		    
	    System.out.println("result : " + result);
	    return result;
	}
		
	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public SummarySourceSystem getSourceSystem()
	{
		return sourceSystem;
	}

	public void setSourceSystem(SummarySourceSystem sourceSystem)
	{
		this.sourceSystem = sourceSystem;
	}

	public SummaryScientificName getAcceptedName()
	{
		return acceptedName;
	}

	public void setAcceptedName(SummaryScientificName acceptedName)
	{
		this.acceptedName = acceptedName;
	}

	public DefaultClassification getDefaultClassification()
	{
		return defaultClassification;
	}

	public void setDefaultClassification(DefaultClassification defaultClassification)
	{
		this.defaultClassification = defaultClassification;
	}

	public List<SummaryScientificName> getSynonyms()
	{
		return synonyms;
	}

	public void setSynonyms(List<SummaryScientificName> synonyms)
	{
		this.synonyms = synonyms;
	}

	public List<SummaryVernacularName> getVernacularNames()
	{
		return vernacularNames;
	}

	public void setVernacularNames(List<SummaryVernacularName> vernacularNames)
	{
		this.vernacularNames = vernacularNames;
	}
}
