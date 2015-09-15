package nl.naturalis.nda.elasticsearch.load.nsr;

/**
 * Removes the {@code .imported} extension from XML files that have been
 * successfully processed, so they will be picked up again by the NSR import
 * programs. Convenient when testing the NSR import programs. Should not be used
 * in production.
 * 
 * @author Ayco Holleman
 *
 */
public class NsrBackupExtensionRemover {

	public static void main(String[] args)
	{
		NsrImportUtil.removeBackupExtension();
	}

}
