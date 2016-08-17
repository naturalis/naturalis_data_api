package nl.naturalis.nba.dao.es.format.dwca;

import static nl.naturalis.nba.common.json.JsonUtil.deserialize;
import static org.domainobject.util.FileUtil.getSubdirectories;
import static org.domainobject.util.FileUtil.newFile;
import static org.domainobject.util.FileUtil.newFileInputStream;
import static org.domainobject.util.StringUtil.rchop;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.dao.es.DaoRegistry;
import nl.naturalis.nba.dao.es.DocumentType;
import nl.naturalis.nba.dao.es.exception.DwcaCreationException;
import nl.naturalis.nba.dao.es.format.DataSetCollection;
import nl.naturalis.nba.dao.es.format.DataSetEntity;
import nl.naturalis.nba.dao.es.format.FieldConfigurator;
import nl.naturalis.nba.dao.es.format.IDataSetField;
import nl.naturalis.nba.dao.es.format.csv.CsvFieldFactory;

public class DwcaDataSetCollectionBuilder {

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
		File home = newFile(getDocumentTypeDirectory(),collectionName);
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
		for (File collDir : getSubdirectories(docTypeDir)) {
			if (collDir.getName().equals(dataSetName)) {
				//Then we have a collection with just one data set.			
				return dataSetName;
			}
			for (File setDir : getSubdirectories(collDir)) {
				if (setDir.getName().equals(dataSetName)) {
					return collDir.getName();
				}
			}
		}
		String fmt = "Directory for data set \"%s\" found under %s";
		String msg = String.format(fmt, dataSetName, docTypeDir.getPath());
		throw new DwcaCreationException(msg);
	}

	private DataSetEntity[] getEntities(File homeDir)
	{
		File[] configs = getFieldConfigFiles(homeDir);
		DataSetEntity[] entities = new DataSetEntity[configs.length];
		for (int i = 0; i < configs.length; i++) {
			File config = configs[i];
			String entityName = rchop(config.getName(), ".config");
			DataSetEntity entity = new DataSetEntity(entityName);
			FieldConfigurator fc = new FieldConfigurator(dt, new CsvFieldFactory());
			IDataSetField[] fields = fc.getFields(config);
			entity.setFields(fields);
			File f = newFile(homeDir, entityName + ".queryspec.json");
			if (f.isFile()) {
				InputStream is = newFileInputStream(f);
				QuerySpec qs = deserialize(is, QuerySpec.class);
				entity.setQuerySpec(qs);
			}
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

	private static File[] getFieldConfigFiles(File dir)
	{
		return dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				if (!f.isFile())
					return false;
				String s = f.getName();
				return s.endsWith(".config") && !s.equals("fields.config");
			}
		});
	}

	private static DwcaCreationException directoryNotFound(File f)
	{
		return new DwcaCreationException("Directory not found: " + f.getPath());
	}
}
