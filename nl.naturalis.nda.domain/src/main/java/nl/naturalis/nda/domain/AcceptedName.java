package nl.naturalis.nda.domain;

import java.util.List;

public class AcceptedName extends ScientificName {

	private String kingdom;
	private String phylum;
	private String className;
	private String order;
	private String superfamily;
	private String family;
	private String genus;
	private String subgenus;
	private String specificEpithet;
	private String infraspecificEpithet;
	
	// This allows for classification that deviates from
	// the fixed classification through the above fields
	private List<Monomial> actualClassification;
	
	
}
