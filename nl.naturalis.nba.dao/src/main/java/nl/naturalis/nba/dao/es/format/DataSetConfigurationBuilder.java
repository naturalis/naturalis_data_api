package nl.naturalis.nba.dao.es.format;

import static nl.naturalis.nba.common.json.JsonUtil.deserialize;
import static org.domainobject.util.FileUtil.newFile;
import static org.domainobject.util.FileUtil.newFileInputStream;

import java.io.File;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.query.QuerySpec;

/**
 * A builder for {@link DataSetConfiguration} instances.
 * 
 * @author Ayco Holleman
 *
 */
public class DataSetConfigurationBuilder {

	private static Logger logger = LogManager.getLogger(DataSetConfigurationBuilder.class);

	private String name;
	private File root;
	private IDataSetFieldFactory fieldFactory;

	/**
	 * Sets the name of the data set.
	 */
	public DataSetConfigurationBuilder dataSetName(String name)
	{
		this.name = name;
		return this;
	}

	/**
	 * Sets the root directory for the directory for the data set. This usually
	 * is the "grandparent" directory of the data set's
	 * {@link DataSetConfiguration#getHome() home directory}, but it could be
	 * the parent directory. See
	 * {@link DataSetCollectionConfigurationBuilder#forDataSet(String, File)
	 * here} for a detailed explanation.
	 */
	public DataSetConfigurationBuilder rootDirectory(File rootDir)
	{
		this.root = rootDir;
		return this;
	}

	/**
	 * Sets an {@link IDataSetFieldFactory} implementation to use when
	 * configuring the data set's {@link EntityConfiguration entities}.
	 */
	public DataSetConfigurationBuilder fieldFactory(IDataSetFieldFactory fieldFactory)
	{
		this.fieldFactory = fieldFactory;
		return this;
	}

	public DataSetConfiguration build() throws DataSetConfigurationException
	{
		// TODO: check instance vars not null
		DataSetCollectionConfigurationBuilder dsccfb;
		dsccfb = DataSetCollectionConfigurationBuilder.forDataSet(name, root);
		DataSetCollectionConfiguration dscc;
		dscc = dsccfb.build(fieldFactory);
		DataSetConfiguration dataSetConfig = new DataSetConfiguration();
		dataSetConfig.setCollectionConfiguration(dscc);
		File home;
		if (dscc.getName().equals(name)) {
			/* Then we have a collection with just one data set */
			home = dscc.getHome();
		}
		else {
			/*
			 * The data set's home directory is a subdirectory of the
			 * collection's home directory
			 */
			home = newFile(dscc.getHome(), name);
		}
		dataSetConfig.setHome(home);
		dataSetConfig.setQuerySpec(getQuerySpec(home));
		return dataSetConfig;
	}

	private QuerySpec getQuerySpec(File home)
	{
		File f = newFile(home, "queryspec.json");
		if (!f.isFile()) {
			logger.warn("Missing query specification (queryspec.json) for data set {}", name);
			return null;
		}
		InputStream is = newFileInputStream(f);
		return deserialize(is, QuerySpec.class);
	}
}
