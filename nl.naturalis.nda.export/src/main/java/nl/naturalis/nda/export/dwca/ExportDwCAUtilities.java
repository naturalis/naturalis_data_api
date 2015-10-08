package nl.naturalis.nda.export.dwca;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import nl.naturalis.nda.export.ExportException;
import nl.naturalis.nda.export.ExportUtil;

import org.domainobject.util.ConfigObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 * <h1>ExportDwCAUtilities</h1>  
 * Description: String Utilites for the StringBuilder Class to Write data to a CSV file
 * <p>
 * Different method used in the DwCAExporter tool
 * 
 *  @version	 1.0
 *  @author 	 Reinier.Kartowikromo 
 *  @since		 12-02-2015
 *  
 */

public class ExportDwCAUtilities {
	static final Logger logger = LoggerFactory.getLogger(ExportDwCAUtilities.class);
	final static int BUFFER = 2048;
	static String propertiesfilename = null;

    /**
     * searching for a character
     * @param s1 set value1
     * @param s2 set value 2
     * @return boolean value
     */
	public static int indexOfFirstContainedCharacter(String s1, String s2)
	{
		if (s1 == null || s1.isEmpty())
			return -1;
		Set<Character> set = new HashSet<>();
		for (int i = 0; i < s2.length(); i++) {
			set.add(s2.charAt(i)); // Build a constant-time lookup table.
		}
		for (int i = 0; i < s1.length(); i++) {
			if (set.contains(s1.charAt(i))) {
				return i; // Found a character in s1 also in s2.
			}
		}
		return -1; // No matches.
	}

	/**
	 * Zip multiple files 
	 * @param fileName set filename
	 * @param zos Class Zos
	 * @throws FileNotFoundException File exception
	 * @throws IOException IO exception
	 * <p>
	 * Example: String zipfilename = zipFileName + zipExtension;
	 * try
	 * ( FileOutputStream fos = new FileOutputStream(zipfilename); 
	 *   ZipOutputStream zos = new ZipOutputStream(fos))
	 *  {
	 * 		StringUtilities.addToZipFile(FILE_NAME_META, zos);
	 * 		StringUtilities.addToZipFile(FILE_NAME_EML, zos);
	 * 		StringUtilities.addToZipFile(csvOutPutFile, zos);
	 * 	}
     *   System.out.println("Zipfile '" + zipfilename +	 * "' created successful.");
	 */
	public static void addToZipFile(String fileName, ZipOutputStream zos) throws FileNotFoundException, IOException
	{
		System.out.println("Writing '" + fileName + "' to zip file");
		logger.info("Writing '" + fileName + "' to zip file");
		byte data[] = new byte[BUFFER];

		File file = new File(fileName);
		try(FileInputStream fis = new FileInputStream(file);
			BufferedInputStream origin = new BufferedInputStream(fis, BUFFER))
		{
			ZipEntry zipEntry = new ZipEntry(fileName);
			zos.putNextEntry(zipEntry);

			int length;
			while ((length = origin.read(data, 0, BUFFER)) != -1) {
				zos.write(data, 0, length);
				zos.flush();
			}
		}
		zos.closeEntry();
	}


	/**
	 * Read the value from properties file 
	 * @param propertyname set propertyname
	 * @param value set Value
	 * @return property value
	 */
	public static String getPropertyValue(String propertyname, String value)
	{
		return getProperty(propertyname, value);
	}


	/**
	 * Read the value from properties file
	 * @param propertyname set propertyname
	 * @param key set key
	 * @return property value
	 */
	public static String readPropertyvalue(String propertyname, String key)
	{
		return getProperty(propertyname, key);
	}


	/**
	 * Create Output Zip directory.
	 */
	public static void createZipOutPutDirectory()
	{
		File directory = new File(readPropertyvalue("OutPut", "ZipDirectory"));
		boolean result = false;
		if (!directory.exists()) {
			try {
				directory.mkdir();
				result = true;
			}
			catch (SecurityException se) {
				se.printStackTrace();
			}
			if (result) {
				logger.info("DwCAExportZip directory was created successfull.");
			}
		}
	}


