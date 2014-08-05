package nl.naturalis.nda.domain.systypes;

public class CoLSynonym {

	private int synonymID;
	private int taxonID;
	private String identifier;
	private String datasetID;
	private String datasetName;
	private String taxonomicStatus;
	private String taxonRank;
	private String verbatimTaxonRank;
	private String scientificName;
	private String kingdom;
	private String genus;
	private String subgenus;
	private String specificEpithet;
	private String infraspecificEpithet;
	private String scientificNameAuthorship;
	private String source;
	private String namePublishedIn;
	private String nameAccordingTo;
	private String modified;
	private String description;
	private int taxonConceptID;
	private String scientificNameID;
	private String references;

	private CoLTaxon taxon;


	public int getSynonymID()
	{
		return synonymID;
	}


	public void setSynonymID(int synonymID)
	{
		this.synonymID = synonymID;
	}


	public int getTaxonID()
	{
		return taxonID;
	}


	public void setTaxonID(int taxonID)
	{
		this.taxonID = taxonID;
	}


	public String getIdentifier()
	{
		return identifier;
	}


	public void setIdentifier(String identifier)
	{
		this.identifier = identifier;
	}


	public String getDatasetID()
	{
		return datasetID;
	}


	public void setDatasetID(String datasetID)
	{
		this.datasetID = datasetID;
	}


	public String getDatasetName()
	{
		return datasetName;
	}


	public void setDatasetName(String datasetName)
	{
		this.datasetName = datasetName;
	}


	public String getTaxonomicStatus()
	{
		return taxonomicStatus;
	}


	public void setTaxonomicStatus(String taxonomicStatus)
	{
		this.taxonomicStatus = taxonomicStatus;
	}


	public String getTaxonRank()
	{
		return taxonRank;
	}


	public void setTaxonRank(String taxonRank)
	{
		this.taxonRank = taxonRank;
	}


	public String getVerbatimTaxonRank()
	{
		return verbatimTaxonRank;
	}


	public void setVerbatimTaxonRank(String verbatimTaxonRank)
	{
		this.verbatimTaxonRank = verbatimTaxonRank;
	}


	public String getScientificName()
	{
		return scientificName;
	}


	public void setScientificName(String scientificName)
	{
		this.scientificName = scientificName;
	}


	public String getKingdom()
	{
		return kingdom;
	}


	public void setKingdom(String kingdom)
	{
		this.kingdom = kingdom;
	}


	public String getGenus()
	{
		return genus;
	}


	public void setGenus(String genus)
	{
		this.genus = genus;
	}


	public String getSubgenus()
	{
		return subgenus;
	}


	public void setSubgenus(String subgenus)
	{
		this.subgenus = subgenus;
	}


	public String getSpecificEpithet()
	{
		return specificEpithet;
	}


	public void setSpecificEpithet(String specificEpithet)
	{
		this.specificEpithet = specificEpithet;
	}


	public String getInfraspecificEpithet()
	{
		return infraspecificEpithet;
	}


	public void setInfraspecificEpithet(String infraspecificEpithet)
	{
		this.infraspecificEpithet = infraspecificEpithet;
	}


	public String getScientificNameAuthorship()
	{
		return scientificNameAuthorship;
	}


	public void setScientificNameAuthorship(String scientificNameAuthorship)
	{
		this.scientificNameAuthorship = scientificNameAuthorship;
	}


	public String getSource()
	{
		return source;
	}


	public void setSource(String source)
	{
		this.source = source;
	}


	public String getNamePublishedIn()
	{
		return namePublishedIn;
	}


	public void setNamePublishedIn(String namePublishedIn)
	{
		this.namePublishedIn = namePublishedIn;
	}


	public String getNameAccordingTo()
	{
		return nameAccordingTo;
	}


	public void setNameAccordingTo(String nameAccordingTo)
	{
		this.nameAccordingTo = nameAccordingTo;
	}


	public String getModified()
	{
		return modified;
	}


	public void setModified(String modified)
	{
		this.modified = modified;
	}


	public String getDescription()
	{
		return description;
	}


	public void setDescription(String description)
	{
		this.description = description;
	}


	public int getTaxonConceptID()
	{
		return taxonConceptID;
	}


	public void setTaxonConceptID(int taxonConceptID)
	{
		this.taxonConceptID = taxonConceptID;
	}


	public String getScientificNameID()
	{
		return scientificNameID;
	}


	public void setScientificNameID(String scientificNameID)
	{
		this.scientificNameID = scientificNameID;
	}


	public String getReferences()
	{
		return references;
	}


	public void setReferences(String references)
	{
		this.references = references;
	}


	public CoLTaxon getTaxon()
	{
		return taxon;
	}


	public void setTaxon(CoLTaxon taxon)
	{
		this.taxon = taxon;
	}

}
