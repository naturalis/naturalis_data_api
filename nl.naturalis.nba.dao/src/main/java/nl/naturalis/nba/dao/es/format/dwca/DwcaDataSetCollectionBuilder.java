package nl.naturalis.nba.dao.es.format.dwca;

import static org.domainobject.util.FileUtil.getSubdirectories;
import static org.domainobject.util.FileUtil.newFile;
import static org.domainobject.util.StringUtil.rchop;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Path;
import java.nio.file.Paths;

import nl.naturalis.nba.dao.es.DaoRegistry;
import nl.naturalis.nba.dao.es.DocumentType;
import nl.naturalis.nba.dao.es.exception.DwcaCreationException;
import nl.naturalis.nba.dao.es.format.DataSetCollection;
import nl.naturalis.nba.dao.es.format.DataSetEntity;
import nl.naturalis.nba.dao.es.format.EntityConfiguration;
import nl.naturalis.nba.dao.es.format.EntityConfigurationParser;
import nl.naturalis.nba.dao.es.format.NoSuchDataSetException;
import nl.naturalis.nba.dao.es.format.csv.CsvFieldFactory;

public class DwcaDataSetCollectionBuilder {

	private static String ENTITY_CONFIG_EXTENSION = ".entity.config";

	private DocumentType<?> dt;
	private String dataSetName;

	public DwcaDataSetCollectionBuilder(DocumentType<?> dt, String dataSetName)
	{
		this.dt = dt;
		this.dataSetName = dataSetName;
	}

	public DataSetCollection build()
	{
		String collectionName = getName();
		File home = newFile(getDocumentTypeDirectory(), collectionName);
		DataSetEntity[] entities = getEntities(home);
		DataSetCollection dsc = new DataSetCollection();
		dsc.setDocumentType(dt);
		dsc.setName(collectionName);
		dsc.setHome(home);
		dsc.setEntities(entities);
		return dsc;
	}

	private String getName()
	{
		File docTypeDir = getDocumentTypeDirectory();
		String name = null;
		for (File collDir : getSubdirectories(docTypeDir)) {
			if (collDir.getName().equals(dataSetName)) {
				//Then we have a collection with just one data set
				if (name != null) {
					throw duplicateDataSet();
				}
				name = dataSetName;
			}
			for (File setDir : getSubdirectories(collDir)) {
				if (setDir.getName().equals(dataSetName)) {
					if (name != null) {
						throw duplicateDataSet();
					}
					name = collDir.getName();
				}
			}
		}
		if (name == null) {
			throw noSuchDataSet();
		}
		return name;
	}

	private DataSetEntity[] getEntities(File homeDir)
	{
		File[] cfgFiles = getEntityConfigFiles(homeDir);
		DataSetEntity[] entities = new DataSetEntity[cfgFiles.length];
		for (int i = 0; i < cfgFiles.length; i++) {
			File cfgFile = cfgFiles[i];
			String entityName = rchop(cfgFile.getName(), ENTITY_CONFIG_EXTENSION);
			DataSetEntity entity = new DataSetEntity(entityName);
			EntityConfigurationParser ec = new EntityConfigurationParser(cfgFile, new CsvFieldFactory());
			EntityConfiguration conf = ec.configure(dt);
			entity.setPathToEntity(conf.getPathToEntity());
			entity.setFields(conf.getFields());
			entities[i] = entity;
		}
		return entities;
	}

	private File getDocumentTypeDirectory()
	{
		File nbaConfDir = DaoRegistry.getInstance().getConfigurationDirectory();
		String docType = dt.toString().toLowerCase();
		Path path = Paths.get(nbaConfDir.getPath(), "dwca", docType);
		File f = path.toFile();
		if (!f.isDirectory())
			throw directoryNotFound(f);
		return f;
	}

	private DwcaCreationException duplicateDataSet()
	{
		File dir = getDocumentTypeDirectory();
		String fmt = "Duplicate data set \"%s\" found under %s";
		String msg = String.format(fmt, dataSetName, dir.getPath());
		return new DwcaCreationException(msg);
	}

	private NoSuchDataSetException noSuchDataSet()
	{
		File dir = getDocumentTypeDirectory();
		String fmt = "Directory for data set \"%s\" found under %s";
		String msg = String.format(fmt, dataSetName, dir.getPath());
		return new NoSuchDataSetException(msg);
	}

	private static DwcaCreationException directoryNotFound(File f)
	{
		return new DwcaCreationException("Directory not found: " + f.getPath());
	}

	private static File[] getEntityConfigFiles(File dir)
	{
		return dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				if (!f.isFile())
					return false;
				return f.getName().endsWith(ENTITY_CONFIG_EXTENSION);
			}
		});
	}

}
