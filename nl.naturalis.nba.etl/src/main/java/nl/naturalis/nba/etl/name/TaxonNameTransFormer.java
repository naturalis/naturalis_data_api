package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.name.NameImportUtil.copySpecimen;
import static nl.naturalis.nba.etl.name.NameImportUtil.createName;
import static nl.naturalis.nba.etl.name.NameImportUtil.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.NameGroup;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.api.model.Taxon;

class TaxonNameTransformer {

	private static final Logger logger = getLogger(TaxonNameTransformer.class);

	private HashMap<String, NameGroup> nameCache;
	private int batchSize;

	private int created;
	private int updated;

	TaxonNameTransformer(int batchSize)
	{
		this.batchSize = batchSize;
		this.nameCache = new HashMap<>(batchSize + 8, 1F);
	}

	public Collection<NameGroup> transform(Collection<Taxon> taxa)
	{
		return nameCache.values();
	}

	public int getNumCreated()
	{
		return created;
	}

	public int getNumUpdated()
	{
		return updated;
	}

	private void transformOne(Taxon taxon)
	{

	}

	private void prepareForBatch(Collection<Taxon> taxa)
	{
	}

}
