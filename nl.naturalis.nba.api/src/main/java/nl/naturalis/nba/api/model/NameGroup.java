package nl.naturalis.nba.api.model;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 * A NameGroup contains a scientific name and the specimens, taxa and various
 * statistics associated with that name. The NameGroup index is a "frozen query"
 * on specimens and taxa, grouping them on their full scientific name. More
 * precisely: on the combination of their genus, specific eptithet and
 * infraspecific epithet. Each NameGroup document contains one such combination,
 * which is guaranteed to be unique.
 * 
 * @author Ayco Holleman
 *
 */
public class NameGroup implements IDocumentObject {

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

	public NameGroup()
	{
	}

	public NameGroup(String name)
	{
		this.name = name;
	}

	public void addSpecimen(SummarySpecimen specimen)
	{
		if (specimens == null) {
			specimens = new TreeSet<>(specimenComparator);
		}
		specimens.add(specimen);
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

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Set<SummarySpecimen> getSpecimens()
	{
		return specimens;
	}

	public void setSpecimens(Set<SummarySpecimen> specimens)
	{
		this.specimens = specimens;
	}

	public Set<SummaryTaxon> getTaxa()
	{
		return taxa;
	}

	public void setTaxa(Set<SummaryTaxon> taxa)
	{
		this.taxa = taxa;
	}

	public int getSpecimenCount()
	{
		return specimenCount;
	}

	public void setSpecimenCount(int specimenCount)
	{
		this.specimenCount = specimenCount;
	}

	public int getTaxonCount()
	{
		return taxonCount;
	}

	public void setTaxonCount(int taxonCount)
	{
		this.taxonCount = taxonCount;
	}

}
