package nl.naturalis.nba.api.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A ScientificNameGroup contains a scientific name and the specimens, taxa and
 * various statistics associated with that name. The ScientificNameGroup index
 * is a "frozen" aggregation query on the {@link Specimen} index and the
 * {@link Taxon} index. It groups specimens and taxa on their full scientific
 * name. In other words, each ScientificNameGroup document represents a group
 * (a.k.a. bucket) with the full scientific name as the group value. To be more
 * precise: specimens and taxa are actually grouped on the combination of their
 * genus, specific eptithet and infraspecific epithet (rather than their full
 * scientific name, which may include an author). Each ScientificNameGroup
 * document contains one such combination, which is guaranteed to be unique
 * within the ScientificNameGroup index as a whole.
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