	/**
	 * Create Archive Zip directory.
	 */
	public static void createArchiveZipDirectory()
	{
		File directory = new File(readPropertyvalue("OutPut", "ZipArchiveDirectory"));
		boolean result = false;
		if (!directory.exists()) {
			try {
				directory.mkdir();
				result = true;
			}
			catch (SecurityException se) {
				se.printStackTrace();
			}
			if (result) {
				logger.info("DwCAZipArchive directory was created successfull.");
			}
		}
	}


	/**
	 * if value is '1' field will be added to CSV file otherwise if value is<br>
	 * '0'. field will not be added. 
	 * @param propertyname set propertyname
	 * @param value set value
	 * @return result boolean value
	 */
	public static boolean isFieldChecked(String propertyname, String value)
	{
		int commaindex = ExportDwCAUtilities.getPropertyValue(propertyname, value).length() - 1;
		String result = ExportDwCAUtilities.getPropertyValue(propertyname, value);
		String resultprop = result.substring(commaindex);
		return resultprop.matches("1(.*)");
	}


	/**
	 * Copy a file from a Source directory to a Destination directory
	 * @param sourceFile set sourceFile
	 * @param DestinationFile set DestinationFile
	 * @throws IOException Copy InputOutput Exception
	 */
	public static void CopyAFile(File sourceFile, File DestinationFile) throws IOException
	{
			if (sourceFile.exists())
			{
				try (InputStream inputstream = new FileInputStream(sourceFile);
					 OutputStream outputstream = new FileOutputStream(DestinationFile))
				{
					byte[] buffer = new byte[2048];
					int length;
					while ((length = inputstream.read(buffer)) > 0) 
					{
						outputstream.write(buffer, 0, length);
					}
				}
			}
	}
	
	
	/**
	 * Renamed the zipfile extension ".zip" file to ".zip.bak"
	 * @param fileToRenamed Renamed the zipfile extension ".zip" file to ".zip.bak"
	 */
	public static void renameDwCAZipFile(File fileToRenamed)
	{
		if (fileToRenamed.exists()) {
			int index = fileToRenamed.getName().indexOf(".");
			String filename = fileToRenamed.getName().substring(0, index);
			File path = fileToRenamed.getParentFile();
			String bakpath = path + "/" + filename + ".zip.bak";
			File filebak = new File(bakpath);
			if (filebak.exists()) {
				filebak.delete();
				logger.info("File '" + filebak + "' successfull deleted.");
			}
			boolean success = fileToRenamed.renameTo(new File(bakpath));
			if (success) {
				logger.info("File successfull renamed to '" + bakpath + "'");
			}
			else {
				logger.info("File in '" + fileToRenamed + "' not successfull renamed.");
			}
		}
	}


	/**
	 *  Renamed the predifined eml.xml file from source directory to eml.xml
	 * @param emlFileToRenamed set emlFilename
	 */
	public static void renameDwCAEMLFile(File emlFileToRenamed)
	{
		if (emlFileToRenamed.exists()) {
			int index = emlFileToRenamed.getName().indexOf(".");
			String filename = emlFileToRenamed.getName().substring(index - 3);
			File path = emlFileToRenamed.getParentFile();
			String emlpath = path + "/" + filename;
			File emlfile = new File(emlpath);
			if (emlfile.exists()) {
				emlfile.delete();
				logger.info("File '" + emlfile + "' successfull deleted.");
			}
			boolean success = emlFileToRenamed.renameTo(new File(emlpath));
			if (success) {
				logger.info("File successfull renamed to '" + emlpath + "'");
			}
			else {
				logger.info("File in '" + emlpath + "' not successfull renamed.");
			}
		}
	}

