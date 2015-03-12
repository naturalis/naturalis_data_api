package nl.naturalis.nda.export.dwca;

import java.io.File;
import java.io.FilenameFilter;


public class FindFile
{

	public FindFile()
	{
		// TODO Auto-generated constructor stub
	}

	public static File[] getFileList(String directory)
	{
		File dir = new File(directory);

		File[] fileList  = dir.listFiles(new FilenameFilter()
		{
		  public boolean accept(File dir, String name)
		  {
			  return name.endsWith(".xml");
		  }
		});
		return fileList;
	}

}
