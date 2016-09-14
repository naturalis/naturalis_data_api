package nl.naturalis.nba.dao.es.format.dwca;

import java.io.File;

import org.domainobject.util.ConfigObject;
import org.domainobject.util.ConfigObject.MissingPropertyException;
import org.domainobject.util.ConfigObject.PropertyNotSetException;
import org.domainobject.util.FileUtil;

import nl.naturalis.nba.api.NoSuchDataSetException;
import nl.naturalis.nba.dao.es.DaoRegistry;
import nl.naturalis.nba.dao.es.format.DataSet;
import nl.naturalis.nba.dao.es.format.DataSetBuilder;
import nl.naturalis.nba.dao.es.format.DataSetConfigurationException;
import nl.naturalis.nba.dao.es.format.Entity;
import nl.naturalis.nba.dao.es.format.csv.CsvFieldFactory;

public class DwcaConfig {

	private static String CONF_FILE_EXTENSION = ".dataset-config.xml";
	private static ConfigObject dwcaConfig = ConfigObject.forResource("/dwca.properties");

	private String dataSetName;
	private DwcaDataSetType dataSetType;
	private DataSet dataSet;
	private ConfigObject myConfig;

	public DwcaConfig(String dataSetName, DwcaDataSetType dataSetType)
			throws DataSetConfigurationException, NoSuchDataSetException
	{
		this.dataSetName = dataSetName;
		this.dataSetType = dataSetType;
		this.myConfig = dwcaConfig.getSection(dataSetType.name().toLowerCase());
		this.dataSet = buildDataSet();
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
			throw new DataSetConfigurationException(e.getMessage());
		}
	}

	String getRowtype(Entity entity) throws DataSetConfigurationException
	{
		String property = "entity." + entity.getName() + ".rowtype";
		try {
			return myConfig.required(property);
		}
		catch (PropertyNotSetException | MissingPropertyException e) {
			throw new DataSetConfigurationException(e.getMessage());
		}
	}

	Entity getCoreEntity() throws DataSetConfigurationException
	{
		return dataSet.getEntity(dataSetType.name());
	}

	private DataSet buildDataSet() throws DataSetConfigurationException, NoSuchDataSetException
	{
		String fileName = dataSetName + CONF_FILE_EXTENSION;
		File confFile = FileUtil.newFile(getHome(), fileName);
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
