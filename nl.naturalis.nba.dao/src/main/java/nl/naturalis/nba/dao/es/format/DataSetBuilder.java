package nl.naturalis.nba.dao.es.format;

import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.dao.es.format.config.DataSetXmlConfig;

class DataSetBuilder {

	private DataSetXmlConfig config;

	DataSetBuilder(DataSetXmlConfig config)
	{
		this.config = config;
	}

	DataSet build() throws DataSetConfigurationException
	{
		String name = config.getName();
		if (name == null || name.trim().isEmpty()) {
			String msg = "Missing or empty \"name\" attribute for element <dataset>";
			throw new DataSetConfigurationException(msg);
		}
		DataSet dataSet = new DataSet();
		dataSet.setName(name);
		if (config.getQuerySpec() != null) {
			try {
				QuerySpecBuilder qsb = new QuerySpecBuilder(config.getQuerySpec());
				QuerySpec querySpec = qsb.build();
				dataSet.setQuerySpec(querySpec);
			}
			catch (DataSetConfigurationException e) {
				String fmt = "Error in <query-spec> element for data set %s: %s";
				String msg = String.format(fmt, name, e.getMessage());
				throw new DataSetConfigurationException(msg);
			}
		}
		return dataSet;
	}

}
