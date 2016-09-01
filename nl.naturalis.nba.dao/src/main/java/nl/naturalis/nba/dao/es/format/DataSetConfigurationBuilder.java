package nl.naturalis.nba.dao.es.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.domainobject.util.IOUtil;

import nl.naturalis.nba.dao.es.format.config.DataSetCollectionConfig;
import nl.naturalis.nba.dao.es.format.config.DataSetConfig;

/**
 * A builder for {@link DataSetConfiguration} instances.
 * 
 * @author Ayco Holleman
 *
 */
public class DataSetConfigurationBuilder {

	private static Logger logger = LogManager.getLogger(DataSetConfigurationBuilder.class);

	private String name;
	private File configFile;
	private IDataSetFieldFactory fieldFactory;

	/**
	 * Sets the name of the data set.
	 */
	public DataSetConfigurationBuilder(String name, File configFile,
			IDataSetFieldFactory fieldFactory)
	{
		this.name = name;
		this.configFile = configFile;
		this.fieldFactory = fieldFactory;
	}

	public void build() throws DataSetConfigurationException
	{
		if (!configFile.isFile()) {
			String msg = "Missing configuration file " + configFile.getPath();
			throw new DataSetConfigurationException(msg);
		}
		JAXBContext ctx;
		try {
			ctx = JAXBContext.newInstance(DataSetCollectionConfig.class);
			Unmarshaller unmarshaller = ctx.createUnmarshaller();
			DataSetCollectionConfig xmlConfig;
			xmlConfig = (DataSetCollectionConfig) unmarshaller.unmarshal(configFile);
			DataSetConfig xmlDataSet = null;
			for (DataSetConfig dsc : xmlConfig.getDataset()) {
				if (dsc.getName().equals(name)) {
					if (xmlDataSet != null)
						throw duplicateDataSet();
					xmlDataSet = dsc;
					break;
				}
			}
			if (xmlDataSet == null)
				throw noSuchDataSet();
			
		}
		catch (JAXBException e) {
			throw new DataSetConfigurationException(e);
		}
	}

	private DataSetConfigurationException duplicateDataSet()
	{
		String fmt = "Duplicate data set \"%s\" found in %s";
		String msg = String.format(fmt, name, configFile.getPath());
		return new DataSetConfigurationException(msg);
	}

	private NoSuchDataSetException noSuchDataSet()
	{
		String fmt = "Missing configuration for data set \"%s\" in %s";
		String msg = String.format(fmt, name, configFile.getPath());
		return new NoSuchDataSetException(msg);
	}
}
