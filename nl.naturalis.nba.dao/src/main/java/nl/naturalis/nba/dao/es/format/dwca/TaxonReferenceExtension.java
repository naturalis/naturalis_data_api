package nl.naturalis.nba.dao.es.format.dwca;


public class TaxonReferenceExtension extends Extension {

	public TaxonReferenceExtension()
	{
		super();
		Files files = new Files();
		files.setLocation("reference.txt");
		CoreId coreId = new CoreId();
		coreId.setIndex(0);
	}

}
