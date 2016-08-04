package nl.naturalis.nba.dao.es.format.dwca;

import static nl.naturalis.nba.dao.es.DocumentType.SPECIMEN;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.domainobject.util.FileUtil;

import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.common.json.JsonDeserializationException;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.es.DAORegistry;
import nl.naturalis.nba.dao.es.exception.DwcaCreationException;
import nl.naturalis.nba.dao.es.format.DataSetCollection;
import nl.naturalis.nba.dao.es.format.FieldConfigurator;
import nl.naturalis.nba.dao.es.format.IDataSetField;
import nl.naturalis.nba.dao.es.format.IDataSetFieldFactory;
import nl.naturalis.nba.dao.es.format.csv.CsvFieldFactory;

/**
 * Utility class for the DwCA generation process.
 * 
 * @author Ayco Holleman
 *
 */
public class DwcaUtil {

	private DwcaUtil()
	{
	}

	/**
	 * Returns an array if {@link IDataSetField} instances to be used to write
	 * the CSV file contained within the DwC archive.
	 * 
	 * @param dsc
	 * @return
	 */
	public static IDataSetField[] getFields(DataSetCollection dsc)
	{
		File confFile = getFieldsConfigFile(dsc);
		IDataSetFieldFactory fieldFactory = new CsvFieldFactory();
		FieldConfigurator configurator = new FieldConfigurator(dsc, fieldFactory);
		return configurator.getFields(confFile);
	}

	/**
	 * Returns the fields&#46;config file for the specified collection of data
	 * sets. This file must be named "fields.config" and it must reside in the
	 * top directory for the data set collection.
	 * 
	 * @see #getDataSetCollectionDirectory(DataSetCollection)
	 * 
	 * @param documentType
	 * @param setType
	 * @return
	 */
	public static File getFieldsConfigFile(DataSetCollection dsc)
	{
		File dir = getDataSetCollectionDirectory(dsc);
		File f = FileUtil.newFile(dir, "fields.config");
		if (!f.isFile())
			throw fileNotFound(f);
		return f;
	}

	/**
	 * Returns the object that will generate the meta&#46;.xml file for the DwC
	 * archive.
	 * 
	 * @param dsc
	 * @param fields
	 * @return
	 */
	public static MetaXmlGenerator getMetaXmlGenerator(DataSetCollection dsc,
			IDataSetField[] fields)
	{
		if (dsc.getDocumentType() == SPECIMEN)
			return new OccurrenceMetaXmlGenerator(fields);
		String fmt = "Cannot generate meta.xml for %s";
		String msg = String.format(fmt, dsc.getDocumentType());
		throw new DwcaCreationException(msg);
	}

	/**
	 * Returns the name of the CSV file contained within the DwC archive. For
	 * specimens the name is "occurence.txt". For taxa it is "taxa.txt".
	 * 
	 * @param dsc
	 * @return
	 */
	public static String getCsvFileName(DataSetCollection dsc)
	{
		if (dsc.getDocumentType() == SPECIMEN)
			return "occurrence.txt";
		String fmt = "Cannot determine CSV file name for %s";
		String msg = String.format(fmt, dsc.getDocumentType());
		throw new DwcaCreationException(msg);
	}

	/**
	 * Returns the eml.xml file for the specified data set. This file must
	 * reside in the {@link #getDatasetDirectory(DataSetCollection, String)
	 * directory} for the specified data set.
	 * 
	 * @param documentType
	 * @param setType
	 * @param setName
	 * @return
	 */
	public static File getEmlFile(DataSetCollection dsc, String setName)
	{
		File dir = getDatasetDirectory(dsc, setName);
		File emlFile = FileUtil.newFile(dir, "eml.xml");
		if (!emlFile.isFile())
			throw fileNotFound(emlFile);
		return emlFile;
	}

	/**
	 * Returns a {@link QuerySpec} instance for the specified data set. This
	 * instance is created through the deserialization of the contents of a file
	 * name queryspec.json. This file must reside in the
	 * {@link #getDatasetDirectory(DataSetCollection, String) directory} for the
	 * specified data set.
	 * 
	 * @param documentType
	 * @param setType
	 * @param setName
	 * @return
	 */
	public static QuerySpec getQuerySpec(DataSetCollection dsc, String setName)
	{
		File dir = getDatasetDirectory(dsc, setName);
		File f = FileUtil.newFile(dir, "queryspec.json");
		if (!f.isFile())
			throw fileNotFound(f);
		byte[] data = FileUtil.getByteContents(f.getAbsolutePath());
		try {
			return JsonUtil.deserialize(data, QuerySpec.class);
		}
		catch (JsonDeserializationException e) {
			String msg = "Invalid JSON in file " + f.getPath();
			throw new DwcaCreationException(msg);
		}
	}

	/**
	 * Returns the directory for the specified data set. This directory is a
	 * subdirectory of the directory for the specified data set collection.
	 * 
	 * 
	 * @param dsc
	 * @param setName
	 * @return
	 */
	public static File getDatasetDirectory(DataSetCollection dsc, String setName)
	{
		File dir = getDataSetCollectionDirectory(dsc);
		if (setName == null)
			return dir;
		File f = FileUtil.newFile(dir, setName);
		if (!f.isDirectory())
			throw directoryNotFound(f);
		return f;
	}

	/**
	 * Returns the top directory for a collection of data sets sharing the same
	 * Elasticsearch document type and the same field configuration for the CSV
	 * payload.
	 * 
	 * @param dsc
	 * @return
	 */
	public static File getDataSetCollectionDirectory(DataSetCollection dsc)
	{
		File nbaConfDir = DAORegistry.getInstance().getConfigurationDirectory();
		String docType = dsc.getDocumentType().toString().toLowerCase();
		Path path = Paths.get(nbaConfDir.getPath(), "dwca", docType, dsc.getName());
		File f = path.toFile();
		if (!f.isDirectory())
			throw directoryNotFound(f);
		return f;
	}

	private static DwcaCreationException fileNotFound(File f)
	{
		return new DwcaCreationException("File not found: " + f.getPath());
	}

	private static DwcaCreationException directoryNotFound(File f)
	{
		return new DwcaCreationException("Directory not found: " + f.getPath());
	}

}
