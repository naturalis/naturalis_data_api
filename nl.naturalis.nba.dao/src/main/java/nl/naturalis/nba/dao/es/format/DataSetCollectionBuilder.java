package nl.naturalis.nba.dao.es.format;

import static org.domainobject.util.StringUtil.rchop;

import java.io.File;
import java.io.FileFilter;

import nl.naturalis.nba.dao.es.DocumentType;
import nl.naturalis.nba.dao.es.format.csv.CsvFieldFactory;

public class DataSetCollectionBuilder {

	static final String ENTITY_CONFIG_EXTENSION = ".entity.config";

	private File home;
	private String name;

	public DataSetCollectionBuilder(String name, File home)
	{
		this.home = home;
		this.name = name;
	}

	public DataSetCollectionBuilder(File home)
	{
		this.home = home;
		this.name = home.getName();
	}

	public DataSetCollection build(IDataSetFieldFactory fieldFactory)
	{
		DataSetCollection dsc = new DataSetCollection();
		dsc.setName(name);
		return null;
	}

	private DataSetEntity[] getEntities(DocumentType<?> documentType, IDataSetFieldFactory fieldFactory)
	{
		File[] cfgFiles = getEntityConfigFiles();
		DataSetEntity[] entities = new DataSetEntity[cfgFiles.length];
		for (int i = 0; i < cfgFiles.length; i++) {
			File cfgFile = cfgFiles[i];
			String entityName = rchop(cfgFile.getName(), ENTITY_CONFIG_EXTENSION);
			DataSetEntity entity = new DataSetEntity(entityName);
			
			EntityConfigurationParser ec = new EntityConfigurationParser(cfgFile,
					new CsvFieldFactory());
			EntityConfiguration conf = ec.configure(dt);
			entity.setPathToEntity(conf.getPathToEntity());
			entity.setFields(conf.getFields());
			entities[i] = entity;
		}
		return entities;
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
}
