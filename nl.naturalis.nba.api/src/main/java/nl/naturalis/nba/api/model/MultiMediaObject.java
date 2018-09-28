package nl.naturalis.nba.api.model;

import static nl.naturalis.nba.api.annotations.Analyzer.CASE_INSENSITIVE;
import static nl.naturalis.nba.api.annotations.Analyzer.DEFAULT;
import static nl.naturalis.nba.api.annotations.Analyzer.LIKE;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nba.api.annotations.Analyzers;
import nl.naturalis.nba.api.annotations.NotStored;
import nl.naturalis.nba.api.model.ResourceType;

/**
 * Class modeling a MultiMediaObject
 */
public class MultiMediaObject extends NbaTraceableObject implements IDocumentObject {

	@NotStored
	private String id;
	private String sourceInstitutionID;
	private String sourceID;
	@SuppressWarnings("unused")
  private List<String> previousSourceID;
	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String owner;
	private LicenseType licenseType;
	private License license;
	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String unitID;
	@SuppressWarnings("unused")
  private String previousUnitsText;
	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String collectionType;
	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String title;
	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String caption;
	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String description;
	private List<ServiceAccessPoint> serviceAccessPoints;
	private ResourceType type;
	private int taxonCount;
	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String creator;
	private String copyrightText;
	private String associatedSpecimenReference;
	private String associatedTaxonReference;
	private Boolean multiMediaPublic;
	private String informationWithheld;
	private String dataGeneralizations;
	private Byte rating;
	private String resourceCreationTechnique;
	private OffsetDateTime modified;
	
	private List<String> subjectParts;
	private List<String> subjectOrientations;
	private List<String> phasesOrStages;
	private List<String> sexes;
	private List<MultiMediaGatheringEvent> gatheringEvents;
	private List<MultiMediaContentIdentification> identifications;
	private List<String> theme;

	// Non-persistent data
	@NotStored
	private Specimen associatedSpecimen;
	@NotStored
	private Taxon associatedTaxon;

	public void addServiceAccessPoint(String uri, String format, String variant)
	{
		if (serviceAccessPoints == null) {
			serviceAccessPoints = new ArrayList<ServiceAccessPoint>();
		}
		serviceAccessPoints.add(new ServiceAccessPoint(uri, format, variant));
	}

	public void addServiceAccessPoint(ServiceAccessPoint sap)
	{
		if (serviceAccessPoints == null) {
			serviceAccessPoints = new ArrayList<ServiceAccessPoint>();
		}
		serviceAccessPoints.add(sap);
	}

	@Override
	public String getId()
	{
		return id;
	}

	@Override
	public void setId(String id)
	{
		this.id = id;
	}

	public String getSourceInstitutionID()
	{
		return sourceInstitutionID;
	}

	public void setSourceInstitutionID(String sourceInstitutionID)
	{
		this.sourceInstitutionID = sourceInstitutionID;
	}

	public String getSourceID()
	{
		return sourceID;
	}

	public void setSourceID(String sourceID)
	{
		this.sourceID = sourceID;
	}

  public List<String> getPreviousSourceID()
  {
    return previousSourceID;
  }
  
  public void setPreviousSourceID(List<String> previousSourceID)
  {
    this.previousSourceID = previousSourceID;
  }

  public String getPreviousUnitsText()
  {
    return previousUnitsText;
  }
  
  public void setPreviousUnitsText(String previousUnitsText)
  {
    this.previousUnitsText = previousUnitsText;
  }
  
	public String getOwner()
	{
		return owner;
	}

	public void setOwner(String owner)
	{
		this.owner = owner;
	}

	public LicenseType getLicenseType()
	{
		return licenseType;
	}

	public void setLicenseType(LicenseType licenseType)
	{
		this.licenseType = licenseType;
	}

	public License getLicense()
	{
		return license;
	}

	public void setLicense(License license)
	{
		this.license = license;
	}

	public String getUnitID()
	{
		return unitID;
	}

	public void setUnitID(String unitID)
	{
		this.unitID = unitID;
	}

	public String getCollectionType()
	{
		return collectionType;
	}

