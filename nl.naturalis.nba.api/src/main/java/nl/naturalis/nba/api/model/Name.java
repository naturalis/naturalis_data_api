package nl.naturalis.nba.api.model;

import java.util.HashSet;
import java.util.Set;

public class Name implements IDocumentObject {

	private String id;
	private String value;
	private Set<NameInfo> sources;

	public Name()
	{
	}

	public Name(String value)
	{
		this.value = value;
	}

	public void addNameInfo(NameInfo source)
	{
		if (sources == null) {
			sources = new HashSet<>(8);
		}
		sources.add(source);
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
		return value;
	}

	public void setName(String name)
	{
		this.value = name;
	}

	public Set<NameInfo> getNameInfos()
	{
		return sources;
	}

	public void setNameInfos(Set<NameInfo> sources)
	{
		this.sources = sources;
	}

}