    /**
     * 
     * @param inputString set inputString
     * @param items set items
     * @return boolean value
     */
	public static boolean stringContainsItemFromList(String inputString, String[] items)
	{
		for (int i = 0; i < items.length; i++) {
			if (inputString.contains(items[i])) {
				return true;
			}
		}
		return false;
	}

    /**
     * used in: 
     * private static void dwcaObjectToXML(Meta meta) from DwCAexporter
     * private void writeCSVHeader() throws IOException
     * @param directory set directory
     * @param fileName set fileName
     * @return get fullpath
     */
	public static File newFile(File directory, String fileName)
	{
		String sep = System.getProperty("file.separator");
		return new File(directory.getAbsolutePath() + sep + fileName);
	}

    /**
     * used in:
     * public void exportDwca(String zipFileName, String namecollectiontype, String totalsize) throws Exception
     * @param directory set directory
     * @param fileName set filename
     * @return get fullpath
     */
	public static String getFullPath(File directory, String fileName)
	{
		String sep = System.getProperty("file.separator");
		return directory.getAbsolutePath() + sep + fileName;
	}


	/**
	 * Root directory for output from the DwCA export program
	 * 
	 * @return Root directory for output from the DwCA export program
	 */
	public static File getDwcaExportDir()
	{
		String outputRoot = ExportUtil.getConfig().required("nda.export.output.dir");
		Path path = FileSystems.getDefault().getPath(outputRoot, "dwca");
		File exportDir = path.toFile();
		if (exportDir.isDirectory()) {
			if (!exportDir.canWrite()) {
				throw new ExportException(String.format("Directory not writable: \"%s\"", path));
			}
		}
		else {
			logger.warn(String.format("No such directory (nda.export.output.dir): \"%s\". Will attempt to create it", path));
			try {
				java.nio.file.Files.createDirectories(path);
			}
			catch (IOException e) {
				throw new ExportException(String.format("Failed to create directory \"%s\"", path), e);
			}
		}
		return exportDir;
	}


	/**
	 * Working directory for the DwCA export program: ${dwcaExportDir}/output
	 * 
	 * @return Working directory for the DwCA export program: ${dwcaExportDir}/output
	 */
	public static File getWorkingDirectory()
	{
		File dir = newFile(getDwcaExportDir(), "output");
		if (!dir.isDirectory()) {
			dir.mkdir();
		}
		return dir;
	}


	/**
	 * Directory into which the DwC archive files will be written:
	 * ${dwcaExportDir}/zip
	 * 
	 * @return Directory into which the DwC archive files will be written:
	 */
	public static File getZipOutputDirectory()
	{
		File zipDir = newFile(getDwcaExportDir(), "zip");
		if (!zipDir.isDirectory()) {
			zipDir.mkdir();
		}
		return zipDir;
	}


	/**
	 * Backup directory for the DwC archive files: ${dwcaExportDir}/bak
	 * 
	 * @return result Backup directory
	 */
	public static File getBackupDirectory()
	{
		File bakDir = newFile(getDwcaExportDir(), "bak");
		if (!bakDir.isDirectory()) {
			bakDir.mkdir();
		}
		return bakDir;
	}

	private static File configRootDir = null;

    /**
     * 
     * @return result config directory
     */
	private static File getConfigRootDir()
	{
		if (configRootDir == null) {
			configRootDir = ExportUtil.getConfig().getDirectory("nda.export.user.conf.dir");
		}
		return configRootDir;
	}


	/**
	 * Directory containing eml files and properties files
	 * 
	 * @return result directory
	 */
	public static File getCollectionConfigDir()
	{
		File dir = newFile(getConfigRootDir(), "dwca");
		if (!dir.isDirectory()) {
			throw new ExportException(String.format("No such directory: \"%s\"", dir.getAbsolutePath()));
		}
		return dir;
	}

