package nl.naturalis.nda.search;

import java.util.ArrayList;
import java.util.List;

public class ResultGroupSet<T, U> implements ResultSet {

	private long totalSize;
	private List<Link> links;
	private QueryParams searchParameters;
	private List<ResultGroup<T, U>> resultGroups;


	public void addLink(String rel, String href)
	{
		if (links == null) {
			links = new ArrayList<>();
		}
		links.add(new Link(rel, href));
	}


	public void addLink(Link link)
	{
		if (links == null) {
			links = new ArrayList<>();
		}
		links.add(link);
	}


	public void addGroup(ResultGroup<T, U> group)
	{
		if (resultGroups == null) {
			resultGroups = new ArrayList<>();
		}
		resultGroups.add(group);
	}


	public long getTotalSize()
	{
		return totalSize;
	}


	public void setTotalSize(long totalSize)
	{
		this.totalSize = totalSize;
	}


	public List<Link> getLinks()
	{
		return links;
	}


	public void setLinks(List<Link> links)
	{
		this.links = links;
	}


	public List<ResultGroup<T, U>> getResultGroups()
	{
		return resultGroups;
	}


	public void setResultGroups(List<ResultGroup<T, U>> resultGroups)
	{
		this.resultGroups = resultGroups;
	}

    @Override
    public QueryParams getQueryParameters() {
        return searchParameters;
    }

    @Override
    public void setQueryParameters(QueryParams searchParameters) {
        this.searchParameters = searchParameters;
    }
}
