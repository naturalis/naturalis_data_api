package nl.naturalis.nda.export.dwca;

import java.io.File;
import java.io.FilenameFilter;


/**
 * <h1>FindFile</h1>
 *  Description: Methods what is used in the DwCAExporter class method<br>
 *               private static String getEmlFileName(String emlDir, String emlfilename)
 *               
 *  @version	 1.0
 *  @author 	 Reinier.Kartowikromo 
 *  @since		 12-02-2015
 *  
 * */


public class FindFile
{

	public FindFile()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * Find all files in directory and added to Array list
	 * @param directory
	 * @return
	 */
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
	
	/**
	 * Find files with certain extension only in a directory
	 * @param folder
	 * @param ext
	 */
	public void listFile(String folder, String ext) {
		 
		GenericExtFilter filter = new GenericExtFilter(ext);
 
		File dir = new File(folder);
 
		if(dir.isDirectory()==false){
			System.out.println("Directory does not exists : " + folder);
			return;
		}
 
		// list out all the file name and filter by the extension
		String[] list = dir.list(filter);
 
		if (list.length == 0) {
			System.out.println("no files end with : " + ext);
			return;
		}
 
		for (String file : list) {
			String temp = new StringBuffer(folder).append(File.separator)
					.append(file).toString();
			System.out.println("file : " + temp);
		}
	}
	
	/**
	 * 
	 * @author Reinier.Kartowikromo
	 *
	 */
	public class GenericExtFilter implements FilenameFilter {
		 
		private String ext;
 
		/**
		 * 
		 * @param ext
		 */
		public GenericExtFilter(String ext) {
			this.ext = ext;
		}
 
		/**
		 * @return
		 */
		public boolean accept(File dir, String name) {
			return (name.endsWith(ext));
		}
	}

}
