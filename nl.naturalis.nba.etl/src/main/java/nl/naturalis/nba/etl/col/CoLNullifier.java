package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.dao.DocumentType.TAXON;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;

import java.util.ArrayList;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.AcidDocumentIterator;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.BulkIndexException;
import nl.naturalis.nba.etl.BulkIndexer;
import nl.naturalis.nba.etl.ETLRegistry;

/**
 * Sets the synonyms and/or vernacular names and/or literature references of all
 * {@link Taxon} documents from the Catalogue of Life to {@code null}. Useful if
 * you want re-import, for example, literature references, but leave everything
 * else intact.
 * 
 * @author Ayco Holleman
 *
 */
public class CoLNullifier {

	public static void main(String[] args) throws Exception
	{
		try {
			CoLNullifier nullifier = new CoLNullifier();
			if (args.length > 0) {
				nullifier.setNullifySynonyms(false);
				nullifier.setNullifyReferences(false);
				nullifier.setNullifyVernacularNames(false);
				for (String arg : args) {
					switch (arg.toLowerCase()) {
						case "-s":
							nullifier.setNullifySynonyms(true);
							break;
						case "-r":
							nullifier.setNullifyReferences(true);
							break;
						case "-v":
							nullifier.setNullifyVernacularNames(true);
							break;
						default:
							String fmt = "Illegal argument: %s. Valid arguments: -s "
									+ "(synonyms), -r (references), -v (vernacular names)";
							String msg = String.format(fmt, arg);
							throw new Exception(msg);

					}
				}
			}
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

	private int batchSize = 500;
	private boolean nullifySynonyms = true;
	private boolean nullifyReferences = true;
	private boolean nullifyVernacularNames = true;

	public void nullify() throws BulkIndexException
	{
		long start = System.currentTimeMillis();
		AcidDocumentIterator<Taxon> iterator = new AcidDocumentIterator<>(TAXON);
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
			if (nullifyReferences && taxon.getReferences() != null) {
				taxon.setReferences(null);
				modified = true;
			}
			if (nullifyVernacularNames && taxon.getVernacularNames() != null) {
				taxon.setVernacularNames(null);
				modified = true;
			}
			if (modified) {
				batch.add(taxon);
				++updated;
				if (batch.size() == batchSize) {
					indexer.index(batch);
					batch.clear();
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
