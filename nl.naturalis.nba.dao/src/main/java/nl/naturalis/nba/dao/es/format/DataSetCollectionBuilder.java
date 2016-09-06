package nl.naturalis.nba.dao.es.format;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import nl.naturalis.nba.common.es.map.Mapping;
import nl.naturalis.nba.dao.es.exception.DaoException;
import nl.naturalis.nba.dao.es.format.config.DataSetXmlConfig;
import nl.naturalis.nba.dao.es.format.config.DataSetsXmlConfig;
import nl.naturalis.nba.dao.es.format.config.EntityXmlConfig;

public class DataSetCollectionBuilder {

	private File configFile;

	public DataSetCollectionBuilder(File configFile)
	{
		this.configFile = configFile;
	}

	public DataSetCollection build(ITypedFieldFactory fieldFactory)
			throws DataSetConfigurationException
	{
		DataSetsXmlConfig root = parseConfigFile();
		Mapping source = ConfigUtil.getSource(root.getSource());
		if (source == null) {
			String msg = "You cannot use an ITypedFieldFactory for a generic data source";
			// This really is a program error rather than a configuration error
			throw new DaoException(msg);
		}
		DataSetCollection collection = new DataSetCollection();
		collection.setSource(source);
		for (DataSetXmlConfig dataSetConfig : root.getDatasets()) {
			collection.addDataSet(new DataSetBuilder(dataSetConfig).build());
		}
		TypedEntityBuilder entityBuilder = new TypedEntityBuilder(fieldFactory, source);
		for (EntityXmlConfig entityConfig : root.getEntities()) {
			collection.addEntity(entityBuilder.build(entityConfig));
		}
		return collection;
	}

	private DataSetsXmlConfig parseConfigFile() throws DataSetConfigurationException
	{
		if (!configFile.isFile()) {
			String msg = "Missing configuration file " + configFile.getPath();
			throw new DataSetConfigurationException(msg);
		}
		try {
			JAXBContext ctx = JAXBContext.newInstance(DataSetsXmlConfig.class);
			Unmarshaller unmarshaller = ctx.createUnmarshaller();
			DataSetsXmlConfig root;
			root = (DataSetsXmlConfig) unmarshaller.unmarshal(configFile);
			return root;
		}
		catch (JAXBException e) {
			throw new DataSetConfigurationException(e);
		}
	}

}
