package nl.naturalis.nba.api.model;

import java.util.ArrayList;
import java.util.List;

public class Name implements IDocumentObject {

	private String id;
	private String value;
	private List<NameInfo> sources;

	public Name()
	{
	}

	public Name(String value)
	{
		this.value = value;
	}

	public void addNameInfo(NameInfo nameInfo)
	{
		if (sources == null) {
			sources = new ArrayList<>(8);
		}
		NameInfo src = find(nameInfo);
		if (src == null) {
			sources.add(nameInfo);
		}
		else {
			src.getDocumentIds().addAll(nameInfo.getDocumentIds());
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
		return value;
	}

	public void setName(String name)
	{
		this.value = name;
	}

	public List<NameInfo> getNameInfos()
	{
		return sources;
	}

	public void setNameInfos(List<NameInfo> sources)
	{
		this.sources = sources;
	}

	private NameInfo find(NameInfo nameInfo)
	{
		List<NameInfo> srcs = this.sources;
		for (NameInfo src : srcs) {
			if (src.equals(nameInfo)) {
				return src;
			}
		}
		return null;
	}

}
