package nl.naturalis.nda.elasticsearch.load.col;

import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_TAXON;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.load.CSVExtractor;
import nl.naturalis.nda.elasticsearch.load.CSVRecordInfo;
import nl.naturalis.nda.elasticsearch.load.ETLRuntimeException;
import nl.naturalis.nda.elasticsearch.load.ExtractionException;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import nl.naturalis.nda.elasticsearch.load.Registry;

import org.domainobject.util.ConfigObject;
import org.domainobject.util.IOUtil;
import org.slf4j.Logger;

public class CoLTaxonImporter {

	public static void main(String[] args) throws Exception
	{
		IndexNative index = null;
		try {
			index = Registry.getInstance().getNbaIndexManager();
			CoLTaxonImporter importer = new CoLTaxonImporter(index);
			String dwcaDir = Registry.getInstance().getConfig().required("col.csv_dir");
			importer.importCsv(dwcaDir + "/taxa.txt");
		}
		finally {
			if (index != null) {
				index.getClient().close();
			}
		}
	}

	private static final Logger logger = Registry.getInstance().getLogger(CoLTaxonImporter.class);

	private final IndexNative index;
	private final boolean suppressErrors;
	private final String colYear;

	public CoLTaxonImporter(IndexNative index)
	{
		this.index = index;
		suppressErrors = ConfigObject.TRUE("col.suppress-errors");
		colYear = Registry.getInstance().getConfig().required("col.year");
	}

	// Override
	public void importCsv(String path)
	{
		long start = System.currentTimeMillis();
		CSVExtractor extractor = null;
		CoLTaxonTransformer transformer = null;
		CoLTaxonLoader loader = null;
		try {
			File f = new File(path);
			if (!f.exists())
				throw new ETLRuntimeException("No such file: " + path);
			index.deleteWhere(LUCENE_TYPE_TAXON, "sourceSystem.code", SourceSystem.COL.getCode());
			transformer = new CoLTaxonTransformer();
			transformer.setColYear(colYear);
			transformer.setSuppressErrors(suppressErrors);
			loader = new CoLTaxonLoader(index);
			logger.info("Processing file " + f.getAbsolutePath());
			extractor = new CSVExtractor(f);
			extractor.setSkipHeader(true);
			extractor.setDelimiter('\t');
			Iterator<CSVRecordInfo> iterator = extractor.iterator();
			while (iterator.hasNext()) {
				try {
					CSVRecordInfo record = iterator.next();
					List<ESTaxon> taxa = transformer.transform(record);
					loader.load(taxa);
					if (record.getLineNumber() % 50000 == 0) {
						logger.info("Records processed: " + record.getLineNumber());
					}
				}
				catch (ExtractionException e) {
					if (!suppressErrors) {
						logger.error("Line " + e.getLineNumber() + ": " + e.getMessage());
						logger.error(e.getLine());
					}
				}
			}
		}
		catch (Throwable t) {
			logger.error(getClass().getSimpleName() + " terminated unexpectedly!", t);
		}
		finally {
			IOUtil.close(loader);
		}
		logger.info(getClass().getSimpleName() + " took " + LoadUtil.getDuration(start));
	}
}
