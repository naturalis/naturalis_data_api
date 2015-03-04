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
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


import org.domainobject.util.ConfigObject;
 
public class StringUtilities
{  
	final static int BUFFER = 2048;
	private static final String propertiesExtension = ".properties";
	static String propertiesfilename = null;
	private static ConfigObject config;
	
    public static int indexOfFirstContainedCharacter(String s1, String s2) {
        if (s1 == null || s1.isEmpty())
            return -1;
        Set<Character> set = new HashSet<Character>();
        for (int i=0; i<s2.length(); i++) {
            set.add(s2.charAt(i)); // Build a constant-time lookup table.
        }
        for (int i=0; i<s1.length(); i++) {
            if (set.contains(s1.charAt(i))) {
                return i; // Found a character in s1 also in s2.
            }
        }
        return -1; // No matches.
    }
     

    /* Zip multiple files */
    public static void addToZipFile(String fileName, ZipOutputStream zos)throws FileNotFoundException, IOException
    {
    	System.out.println("Writing '" + fileName + "' to zip file");
    	
    	BufferedInputStream origin = null;
    	byte data[]  = new byte[BUFFER];
    	
    	File file = new File(fileName);
    	FileInputStream fis = new FileInputStream(file);
    	origin = new BufferedInputStream(fis, BUFFER);
    	ZipEntry zipEntry = new ZipEntry(fileName);
    	zos.putNextEntry(zipEntry);
    	
    	int length;
    	while ((length = origin.read(data, 0, BUFFER)) != -1)
    	{
    		zos.write(data, 0, length);
    		zos.flush();
    	}
    	origin.close();
    	zos.closeEntry();
    	fis.close();
    }
    
    
    /* Read the value from properties file */
    public static String readPropertyvalue(String propertyname, String key)
    {
    	propertiesfilename = propertyname + propertiesExtension;
    	String result = null;
    	Properties prop = new Properties();
    	try
		{
    		/* load a properties file */
    		prop.load(StringUtilities.class.getClassLoader().getResourceAsStream(propertiesfilename));
    		result = prop.getProperty(key);
    		
		} catch (IOException ex)
		{
			ex.printStackTrace();
		}
    	return result;
    }
    
    
    public static void createOutPutDirectory()
    {
    	File directory = new File(readPropertyvalue("OutPut", "Directory"));
		boolean result = false;
		if (!directory.exists())
		{
			try
			{
				directory.mkdir();
				result = true;
			} catch (SecurityException se)
			{
				se.printStackTrace();
			}
			if (result)
			{
				System.out.println("DwCAExport directory was created successfull.");
			}
		}	
    }
    
    public static void createZipOutPutDirectory()
    {
    	File directory = new File(readPropertyvalue("OutPut", "ZipDirectory"));
		boolean result = false;
		if (!directory.exists())
		{
			try
			{
				directory.mkdir();
				result = true;
			} catch (SecurityException se)
			{
				se.printStackTrace();
			}
			if (result)
			{
				System.out.println("DwCAExportZip directory was created successfull.");
			}
		}	
    }
    
    
//    public static ConfigObject getConfig()
//	{
//		if (config == null) {
//			String ndaConfDir = System.getProperty("ndaConfDir");
//			if (ndaConfDir != null) {
//				logger.debug("Using system property \"ndaConfDir\" to locate configuration file " + PROPERTY_FILE_NAME);
//				File dir = new File(ndaConfDir);
//				if (!dir.isDirectory()) {
//					throw new RuntimeException(String.format("Invalid directory specified for property \"ndaConfDir\": \"%s\"", ndaConfDir));
//				}
//				try {
//					File file = new File(dir.getCanonicalPath() + "/" + PROPERTY_FILE_NAME);
//					if (!file.isFile()) {
//						throw new RuntimeException(String.format("Configuration file missing: %s", file.getCanonicalPath()));
//					}
//					logger.debug(String.format("Using configuration file %s", file.getCanonicalPath()));
//					config = new ConfigObject(file);
//				}
//				catch (IOException e) {
//					throw new RuntimeException(e);
//				}
//			}
//			else {
//				logger.debug("Searching classpath for configuration file " + PROPERTY_FILE_NAME);
//				try (InputStream is = LoadUtil.class.getResourceAsStream("/" + PROPERTY_FILE_NAME)) {
//					if (is == null) {
//						throw new RuntimeException(String.format("Configuration file missing: %s", PROPERTY_FILE_NAME));
//					}
//					config = new ConfigObject(is);
//				}
//				catch (IOException e) {
//					throw new RuntimeException(e);
//				}
//			}
//		}
//		return config;
//	}


}