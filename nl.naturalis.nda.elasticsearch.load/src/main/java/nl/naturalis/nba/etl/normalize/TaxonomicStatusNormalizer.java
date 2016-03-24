package nl.naturalis.nba.etl.normalize;

import nl.naturalis.nba.api.model.TaxonomicStatus;

/**
 * Normalizes different names for the various taxonomic statuses.
 * 
 * @author Ayco Holleman
 *
 */
public class TaxonomicStatusNormalizer extends ClasspathMappingFileNormalizer<TaxonomicStatus> {

	private static final String RESOURCE = "/normalize/taxonomic-status.csv";

	private static TaxonomicStatusNormalizer instance;

	public static TaxonomicStatusNormalizer getInstance()
	{
		if (instance == null) {
			instance = new TaxonomicStatusNormalizer();
		}
		return instance;
	}

	private TaxonomicStatusNormalizer()
	{
		super(TaxonomicStatus.class, RESOURCE);
	}

}
