package nl.naturalis.nba.dao.es.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.domainobject.util.ConfigObject;

import nl.naturalis.nba.dao.es.Registry;

public class IndexInfo {

	private static final Logger logger = Registry.getInstance().getLogger(IndexInfo.class);

	private final String name;
	private final int numShards;
	private final int numReplicas;
	private final List<DocumentType> types;

	IndexInfo(ConfigObject cfg)
	{
		name = cfg.required("name");
		logger.info("Retrieving info for index {}", name);
		numShards = cfg.required("shards", int.class);
		numReplicas = cfg.required("replicas", int.class);
		String[] typeNames = cfg.required("types").split(",");
		types = new ArrayList<>(typeNames.length);
		for (String typeName : typeNames) {
			typeName = typeName.trim();
			DocumentType type = DocumentType.forName(typeName);
			type.indexInfo = this;
			types.add(type);
			logger.info("Document type {} linked to index {}", typeName, name);
		}
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

	public List<DocumentType> getTypes()
	{
		return types;
	}

	void addType(DocumentType type)
	{
		types.add(type);
	}

}
