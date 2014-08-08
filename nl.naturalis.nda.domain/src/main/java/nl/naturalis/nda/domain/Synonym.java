package nl.naturalis.nda.domain;

import java.util.List;

public class Synonym {
	
	private ScientificName scientificName;
	private List<Taxon> taxa;


	public ScientificName getScientificName()
	{
		return scientificName;
	}


	public void setScientificName(ScientificName scientificName)
	{
		this.scientificName = scientificName;
	}


	public List<Taxon> getTaxa()
	{
		return taxa;
	}


	public void setTaxa(List<Taxon> taxa)
	{
		this.taxa = taxa;
	}

}
