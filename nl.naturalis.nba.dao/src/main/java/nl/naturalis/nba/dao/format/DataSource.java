package nl.naturalis.nba.dao.format;

import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.common.Path;
import nl.naturalis.nba.common.es.map.Mapping;

public class DataSource {

	private Mapping<?> mapping;
	private Path path;
	private QuerySpec querySpec;

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

	public QuerySpec getQuerySpec()
	{
		return querySpec;
	}

	void setQuerySpec(QuerySpec querySpec)
	{
		this.querySpec = querySpec;
	}

}
