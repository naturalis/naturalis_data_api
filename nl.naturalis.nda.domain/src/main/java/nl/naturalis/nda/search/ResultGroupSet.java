package nl.naturalis.nda.search;

import java.util.ArrayList;
import java.util.List;

public class ResultGroupSet<T, U> extends AbstractResultSet {

	private List<ResultGroup<T, U>> resultGroups;


	public void addGroup(ResultGroup<T, U> group)
	{
		if (resultGroups == null) {
			resultGroups = new ArrayList<>();
		}
		resultGroups.add(group);
	}


	public List<ResultGroup<T, U>> getResultGroups()
	{
		return resultGroups;
	}


	public void setResultGroups(List<ResultGroup<T, U>> resultGroups)
	{
		this.resultGroups = resultGroups;
	}

}
