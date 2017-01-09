package nl.naturalis.nba.api.model;

import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nba.api.annotations.NotIndexed;

public class Name implements IDocumentObject {

	private String id;
	private String name;
	@NotIndexed
	private List<String> specimenUnitIDs;

	public Name()
	{
	}

	public Name(String value)
	{
		this.name = value;
	}

	public void addSpecimenUnitID(String id)
	{
		if (specimenUnitIDs == null) {
			specimenUnitIDs = new ArrayList<>(4);
			specimenUnitIDs.add(id);
		}
		else {
			for (String e : specimenUnitIDs) {
				if (e.equals(id)) {
					return;
				}
			}
			specimenUnitIDs.add(id);
		}
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

	public List<String> getSpecimenUnitIDs()
	{
		return specimenUnitIDs;
	}

	public void setSpecimenUnitIDs(List<String> specimenUnitIDs)
	{
		this.specimenUnitIDs = specimenUnitIDs;
	}

}
