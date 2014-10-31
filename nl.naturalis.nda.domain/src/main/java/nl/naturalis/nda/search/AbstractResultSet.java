package nl.naturalis.nda.search;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Quinten Krijger
 */
public abstract class AbstractResultSet {

	private long totalSize;
	private List<Link> links;
	private QueryParams queryParameters;


	/**
	 * Add a REST link. The href property is URL encoded. In encoding fails, the
	 * original href value is used.
	 * 
	 * @param rel
	 * @param href The <i>unencoded</i> href
	 */
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


	public QueryParams getQueryParameters()
	{
		return queryParameters;
	}


	public void setQueryParameters(QueryParams queryParameters)
	{
		this.queryParameters = queryParameters;
	}

}
