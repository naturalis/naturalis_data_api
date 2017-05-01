package nl.naturalis.nba.etl.enrich;

import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;

import java.util.ArrayList;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.api.model.TaxonomicEnrichment;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.DocumentIterator;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.BulkIndexException;
import nl.naturalis.nba.etl.BulkIndexer;
import nl.naturalis.nba.etl.ETLRegistry;

/**
 * Nullifies the {@link TaxonomicEnrichment} objects within specimens. Useful if
 * something went wrong during taxonomic enrichment (see
 * {@link SpecimenEnricher}) and you have to do it over again.
 * 
 * @author Ayco Holleman
 *
 */
public class SpecimenEnrichmentNullifier {

	public static void main(String[] args) throws Exception
	{
		try {
			SpecimenEnrichmentNullifier nullifier = new SpecimenEnrichmentNullifier();
			nullifier.nullify();
		}
		finally {
			ESUtil.refreshIndex(SPECIMEN);
			ESClientManager.getInstance().closeClient();
		}
	}

	private static final Logger logger = ETLRegistry.getInstance()
			.getLogger(SpecimenEnrichmentNullifier.class);

	public SpecimenEnrichmentNullifier()
	{
	}

	private int batchSize = 500;

	public void nullify() throws BulkIndexException
	{
		long start = System.currentTimeMillis();
		DocumentIterator<Specimen> iterator = new DocumentIterator<>(SPECIMEN);
		iterator.setBatchSize(batchSize);
		BulkIndexer<Specimen> indexer = new BulkIndexer<>(SPECIMEN);
		ArrayList<Specimen> batch = new ArrayList<>(batchSize);
		int processed = 0;
		int updated = 0;
		logger.info("Processing specimens");
		for (Specimen specimen : iterator) {
			boolean modified = false;
			for (SpecimenIdentification si : specimen.getIdentifications()) {
				if (si.getTaxonomicEnrichments() != null) {
					si.setTaxonomicEnrichments(null);
					modified = true;
				}
			}
			if (modified) {
				batch.add(specimen);
				++updated;
				if (batch.size() == batchSize) {
					indexer.index(batch);
					batch.clear();
				}
			}
			if (++processed % 100000 == 0) {
				logger.info("Specimens processed: {}", processed);
				logger.info("Specimens updated: {}", updated);
			}
		}
		if (batch.size() != 0) {
			indexer.index(batch);
		}
		logger.info("Specimens processed: {}", processed);
		logger.info("Specimens updated: {}", updated);
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
