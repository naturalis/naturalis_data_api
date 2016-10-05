package nl.naturalis.nba.etl.crs;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.etl.ETLRegistry;

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
			CrsImportAll crsImportAll = new CrsImportAll();
			crsImportAll.importAll();
		}
		finally {
			ESClientManager.getInstance().closeClient();
		}
	}

	@SuppressWarnings("unused")
	private static final Logger logger = ETLRegistry.getInstance().getLogger(CrsImportAll.class);

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
