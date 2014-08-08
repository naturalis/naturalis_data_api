package nl.naturalis.nda.search;

import java.util.ArrayList;
import java.util.List;

public abstract class SearchResultSet<T> {

	private long size;
	private List<Link> links;
	private List<SearchResult<T>> results;


	public void addResult(SearchResult<T> result)
	{
		if (results == null) {
			results = new ArrayList<SearchResult<T>>();
		}
		results.add(result);
	}


	public void addResult(T result)
	{
		if (results == null) {
			results = new ArrayList<SearchResult<T>>();
		}
		results.add(new SearchResult<T>(result));
	}


	public void addLink(String rel, String href)
	{
		if (links == null) {
			links = new ArrayList<Link>();
		}
		links.add(new Link(rel, href));
	}


	public void addLink(Link link)
	{
		if (links == null) {
			links = new ArrayList<Link>();
		}
		links.add(link);
	}


	public long getSize()
	{
		return size;
	}


	public void setSize(long size)
	{
		this.size = size;
	}


	public List<Link> getLinks()
	{
		return links;
	}


	public void setLinks(List<Link> links)
	{
		this.links = links;
	}


	public List<SearchResult<T>> getResults()
	{
		return results;
	}


	public void setResults(List<SearchResult<T>> results)
	{
		this.results = results;
	}

}
