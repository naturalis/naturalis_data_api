package nl.naturalis.nba.etl.brahms;

/**
 * Removes the {@code .imported} extension from CSV files that have been
 * successfully processed, so they will be picked up again by the Brahms import
 * programs. Convenient when testing the Brahms import programs. Should not be
 * used in production.
 * 
 * @author Ayco Holleman
 *
 */
public class BrahmsBackupExtensionRemover {

	public static void main(String[] args)
	{
		BrahmsImportUtil.removeBackupExtension();
	}

}
