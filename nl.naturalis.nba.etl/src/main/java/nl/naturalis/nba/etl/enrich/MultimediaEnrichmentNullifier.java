package nl.naturalis.nba.etl.enrich;

import static nl.naturalis.nba.dao.DocumentType.MULTI_MEDIA_OBJECT;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;

import java.util.ArrayList;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.api.model.MultiMediaContentIdentification;
import nl.naturalis.nba.api.model.TaxonomicEnrichment;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.AcidDocumentIterator;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.BulkIndexException;
import nl.naturalis.nba.etl.BulkIndexer;
import nl.naturalis.nba.etl.ETLRegistry;

/**
 * Nullifies the {@link TaxonomicEnrichment} objects within specimens. Useful if
 * something went wrong during taxonomic enrichment (see
 * {@link SpecimenTaxonomicEnricher}) and you have to do it over again.
 * 
 * @author Ayco Holleman
 *
 */
public class MultimediaEnrichmentNullifier {

	public static void main(String[] args) throws Exception
	{
		try {
			MultimediaEnrichmentNullifier nullifier = new MultimediaEnrichmentNullifier();
			nullifier.nullify();
		}
		finally {
			ESUtil.refreshIndex(MULTI_MEDIA_OBJECT);
			ESClientManager.getInstance().closeClient();
		}
	}

	private static final Logger logger = ETLRegistry.getInstance()
			.getLogger(MultimediaEnrichmentNullifier.class);

	public MultimediaEnrichmentNullifier()
	{
	}

	private int batchSize = 500;

	public void nullify() throws BulkIndexException
	{
		long start = System.currentTimeMillis();
		AcidDocumentIterator<MultiMediaObject> iterator = new AcidDocumentIterator<>(MULTI_MEDIA_OBJECT);
		iterator.setBatchSize(batchSize);
		BulkIndexer<MultiMediaObject> indexer = new BulkIndexer<>(MULTI_MEDIA_OBJECT);
		ArrayList<MultiMediaObject> batch = new ArrayList<>(batchSize);
		int processed = 0;
		int updated = 0;
		logger.info("Processing multimedia");
		for (MultiMediaObject mmo : iterator) {
			boolean modified = false;
			for (MultiMediaContentIdentification mmci : mmo.getIdentifications()) {
				if (mmci.getTaxonomicEnrichments() != null) {
					mmci.setTaxonomicEnrichments(null);
					modified = true;
				}
			}
			if (modified) {
				batch.add(mmo);
				++updated;
				if (batch.size() == batchSize) {
					indexer.index(batch);
					batch.clear();
				}
			}
			if (++processed % 100000 == 0) {
				logger.info("Multimedia processed: {}", processed);
				logger.info("Multimedia updated: {}", updated);
			}
		}
		if (batch.size() != 0) {
			indexer.index(batch);
		}
		logger.info("Multimedia processed: {}", processed);
		logger.info("Multimedia updated: {}", updated);
		logDuration(logger, getClass(), start);
	}

	public int getBatchSize()
	{
		return batchSize;
	}

	public void setBatchSize(int batchSize)
	{
		this.batchSize = batchSize;
	}

}
