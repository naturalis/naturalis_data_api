package nl.naturalis.nda.export.dwca;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h1>ZIPDwCA</h1>
 *  Description: Methods what is used in the DwCAExporter class methods<br>
 *               public static void createZipFiles(String zipFileName) 
 *              
 *               
 *  @version	 1.0
 *  @author 	 Reinier.Kartowikromo 
 *  @since		 12-02-2015
 *  
 * */

public class ZipDwCA
{

	static final Logger logger = LoggerFactory.getLogger(ZipDwCA.class);
	final static int BUFFER = 2048;
	public ZipDwCA() 
	{

	}
	
	/**
	 * Zip the files to the directory
	 * @param dirName Zip the files to the directory
	 * @param nameZipFile Name of the zipfile
	 * @throws IOException Stream exception
	 */
	public void zipDirectory(String dirName, String nameZipFile) throws IOException
	{
		try( FileOutputStream fos = new FileOutputStream(nameZipFile);
			 ZipOutputStream zos = new ZipOutputStream(fos))
			 {
				addFolderToZip("", dirName, zos);
			 }
	}

	/**
	 * addFolderToZip
	 * @param path Folder path
	 * @param srcFolder destination folder
	 * @param zip output zipfile
	 * @throws IOException zip exception
	 */
	private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) throws IOException
	{
		File folder = new File(srcFolder);
		if (folder.list().length == 0)
		{
			addFileToZip(path, srcFolder, zip, true);
		} else
		{
			for (String fileName : folder.list())
			{
				if (path.equals(""))
				{
					addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip, false); 
				} else
				{
					addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip, false);
				}
			}
		}
	}

	/**
	 * addFileToZip
	 * @param path Zip path
	 * @param srcFile  source file to zip
	 * @param zip output zip
	 * @param flag true or false
	 * @throws IOException IO exception
	 */
	private void addFileToZip(String path, String srcFile, ZipOutputStream zip, boolean flag)
			throws IOException
	{
		File folder = new File(srcFile);
		if (flag)
		{
			zip.putNextEntry(new ZipEntry(path + "/" + folder.getName() + "/"));
		} else
		{
			if (folder.isDirectory())
			{
				addFolderToZip(path, srcFile, zip);
			} else
			{
				byte[] buf = new byte[BUFFER];
				int len;
				try(
				FileInputStream fis = new FileInputStream(srcFile)) 
				{
					logger.info("Writing '" + folder.getName() + "' to zip file");
					zip.putNextEntry(new ZipEntry(folder.getName())); 
					while ((len = fis.read(buf)) > 0)
					{
						zip.write(buf, 0, len);
					}
				}
			}
		}
	}
	
	

}
