package nl.naturalis.nba.dao.es.format;

import java.net.URI;

public abstract class AbstractField implements IField {

	private String name;
	private URI term;

	protected AbstractField(String name, URI term)
	{
		this.name = name;
		this.term = term;
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

}
