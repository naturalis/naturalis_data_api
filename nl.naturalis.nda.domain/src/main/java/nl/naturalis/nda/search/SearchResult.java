package nl.naturalis.nda.search;

import java.util.ArrayList;
import java.util.List;

public class SearchResult<T> {

	private T result;
	private List<Link> links;


	public SearchResult(T result)
	{
		this.result = result;
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


	public T getResult()
	{
		return result;
	}


	public void setResult(T result)
	{
		this.result = result;
	}


	public List<Link> getLinks()
	{
		return links;
	}


	public void setLinks(List<Link> links)
	{
		this.links = links;
	}

}
