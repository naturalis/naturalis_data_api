package nl.naturalis.nba.api.model;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import nl.naturalis.nba.api.annotations.NotIndexed;
import nl.naturalis.nba.api.annotations.NotNested;

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

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj instanceof SpecimenSummary)
				return ((SpecimenSummary) obj).id.equals(id);
			return false;
		}

		public int hashCode()
		{
			return id.hashCode();
		}
	}

	public static class TaxonSummary implements INbaModelObject {

		@NotIndexed
		private String id;
		@NotIndexed
		private String sourceSystem;
		@NotIndexed
		private String rank;

		public String getId()
		{
			return id;
		}

		public void setId(String id)
		{
			this.id = id;
		}

		public String getSourceSystem()
		{
			return sourceSystem;
		}

		public void setSourceSystem(String sourceSystem)
		{
			this.sourceSystem = sourceSystem;
		}

		public String getRank()
		{
			return rank;
		}

		public void setRank(String rank)
		{
			this.rank = rank;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj instanceof TaxonSummary)
				return ((TaxonSummary) obj).id.equals(id);
			return false;
		}

		public int hashCode()
		{
			return id.hashCode();
		}
	}

	private String id;
	private String fullScientificName;

	private Set<String> vernacularNames;
	private Set<String> synonyms;
	private Set<String> kingdoms;
	private Set<String> phylae;
	private Set<String> classes;
	private Set<String> orders;
	private Set<String> families;
	private Set<String> genera;
	private Set<String> specificEpithets;
	private Set<String> infraspecificEpithets;

	@NotNested
	private Set<SpecimenSummary> specimens;
	@NotNested
	private Set<TaxonSummary> taxa;

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

	public void addSynonym(String synonym)
	{
		if (synonyms == null)
			synonyms = new HashSet<>(8);
		synonyms.add(synonym);
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

	public void addSpecimen(SpecimenSummary specimen)
	{
		if (specimens == null)
			specimens = new LinkedHashSet<>(8);
		specimens.add(specimen);
	}

	public void addTaxon(TaxonSummary taxon)
	{
		if (taxa == null)
			taxa = new LinkedHashSet<>(4);
		taxa.add(taxon);
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

	public Set<String> getSynonyms()
	{
		return synonyms;
	}

	public void setSynonyms(Set<String> synonyms)
	{
		this.synonyms = synonyms;
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

	public Set<SpecimenSummary> getSpecimens()
	{
		return specimens;
	}

	public void setSpecimens(Set<SpecimenSummary> specimens)
	{
		this.specimens = specimens;
	}

	public Set<TaxonSummary> getTaxa()
	{
		return taxa;
	}

	public void setTaxa(Set<TaxonSummary> taxa)
	{
		this.taxa = taxa;
	}

}
