package nl.naturalis.nba.dao.es.util;

import org.domainobject.util.ConfigObject;

public class IndexInfo {

	private final String id;
	private final String name;
	private final int numShards;
	private final int numReplicas;

	public IndexInfo(ConfigObject cfg)
	{
		id = cfg.getSectionName();
		name = cfg.required("name");
		numShards = cfg.required("shards", int.class);
		numReplicas = cfg.required("replicas", int.class);
	}

	public String getName()
	{
		return name;
	}

	public int getNumShards()
	{
		return numShards;
	}

	public int getNumReplicas()
	{
		return numReplicas;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		return id.equals(((IndexInfo) obj).id);
	}

	@Override
	public int hashCode()
	{
		return id.hashCode();
	}

}
