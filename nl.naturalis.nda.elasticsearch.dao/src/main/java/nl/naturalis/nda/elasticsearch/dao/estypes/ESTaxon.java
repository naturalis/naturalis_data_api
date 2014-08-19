package nl.naturalis.nda.elasticsearch.dao.estypes;

import java.util.List;

import nl.naturalis.nda.domain.DefaultClassification;
import nl.naturalis.nda.domain.Monomial;
import nl.naturalis.nda.domain.NdaTraceableObject;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.TaxonDescription;

public class ESTaxon extends NdaTraceableObject {

	private ScientificName acceptedName;
	private String taxonRank;

	private DefaultClassification defaultClassification;
	
	private int numMonomials;
	private Monomial monomial00;
	private Monomial monomial01;
	private Monomial monomial02;
	private Monomial monomial03;
	private Monomial monomial04;
	private Monomial monomial05;
	private Monomial monomial06;
	private Monomial monomial07;
	private Monomial monomial08;
	private Monomial monomial09;
	private Monomial monomial10;
	private Monomial monomial11;

	private List<String> synonyms;
	private List<String> commonNames;

	private int numDescriptions;
	private TaxonDescription description00;
	private TaxonDescription description01;
	private TaxonDescription description02;
	private TaxonDescription description03;
	private TaxonDescription description04;
	private TaxonDescription description05;
	private TaxonDescription description06;
	private TaxonDescription description07;
	private TaxonDescription description08;
	private TaxonDescription description09;

}
