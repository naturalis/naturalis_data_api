package nl.naturalis.nda.elasticsearch.load.normalize;

import nl.naturalis.nda.domain.TaxonomicStatus;

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
