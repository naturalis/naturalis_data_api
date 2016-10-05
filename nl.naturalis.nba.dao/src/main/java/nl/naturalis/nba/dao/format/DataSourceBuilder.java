package nl.naturalis.nba.dao.format;

import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.common.es.map.Mapping;
import nl.naturalis.nba.common.es.map.MappingFactory;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.exception.DaoException;
import nl.naturalis.nba.dao.format.config.DataSourceXmlConfig;
import nl.naturalis.nba.dao.format.config.QuerySpecXmlConfig;

class DataSourceBuilder {

	private DataSourceXmlConfig dataSourceConfig;

	DataSourceBuilder(DataSourceXmlConfig dataSourceConfig)
	{
		this.dataSourceConfig = dataSourceConfig;
	}

	DataSource build() throws DataSetConfigurationException
	{
		DataSource dataSource = new DataSource();
		dataSource.setMapping(getMapping());
		dataSource.setQuerySpec(getQuerySpec());
		return dataSource;
	}

	private Mapping getMapping() throws DataSetConfigurationException
	{
		Mapping mapping = null;
		if (dataSourceConfig.getDocument() != null) {
			mapping = getMappingForDocument();
		}
		if (dataSourceConfig.getJavaClass() != null) {
			if (mapping != null) {
				String msg = "You cannot include both a <document> element and a "
						+ "<java-class> element within a <data-source> element";
				throw new DataSetConfigurationException(msg);
			}
			mapping = getMappingForJavaClass();
		}
		return mapping;
	}

	private Mapping getMappingForJavaClass() throws DataSetConfigurationException
	{
		try {
			Class<?> cls = Class.forName(dataSourceConfig.getJavaClass());
			return MappingFactory.getMapping(cls);
		}
		catch (ClassNotFoundException e) {
			String msg = "Invalid value in <java-class> element. Please specify "
					+ "a fully qualified Java class name";
			throw new DataSetConfigurationException(msg);
		}
	}

	private Mapping getMappingForDocument() throws DataSetConfigurationException
	{
		try {
			DocumentType<?> dt = DocumentType.forName(dataSourceConfig.getDocument());
			return dt.getMapping();
		}
		catch (DaoException e) {
			String msg = "Invalid value in <document> element. Please specify "
					+ "a valid Elasticsearch document type";
			throw new DataSetConfigurationException(msg);
		}
	}

	private QuerySpec getQuerySpec() throws DataSetConfigurationException
	{
		QuerySpecXmlConfig querySpecConfig = dataSourceConfig.getQuerySpec();
		if (querySpecConfig == null)
			return null;
		return new QuerySpecBuilder(querySpecConfig).build();
	}

}
