package nl.naturalis.nda.domain.systypes;

import java.util.List;

import nl.naturalis.nda.domain.DefaultClassification;

public class NsrTaxon {

	private int id;
	private int parentId;
	private String nsrId;
	private String url;
	private String rank;
	
	private NsrScientificName acceptedName;
	private DefaultClassification defaultClassification;
	private List<NsrMonomial> providedClassification;

	private NsrTaxonStatus status;

	private List<NsrSynonym> synonyms;
	private List<NsrCommonName> commonNames;
	private List<NsrTaxonDescription> descriptions;
	


}
