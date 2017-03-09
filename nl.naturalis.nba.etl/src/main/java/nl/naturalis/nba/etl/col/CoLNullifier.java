package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.dao.DocumentType.TAXON;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;
import static nl.naturalis.nba.utils.CollectionUtil.isEmpty;

import java.util.ArrayList;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.DocumentIterator;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.BulkIndexException;
import nl.naturalis.nba.etl.BulkIndexer;
import nl.naturalis.nba.etl.ETLRegistry;

/**
 * Sets the synonyms, vernacular names and literature references of all
 * {@link Taxon} documents to {@code null}. This allows you to safely re-import
 * them again.
 * 
 * @author Ayco Holleman
 *
 */
public class CoLNullifier {

	public static void main(String[] args) throws BulkIndexException
	{
		try {
			CoLNullifier nullifier = new CoLNullifier();
			nullifier.nullify();
		}
		finally {
			ESUtil.refreshIndex(TAXON);
			ESClientManager.getInstance().closeClient();
		}
	}

	private static final Logger logger = ETLRegistry.getInstance().getLogger(CoLNullifier.class);

	public CoLNullifier()
	{
	}

	private int batchSize = 1000;
	private boolean nullifySynonyms = true;
	private boolean nullifyReferences = true;
	private boolean nullifyVernacularNames = true;

	public void nullify() throws BulkIndexException
	{
		long start = System.currentTimeMillis();
		DocumentIterator<Taxon> iterator = new DocumentIterator<>(TAXON);
		iterator.setBatchSize(batchSize);
		BulkIndexer<Taxon> indexer = new BulkIndexer<>(TAXON);
		ArrayList<Taxon> batch = new ArrayList<>(batchSize);
		int processed = 0;
		int updated = 0;
		logger.info("Processing taxa");
		for (Taxon taxon : iterator) {
			if (taxon.getSourceSystem() != SourceSystem.COL) {
				continue;
			}
			boolean modified = false;
			if (nullifySynonyms && taxon.getSynonyms() != null) {
				taxon.setSynonyms(null);
				modified = true;
			}
			if (nullifyReferences && !isEmpty(taxon.getReferences())) {
				taxon.setReferences(null);
				modified = true;
			}
			if (nullifyVernacularNames && !isEmpty(taxon.getVernacularNames())) {
				taxon.setVernacularNames(null);
				modified = true;
			}
			if (modified) {
				batch.add(taxon);
				++updated;
				if (batch.size() == batchSize) {
					indexer.index(batch);
				}
			}
			if (++processed % 100000 == 0) {
				logger.info("Taxa processed: {}", processed);
				logger.info("Taxa updated: {}", updated);
			}
		}
		if (batch.size() != 0) {
			indexer.index(batch);
		}
		logger.info("Taxa processed: {}", processed);
		logger.info("Taxa updated: {}", updated);
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

	public boolean isNullifySynonyms()
	{
		return nullifySynonyms;
	}

	public void setNullifySynonyms(boolean nullifySynonyms)
	{
		this.nullifySynonyms = nullifySynonyms;
	}

	public boolean isNullifyReferences()
	{
		return nullifyReferences;
	}

	public void setNullifyReferences(boolean nullifyReferences)
	{
		this.nullifyReferences = nullifyReferences;
	}

	public boolean isNullifyVernacularNames()
	{
		return nullifyVernacularNames;
	}

	public void setNullifyVernacularNames(boolean nullifyVernacularNames)
	{
		this.nullifyVernacularNames = nullifyVernacularNames;
	}

}
