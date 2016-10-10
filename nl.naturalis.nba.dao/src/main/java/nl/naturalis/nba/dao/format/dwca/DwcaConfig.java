package nl.naturalis.nba.dao.format.dwca;

import java.io.File;
import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.domainobject.util.ConfigObject;
import org.domainobject.util.ConfigObject.MissingPropertyException;
import org.domainobject.util.ConfigObject.PropertyNotSetException;
import org.domainobject.util.FileUtil;

import nl.naturalis.nba.api.NoSuchDataSetException;
import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.dao.format.DataSet;
import nl.naturalis.nba.dao.format.DataSetBuilder;
import nl.naturalis.nba.dao.format.DataSetConfigurationException;
import nl.naturalis.nba.dao.format.Entity;
import nl.naturalis.nba.dao.format.csv.CsvFieldFactory;

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

	public static String CONF_FILE_EXTENSION = ".dataset-config.xml";

	private static ConfigObject dwcaConfig = ConfigObject.forResource("/dwca.properties");
	private static Logger logger = LogManager.getLogger(DwcaConfig.class);

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
		logger.info("Loading configuration for data set \"{}\")", dataSetName);
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
	 * specified in the XML configuration file.
	 * 
	 * @param out
	 * @return
	 */
	public IDwcaWriter getWriter(OutputStream out)
	{
		if (dataSet.getSharedDataSource() == null) {
			return new MultiDataSourceDwcaWriter(this, out);
		}
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
		String fileName = dataSetName + CONF_FILE_EXTENSION;
		File confFile = FileUtil.newFile(getHome(), fileName);
		logger.info("Configuration file: {}", confFile.getPath());
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
