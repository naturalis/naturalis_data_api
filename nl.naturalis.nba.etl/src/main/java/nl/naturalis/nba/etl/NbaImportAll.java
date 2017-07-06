package nl.naturalis.nba.etl;

import java.util.Set;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.IndexInfo;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.brahms.BrahmsImportAll;
import nl.naturalis.nba.etl.col.CoLImportAll;
import nl.naturalis.nba.etl.crs.CrsImportAll;
import nl.naturalis.nba.etl.enrich.MultimediaTaxonomicEnricher2;
import nl.naturalis.nba.etl.enrich.SpecimenMultimediaEnricher;
import nl.naturalis.nba.etl.enrich.SpecimenTaxonomicEnricher2;
import nl.naturalis.nba.etl.geo.GeoImporter;
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

			logger.info("[>--- Starting multimedia enrichment of specimens ---<]");
			SpecimenMultimediaEnricher specimenEnricherMM = new SpecimenMultimediaEnricher();
			specimenEnricherMM.configureWithSystemProperties();
			specimenEnricherMM.enrich();

			logger.info("[>--- Starting taxonomic enrichment of specimens ---<]");
			SpecimenTaxonomicEnricher2 specimenEnricherTE = new SpecimenTaxonomicEnricher2();
			specimenEnricherTE.configureWithSystemProperties();
			specimenEnricherTE.enrich();

			logger.info("[>--- Starting taxonomic enrichment of multimedia ---<]");
			MultimediaTaxonomicEnricher2 multimediaEnricherTE = new MultimediaTaxonomicEnricher2();
			multimediaEnricherTE.configureWithSystemProperties();
			multimediaEnricherTE.enrich();

			Set<IndexInfo> indices = ESUtil.getDistinctIndices();
			for (IndexInfo index : indices) {
				ESUtil.refreshIndex(index);
			}

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
