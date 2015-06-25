package nl.naturalis.nda.export.dwca;

/*  
 *  Created by : Reinier.Kartowikromo 
 *  Date: 12-02-2015
 *  Description: String Utilites for the StringBuilder Class to Write data to a CSV file
 */

import java.io.BufferedInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ExportDwCAUtilities {

	static final Logger logger = LoggerFactory.getLogger(ExportDwCAUtilities.class);
	final static int BUFFER = 2048;
	static String propertiesfilename = null;


	public static int indexOfFirstContainedCharacter(String s1, String s2)
	{
		if (s1 == null || s1.isEmpty())
			return -1;
		Set<Character> set = new HashSet<Character>();
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


	/*
	 * Example: try { String zipfilename = zipFileName + zipExtension;
	 * FileOutputStream fos = new FileOutputStream(zipfilename); ZipOutputStream
	 * zos = new ZipOutputStream(fos);
	 * 
	 * StringUtilities.addToZipFile(FILE_NAME_META, zos);
	 * StringUtilities.addToZipFile(FILE_NAME_EML, zos);
	 * StringUtilities.addToZipFile(csvOutPutFile, zos);
	 * 
	 * zos.close(); fos.close(); System.out.println("Zipfile '" + zipfilename +
	 * "' created successfull.");
	 * 
	 * } catch (FileNotFoundException e) { e.printStackTrace(); } catch
	 * (IOException e) { e.printStackTrace(); }
	 */
	/* Zip multiple files */
	public static void addToZipFile(String fileName, ZipOutputStream zos) throws FileNotFoundException, IOException
	{
		System.out.println("Writing '" + fileName + "' to zip file");
		logger.info("Writing '" + fileName + "' to zip file");

		BufferedInputStream origin = null;
		byte data[] = new byte[BUFFER];

		File file = new File(fileName);
		FileInputStream fis = new FileInputStream(file);
		origin = new BufferedInputStream(fis, BUFFER);
		ZipEntry zipEntry = new ZipEntry(fileName);
		zos.putNextEntry(zipEntry);

		int length;
		while ((length = origin.read(data, 0, BUFFER)) != -1) {
			zos.write(data, 0, length);
			zos.flush();
		}
		origin.close();
		zos.closeEntry();
		fis.close();
	}


	/* Read the value from properties file */
	public static String getPropertyValue(String propertyname, String value)
	{
		return getProperty(propertyname, value);
	}


	/* Read the value from properties file */
	public static String readPropertyvalue(String propertyname, String key)
	{
		return getProperty(propertyname, key);
	}


	/* Create Output Zip directory. */
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


	/* Create Archive Zip directory. */
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


	/*
	 * if value is '1' field will be added to CSV file otherwise if value is
	 * '0'. field will not be added.
	 */
	public static boolean isFieldChecked(String propertyname, String value)
	{
		int commaindex = ExportDwCAUtilities.getPropertyValue(propertyname, value).length() - 1;
		String result = ExportDwCAUtilities.getPropertyValue(propertyname, value);
		String resultprop = result.substring(commaindex);
		return resultprop.matches("1(.*)");
	}


	/* Copy a file from a Source directory to a Destination directory */
	public static void CopyAFile(File sourceFile, File DestinationFile) throws IOException
	{
		InputStream inputstream = null;
		OutputStream outputstream = null;
		try {
			inputstream = new FileInputStream(sourceFile);
			outputstream = new FileOutputStream(DestinationFile);
			byte[] buffer = new byte[2048];
			int length;
			while ((length = inputstream.read(buffer)) > 0) {
				outputstream.write(buffer, 0, length);
			}
		}
		finally {
			if (inputstream != null) {
				inputstream.close();
			}
			if (outputstream != null) {
				outputstream.close();
			}
		}
	}


	/* Renamed the zipfile extension ".zip" file to ".zip.bak" */
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


	/* Renamed the predifined eml.xml file from source directory to eml.xml */
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


	public static boolean stringContainsItemFromList(String inputString, String[] items)
	{
		for (int i = 0; i < items.length; i++) {
			if (inputString.contains(items[i])) {
				return true;
			}
		}
		return false;
	}


	public static File newFile(File directory, String fileName)
	{
		String sep = System.getProperty("file.separator");
		return new File(directory.getAbsolutePath() + sep + fileName);
	}


	public static String getFullPath(File directory, String fileName)
	{
		String sep = System.getProperty("file.separator");
		return directory.getAbsolutePath() + sep + fileName;
	}


	/**
	 * Root directory for output from the DwCA export program
	 * 
	 * @return
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
	 * @return
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
	 * @return
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
	 * @return
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
	 * @return
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
	private static final HashMap<String, ConfigObject> collectionProps = new HashMap<String, ConfigObject>();


	/**
	 * Get all configuration settings for a collection
	 * 
	 * @param collectionName
	 * @return
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
	 * @param collectionName
	 * @param propertyName
	 * @return
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
	 * @param jsonString
	 * @return
	 */
	public static String toPrettyFormat(String jsonString)
	{
		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(jsonString).getAsJsonObject();

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String prettyJson = gson.toJson(json);

		return prettyJson;
	}


	@SuppressWarnings("unchecked")
	public static void writeLogToJSON(String collectionName, String messages) throws IOException
	{

		/*
		 * String Filename = "c:\\Temp\\" + collectionName + ".json"; try(Writer
		 * writer = new OutputStreamWriter(new FileOutputStream(Filename, true)
		 * , "UTF-8")) { Gson gson = new GsonBuilder().create();
		 * gson.toJsonTree(collectionName); gson.toJson(collectionName, writer);
		 * gson.toJson("Author: Reinier.Kartowikromo", writer);
		 * gson.toJson("INFO: " + messages, writer); }
		 */

		String Filename = "c:\\Temp\\" + collectionName + ".json";

		JSONObject collection = new JSONObject();
		collection.put("Collectionname", collectionName);
		collection.put("Author", "Reinier.Kartowikromo");

		JSONArray listOfMessages = new JSONArray();
		listOfMessages.add(messages);

		collection.put("INFO", listOfMessages);

		try {

			// Writing to a file  
			File file = new File(Filename);
			file.createNewFile();
			FileWriter fileWriter = new FileWriter(file, true);
			System.out.println("Writing JSON object to file");
			System.out.println("-----------------------");
			System.out.print(collection);

			fileWriter.write(collection.toJSONString());
			fileWriter.flush();
			fileWriter.close();

		}
		catch (IOException e) {
			e.printStackTrace();
		}

		/*
		 * JSONObject json = new JSONObject(); json.put("Collectionname",
		 * collectionName); json.put("Author", "Reinier.Kartowikromo");
		 * 
		 * JSONArray jsonArray = new JSONArray(); jsonArray.add(messages);
		 * jsonArray.spliterator();
		 * 
		 * json.put("INFO", jsonArray); try {
		 * System.out.println("Writting JSON into file ...");
		 * System.out.println(json);
		 * 
		 * FileWriter jsonFileWriter = new FileWriter(Filename, true);
		 * jsonFileWriter.write(json.toJSONString()); jsonFileWriter.flush();
		 * jsonFileWriter.close(); System.out.println("Done"); } catch
		 * (IOException e) { e.printStackTrace(); }
		 */
		/*
		 * String Filename = collectionName + ".json";
		 * 
		 * JsonFactory jfactory = new JsonFactory(); JsonGenerator jGenerator =
		 * jfactory.createGenerator(new File("c:\\Temp\\" + Filename),
		 * JsonEncoding.UTF8);
		 * 
		 * jGenerator.writeStartObject();
		 * jGenerator.writeStringField("Collectionname", collectionName);
		 * jGenerator.writeStringField("Author", "Reinier.Kartowikromo");
		 * 
		 * jGenerator.writeFieldName("INFO"); jGenerator.writeStartArray(); // [
		 * 
		 * jGenerator.writeString(messages); // messages
		 * 
		 * jGenerator.writeEndArray(); // ]
		 * 
		 * jGenerator.writeEndObject(); // }
		 * 
		 * jGenerator.close();
		 */
	}

	/*
	 * public static String searchForCollectionName(File emlDir, String
	 * searchQuery) throws IOException { searchQuery = searchQuery.trim();
	 * //List<String> fileresult = new ArrayList<String>(); StringBuilder
	 * builder = new StringBuilder(); String extensions = ".properties";
	 * FilenameFilter filter = new FilenameFilter() { public boolean accept(File
	 * dir, String name) { return name.endsWith(extensions); } };
	 * 
	 * 
	 * if (emlDir.exists()) { try { File[] listOfFiles =
	 * emlDir.listFiles(filter); for (File file : listOfFiles) { ConfigObject
	 * config = new ConfigObject(file); if (config.hasProperty(searchQuery)) {
	 * config.required("collectionName");
	 * builder.append(config.get(searchQuery)); builder.append("\n");
	 * //fileresult.add(cfg.get(searchQuery)); } } } catch (Exception e) {
	 * System.err.println(e.toString()); } }
	 * 
	 * return builder.toString(); // fileresult.toString();
	 * 
	 * }
	 */

}