package nl.naturalis.nba.api.model;

import java.util.List;

public class NameGroup implements IDocumentObject {

	private String id;
	private String name;
	private List<SummarySpecimen> specimens;
	private List<SummaryTaxon> taxa;

	public NameGroup(String name)
	{
		this.name = name;
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

	public List<SummarySpecimen> getSpecimens()
	{
		return specimens;
	}

	public void setSpecimens(List<SummarySpecimen> specimens)
	{
		this.specimens = specimens;
	}

	public List<SummaryTaxon> getTaxa()
	{
		return taxa;
	}

	public void setTaxa(List<SummaryTaxon> taxa)
	{
		this.taxa = taxa;
	}

}
