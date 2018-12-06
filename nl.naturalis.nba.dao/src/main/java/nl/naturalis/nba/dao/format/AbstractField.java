package nl.naturalis.nba.dao.format;

import java.net.URI;

public abstract class AbstractField implements IField {

	private String name;
	private URI term;
	private Boolean isCoreId;

  protected AbstractField(String name, URI term, Boolean isCoreId)
	{
		this.name = name;
		this.term = term;
		this.isCoreId = isCoreId;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public URI getTerm()
	{
		return term;
	}
	
	public Boolean getIsCoreId() {
	  return isCoreId;
	}
}
