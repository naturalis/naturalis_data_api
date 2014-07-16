package nl.naturalis.nda.domain;

import java.util.ArrayList;
import java.util.List;

public abstract class SearchResult<T> {

	private long size;
	private List<T> results;
	
	public void addResult(T result) {
		if(results == null) {
			results = new ArrayList<T>();
		}
		results.add(result);
	}

	public long getSize()
	{
		return size;
	}


	public void setSize(long size)
	{
		this.size = size;
	}


	public List<T> getResults()
	{
		return results;
	}


	public void setResults(List<T> results)
	{
		this.results = results;
	}


}
