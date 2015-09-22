package nl.naturalis.nda.elasticsearch.load.nsr;

import static nl.naturalis.nda.domain.SourceSystem.NSR;
import static nl.naturalis.nda.elasticsearch.load.NBAImportAll.LUCENE_TYPE_MULTIMEDIA_OBJECT;
import static nl.naturalis.nda.elasticsearch.load.NBAImportAll.LUCENE_TYPE_TAXON;
import static nl.naturalis.nda.elasticsearch.load.nsr.NsrImportUtil.backupXmlFile;

import java.io.File;
import java.util.List;

import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;
import nl.naturalis.nda.elasticsearch.load.LoadConstants;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import nl.naturalis.nda.elasticsearch.load.Registry;
import nl.naturalis.nda.elasticsearch.load.XMLRecordInfo;

import org.domainobject.util.ConfigObject;
import org.domainobject.util.IOUtil;
import org.slf4j.Logger;

public class NsrImporter {

	public static void main(String[] args)
	{
		new NsrImporter().run();
	}

	private static final Logger logger;

	static {
		logger = Registry.getInstance().getLogger(NsrImporter.class);
	}

	private final boolean suppressErrors;
	private final int esBulkRequestSize;

	public NsrImporter()
	{
		suppressErrors = ConfigObject.isEnabled("nsr.suppress-errors");
		String key = LoadConstants.SYSPROP_ES_BULK_REQUEST_SIZE;
		String val = System.getProperty(key, "1000");
		esBulkRequestSize = Integer.parseInt(val);
	}

	public void run()
	{
		long start = System.currentTimeMillis();
		File[] xmlFiles = NsrImportUtil.getXmlFiles();
		if (xmlFiles.length == 0) {
			logger.info("No XML files to process");
			return;
		}
		LoadUtil.truncate(LUCENE_TYPE_TAXON, NSR);
		LoadUtil.truncate(LUCENE_TYPE_MULTIMEDIA_OBJECT, NSR);
		ETLStatistics tStats = new ETLStatistics();
		ETLStatistics mStats = new ETLStatistics();
		mStats.setUseObjectsAccepted(true);
		NsrExtractor extractor = null;
		NsrTaxonTransformer tTransformer = new NsrTaxonTransformer(tStats);
		tTransformer.setSuppressErrors(suppressErrors);
		NsrMultiMediaTransformer mTransformer = new NsrMultiMediaTransformer(mStats);
		mTransformer.setSuppressErrors(suppressErrors);
		NsrTaxonLoader taxonLoader = null;
		NsrMultiMediaLoader multimediaLoader = null;
		try {
			taxonLoader = new NsrTaxonLoader(esBulkRequestSize, tStats);
			multimediaLoader = new NsrMultiMediaLoader(esBulkRequestSize, mStats);
			for (File f : xmlFiles) {
				logger.info("Processing file " + f.getAbsolutePath());
				extractor = new NsrExtractor(f, tStats);
				int i = 0;
				for (XMLRecordInfo extracted : extractor) {
					List<ESTaxon> taxa = tTransformer.transform(extracted);
					taxonLoader.load(taxa);
					mTransformer.setTaxon(taxa == null ? null : taxa.get(0));
					List<ESMultiMediaObject> multimedia = mTransformer.transform(extracted);
					multimediaLoader.load(multimedia);
					if (++i % 5000 == 0)
						logger.info("Records processed: " + i);
				}
				backupXmlFile(f);
			}
		}
		finally {
			IOUtil.close(taxonLoader, multimediaLoader);
		}
		tStats.logStatistics(logger, "taxa");
		/*
		 * NB The multimedia transformer did not keep track of record-level
		 * statistics. Note though that the multimedia statistics object may
		 * still have a non-zero value for the recordsRejected counter, because
		 * that record could also have been updated by the multimedia loader, in
		 * case ElasticSearch could not index So now we need to copy
		 * record-level statics to the media statistics object.
		 */
		mStats.recordsProcessed = tStats.recordsProcessed;
		mStats.recordsSkipped = tStats.recordsSkipped;
		mStats.recordsRejected = tStats.recordsRejected;
		mStats.logStatistics(logger, "multimedia");
		LoadUtil.logDuration(logger, getClass(), start);
	}
}
