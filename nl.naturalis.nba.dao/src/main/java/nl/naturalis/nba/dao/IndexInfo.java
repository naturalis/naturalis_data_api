package nl.naturalis.nba.dao;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Logger;
import nl.naturalis.nba.dao.exception.InitializationException;
import nl.naturalis.nba.utils.ConfigObject;

/**
 * Provides information about an Elasticsearch index, for example the document types hosted by it.
 * 
 * @author Ayco Holleman
 *
 */
public class IndexInfo {

  private static final Logger logger = DaoRegistry.getInstance().getLogger(IndexInfo.class);

  private static final String suffix = System.getProperty("elasticsearch.index.default.suffix");

  private String name;
  private int numShards;
  private int numReplicas;
  private List<DocumentType<?>> types;

  IndexInfo(ConfigObject cfg) {
    name = cfg.required("name");
    logger.info("Retrieving info for index \"{}\"", name);
    if (suffix != null) {
      name += suffix;
      logger.info("Appending suffix to index name: \"{}\"", name);
    }
    String val = cfg.get("shards");
    if (val == null) {
      val = cfg.get("defaultNumShards");
      if (val == null) {
        String msg = "Number of shards not specified for index " + name;
        throw new InitializationException(msg);
      }
    }
    try {
      numShards = Integer.parseInt(val);
      logger.info("-> Number of shards: " + numShards);
    } catch (NumberFormatException e) {
      String fmt = "Invalid number of shards: \"%s\"";
      String msg = String.format(fmt, val);
      throw new InitializationException(msg);
    }
    val = cfg.get("replicas");
    if (val == null) {
      val = cfg.get("defaultNumReplicas");
      if (val == null) {
        String msg = "Number of replicas not specified for index " + name;
        throw new InitializationException(msg);
      }
    }
    try {
      numReplicas = Integer.parseInt(val);
      logger.info("-> Number of replicas: " + numReplicas);
    } catch (NumberFormatException e) {
      String fmt = "Invalid number of replicas: \"%s\"";
      String msg = String.format(fmt, val);
      throw new InitializationException(msg);
    }
    val = cfg.required("types");
    String[] typeNames = val.split(",");
    types = new ArrayList<>(typeNames.length);
    for (String typeName : typeNames) {
      typeName = typeName.trim();
      DocumentType<?> type = DocumentType.forName(typeName);
      type.indexInfo = this;
      types.add(type);
    }
    logger.info("-> Document type(s): {}", val);
  }

  /**
   * Returns the name of the index.
   * 
   * @return
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the number of shards that the index is distributed across.
   * 
   * @return
   */
  public int getNumShards() {
    return numShards;
  }

  /**
   * Returns the number of replicas per shard.
   * 
   * @return
   */
  public int getNumReplicas() {
    return numReplicas;
  }

  /**
   * Returns the {@link DocumentType document types} hosted by the index.
   * 
   * @return
   */
  public List<DocumentType<?>> getTypes() {
    return types;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj != null && obj instanceof IndexInfo) {
      return ((IndexInfo) obj).name.equals(name);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public String toString() {
    return name;
  }

  void addType(DocumentType<?> type) {
    types.add(type);
  }

}
