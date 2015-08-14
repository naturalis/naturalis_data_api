package nl.naturalis.nda.search;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Quinten Krijger
 */
public abstract class AbstractResultSet {

	/* NDA-293: 
	 * By: Reinier.Kartowikromo
	 * Date: 14-08-2015
	 * Change label names in reponse From: 
	 * 		totalSize to totalGroupSize
	 * 		totalBuckets to totalGroups
	 *      setTotalGroups is not used in none of the classes?. 
	 * */
    private long totalGroupSize;
    private long totalGroups;
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


	public long getTotalGroupSize()
	{
		return totalGroupSize;
	}


	public void setTotalGroupSize(long totalSize)
	{
		this.totalGroupSize = totalSize;
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

    public void setTotalGroups(long totalBuckets) {
        this.totalGroups = totalBuckets;
    }

    public long getTotalGroups() {
        return totalGroups;
    }
}
