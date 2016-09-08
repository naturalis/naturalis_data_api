package nl.naturalis.nba.dao.es.format;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.dao.es.format.config.DataSetXmlConfig;
import nl.naturalis.nba.dao.es.format.config.EntityXmlConfig;
import nl.naturalis.nba.dao.es.format.config.FieldXmlConfig;

public class DataSetBuilder {

	private static String ERR_BAD_ENTITY = "Entity %s: %s";
	private static String ERR_NO_FIELD_FACTORY = "Entity %s: configuration requires a default or dedicated instance of IFieldFactory";
	private static String ERR_BAD_FIELD = "Entity %s, field %s: %s";

	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger(DataSetBuilder.class);

	private InputStream config;

	private IFieldFactory defaultFieldFactory;
	private HashMap<String, IFieldFactory> entityFieldFactories;

	public DataSetBuilder(String configFile, boolean isResource)
			throws DataSetConfigurationException
	{
		if (isResource) {
			config = getClass().getResourceAsStream(configFile);
			System.out.println(config);
		}
		else {
			try {
				config = new FileInputStream(configFile);
			}
			catch (FileNotFoundException e) {
				String msg = "Missing configuration file: " + configFile;
				throw new DataSetConfigurationException(msg);
			}
		}
	}

	public DataSetBuilder setDefaultFieldFactory(IFieldFactory fieldFactory)
	{
		this.defaultFieldFactory = fieldFactory;
		return this;
	}

	public DataSetBuilder setFieldFactoryForEntity(String entityName, IFieldFactory fieldFactory)
	{
		if (entityFieldFactories == null)
			entityFieldFactories = new HashMap<>(8);
		entityFieldFactories.put(entityName, fieldFactory);
		return this;
	}

	public DataSet build() throws DataSetConfigurationException
	{
		DataSetXmlConfig dataSetConfig = parseConfigFile();
		DataSet dataSet = new DataSet();
		for (EntityXmlConfig entityConfig : dataSetConfig.getEntity()) {
			Entity entity = new Entity();
			dataSet.addEntity(entity);
			entity.setName(entityConfig.getName());
			DataSourceBuilder dsb = new DataSourceBuilder(entityConfig.getDataSource());
			DataSource dataSource = dsb.build();
			entity.setDataSource(dataSource);
			IFieldFactory fieldFactory = getFieldFactory(entityConfig.getName());
			FieldBuilder fieldBuilder = new FieldBuilder(fieldFactory, dataSource);
			for (FieldXmlConfig field : entityConfig.getFields().getField()) {
				try {
					entity.addField(fieldBuilder.build(field));
				}
				catch (FieldConfigurationException e0) {
					String msg = String.format(ERR_BAD_FIELD, e0.getField(), e0.getMessage());
					throw new DataSetConfigurationException(msg);
				}
				catch (DataSetConfigurationException e1) {
					String msg = String.format(ERR_BAD_ENTITY, e1.getMessage());
					throw new DataSetConfigurationException(msg);
				}
			}
		}
		return dataSet;
	}

	private IFieldFactory getFieldFactory(String entityName) throws DataSetConfigurationException
	{
		IFieldFactory ff = entityFieldFactories.get(entityName);
		if (ff == null) {
			ff = defaultFieldFactory;
		}
		if (ff == null) {
			String msg = String.format(ERR_NO_FIELD_FACTORY, entityName);
			throw new DataSetConfigurationException(msg);
		}
		return ff;
	}

	private DataSetXmlConfig parseConfigFile() throws DataSetConfigurationException
	{
		try {
			JAXBContext ctx = JAXBContext.newInstance(DataSetXmlConfig.class);
			Unmarshaller unmarshaller = ctx.createUnmarshaller();
			DataSetXmlConfig root;
			root = (DataSetXmlConfig) unmarshaller.unmarshal(config);
			return root;
		}
		catch (JAXBException e) {
			throw new DataSetConfigurationException(e);
		}
	}

}
