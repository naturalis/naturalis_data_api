package nl.naturalis.nba.api.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.naturalis.nba.api.annotations.NotIndexed;

public class ScientificNameSummary implements IDocumentObject {

	public static class SpecimenSummary implements INbaModelObject {

		@NotIndexed
		String id;
		@NotIndexed
		String unitID;
		@NotIndexed
		String sourceSystem;
		@NotIndexed
		String recordBasis;

		public String getId()
		{
			return id;
		}

		public void setId(String id)
		{
			this.id = id;
		}

		public String getUnitID()
		{
			return unitID;
		}

		public void setUnitID(String unitID)
		{
			this.unitID = unitID;
		}

		public String getSourceSystem()
		{
			return sourceSystem;
		}

		public void setSourceSystem(String sourceSystem)
		{
			this.sourceSystem = sourceSystem;
		}

		public String getRecordBasis()
		{
			return recordBasis;
		}

		public void setRecordBasis(String recordBasis)
		{
			this.recordBasis = recordBasis;
		}

	}

	private String id;
	private String fullScientificName;

	private Set<String> vernacularNames;
	private Set<String> kingdoms;
	private Set<String> phylae;
	private Set<String> classes;
	private Set<String> orders;
	private Set<String> families;
	private Set<String> genera;
	private Set<String> specificEpithets;
	private Set<String> infraspecificEpithets;

	@NotIndexed
	private List<String> specimenDocumentIds;
	@NotIndexed
	private List<String> specimenUnitIDs;
	@NotIndexed
	private List<String> specimenSourceSystems;
	@NotIndexed
	private List<String> specimenRecordBases;
	@NotIndexed
	private List<String> taxonDocumentIds;

	public ScientificNameSummary()
	{
	}

	public ScientificNameSummary(String fullScientificName)
	{
		this.fullScientificName = fullScientificName;
	}

	public void addVernacularName(String vernacularName)
	{
		if (vernacularNames == null)
			vernacularNames = new HashSet<>(8);
		vernacularNames.add(vernacularName);
	}

	public void addKingdom(String kingdom)
	{
		if (kingdoms == null)
			kingdoms = new HashSet<>(4);
		kingdoms.add(kingdom);
	}

	public void addPhylum(String phylum)
	{
		if (phylae == null)
			phylae = new HashSet<>(4);
		phylae.add(phylum);
	}

	public void addClass(String className)
	{
		if (classes == null)
			classes = new HashSet<>(4);
		classes.add(className);
	}

	public void addOrder(String order)
	{
		if (orders == null)
			orders = new HashSet<>(4);
		orders.add(order);
	}

	public void addFamily(String family)
	{
		if (families == null)
			families = new HashSet<>(4);
		families.add(family);
	}

	public void addGenus(String genus)
	{
		if (genera == null)
			genera = new HashSet<>(4);
		genera.add(genus);
	}

	public void addSpecificEpithet(String specificEpithet)
	{
		if (specificEpithets == null)
			specificEpithets = new HashSet<>(4);
		specificEpithets.add(specificEpithet);
	}

	public void addInfraspecificEpithet(String infraspecificEpithet)
	{
		if (infraspecificEpithets == null)
			infraspecificEpithets = new HashSet<>(4);
		infraspecificEpithets.add(infraspecificEpithet);
	}

	public void addSpecimenSourceSystem(String specimenSourceSystem)
	{
		if (specimenSourceSystems == null)
			specimenSourceSystems = new ArrayList<>(8);
		specimenSourceSystems.add(specimenSourceSystem);
	}

	public void addSpecimenDocumentId(String specimenDocumentId)
	{
		if (specimenDocumentIds == null)
			specimenDocumentIds = new ArrayList<>(8);
		specimenDocumentIds.add(specimenDocumentId);
	}

	public void addSpecimenUnitID(String id)
	{
		if (specimenUnitIDs == null)
			specimenUnitIDs = new ArrayList<>(8);
		specimenUnitIDs.add(id);
	}

	public void addSpecimenRecordBasis(String specimenRecordBasis)
	{
		if (specimenRecordBases == null)
			specimenRecordBases = new ArrayList<>(8);
		specimenRecordBases.add(specimenRecordBasis);
	}

	public void addTaxonDocumentId(String taxonDocumentId)
	{
		if (taxonDocumentIds == null)
			taxonDocumentIds = new ArrayList<>(4);
		taxonDocumentIds.add(taxonDocumentId);
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

	public String getFullScientificName()
	{
		return fullScientificName;
	}

	public void setFullScientificName(String fullScientificName)
	{
		this.fullScientificName = fullScientificName;
	}

	public Set<String> getVernacularNames()
	{
		return vernacularNames;
	}

	public void setVernacularNames(Set<String> vernacularNames)
	{
		this.vernacularNames = vernacularNames;
	}

	public Set<String> getKingdoms()
	{
		return kingdoms;
	}

	public void setKingdoms(Set<String> kingdoms)
	{
		this.kingdoms = kingdoms;
	}

	public Set<String> getPhylae()
	{
		return phylae;
	}

	public void setPhylae(Set<String> phylae)
	{
		this.phylae = phylae;
	}

	public Set<String> getClasses()
	{
		return classes;
	}

	public void setClasses(Set<String> classes)
	{
		this.classes = classes;
	}

	public Set<String> getOrders()
	{
		return orders;
	}

	public void setOrders(Set<String> orders)
	{
		this.orders = orders;
	}

	public Set<String> getFamilies()
	{
		return families;
	}

	public void setFamilies(Set<String> families)
	{
		this.families = families;
	}

	public Set<String> getGenera()
	{
		return genera;
	}

	public void setGenera(Set<String> genera)
	{
		this.genera = genera;
	}

	public Set<String> getSpecificEpithets()
	{
		return specificEpithets;
	}

	public void setSpecificEpithets(Set<String> specificEpithets)
	{
		this.specificEpithets = specificEpithets;
	}

	public Set<String> getInfraspecificEpithets()
	{
		return infraspecificEpithets;
	}

	public void setInfraspecificEpithets(Set<String> infraspecificEpithets)
	{
		this.infraspecificEpithets = infraspecificEpithets;
	}

	public List<String> getSpecimenDocumentIds()
	{
		return specimenDocumentIds;
	}

	public void setSpecimenDocumentIds(List<String> specimenDocumentIds)
	{
		this.specimenDocumentIds = specimenDocumentIds;
	}

	public List<String> getSpecimenUnitIDs()
	{
		return specimenUnitIDs;
	}

	public void setSpecimenUnitIDs(List<String> specimenUnitIDs)
	{
		this.specimenUnitIDs = specimenUnitIDs;
	}

	public List<String> getSpecimenSourceSystems()
	{
		return specimenSourceSystems;
	}

	public void setSpecimenSourceSystems(List<String> specimenSourceSystems)
	{
		this.specimenSourceSystems = specimenSourceSystems;
	}

	public List<String> getSpecimenRecordBases()
	{
		return specimenRecordBases;
	}

	public void setSpecimenRecordBases(List<String> specimenRecordBases)
	{
		this.specimenRecordBases = specimenRecordBases;
	}

	public List<String> getTaxonDocumentIds()
	{
		return taxonDocumentIds;
	}

	public void setTaxonDocumentIds(List<String> taxonDocumentIds)
	{
		this.taxonDocumentIds = taxonDocumentIds;
	}

}
