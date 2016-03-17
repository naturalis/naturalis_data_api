package nl.naturalis.nda.elasticsearch.load;

import java.util.Arrays;

import nl.naturalis.nba.elasticsearch.map.Mapping;
import nl.naturalis.nba.elasticsearch.map.MappingFactory;
import nl.naturalis.nba.elasticsearch.map.MappingSerializer;
import nl.naturalis.nda.elasticsearch.client.IndexManagerNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.load.brahms.BrahmsImportAll;
import nl.naturalis.nda.elasticsearch.load.col.CoLImportAll;
import nl.naturalis.nda.elasticsearch.load.crs.CrsImportAll;
import nl.naturalis.nda.elasticsearch.load.ndff.NdffSpecimenImporter;
import nl.naturalis.nda.elasticsearch.load.nsr.NsrImporter;

import org.apache.logging.log4j.Logger;
import org.domainobject.util.FileUtil;

/**
 * The "main" class of the import library. Start here. Allows you to bootstrap
 * the NBA index (i.e. create an empty NBA index) and to import all datasources
 * one by one. In other words this class lets you do a full import.
 * 
 * @author Ayco Holleman
 * 
 */
public class NBAImportAll {

	public static void main(String[] args)
	{
		IndexManagerNative index = null;
		try {
			index = Registry.getInstance().getNbaIndexManager();
			NBAImportAll indexManager = new NBAImportAll(index);
			if (args.length == 0 || Arrays.asList(args).contains("bootstrap")) {
				indexManager.bootstrap();
			}
			if (args.length == 0 || Arrays.asList(args).contains("import")) {
				indexManager.importAll();
			}
		}
		catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
		finally {
			if (index != null) {
				index.getClient().close();
			}
		}
	}

	public static final String LUCENE_TYPE_TAXON = "Taxon";
	public static final String LUCENE_TYPE_SPECIMEN = "Specimen";
	public static final String LUCENE_TYPE_MULTIMEDIA_OBJECT = "MultiMediaObject";

	private static final Logger logger = Registry.getInstance().getLogger(NBAImportAll.class);

	private final IndexManagerNative index;

	public NBAImportAll(IndexManagerNative index)
	{
		this.index = index;
	}

	/**
	 * Runs all individual import programs in the following order: NSR, Brahms,
	 * CRS, CoL.
	 */
	public void importAll()
	{

		long start = System.currentTimeMillis();

		try {

			logger.info("[>--- Starting NDFF import ---<]");
			NdffSpecimenImporter ndffImporter = new NdffSpecimenImporter();
			ndffImporter.importSpecimens();

			logger.info("[>--- Starting NSR import ---<]");
			NsrImporter nsrImporter = new NsrImporter();
			nsrImporter.importAll();

			logger.info("[>--- Starting Brahms import ---<]");
			BrahmsImportAll brahmsImportAll = new BrahmsImportAll();
			brahmsImportAll.importAll();

			logger.info("[>--- Starting CRS import ---<]");
			CrsImportAll crsImportAll = new CrsImportAll();
			crsImportAll.importAll();

			logger.info("[>--- Starting CoL import ---<]");
			CoLImportAll colImportAll = new CoLImportAll();
			colImportAll.importAll();

		}
		catch (Throwable t) {
			logger.error("NBA Import failed!");
		}
		finally {
			LoadUtil.logDuration(logger, getClass(), start);
			Registry.getInstance().closeESClient();
		}

		int i = MimeTypeCacheFactory.getInstance().getCache().getMisses();
		if (i != 0) {
			String fmt = "%s mime type cache lookup failures";
			logger.warn(String.format(fmt, String.valueOf(i)));
			logger.warn("The mime type cache is out-of-date!");
		}

	}

	/**
	 * Creates the NDA schema from scratch. Will delete the entire index
	 * (mappings and documents) and then re-create it. !!! WATCH OUT !!!
	 */
	public void bootstrap()
	{
		index.delete();
		Registry reg = Registry.getInstance();
		String settings = FileUtil.getContents(reg.getFile("es-settings.json"));
		logger.info("Creating index using settings: " + settings);
		index.create(settings);
		MappingFactory mf = new MappingFactory();
		MappingSerializer ms = MappingSerializer.getInstance();

		Mapping mapping = mf.getMapping(ESSpecimen.class);
		String json = ms.serialize(mapping);
		index.addType(LUCENE_TYPE_SPECIMEN, json);

		mapping = mf.getMapping(ESMultiMediaObject.class);
		json = ms.serialize(mapping);
		index.addType(LUCENE_TYPE_MULTIMEDIA_OBJECT, json);

		mapping = mf.getMapping(ESTaxon.class);
		json = ms.serialize(mapping);
		index.addType(LUCENE_TYPE_TAXON, json);
	}
}
