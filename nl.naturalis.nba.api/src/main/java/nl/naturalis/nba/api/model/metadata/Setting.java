package nl.naturalis.nba.api.model.metadata;

public class Setting {

	private String name;
	private Object value;
	private String description;

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		}
		if (obj != null && obj.getClass() == Setting.class) {
			return ((Setting) obj).name.equals(name);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return name.hashCode();
	}

	@Override
	public String toString()
	{
		return name;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Object getValue()
	{
		return value;
	}

	public void setValue(Object value)
	{
		this.value = value;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

}
