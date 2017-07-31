package nl.naturalis.nba.etl.enrich;

import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.TaxonomicEnrichment;
import nl.naturalis.nba.api.model.TaxonomicIdentification;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.DirtyDocumentIterator;
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
public class SpecimenEnrichmentNullifier {

	public static void main(String[] args) throws Exception
	{
		try {
			SpecimenEnrichmentNullifier nullifier = new SpecimenEnrichmentNullifier();
			if (args.length == 0 || args.length > 2) {
				System.err.println("USAGE: java SpecimenEnrichmentNullifier -t|-m");
				System.err.println("       -t  Nullify taxonomic enrichments");
				System.err.println("       -m  Nullify multimedia enrichments");
				System.exit(1);
			}
			Set<String> opts = new HashSet<>(Arrays.asList(args));
			if (opts.contains("-t")) {
				nullifier.setNullifyTaxonomicEnrichments(true);
			}
			if (opts.contains("-m")) {
				nullifier.setNullifyMultiMediaUris(true);
			}
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

	private int batchSize = 1000;
	private boolean nullifyTaxonomicEnrichments;
	private boolean nullifyMultiMediaUris;

	public void nullify() throws BulkIndexException
	{
		long start = System.currentTimeMillis();
		logger.info("Nullify taxonomic enrichments: " + nullifyTaxonomicEnrichments);
		logger.info("Nullify multimedia URIs: " + nullifyMultiMediaUris);
		DocumentType<Specimen> dt = SPECIMEN;
		QuerySpec qs = new QuerySpec();
		qs.setConstantScore(true);
		qs.setSize(batchSize);
		if (!nullifyTaxonomicEnrichments) {
			qs.addCondition(new QueryCondition("sourceSystem.code", "=", "CRS"));
		}
		DirtyDocumentIterator<Specimen> iterator = new DirtyDocumentIterator<>(dt, qs);
		BulkIndexer<Specimen> indexer = new BulkIndexer<>(SPECIMEN);
		ArrayList<Specimen> batch = new ArrayList<>(batchSize);
		int processed = 0;
		int updated = 0;
		logger.info("Processing specimens");
		for (Specimen specimen : iterator) {
			boolean modified = false;
			if (nullifyTaxonomicEnrichments) {
				for (TaxonomicIdentification si : specimen.getIdentifications()) {
					if (si.getTaxonomicEnrichments() != null) {
						si.setTaxonomicEnrichments(null);
						modified = true;
					}
				}
			}
			if (nullifyMultiMediaUris && specimen.getSourceSystem() == SourceSystem.CRS) {
				if (specimen.getAssociatedMultiMediaUris() != null) {
					specimen.setAssociatedMultiMediaUris(null);
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

	public boolean isNullifyTaxonomicEnrichments()
	{
		return nullifyTaxonomicEnrichments;
	}

	public void setNullifyTaxonomicEnrichments(boolean nullifyTaxonomicEnrichments)
	{
		this.nullifyTaxonomicEnrichments = nullifyTaxonomicEnrichments;
	}

	public boolean isNullifyMultiMediaUris()
	{
		return nullifyMultiMediaUris;
	}

	public void setNullifyMultiMediaUris(boolean nullifyMultiMediaUris)
	{
		this.nullifyMultiMediaUris = nullifyMultiMediaUris;
	}

}
