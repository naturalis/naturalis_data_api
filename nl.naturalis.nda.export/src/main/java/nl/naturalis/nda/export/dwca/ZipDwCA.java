package nl.naturalis.nda.export.dwca;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipDwCA
{

	static final Logger logger = LoggerFactory.getLogger(ZipDwCA.class);
	final static int BUFFER = 2048;
	public ZipDwCA() 
	{

	}
	
	public void zipDirectory(String dirName, String nameZipFile) throws IOException
	{
		ZipOutputStream zos = null;
		FileOutputStream fos = null;
		fos = new FileOutputStream(nameZipFile);
		zos = new ZipOutputStream(fos);
		addFolderToZip("", dirName, zos);
		zos.close();
		fos.close();		
	}

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
				FileInputStream fis = new FileInputStream(srcFile);
				//System.out.println("Writing '" + folder.getName() + "' to zip file");
				logger.info("Writing '" + folder.getName() + "' to zip file");
				zip.putNextEntry(new ZipEntry(folder.getName())); 
				while ((len = fis.read(buf)) > 0)
				{
					zip.write(buf, 0, len);
				}
				fis.close();
			}
		}
	}
	
	

}
