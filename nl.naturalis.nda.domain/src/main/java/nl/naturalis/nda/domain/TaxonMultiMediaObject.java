package nl.naturalis.nda.domain;

public class TaxonMultiMediaObject extends MultiMediaObject {

	private Taxon taxon;


	public Taxon getTaxon()
	{
		return taxon;
	}


	public void setTaxon(Taxon taxon)
	{
		this.taxon = taxon;
	}

}
