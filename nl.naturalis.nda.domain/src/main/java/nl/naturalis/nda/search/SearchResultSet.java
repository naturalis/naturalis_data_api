package nl.naturalis.nda.search;

import java.util.ArrayList;
import java.util.List;

public class SearchResultSet<T> extends AbstractResultSet {

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


	public List<SearchResult<T>> getSearchResults()
	{
		return searchResults;
	}


	public void setSearchResults(List<SearchResult<T>> searchResults)
	{
		this.searchResults = searchResults;
	}

}
