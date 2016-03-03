package nl.naturalis.nda.elasticsearch.load.normalize;

import nl.naturalis.nda.domain.TaxonomicStatus;

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
