package nl.naturalis.nda.elasticsearch.load.brahms;

import java.io.File;
import java.io.FilenameFilter;

import nl.naturalis.nda.elasticsearch.load.LoadUtil;

/**
 * Removes the ".imported" extension from imported CSV files, so they will be
 * picked up again by the import programs.
 * 
 * @author Ayco Holleman
 * @created Jul 28, 2015
 *
 */
public class Unimport {

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		File dir = LoadUtil.getConfig().getDirectory("brahms.csv_dir");
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name)
			{
				return name.toLowerCase().endsWith(".imported");
			}
		});
		for (File file : files) {
			int pos = file.getName().toLowerCase().indexOf(".csv");
			String chopped = file.getName().substring(0, pos + 4);
			System.out.println(file.getName() + " ---> " + chopped);
			chopped = dir.getAbsolutePath() + "/" + chopped;
			file.renameTo(new File(chopped));
		}
	}

}
