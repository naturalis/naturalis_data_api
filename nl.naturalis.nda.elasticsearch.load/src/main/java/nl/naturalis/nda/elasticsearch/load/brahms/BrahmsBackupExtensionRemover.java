package nl.naturalis.nda.elasticsearch.load.brahms;

/**
 * Removes the ".imported" extension from imported CSV files, so they will be
 * picked up again by the import programs. Utility class. Should not be used in
 * production.
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
