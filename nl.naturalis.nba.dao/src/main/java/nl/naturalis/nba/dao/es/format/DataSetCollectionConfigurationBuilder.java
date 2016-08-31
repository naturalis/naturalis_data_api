package nl.naturalis.nba.dao.es.format;

import static org.domainobject.util.FileUtil.getSubdirectories;
import static org.domainobject.util.StringUtil.rchop;

import java.io.File;
import java.io.FileFilter;

/**
 * A builder for {@link DataSetCollectionConfiguration} instances.
 * 
 * @author Ayco Holleman
 *
 */
public class DataSetCollectionConfigurationBuilder {

	static final String ENTITY_CONFIG_EXTENSION = ".entity.config";

	/**
	 * Creates a {@link DataSetCollectionConfigurationBuilder} for the specified
	 * data set. The {@code rootDir} argument must be the parent directory of
	 * collection's {@link DataSetCollectionConfiguration#getHome() home
	 * directory}. So for the file system make-up shown below, if you wanted to
	 * get hold of a {@code DataSetCollection} instance for the lepidoptera data
	 * set, you would pass the string "lepidoptera" and a {@link File} instance
	 * corresponding to the specimen directory. This works because data set
	 * names must be unique across <i>all</i> collections. It would, for
	 * example, be illegal to have a "utrecht" data set in both the zoology
	 * collection and the botany collection. Thus, knowing just the name of the
	 * data set and the file system root for all collections is enough to figure
	 * out the collection that contains the data set. By being able to look up
	 * the collection from the data set, we can keep URLs for our predefined
	 * data sets simple. Users only need to remember the name of the data set.
	 * e.g. http://api.biodiversitydata.nl/specimen/dwca/lepidoptera in stead of
	 * http://api.biodiversitydata.nl/specimen/dwca/zoology/lepidoptera.<br>
	 * <br>
	 * <code>
	 * specimen
	 *    |____zoology
	 *    |       |____mollusca
	 *    |       |____mammalia
	 *    |       |____lepidoptera
	 *    |____botany
	 *            |____leiden
	 *            |____wageningen  
	 *            |____utrecht          
	 * </code><br>
	 * <br>
	 * Another factor determining the lookup mechanism is that you can have
	 * collections with just one data set. In that case, it is allowed to merge
	 * the collection's home directory and the data set's home directory. In
	 * other words, collection directory and data set directory are one and the
	 * same, and this one directory will contain both collection-specific
	 * artefacts (the entity configuration files) and dataset-specific artefacts
	 * (e.g. eml.xml for DwC-A data sets).
	 * 
	 * @throws DataSetConfigurationException
	 */
	public static DataSetCollectionConfigurationBuilder forDataSet(String dataSetName, File rootDir)
			throws DataSetConfigurationException
	{
		File home = null;
		for (File collDir : getSubdirectories(rootDir)) {
			if (collDir.getName().equals(dataSetName)) {
				// Then we have a collection with just one data set
				if (home != null) {
					throw duplicateDataSet(dataSetName, rootDir);
				}
				home = collDir;
			}
			for (File setDir : getSubdirectories(collDir)) {
				if (setDir.getName().equals(dataSetName)) {
					if (home != null) {
						throw duplicateDataSet(dataSetName, rootDir);
					}
					home = collDir;
				}
			}
		}
		if (home == null) {
			throw noSuchDataSet(dataSetName, rootDir);
		}
		return new DataSetCollectionConfigurationBuilder(home);
	}

	private String name;
	private File home;

	public DataSetCollectionConfigurationBuilder(String name, File home)
	{
		this.name = name;
		this.home = home;
	}

	public DataSetCollectionConfigurationBuilder(File home)
	{
		this.name = home.getName();
		this.home = home;
	}

	public DataSetCollectionConfiguration build(IDataSetFieldFactory fieldFactory)
			throws EntityConfigurationException
	{
		DataSetCollectionConfiguration dsc = new DataSetCollectionConfiguration();
		dsc.setName(name);
		parseEntityConfigFiles(dsc, fieldFactory);
		return dsc;
	}

	private void parseEntityConfigFiles(DataSetCollectionConfiguration dsc,
			IDataSetFieldFactory fieldFactory) throws EntityConfigurationException
	{
		File[] configFiles = getEntityConfigFiles();
		EntityConfiguration[] entities = new EntityConfiguration[configFiles.length];
		for (int i = 0; i < configFiles.length; i++) {
			File file = configFiles[i];
			String entityName = rchop(file.getName(), ENTITY_CONFIG_EXTENSION);
			EntityConfiguration entity = new EntityConfiguration(entityName);
			SettingsParser sp = new SettingsParser(file);
			sp.parse(entity);
			FieldsParser fp = new FieldsParser(file);
			fp.parse(entity, fieldFactory);
			entities[i] = entity;
		}
		dsc.setEntities(entities);
	}

	private File[] getEntityConfigFiles()
	{
		return home.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				if (!f.isFile())
					return false;
				return f.getName().endsWith(ENTITY_CONFIG_EXTENSION);
			}
		});
	}

	private static DataSetConfigurationException duplicateDataSet(String dataSet, File root)
	{
		String fmt = "Duplicate data set \"%s\" found under %s";
		String msg = String.format(fmt, dataSet, root.getPath());
		return new DataSetConfigurationException(msg);
	}

	private static NoSuchDataSetException noSuchDataSet(String dataSet, File root)
	{
		String fmt = "Data set \"%s\" not found under %s";
		String msg = String.format(fmt, dataSet, root.getPath());
		return new NoSuchDataSetException(msg);
	}

}
