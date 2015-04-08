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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import nl.naturalis.nda.export.ExportException;

import org.domainobject.util.ConfigObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringUtilities {
	static final Logger logger = LoggerFactory.getLogger(StringUtilities.class);
	final static int BUFFER = 2048;
	private static final String propertiesExtension = ".properties";
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
		propertiesfilename = propertyname + propertiesExtension;
		String result = null;
		boolean found = false;
		Properties prop = new Properties();
		try {
			/* load a properties file */
			prop.load(StringUtilities.class.getClassLoader().getResourceAsStream(propertiesfilename));
			Enumeration<?> e = prop.propertyNames();
			while (e.hasMoreElements() && !found) {
				String propertyName = (String) e.nextElement();
				result = prop.getProperty(propertyName);
				// System.out.println("Result: " + result);
				if (result.equals(value)) {
					found = true;
					break;
				}
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		return result;
	}


	/* Read the value from properties file */
	public static String readPropertyvalue(String propertyname, String key)
	{
		propertiesfilename = propertyname + propertiesExtension;
		String result = null;
		Properties prop = new Properties();
		try {
			/* load a properties file */
			if (propertiesfilename != null) {
				prop.load(StringUtilities.class.getClassLoader().getResourceAsStream(propertiesfilename));
				result = prop.getProperty(key);
			}

		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		return result;
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
		int commaindex = StringUtilities.getPropertyValue(propertyname, value).length() - 1;
		String result = StringUtilities.getPropertyValue(propertyname, value);
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
			String bakpath = path + "\\" + filename + ".zip.bak";
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
			String emlpath = path + "\\" + filename;
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
		String path = LoadUtil.getConfig().required("dwca.export.dir");
		File rootDir = new File(path);
		if (!rootDir.isDirectory() || !rootDir.canWrite()) {
			throw new ExportException(String.format("Directory does not exist or is not writable: \"%s\"", path));
		}
		return rootDir;
	}


	/**
	 * Working directory for the DwCA export program: ${dwcaExportDir}/output
	 * 
	 * @return
	 */
	public static File getWorkingDirectory()
	{
		File outputDir = newFile(getDwcaExportDir(), "output");
		if (!outputDir.isDirectory()) {
			outputDir.mkdir();
		}
		return outputDir;
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


	/**
	 * Root directory for input for the DwCA export program
	 * 
	 * @return
	 */
	public static File getDwcaConfigDir()
	{
		String path = LoadUtil.getConfig().required("dwca.config.dir");
		File confDir = new File(path);
		if (!confDir.isDirectory()) {
			throw new ExportException(String.format("No such directory: \"%s\"", path));
		}
		return confDir;
	}


	/**
	 * Directory containing EML files: ${dwcaConfigDir}/eml
	 * 
	 * @return
	 */
	public static File getEmlDirectory()
	{
		File emlDir = newFile(getDwcaConfigDir(), "eml");
		if (!emlDir.isDirectory()) {
			throw new ExportException(String.format("No such directory: \"%s\"", emlDir.getAbsolutePath()));
		}
		return emlDir;
	}


	/**
	 * Directory containing collection-related property files:
	 * ${dwcaConfigDir}/config
	 * 
	 * @return
	 */
	public static File getPropertiesDir()
	{
		File propsDir = newFile(getDwcaConfigDir(), "config");
		if (!propsDir.isDirectory()) {
			throw new ExportException(String.format("No such directory: \"%s\"", propsDir.getAbsolutePath()));
		}
		return propsDir;
	}

	// Cache for collection configuration settings
	private static final HashMap<String, ConfigObject> collectionProps = new HashMap<String, ConfigObject>();


	/**
	 * Get all configuration settings for a collection
	 * 
	 * @param collectionName
	 * @return
	 */
	public static ConfigObject getProperties(String collectionName)
	{
		ConfigObject cfg = collectionProps.get(collectionName);
		if (cfg == null) {
			File propsFile = newFile(getPropertiesDir(), collectionName + ".properties");
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
		return getProperties(collectionName).required(propertyName);
	}

}