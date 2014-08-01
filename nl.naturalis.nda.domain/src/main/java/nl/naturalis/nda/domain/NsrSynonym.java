package nl.naturalis.nda.domain;

public class NsrSynonym extends NsrScientificName {

	private NsrTaxon taxon;


	public NsrTaxon getTaxon()
	{
		return taxon;
	}


	public void setTaxon(NsrTaxon taxon)
	{
		this.taxon = taxon;
	}

}