	// Cache for collection configuration settings
	private static final HashMap<String, ConfigObject> collectionProps = new HashMap<>();


	/**
	 * Get all configuration settings for a collection
	 * 
	 * @param collectionName set collectionName
	 * @return result collectionname
	 */
	public static ConfigObject getCollectionConfiguration(String collectionName)
	{
		ConfigObject cfg = collectionProps.get(collectionName);
		if (cfg == null) {
			File propsFile = newFile(getCollectionConfigDir(), collectionName + ".properties");
			if (!propsFile.isFile()) {
				throw new ExportException(String.format("No such file: \"%s\"", propsFile.getAbsolutePath()));
			}
			cfg = new ConfigObject(propsFile);
			collectionProps.put(collectionName, cfg);
		}
		return cfg;
	}


	/**
	 * Get a configuration setting for a collection
	 * 
	 * @param collectionName set collectionName
	 * @param propertyName set propertyName
	 * @return result collectionname
	 */
	public static String getProperty(String collectionName, String propertyName)
	{
		return getCollectionConfiguration(collectionName).required(propertyName);
	}


	public static boolean isEnabled(String collectionName, String propertyName)
	{
		String val = getProperty(collectionName, propertyName);
		String[] chunks = val.split(",");
		if (chunks[1].equals("1")) {
			return true;
		}
		return false;
	}


	/*
	 * public String convertStringToUTF8(String text) { String value = null;
	 * byte ptext[]; if (text != null) { ptext = text.getBytes(); value = new
	 * String(ptext, Charset.forName("UTF-8")); } return value; }
	 */

	/* Date: 15 juni 2015
	 * Jira: NDA-303/NDA-372
	public String convertStringToUTF8(String text)
	{
		String value = null;
		byte[] bytes = text.getBytes(Charset.forName("ISO-8859-1"));
		byte ptext[] = text.getBytes(Charset.forName("UTF-8"));
		if (!validUTF8(bytes))
			return text;
		if (validUTF8(ptext)) {
			value = text;
		}
		return value;
	}
	*/

    /**
     * 
     * @param input a byte value
     * @return result
     */
	public static boolean validUTF8(byte[] input)
	{
		int i = 0;
		// Check for BOM
		if (input.length >= 3 && (input[0] & 0xFF) == 0xEF && (input[1] & 0xFF) == 0xBB & (input[2] & 0xFF) == 0xBF) {
			i = 3;
		}

		int end = 0;
		for (int j = input.length; i < j; ++i) {
			int octet = input[i];
			if ((octet & 0x80) == 0) {
				continue; // ASCII
			}

			// Check for UTF-8 leading byte
			if ((octet & 0xE0) == 0xC0) {
				end = i + 1;
			}
			else if ((octet & 0xF0) == 0xE0) {
				end = i + 2;
			}
			else if ((octet & 0xF8) == 0xF0) {
				end = i + 3;
			}
			else {
				// Java only supports BMP so 3 is max
				return false;
			}

			while (i < end) {
				i++;
				octet = input[i - 1];
				if ((octet & 0xC0) != 0x80) {
					// Not a valid trailing byte
					return false;
				}
			}
		}
		return true;
	}

    /**
     * 
     * @param simpleJSON set simpleJSON
     * @return result
     */
	public static String crunchifyPrettyJSONUtility(String simpleJSON)
	{
		JsonParser crunhifyParser = new JsonParser();
		JsonObject json = crunhifyParser.parse(simpleJSON).getAsJsonObject();

		Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
		String prettyJson = prettyGson.toJson(json);

		return prettyJson;
	}


	/**
	 * Convert a JSON string to pretty print version
	 * 
	 * @param jsonString set jsonString
	 * @return Json result 
	 */
	public static String toPrettyFormat(String jsonString)
	{
		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(jsonString).getAsJsonObject();

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String prettyJson = gson.toJson(json);

		return prettyJson;
	}

}