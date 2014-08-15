package nl.naturalis.nda.search;

import java.util.ArrayList;
import java.util.List;

public class SearchResult<T> {

	private float score;
	private String actualType;
	private List<Link> links;
	private List<MatchInfo<?>> matchInfo;
	private T result;


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


	public void addMatchInfo(MatchInfo<?> matchInfo)
	{
		if (this.matchInfo == null) {
			this.matchInfo = new ArrayList<MatchInfo<?>>();
		}
		this.matchInfo.add(matchInfo);
	}


	public float getScore()
	{
		return score;
	}


	public void setScore(float score)
	{
		this.score = score;
	}


	public String getActualType()
	{
		return actualType;
	}


	public void setActualType(String actualType)
	{
		this.actualType = actualType;
	}


	public List<MatchInfo<?>> getMatchInfo()
	{
		return matchInfo;
	}


	public void setMatchInfo(List<MatchInfo<?>> matchInfo)
	{
		this.matchInfo = matchInfo;
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
