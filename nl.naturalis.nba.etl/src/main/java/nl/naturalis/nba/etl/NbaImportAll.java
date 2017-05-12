package nl.naturalis.nba.etl;

import static nl.naturalis.nba.dao.DocumentType.GEO_AREA;
import static nl.naturalis.nba.dao.DocumentType.MULTI_MEDIA_OBJECT;
import static nl.naturalis.nba.dao.DocumentType.SCIENTIFIC_NAME_GROUP;
import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;
import static nl.naturalis.nba.dao.DocumentType.TAXON;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.brahms.BrahmsImportAll;
import nl.naturalis.nba.etl.col.CoLImportAll;
import nl.naturalis.nba.etl.crs.CrsImportAll;
import nl.naturalis.nba.etl.enrich.MultimediaTaxonomicEnricher;
import nl.naturalis.nba.etl.enrich.SpecimenTaxonomicEnricher;
import nl.naturalis.nba.etl.geo.GeoImporter;
import nl.naturalis.nba.etl.name.NameImportAll;
import nl.naturalis.nba.etl.nsr.NsrImporter;

/**
 * The central class of the import library. Start here. Allows you to bootstrap
 * the NBA index (i.e. create an empty NBA index) and to import all datasources
 * one by one. In other words this class lets you do a full import.
 * 
 * @author Ayco Holleman
 * 
 */
public class NbaImportAll {

	public static void main(String[] args)
	{
		NbaImportAll nbaImportAll = new NbaImportAll();
		nbaImportAll.importAll();
	}

	private static final Logger logger = ETLRegistry.getInstance().getLogger(NbaImportAll.class);

	public void importAll()
	{

		long start = System.currentTimeMillis();

		try {

			logger.info("[>--- Bootstrapping NBA indices ---<]");
			NbaBootstrap bootstrap = new NbaBootstrap();
			bootstrap.bootstrap("all");

			logger.info("[>--- Starting NSR import ---<]");
			NsrImporter nsrImporter = new NsrImporter();
			nsrImporter.reset();
			nsrImporter.importAll();

			logger.info("[>--- Starting BRAHMS import ---<]");
			BrahmsImportAll brahmsImportAll = new BrahmsImportAll();
			brahmsImportAll.reset();
			brahmsImportAll.importAll();

			logger.info("[>--- Starting CRS import ---<]");
			CrsImportAll crsImportAll = new CrsImportAll();
			crsImportAll.importAll();

			logger.info("[>--- Starting COL import ---<]");
			CoLImportAll colImportAll = new CoLImportAll();
			colImportAll.importAll();

			logger.info("[>--- Starting GEO import ---<]");
			GeoImporter geoImporter = new GeoImporter();
			geoImporter.importAll();

			logger.info("[>--- Starting Specimen enrichment ---<]");
			SpecimenTaxonomicEnricher specimenEnricher = new SpecimenTaxonomicEnricher();
			specimenEnricher.configureWithSystemProperties();
			specimenEnricher.enrich();

			logger.info("[>--- Starting MultiMediaObject enrichment ---<]");
			MultimediaTaxonomicEnricher multimediaEnricher = new MultimediaTaxonomicEnricher();
			multimediaEnricher.configureWithSystemProperties();
			multimediaEnricher.enrich();

			logger.info("[>--- Starting ScientificNameGroup import ---<]");
			NameImportAll nameImporter = new NameImportAll();
			nameImporter.importNames();

			ESUtil.refreshIndex(TAXON);
			ESUtil.refreshIndex(MULTI_MEDIA_OBJECT);
			ESUtil.refreshIndex(SPECIMEN);
			ESUtil.refreshIndex(GEO_AREA);
			ESUtil.refreshIndex(SCIENTIFIC_NAME_GROUP);

		}
		catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
		finally {
			ETLUtil.logDuration(logger, getClass(), start);
			ESClientManager.getInstance().closeClient();
		}

		int i = MimeTypeCacheFactory.getInstance().getCache().getMisses();
		if (i != 0) {
			String fmt = "%s mime type cache lookup failures";
			logger.warn(String.format(fmt, String.valueOf(i)));
			logger.warn("The mime type cache is out-of-date!");
		}
	}

}
