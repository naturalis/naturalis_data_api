package nl.naturalis.nba.api.model;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import nl.naturalis.nba.api.model.summary.SummarySpecimen;
import nl.naturalis.nba.api.model.summary.SummaryTaxon;

/**
 * A ScientificNameGroup contains a scientific name and the specimens, taxa and
 * various statistics associated with that name. The ScientificNameGroup index
 * is a "frozen" aggregation query on the {@link Specimen} index and the
 * {@link Taxon} index. It groups specimens and taxa on their full scientific
 * name. More precisely: it groups them on the combination of their genus,
 * specific eptithet and infraspecific epithet. Each ScientificNameGroup
 * document contains one such combination, which is guaranteed to be unique
 * within the ScientificNameGroup index as a whole.
 * 
 * @author Ayco Holleman
 *
 */
public class ScientificNameGroup implements IDocumentObject {

	private static final Comparator<SummarySpecimen> specimenComparator = new Comparator<SummarySpecimen>() {

		@Override
		public int compare(SummarySpecimen o1, SummarySpecimen o2)
		{
			return o1.getUnitID().compareTo(o2.getUnitID());
		}
	};

	private String id;
	private String name;
	private Set<SummarySpecimen> specimens;
	private Set<SummaryTaxon> taxa;

	private int specimenCount;
	private int taxonCount;

	public ScientificNameGroup()
	{
	}

	public ScientificNameGroup(String name)
	{
		this.id = name;
		this.name = name;
	}

	public void addSpecimen(SummarySpecimen specimen)
	{
		if (specimens == null) {
			specimens = new TreeSet<>(specimenComparator);
		}
		specimens.add(specimen);
	}

	public void addTaxon(SummaryTaxon taxon)
	{
		if (taxa == null) {
			taxa = new LinkedHashSet<SummaryTaxon>(2);
		}
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
	 * Returns the specimens in this group.
	 * 
	 * @return
	 */
	public Set<SummarySpecimen> getSpecimens()
	{
		return specimens;
	}

	/**
	 * Sets the specimens in this group.
	 * 
	 * @param specimens
	 */
	public void setSpecimens(Set<SummarySpecimen> specimens)
	{
		this.specimens = specimens;
	}

	/**
	 * Returns the taxa in this group.
	 * 
	 * @return
	 */
	public Set<SummaryTaxon> getTaxa()
	{
		return taxa;
	}

	/**
	 * Sets the taxa in this group.
	 * 
	 * @param taxa
	 */
	public void setTaxa(Set<SummaryTaxon> taxa)
	{
		this.taxa = taxa;
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

}
