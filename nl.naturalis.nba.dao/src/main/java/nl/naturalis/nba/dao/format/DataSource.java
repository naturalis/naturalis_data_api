package nl.naturalis.nba.dao.format;

import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.common.es.map.Mapping;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.format.config.DataSourceXmlConfig;

/**
 * A {@code DataSource} contains the configuration for how to retrieve the
 * Elasticsearch documents from which a dataset is created. It also optionally
 * specifies the path to a nested object within the documents that is the prime
 * focus of the dataset. See {@link EntityObject}. This class basically
 * corresponds to the &lt;shared-data-source&gt; c.q. &lt;data-source&gt;
 * element of the XML configuration file for a dataset and is a beautified
 * version of JAXB class {@link DataSourceXmlConfig}.
 * 
 * @author Ayco Holleman
 *
 */
public class DataSource {

	private Mapping<?> mapping;
	private Path path;
	private QuerySpec querySpec;
	private DocumentType<?> dt;

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

  public DocumentType<?> getDocumentType() {
    return dt;
  }

  public void setDocumentType(String document) {
    
    this.dt = DocumentType.forName(document);
  }

}
