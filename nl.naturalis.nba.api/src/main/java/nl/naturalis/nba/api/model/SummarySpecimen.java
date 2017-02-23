package nl.naturalis.nba.api.model;

import java.util.List;

public class SummarySpecimen implements INbaModelObject {

	private String id;
	private SummarySourceSystem sourceSystem;
	private String unitID;
	private String collectorsFieldNumber;
	private SpecimenTypeStatus typeStatus;
	private Sex sex;
	private PhaseOrStage phaseOrStage;
	private SummaryGatheringEvent gatheringEvent;
	private List<SummarySpecimenIdentification> identifications;

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

	public SpecimenTypeStatus getTypeStatus()
	{
		return typeStatus;
	}

	public void setTypeStatus(SpecimenTypeStatus typeStatus)
	{
		this.typeStatus = typeStatus;
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

	public List<SummarySpecimenIdentification> getIdentifications()
	{
		return identifications;
	}

	public void setIdentifications(List<SummarySpecimenIdentification> identifications)
	{
		this.identifications = identifications;
	}

}
