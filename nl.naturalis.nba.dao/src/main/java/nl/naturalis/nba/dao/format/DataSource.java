package nl.naturalis.nba.dao.format;

import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.SearchSpec;
import nl.naturalis.nba.common.es.map.Mapping;

public class DataSource {

	private Mapping<?> mapping;
	private Path path;
	private SearchSpec querySpec;

	DataSource()
	{
	}

	DataSource(DataSource other)
	{
		this.mapping = other.mapping;
		this.querySpec = other.querySpec;
	}

	public Mapping<?> getMapping()
	{
		return mapping;
	}

	void setMapping(Mapping<?> mapping)
	{
		this.mapping = mapping;
	}

	public Path getPath()
	{
		return path;
	}

	void setPath(Path path)
	{
		this.path = path;
	}

	public SearchSpec getQuerySpec()
	{
		return querySpec;
	}

	void setQuerySpec(SearchSpec querySpec)
	{
		this.querySpec = querySpec;
	}

}
