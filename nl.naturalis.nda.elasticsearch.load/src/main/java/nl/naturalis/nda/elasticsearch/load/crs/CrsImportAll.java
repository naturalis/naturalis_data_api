package nl.naturalis.nda.elasticsearch.load.crs;

import nl.naturalis.nda.elasticsearch.load.Registry;

import org.apache.logging.log4j.Logger;


/**
 * Class that manages the import of CRS specimens and multimedia. Currently,
 * this class can only handle "offline" imports where data is source from the
 * local file system rather than through "live" calls to the CRS OAI interface.
 * This class basically just runs
 * {@link CrsSpecimenImportOffline#importSpecimens()} first and then
 * {@link CrsMultiMediaImportOffline#importMultimedia()}.
 * 
 * @author Ayco Holleman
 *
 */
public class CrsImportAll {
	
	public static void main(String[] args) throws Exception
	{
		try {
			logger.info("Hello, World!");
//			CrsImportAll crsImportAll = new CrsImportAll();
//			crsImportAll.importAll();
		}
		finally {
			Registry.getInstance().closeESClient();
		}
	}

	@SuppressWarnings("unused")
	private static final Logger logger = Registry.getInstance().getLogger(CrsImportAll.class);

	/**
	 * Import CRS specimens and multimedia.
	 * 
	 * @throws Exception
	 */
	public void importAll() throws Exception
	{
		CrsSpecimenImportOffline specimenImporter = new CrsSpecimenImportOffline();
		specimenImporter.importSpecimens();
		CrsMultiMediaImportOffline multimediaImporter = new CrsMultiMediaImportOffline();
		multimediaImporter.importMultimedia();
	}
}
