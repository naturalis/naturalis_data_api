package nl.naturalis.nba.dao.format.dwca;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;

import java.io.File;
import java.io.OutputStream;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.NoSuchDataSetException;
import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.dao.format.DataSet;
import nl.naturalis.nba.dao.format.DataSetBuilder;
import nl.naturalis.nba.dao.format.DataSetConfigurationException;
import nl.naturalis.nba.dao.format.Entity;
import nl.naturalis.nba.dao.format.csv.CsvFieldFactory;
import nl.naturalis.nba.utils.ConfigObject;
import nl.naturalis.nba.utils.FileUtil;
import nl.naturalis.nba.utils.ConfigObject.MissingPropertyException;
import nl.naturalis.nba.utils.ConfigObject.PropertyNotSetException;

/**
 * Captures the information in the XML configuration file for a DarwinCore
 * archive. A {@code DwcaConfig} is used by {@link IDwcaWriter} instances to
 * drive the generation of DarwinCore archives. At the same time, the
 * {@code DwcaConfig} class functions as a factory for {@code IDwcaWriter}
 * instances since different configurations require different implementations of
 * the {@code IDwcaWriter} interface.
 * 
 * @author Ayco Holleman
 *
 */
public class DwcaConfig {

	private static final Logger logger = getLogger(DwcaConfig.class);

	/**
	 * The file extension of configuration files driving the generation of data
	 * sets. (&#34;.dataset-config.xml&#34;).
	 */
	public static String CONF_FILE_EXTENSION = ".dataset-config.xml";

	private static ConfigObject dwcaConfig = ConfigObject.forResource("/dwca.properties");

	/**
	 * Returns a {@code DwcaConfig} instance for the generation of DwCA files
	 * from &#34;live queries&#34;. Note that these, too, require a
	 * configuration file just like the configuration files for predefined data
	 * sets. The only difference is that the &lt;data-source&gt; c.q.
	 * &lt;shared-data-source&gt; element is ignored.
	 * 
	 * @param dataSetType
	 * @return
	 * @throws DataSetConfigurationException
	 */
	public static DwcaConfig getDynamicDwcaConfig(DwcaDataSetType dataSetType)
			throws DataSetConfigurationException
	{
		try {
			return new DwcaConfig("dynamic", dataSetType);
		}
		catch (NoSuchDataSetException e) {
			String msg = "Missing configuration file dynamic" + CONF_FILE_EXTENSION;
			throw new DataSetConfigurationException(msg);
		}
	}

	private String dataSetName;
	private DwcaDataSetType dataSetType;
	private DataSet dataSet;
	private ConfigObject myConfig;

	public DwcaConfig(String dataSetName, DwcaDataSetType dataSetType)
			throws DataSetConfigurationException, NoSuchDataSetException
	{
		String type = dataSetType.name().toLowerCase();
		logger.info("Loading configuration for data set \"{}\"", dataSetName);
		this.dataSetName = dataSetName;
		this.dataSetType = dataSetType;
		this.myConfig = dwcaConfig.getSection(type);
		if (myConfig == null) {
			String fmt = "Missing section \"%s\" in dwca.properties";
			String msg = String.format(fmt, type);
			throw new DataSetConfigurationException(msg);
		}
		this.dataSet = buildDataSet();
	}

	/**
	 * Returns a DarwinCore archive writer tailored to the requirements
	 * specified in the XML configuration file. Notably, if the configuration
	 * file specified a &lt;shared-data-source&gt; you wil get a writer that
	 * generates all CSV files while iterating just once over an Elasticsearch
	 * result set while otherwise you will get a writer that executes a new
	 * query for each CSV file.
	 * 
	 * @param out
	 * @return
	 */
	public IDwcaWriter getWriter(OutputStream out)
	{
		if (dataSet.getSharedDataSource() == null) {
			logger.info("Creating DwCA writer for per-entity data source setup");
			return new MultiDataSourceDwcaWriter(this, out);
		}
		logger.info("Creating DwCA writer for shared data source setup");
		return new SingleDataSourceDwcaWriter(this, out);
	}

	String getDataSetName()
	{
		return dataSetName;
	}

	DwcaDataSetType getDataSetType()
	{
		return dataSetType;
	}

	DataSet getDataSet()
	{
		return dataSet;
	}

	File getEmlFile() throws DataSetConfigurationException
	{
		String emlFile = dataSetName + "/eml.xml";
		File f = FileUtil.newFile(getHome(), emlFile);
		if (!f.isFile()) {
			String msg = "Missing eml.xml for data set " + dataSetName;
			throw new DataSetConfigurationException(msg);
		}
		return f;
	}

	String getCsvFileName(Entity entity) throws DataSetConfigurationException
	{
		String property = "entity." + entity.getName() + ".location";
		try {
			return myConfig.required(property);
		}
		catch (PropertyNotSetException | MissingPropertyException e) {
			String fmt = "%s (check dwca.properties)";
			String msg = String.format(fmt, e.getMessage());
			throw new DataSetConfigurationException(msg);
		}
	}

	String getRowtype(Entity entity) throws DataSetConfigurationException
	{
		String property = "entity." + entity.getName() + ".rowtype";
		try {
			return myConfig.required(property);
		}
		catch (PropertyNotSetException | MissingPropertyException e) {
			String fmt = "%s (check dwca.properties)";
			String msg = String.format(fmt, e.getMessage());
			throw new DataSetConfigurationException(msg);
		}
	}

	Entity getCoreEntity() throws DataSetConfigurationException
	{
		return dataSet.getEntity(dataSetType.name().toLowerCase());
	}

	private DataSet buildDataSet() throws DataSetConfigurationException, NoSuchDataSetException
	{
		File confDir = getHome();
		String fileName = dataSetName + CONF_FILE_EXTENSION;
		File confFile = FileUtil.newFile(confDir, fileName);
		logger.info("Searching for {} in {}", fileName, confDir.getPath());
		if (!confFile.isFile()) {
			throw new NoSuchDataSetException(dataSetName);
		}
		DataSetBuilder dsb = new DataSetBuilder(confFile);
		dsb.setDefaultFieldFactory(new CsvFieldFactory());
		return dsb.build();
	}

	private File getHome()
	{
		File root = DaoRegistry.getInstance().getConfigurationDirectory();
		String subdir = "dwca/" + dataSetType.name().toLowerCase();
		return FileUtil.newFile(root, subdir);
	}

}
