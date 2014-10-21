package nl.naturalis.nda.search;

import java.util.ArrayList;
import java.util.List;

public class SearchResultSet<T> implements ResultSet {

	private long totalSize;
	private List<Link> links;
	private QueryParams queryParameters;
	private List<SearchResult<T>> searchResults;


	public void addSearchResult(SearchResult<T> result)
	{
		if (searchResults == null) {
			searchResults = new ArrayList<>();
		}
		searchResults.add(result);
	}


	public void addSearchResult(T result)
	{
		if (searchResults == null) {
			searchResults = new ArrayList<>();
		}
		searchResults.add(new SearchResult<T>(result));
	}


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

    @Override
    public QueryParams getQueryParameters() {
        return queryParameters;
    }

    @Override
    public void setQueryParameters(QueryParams queryParameters) {
        this.queryParameters = queryParameters;
    }

    public List<SearchResult<T>> getSearchResults()
	{
		return searchResults;
	}


	public void setSearchResults(List<SearchResult<T>> searchResults)
	{
		this.searchResults = searchResults;
	}

}
