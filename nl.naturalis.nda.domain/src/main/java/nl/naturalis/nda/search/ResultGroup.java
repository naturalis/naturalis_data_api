package nl.naturalis.nda.search;

import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nda.domain.SpecimenUnit;
import nl.naturalis.nda.domain.Taxon;

/**
 * A {@code ResultGroup} is a group of {@link SearchResult} that have a value in
 * common.
 * 
 * @param <T> The type of the domain objects that the group consists of (e.g.
 *            {@link Taxon}, {@link SpecimenUnit}, etc.)
 * @param <U> The type of the value shared by the domain objects in the group
 */
public class ResultGroup<T, U> {

	U sharedValue;
	List<Link> links;
	List<SearchResult<T>> searchResults;


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


	public void addSearchResult(SearchResult<T> result)
	{
		if (searchResults == null) {
			searchResults = new ArrayList<SearchResult<T>>();
		}
		searchResults.add(result);
	}


	public void addSearchResult(T result)
	{
		if (searchResults == null) {
			searchResults = new ArrayList<SearchResult<T>>();
		}
		searchResults.add(new SearchResult<T>(result));
	}


	public U getSharedValue()
	{
		return sharedValue;
	}


	public void setSharedValue(U sharedValue)
	{
		this.sharedValue = sharedValue;
	}


	public List<Link> getLinks()
	{
		return links;
	}


	public void setLinks(List<Link> links)
	{
		this.links = links;
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
