package nl.naturalis.nba.dao.format;

import static java.lang.String.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.format.config.DataSetXmlConfig;
import nl.naturalis.nba.dao.format.config.DataSourceXmlConfig;
import nl.naturalis.nba.dao.format.config.EntityXmlConfig;
import nl.naturalis.nba.dao.format.config.FieldXmlConfig;
import nl.naturalis.nba.dao.format.config.MappingXmlConfig;
import nl.naturalis.nba.dao.format.config.PluginXmlConfig;
import nl.naturalis.nba.utils.StringUtil;

/*
 * N.B. I After the DataSetXmlConfig JAXB class has been generated by xjc, you
 * MUST modify it as follows: [1] Annotate the class with: @XmlRootElement(name
 * = "dataset-config") [2] Set name attribute of @XmlType annotation to empty
 * string Otherwise you WILL get errors with very unhelpful messages when
 * parsing data set configuration files !!!
 * 
 * N.B. II All builder classes strongly rely on XML values being
 * whitespace-trimmed and yielding null if nothing remains (rather than an empty
 * string). This is guaranteed by the use of StringTrimXmlAdapter class in the
 * config package and by the &#64;XmlJavaTypeAdapter annotation in the
 * package-info.java file of the config package. So be careful when
 * re-generating the JAXB classes, which also end up in that package. Do not
 * just delete all Java files in the package folder !!! Use the
 * xjc-dataset-config.sh script.
 */
public class DataSetBuilder {

	private static String ERR_NO_CONFIG = "Missing configuration file: %s";
	private static String ERR_BAD_ENTITY = "Entity %s: %s";
	private static String ERR_NO_FIELD_FACTORY = "Entity %s: configuration requires a default or dedicated instance of IFieldFactory";
	private static String ERR_BAD_FIELD = "Entity %s, field %s: %s";

	private InputStream config;

	private IFieldFactory defaultFieldFactory;
	private HashMap<String, IFieldFactory> entityFieldFactories;

	public DataSetBuilder(File configFile) throws DataSetConfigurationException
	{
		try {
			config = new FileInputStream(configFile);
		}
		catch (FileNotFoundException e) {
			String msg = format(ERR_NO_CONFIG, configFile.getAbsolutePath());
			throw new DataSetConfigurationException(msg);
		}
	}

