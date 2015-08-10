package nl.naturalis.nda.elasticsearch.load.nsr;


/**
 * Removes the ".imported" extension from imported CSV files, so they will be
 * picked up again by the import programs.
 * 
 * @author Ayco Holleman
 * @created Jul 28, 2015
 *
 */
public class NsrBackupExtensionRemover {

	public static void main(String[] args)
	{
		NsrImportUtil.removeBackupExtension();
	}

}
