package nl.naturalis.nba.api.model.summary;

import static nl.naturalis.nba.api.annotations.Analyzer.CASE_INSENSITIVE;
import static nl.naturalis.nba.api.annotations.Analyzer.DEFAULT;
import static nl.naturalis.nba.api.annotations.Analyzer.LIKE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import nl.naturalis.nba.api.annotations.Analyzers;
import nl.naturalis.nba.api.annotations.NotIndexed;
import nl.naturalis.nba.api.model.INbaModelObject;
import nl.naturalis.nba.api.model.PhaseOrStage;
import nl.naturalis.nba.api.model.Sex;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;

public class SummarySpecimen implements INbaModelObject {

	@NotIndexed
	private String id;
	private SummarySourceSystem sourceSystem;
	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String unitID;
	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String collectorsFieldNumber;
	private String assemblageID;
	private String collectionType;
	private Sex sex;
	private PhaseOrStage phaseOrStage;
	private SummaryGatheringEvent gatheringEvent;
	private List<SummarySpecimenIdentification> matchingIdentifications;
	private List<SummarySpecimenIdentification> otherIdentifications;

	/**
	 * Determines whether this object is the summary of a given
	 * {@code Specimen} object, i.e. if the (nested) fields of
	 * the  {@code SummarySpecimen} object all match the given 
	 * {@code Specimen} object.
	 * 
	 * @param sp the {@code Specimen} object to compare to
	 * @return true of this object is a summary of the object given in argument 
	 */
	public boolean isSummaryOf(Specimen sp)
	{
	    	boolean result = true;
	    	result &= Objects.equals(this.getAssemblageID(), sp.getAssemblageID());
	    	result &= Objects.equals(this.getCollectionType(), sp.getCollectionType());
	    	result &= Objects.equals(this.getCollectorsFieldNumber(), sp.getCollectorsFieldNumber());
	    	result &= Objects.equals(this.getPhaseOrStage(), sp.getPhaseOrStage());
	    	result &= Objects.equals(this.getSex(), sp.getSex());
	    	result &= Objects.equals(this.getUnitID(), sp.getUnitID());
	    	result &= Objects.equals(this.getSourceSystem().getCode(), sp.getSourceSystem().getCode());
	    	result &= this.getGatheringEvent().isSummaryOf(sp.getGatheringEvent());	    		    
	    	
	    	
	    	// compare identifications
		List<SummarySpecimenIdentification> summaryMatchingIdentifications = this.getMatchingIdentifications();
		List<SummarySpecimenIdentification> summaryOtherIdentifications = this.getOtherIdentifications();
		List<SummarySpecimenIdentification> summaryIdentifications = new ArrayList<>();
		if (summaryMatchingIdentifications != null) {
		    summaryIdentifications.addAll(summaryMatchingIdentifications);
		}
		if (summaryOtherIdentifications != null) {
		    summaryIdentifications.addAll(summaryOtherIdentifications);
		}		
		Collections.sort(summaryIdentifications, new Comparator<SummarySpecimenIdentification>() {		    
		    @Override
		    public int compare(SummarySpecimenIdentification si1, SummarySpecimenIdentification si2)
		    {
			return si1.getScientificName().getFullScientificName().compareTo(si2.getScientificName().getFullScientificName());
		    }
		});
	    	
		List<SpecimenIdentification> identifications = sp.getIdentifications();
		Collections.sort(identifications, new Comparator<SpecimenIdentification>() {		    
		    @Override
		    public int compare(SpecimenIdentification si1, SpecimenIdentification si2)
		    {
			return si1.getScientificName().getFullScientificName().compareTo(si2.getScientificName().getFullScientificName());
		    }
		});

		result &= summaryIdentifications.size() == identifications.size();    	    
	    	for (int i=0; i<summaryIdentifications.size(); i++){
	    	    result &= summaryIdentifications.get(i).isSummaryOf(identifications.get(i));
	    	}

	    	return result;
	}
	
	public void addMatchingIdentification(SummarySpecimenIdentification ssi)
	{
		if (matchingIdentifications == null) {
			matchingIdentifications = new ArrayList<>(3);
		}
		matchingIdentifications.add(ssi);
	}

	public void addOtherIdentification(SummarySpecimenIdentification ssi)
	{
		if (otherIdentifications == null) {
			otherIdentifications = new ArrayList<>(3);
		}
		otherIdentifications.add(ssi);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		}
		if (obj instanceof SummarySpecimen) {
			return ((SummarySpecimen) obj).id.equals(id);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return id.hashCode();
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

	public String getUnitID()
	{
		return unitID;
	}

	public void setUnitID(String unitID)
	{
		this.unitID = unitID;
	}

	public String getCollectorsFieldNumber()
	{
		return collectorsFieldNumber;
	}

	public void setCollectorsFieldNumber(String collectorsFieldNumber)
	{
		this.collectorsFieldNumber = collectorsFieldNumber;
	}

	public String getAssemblageID()
	{
		return assemblageID;
	}

	public void setAssemblageID(String assemblageID)
	{
		this.assemblageID = assemblageID;
	}

	public String getCollectionType()
	{
		return collectionType;
	}

	public void setCollectionType(String collectionType)
	{
		this.collectionType = collectionType;
	}

	public Sex getSex()
	{
		return sex;
	}

	public void setSex(Sex sex)
	{
		this.sex = sex;
	}

	public PhaseOrStage getPhaseOrStage()
	{
		return phaseOrStage;
	}

	public void setPhaseOrStage(PhaseOrStage phaseOrStage)
	{
		this.phaseOrStage = phaseOrStage;
	}

	public SummaryGatheringEvent getGatheringEvent()
	{
		return gatheringEvent;
	}

	public void setGatheringEvent(SummaryGatheringEvent gatheringEvent)
	{
		this.gatheringEvent = gatheringEvent;
	}

	public List<SummarySpecimenIdentification> getMatchingIdentifications()
	{
		return matchingIdentifications;
	}

	public void setMatchingIdentifications(
			List<SummarySpecimenIdentification> matchingIdentifications)
	{
		this.matchingIdentifications = matchingIdentifications;
	}

	public List<SummarySpecimenIdentification> getOtherIdentifications()
	{
		return otherIdentifications;
	}

	public void setOtherIdentifications(List<SummarySpecimenIdentification> otherIdentifications)
	{
		this.otherIdentifications = otherIdentifications;
	}

}