	public DataSetBuilder(String configFile, boolean isResource)
			throws DataSetConfigurationException
	{
		if (isResource) {
			config = getClass().getResourceAsStream(configFile);
		}
		else {
			try {
				config = new FileInputStream(configFile);
			}
			catch (FileNotFoundException e) {
				String msg = format(ERR_NO_CONFIG, configFile);
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
		if (entityFieldFactories == null) {
			entityFieldFactories = new HashMap<>(8);
		}
		entityFieldFactories.put(entityName, fieldFactory);
		return this;
	}

	public DataSet build() throws DataSetConfigurationException
	{
		DataSetXmlConfig dataSetConfig = parseConfigFile();
		DataSet dataSet = new DataSet();
		DataSource sharedDataSource = null;
		if (dataSetConfig.getSharedDataSource() != null) {
			DataSourceXmlConfig config = dataSetConfig.getSharedDataSource();
			sharedDataSource = new DataSourceBuilder(config).build();
			dataSet.setSharedDataSource(sharedDataSource);
		}
		Entity[] entities = new Entity[dataSetConfig.getEntity().size()];
		int i = 0;
		for (EntityXmlConfig entityConfig : dataSetConfig.getEntity()) {
			Entity entity = new Entity();
			entities[i++] = entity;
			String entityName = entityConfig.getName();
			entity.setName(entityName);
			IEntityFilter[] filters = getEntityFilters(entityConfig);
			entity.setFilters(filters);
			DataSource myDataSource = getDataSource(entityConfig, sharedDataSource);
			entity.setDataSource(myDataSource);
			IField[] fields = getFields(entityConfig, myDataSource);
			entity.setFields(fields);
		}
		dataSet.setEntities(entities);
		return dataSet;
	}

	private static IEntityFilter[] getEntityFilters(EntityXmlConfig entityConfig)
			throws DataSetConfigurationException
	{
		List<PluginXmlConfig> filterConfigs = entityConfig.getFilter();
		IEntityFilter[] filters = new IEntityFilter[filterConfigs.size()];
		int i = 0;
		for (PluginXmlConfig filterConfig : filterConfigs) {
			try {
				IEntityFilter filter = new FilterBuilder(filterConfig).build();
				filters[i++] = filter;
			}
			catch (DataSetConfigurationException e) {
				String msg = String.format(ERR_BAD_ENTITY, entityConfig.getName(), e.getMessage());
				throw new DataSetConfigurationException(msg);
			}
		}
		return filters;
	}

	private static DataSource getDataSource(EntityXmlConfig entityConfig,
			DataSource sharedDataSource) throws DataSetConfigurationException
	{
		DataSourceXmlConfig dsConfig = entityConfig.getDataSource();
		DataSource dataSource;
		if (dsConfig == null) {
			if (sharedDataSource == null) {
				String msg = format(ERR_BAD_ENTITY, entityConfig.getName(),
						"Missing <data-source> element and no <shared-data-source> "
								+ "defined under <dataset-config>");
				throw new DataSetConfigurationException(msg);
			}
			dataSource = new DataSource(sharedDataSource);
		}
		else {
			if (sharedDataSource != null) {
				String msg = format(ERR_BAD_ENTITY, entityConfig.getName(),
						"Mutually exclusive: <shared-data-source>, <data-source>");
				throw new DataSetConfigurationException(msg);
			}
			dataSource = new DataSourceBuilder(dsConfig).build();
		}
		String path = entityConfig.getPath();
		if (path != null) {
			dataSource.setPath(new Path(path));
		}
		return dataSource;
	}

	private IField[] getFields(EntityXmlConfig entityConfig, DataSource dataSource)
			throws DataSetConfigurationException
	{
		MappingXmlConfig mappingConfig = entityConfig.getMapping();
		if (mappingConfig == null) {
			String msg = format(ERR_BAD_ENTITY, entityConfig.getName(), "Missing required element: <mapping>");
			throw new DataSetConfigurationException(msg);
		}
		List<FieldXmlConfig> fieldConfigs = mappingConfig.getField();
		IFieldFactory fieldFactory = getFieldFactory(entityConfig.getName());
		FieldBuilder fieldBuilder = new FieldBuilder(fieldFactory, dataSource);
		IField[] fields = new IField[fieldConfigs.size()];
		int i = 0;
		for (FieldXmlConfig field : fieldConfigs) {
			try {
				fields[i++] = fieldBuilder.build(field);
			}
			catch (FieldConfigurationException e0) {
				String msg = format(ERR_BAD_FIELD, entityConfig.getName(), e0.getField(), e0.getMessage());
				throw new DataSetConfigurationException(msg);
			}
			catch (DataSetConfigurationException e1) {
				String msg = format(ERR_BAD_ENTITY, entityConfig.getName(), e1.getMessage());
				throw new DataSetConfigurationException(msg);
			}
		}
		return fields;
	}

	private IFieldFactory getFieldFactory(String entity) throws DataSetConfigurationException
	{
		IFieldFactory ff;
		if (entityFieldFactories == null || ((ff = entityFieldFactories.get(entity)) == null))
			ff = defaultFieldFactory;
		if (ff == null) {
			String msg = format(ERR_NO_FIELD_FACTORY, entity);
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
			if (e.getMessage() == null) {
				throw new DataSetConfigurationException(e);
			}
			if (e.getMessage().indexOf(
					"unexpected element (uri:\"http://data.naturalis.nl/nba-dataset-config\", "
							+ "local:\"dataset-config\")") != -1) {
				String msg = "Your friendly programmer has re-generated the JAXB "
						+ "classes from dataset-config.xsd, but forgot to edit the "
						+ "DataSetXmlConfig class afterwards. See instructions in "
						+ "dataset-config.xsd in src/main/resources";
				throw new DataSetConfigurationException(msg);
			}
			String fmt = "Error while parsing configuration file: %s";
			String msg = format(fmt, e.getMessage());
			throw new DataSetConfigurationException(msg);
		}
	}

}
