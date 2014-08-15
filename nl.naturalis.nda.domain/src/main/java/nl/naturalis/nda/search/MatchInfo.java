package nl.naturalis.nda.search;

public class MatchInfo<T> {

	private String path;
	private T value;


	public String getPath()
	{
		return path;
	}


	public void setPath(String path)
	{
		this.path = path;
	}


	public T getValue()
	{
		return value;
	}


	public void setValue(T value)
	{
		this.value = value;
	}

}
