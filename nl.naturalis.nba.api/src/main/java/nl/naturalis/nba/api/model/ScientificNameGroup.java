package nl.naturalis.nba.api.model;

import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nba.api.ISpecimenAccess;
import nl.naturalis.nba.api.ITaxonAccess;

/**
 * The ScientificNameGroup class is used to capture the output from
 * {@link ITaxonAccess#groupByScientificName(nl.naturalis.nba.api.GroupByScientificNameQuerySpec)
 * ITaxonAccess.groupByScientificName} and
 * {@link ISpecimenAccess#groupByScientificName(nl.naturalis.nba.api.GroupByScientificNameQuerySpec)
 * ISpecimenAccess.groupByScientificName}. Each instance represents a single
 * group returned from the GROUP BY query. It contains a {@link #getName()
 * name}, which is the scientific name and a list of specimens and taxa
 * associated with that name.
 * 
 * @see ScientificName#getScientificNameGroup()
 * 
 * @author Ayco Holleman
 *
 */
public class ScientificNameGroup {

	private String name;
	private int specimenCount;
	private int taxonCount;
	private List<Specimen> specimens;
	private List<Taxon> taxa;

	public ScientificNameGroup()
	{
	}

	public ScientificNameGroup(String name)
	{
		this.name = name;
	}

	public void addSpecimen(Specimen specimen)
	{
		if (specimens == null) {
			specimens = new ArrayList<>();
		}
		specimens.add(specimen);
	}

	public void addTaxon(Taxon taxon)
	{
		if (taxa == null) {
			taxa = new ArrayList<>(2);
		}
		taxa.add(taxon);
	}

	/**
	 * Returns the group value of this {@code ScientificNameGroup}.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the group value of this {@code ScientificNameGroup}.
	 * 
	 * @param name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Returns the total number of specimens in this group.
	 * 
	 * @return
	 */
	public int getSpecimenCount()
	{
		return specimenCount;
	}

	/**
	 * Sets the total number of specimens in this group.
	 * 
	 * @param specimenCount
	 */
	public void setSpecimenCount(int specimenCount)
	{
		this.specimenCount = specimenCount;
	}

	/**
	 * Returns the total number of taxa in this group.
	 * 
	 * @return
	 */
	public int getTaxonCount()
	{
		return taxonCount;
	}

	/**
	 * Sets the total number of taxa in this group.
	 * 
	 * @param taxonCount
	 */
	public void setTaxonCount(int taxonCount)
	{
		this.taxonCount = taxonCount;
	}

	/**
	 * Returns the specimens in this group.
	 * 
	 * @return
	 */
	public List<Specimen> getSpecimens()
	{
		return specimens;
	}

	/**
	 * Sets the specimens in this group.
	 * 
	 * @param specimens
	 */
	public void setSpecimens(List<Specimen> specimens)
	{
		this.specimens = specimens;
	}

	/**
	 * Returns the taxa in this group.
	 * 
	 * @return
	 */
	public List<Taxon> getTaxa()
	{
		return taxa;
	}

	/**
	 * Sets the taxa in this group.
	 * 
	 * @param taxa
	 */
	public void setTaxa(List<Taxon> taxa)
	{
		this.taxa = taxa;
	}

}