	public void setCollectionType(String collectionType)
	{
		this.collectionType = collectionType;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getCaption()
	{
		return caption;
	}

	public void setCaption(String caption)
	{
		this.caption = caption;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public List<ServiceAccessPoint> getServiceAccessPoints()
	{
		return serviceAccessPoints;
	}

	public void setServiceAccessPoints(List<ServiceAccessPoint> serviceAccessPoints)
	{
		this.serviceAccessPoints = serviceAccessPoints;
	}

	public ResourceType getType()
	{
		return type;
	}

	public void setType(ResourceType type)
	{
		this.type = type;
	}

	public int getTaxonCount()
	{
		return taxonCount;
	}

	public void setTaxonCount(int taxonCount)
	{
		this.taxonCount = taxonCount;
	}

	public String getCreator()
	{
		return creator;
	}

	public void setCreator(String creator)
	{
		this.creator = creator;
	}

	public String getCopyrightText()
	{
		return copyrightText;
	}

	public void setCopyrightText(String copyrightText)
	{
		this.copyrightText = copyrightText;
	}

	public String getAssociatedSpecimenReference()
	{
		return associatedSpecimenReference;
	}

	public void setAssociatedSpecimenReference(String associatedSpecimenReference)
	{
		this.associatedSpecimenReference = associatedSpecimenReference;
	}

	public String getAssociatedTaxonReference()
	{
		return associatedTaxonReference;
	}

	public void setAssociatedTaxonReference(String associatedTaxonReference)
	{
		this.associatedTaxonReference = associatedTaxonReference;
	}

	public Boolean isMultiMediaPublic()
	{
		return multiMediaPublic;
	}

	public void setMultiMediaPublic(Boolean multiMediaPublic)
	{
		this.multiMediaPublic = multiMediaPublic;
	}

	public List<String> getSubjectParts()
	{
		return subjectParts;
	}

	public void setSubjectParts(List<String> subjectParts)
	{
		this.subjectParts = subjectParts;
	}

	public List<String> getSubjectOrientations()
	{
		return subjectOrientations;
	}

	public void setSubjectOrientations(List<String> subjectOrientations)
	{
		this.subjectOrientations = subjectOrientations;
	}

	public List<String> getPhasesOrStages()
	{
		return phasesOrStages;
	}

	public void setPhasesOrStages(List<String> phasesOrStages)
	{
		this.phasesOrStages = phasesOrStages;
	}

	public List<String> getSexes()
	{
		return sexes;
	}

	public void setSexes(List<String> sexes)
	{
		this.sexes = sexes;
	}

	public List<MultiMediaGatheringEvent> getGatheringEvents()
	{
		return gatheringEvents;
	}

	public void setGatheringEvents(List<MultiMediaGatheringEvent> gatheringEvents)
	{
		this.gatheringEvents = gatheringEvents;
	}

	public List<MultiMediaContentIdentification> getIdentifications()
	{
		return identifications;
	}

	public void setIdentifications(List<MultiMediaContentIdentification> identifications)
	{
		this.identifications = identifications;
	}

	public List<String> getTheme()
	{
		return theme;
	}

	public void setTheme(List<String> theme)
	{
		this.theme = theme;
	}

	public Specimen getAssociatedSpecimen()
	{
		return associatedSpecimen;
	}

	public void setAssociatedSpecimen(Specimen associatedSpecimen)
	{
		this.associatedSpecimen = associatedSpecimen;
	}

	public Taxon getAssociatedTaxon()
	{
		return associatedTaxon;
	}

	public void setAssociatedTaxon(Taxon associatedTaxon)
	{
		this.associatedTaxon = associatedTaxon;
	}

  public String getInformationWithheld() {
    return informationWithheld;
  }

  public void setInformationWithheld(String informationWithheld) {
    this.informationWithheld = informationWithheld;
  }

  public String getDataGeneralizations() {
    return dataGeneralizations;
  }

  public void setDataGeneralizations(String dataGeneralizations) {
    this.dataGeneralizations = dataGeneralizations;
  }

  public Byte getRating() {
    return rating;
  }

  /**
   * Rating is an integer from the scale: -1, 0, 1, 2, 3, 4, 5
   * @param rating
   */
  public void setRating(Byte rating) {
    if (rating >= -1 && rating <= 5) {
      this.rating = rating;
    }
  }

  public String getResourceCreationTechnique() {
    return resourceCreationTechnique;
  }

  public void setResourceCreationTechnique(String resourceCreationTechnique) {
    this.resourceCreationTechnique = resourceCreationTechnique;
  }

  public OffsetDateTime getDateLastEdited() {
    return modified;
  }

  public void setDateLastEdited(OffsetDateTime dateLastEdited) {
    this.modified = dateLastEdited;
  }

}
