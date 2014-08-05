package nl.naturalis.nda.domain;

import java.util.List;

public class Taxon {

	private ScientificName acceptedName;

	private List<Synonym> synonyms;
	private List<CommonName> commonNames;
	private List<TaxonDescription> descriptions;

}
