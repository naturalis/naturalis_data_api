package nl.naturalis.nba.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.domainobject.util.ConfigObject;

/**
 * Provides information about an Elasticsearch index, for example the document
 * types hosted by it.
 * 
 * @author Ayco Holleman
 *
 */
public class IndexInfo {

	private static final Logger logger = DaoRegistry.getInstance().getLogger(IndexInfo.class);

	private final String name;
	private final int numShards;
	private final int numReplicas;
	private final List<DocumentType<?>> types;

	IndexInfo(ConfigObject cfg)
	{
		name = cfg.required("name");
		logger.info("Retrieving info for index \"{}\"", name);
		numShards = cfg.required("shards", int.class);
		numReplicas = cfg.required("replicas", int.class);
		String[] typeNames = cfg.required("types").split(",");
		types = new ArrayList<>(typeNames.length);
		for (String typeName : typeNames) {
			typeName = typeName.trim();
			DocumentType<?> type = DocumentType.forName(typeName);
			type.indexInfo = this;
			types.add(type);
			logger.info("Document type {} assigned to index {}", typeName, name);
		}
	}

	/**
	 * Returns the name of the index.
	 * 
	 * @return
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the number of shards that the index is distributed across.
	 * 
	 * @return
	 */
	public int getNumShards()
	{
		return numShards;
	}

	/**
	 * Returns the number of replicas per shard.
	 * 
	 * @return
	 */
	public int getNumReplicas()
	{
		return numReplicas;
	}

	/**
	 * Returns the {@link DocumentType document types} hosted by the index.
	 * 
	 * @return
	 */
	public List<DocumentType<?>> getTypes()
	{
		return types;
	}

	void addType(DocumentType<?> type)
	{
		types.add(type);
	}

}
