package nl.naturalis.nba.dao.es.test;

import java.util.List;

import nl.naturalis.nba.api.annotations.Analyzer;
import nl.naturalis.nba.api.annotations.Analyzers;
import nl.naturalis.nba.api.annotations.NotIndexed;
import nl.naturalis.nba.dao.common.test.PetSpecies;

public class Pet {

	@Analyzers({ Analyzer.DEFAULT, Analyzer.LIKE, Analyzer.CASE_INSENSITIVE })
	private String name;
	private PetSpecies species;
	@NotIndexed
	private List<String> colors;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public PetSpecies getSpecies()
	{
		return species;
	}

	public void setSpecies(PetSpecies species)
	{
		this.species = species;
	}

	public List<String> getColors()
	{
		return colors;
	}

	public void setColors(List<String> colors)
	{
		this.colors = colors;
	}

}
