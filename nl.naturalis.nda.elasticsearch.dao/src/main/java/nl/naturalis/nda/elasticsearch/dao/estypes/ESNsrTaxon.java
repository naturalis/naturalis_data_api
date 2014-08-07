package nl.naturalis.nda.elasticsearch.dao.estypes;

import nl.naturalis.nda.domain.DefaultClassification;
import nl.naturalis.nda.domain.Monomial;
import nl.naturalis.nda.domain.systypes.NsrScientificName;
import nl.naturalis.nda.domain.systypes.NsrTaxonStatus;

public class ESNsrTaxon {

	private int id;
	private int parentId;
	private String nsrId;
	private String url;
	private String rank;

	private NsrScientificName scientificName;
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

	private NsrTaxonStatus nameStatus;


	public int getId()
	{
		return id;
	}


	public void setId(int id)
	{
		this.id = id;
	}


	public int getParentId()
	{
		return parentId;
	}


	public void setParentId(int parentId)
	{
		this.parentId = parentId;
	}


	public String getNsrId()
	{
		return nsrId;
	}


	public void setNsrId(String nsrId)
	{
		this.nsrId = nsrId;
	}


	public String getUrl()
	{
		return url;
	}


	public void setUrl(String url)
	{
		this.url = url;
	}


	public String getRank()
	{
		return rank;
	}


	public void setRank(String rank)
	{
		this.rank = rank;
	}


	public NsrScientificName getScientificName()
	{
		return scientificName;
	}


	public void setScientificName(NsrScientificName scientificName)
	{
		this.scientificName = scientificName;
	}


	public DefaultClassification getDefaultClassification()
	{
		return defaultClassification;
	}


	public void setDefaultClassification(DefaultClassification defaultClassification)
	{
		this.defaultClassification = defaultClassification;
	}


	public int getNumMonomials()
	{
		return numMonomials;
	}


	public void setNumMonomials(int numMonomials)
	{
		this.numMonomials = numMonomials;
	}


	public Monomial getMonomial00()
	{
		return monomial00;
	}


	public void setMonomial00(Monomial monomial00)
	{
		this.monomial00 = monomial00;
	}


	public Monomial getMonomial01()
	{
		return monomial01;
	}


	public void setMonomial01(Monomial monomial01)
	{
		this.monomial01 = monomial01;
	}


	public Monomial getMonomial02()
	{
		return monomial02;
	}


	public void setMonomial02(Monomial monomial02)
	{
		this.monomial02 = monomial02;
	}


	public Monomial getMonomial03()
	{
		return monomial03;
	}


	public void setMonomial03(Monomial monomial03)
	{
		this.monomial03 = monomial03;
	}


	public Monomial getMonomial04()
	{
		return monomial04;
	}


	public void setMonomial04(Monomial monomial04)
	{
		this.monomial04 = monomial04;
	}


	public Monomial getMonomial05()
	{
		return monomial05;
	}


	public void setMonomial05(Monomial monomial05)
	{
		this.monomial05 = monomial05;
	}


	public Monomial getMonomial06()
	{
		return monomial06;
	}


	public void setMonomial06(Monomial monomial06)
	{
		this.monomial06 = monomial06;
	}


	public Monomial getMonomial07()
	{
		return monomial07;
	}


	public void setMonomial07(Monomial monomial07)
	{
		this.monomial07 = monomial07;
	}


	public Monomial getMonomial08()
	{
		return monomial08;
	}


	public void setMonomial08(Monomial monomial08)
	{
		this.monomial08 = monomial08;
	}


	public Monomial getMonomial09()
	{
		return monomial09;
	}


	public void setMonomial09(Monomial monomial09)
	{
		this.monomial09 = monomial09;
	}


	public Monomial getMonomial10()
	{
		return monomial10;
	}


	public void setMonomial10(Monomial monomial10)
	{
		this.monomial10 = monomial10;
	}


	public Monomial getMonomial11()
	{
		return monomial11;
	}


	public void setMonomial11(Monomial monomial11)
	{
		this.monomial11 = monomial11;
	}


	public NsrTaxonStatus getNameStatus()
	{
		return nameStatus;
	}


	public void setNameStatus(NsrTaxonStatus nameStatus)
	{
		this.nameStatus = nameStatus;
	}

}
