package nl.naturalis.nda.domain;

import java.util.ArrayList;
import java.util.List;

public class SpecimenSearchResult {

	private long totalSize;
	private List<Specimen> specimens;
	
	public void addResult(Specimen result) {
		if(specimens == null) {
			specimens = new ArrayList<Specimen>();
		}
		specimens.add(result);
	}

	public long getTotalSize()
	{
		return totalSize;
	}


	public void setTotalSize(long size)
	{
		this.totalSize = size;
	}


	public List<Specimen> getSpecimens()
	{
		return specimens;
	}


	public void setSpecimens(List<Specimen> results)
	{
		this.specimens = results;
	}
}
