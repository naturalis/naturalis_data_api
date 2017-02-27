package nl.naturalis.nba.api.model;

import java.util.HashSet;
import java.util.Set;

public class NameGroup implements IDocumentObject {

	private String id;
	private String name;
	private Set<SummarySpecimen> specimens;
	private Set<SummaryTaxon> taxa;

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
			specimens = new HashSet<>();
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

}
