package nl.naturalis.nba.api.model;

import java.util.List;

public class SummarySpecimen implements INbaModelObject {

	private String id;
	private SourceSystem sourceSystem;
	private String unitID;
	private String collectorsFieldNumber;
	private SpecimenTypeStatus typeStatus;
	private Sex sex;
	private PhaseOrStage phaseOrStage;
	private SummaryGatheringEvent gatheringEvent;
	private List<SummarySpecimenIdentification> identifications;


}
