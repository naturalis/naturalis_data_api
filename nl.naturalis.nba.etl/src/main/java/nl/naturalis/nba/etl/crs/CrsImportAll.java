package nl.naturalis.nba.etl.crs;

import static nl.naturalis.nba.dao.DocumentType.MULTI_MEDIA_OBJECT;
import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;

import org.apache.logging.log4j.Logger;
import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.ESUtil;
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

	public static void main(String[] args)
	{
		try {
			CrsImportAll crsImportAll = new CrsImportAll();
			crsImportAll.importAll();
		}
		catch (Throwable t) {
			logger.error("CrsImportAll terminated unexpectedly!", t);
			System.exit(1);
		}
		finally {
		  if (!DaoRegistry.getInstance().getConfiguration().get("etl.output", "es").equals("file")) {
  			ESUtil.refreshIndex(SPECIMEN);
  			ESUtil.refreshIndex(MULTI_MEDIA_OBJECT);
		  }
			ESClientManager.getInstance().closeClient();
		}
	}

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
