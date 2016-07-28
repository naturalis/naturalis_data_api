package nl.naturalis.nba.dao.es.format.dwca;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.domainobject.util.FileUtil;

import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.common.json.JsonDeserializationException;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.es.DAORegistry;
import nl.naturalis.nba.dao.es.DocumentType;
import nl.naturalis.nba.dao.es.exception.DwcaCreationException;
import nl.naturalis.nba.dao.es.format.DataSetCollection;
import nl.naturalis.nba.dao.es.format.FieldConfigurator;
import nl.naturalis.nba.dao.es.format.IDataSetField;
import nl.naturalis.nba.dao.es.format.csv.CsvFieldFactory;

import static nl.naturalis.nba.dao.es.DocumentType.*;

public class DwcaUtil {

	private DwcaUtil()
	{
	}

	public static IDataSetField[] getFields(DataSetCollection dsc)
	{
		File confFile = getFieldsConfigFile(dsc);
		FieldConfigurator configurator = new FieldConfigurator(dsc, new CsvFieldFactory());
		return configurator.getFields(confFile);
	}

	/**
	 * Get the fields.config file for the specified document type and the
	 * specified type of DwCA files.
	 * 
	 * @see #getDataSetCollectionHomeDir(DocumentType, String)
	 * 
	 * @param documentType
	 * @param setType
	 * @return
	 */
	public static File getFieldsConfigFile(DataSetCollection dsc)
	{
		File dir = getDataSetCollectionHomeDir(dsc);
		File f = FileUtil.newFile(dir, "fields.config");
		if (!f.isFile())
			throw fileNotFound(f);
		return f;
	}

	public static MetaXmlGenerator getMetaXmlGenerator(DataSetCollection dsc,
			IDataSetField[] fields)
	{
		if (dsc.getDocumentType() == SPECIMEN)
			return new OccurrenceMetaXmlGenerator(fields);
		String fmt = "Cannot generate meta.xml for %s";
		String msg = String.format(fmt, dsc.getDocumentType());
		throw new DwcaCreationException(msg);
	}

	public static String getCsvFileName(DataSetCollection dsc)
	{
		if (dsc.getDocumentType() == SPECIMEN)
			return "occurrence.txt";
		String fmt = "Cannot determine CSV file name for %s";
		String msg = String.format(fmt, dsc.getDocumentType());
		throw new DwcaCreationException(msg);
	}

	/**
	 * Returns the eml.xml file for the specified data set.
	 * 
	 * @param documentType
	 * @param setType
	 * @param setName
	 * @return
	 */
	public static File getEmlFile(DataSetCollection dsc, String setName)
	{
		File dir = getDatasetDir(dsc, setName);
		File emlFile = FileUtil.newFile(dir, "eml.xml");
		if (!emlFile.isFile())
			throw fileNotFound(emlFile);
		return emlFile;
	}

	/**
	 * Returns a {@link QuerySpec} for the specified data set.
	 * 
	 * @param documentType
	 * @param setType
	 * @param setName
	 * @return
	 */
	public static QuerySpec getQuerySpec(DataSetCollection dsc, String setName)
	{
		File dir = getDatasetDir(dsc, setName);
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

	public static File getDatasetDir(DataSetCollection dsc, String setName)
	{
		File dir = getDataSetCollectionHomeDir(dsc);
		if (setName == null)
			return dir;
		File f = FileUtil.newFile(dir, setName);
		if (!f.isDirectory())
			throw directoryNotFound(f);
		return f;
	}

	public static File getDataSetCollectionHomeDir(DataSetCollection dsc)
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
