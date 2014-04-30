package nl.naturalis.nda.domain;

public class Determination {

	private boolean preferred;
	private String scientificName;
	private String genusOrMonomial;
	private String subgenus;
	private String speciesEpithet;
	private String infraSubspecificRank;
	private String subSpeciesEpithet;
	private String infraSubspecificName;
	private String authorTeamOriginalAndYear;
	private String typeStatus;
	private String nameAddendum;
	private String identificationQualifier1;
	private String identificationQualifier2;
	private String identificationQualifier3;

	private Specimen specimen;


	public boolean isPreferred()
	{
		return preferred;
	}


	public void setPreferred(boolean preferred)
	{
		this.preferred = preferred;
	}


	public String getScientificName()
	{
		return scientificName;
	}


	public void setScientificName(String scientificName)
	{
		this.scientificName = scientificName;
	}


	public String getGenusOrMonomial()
	{
		return genusOrMonomial;
	}


	public void setGenusOrMonomial(String genusOrMonomial)
	{
		this.genusOrMonomial = genusOrMonomial;
	}


	public String getSubgenus()
	{
		return subgenus;
	}


	public void setSubgenus(String subgenus)
	{
		this.subgenus = subgenus;
	}


	public String getSpeciesEpithet()
	{
		return speciesEpithet;
	}


	public void setSpeciesEpithet(String speciesEpithet)
	{
		this.speciesEpithet = speciesEpithet;
	}


	public String getInfraSubspecificRank()
	{
		return infraSubspecificRank;
	}


	public void setInfraSubspecificRank(String infraSubspecificRank)
	{
		this.infraSubspecificRank = infraSubspecificRank;
	}


	public String getSubSpeciesEpithet()
	{
		return subSpeciesEpithet;
	}


	public void setSubSpeciesEpithet(String subSpeciesEpithet)
	{
		this.subSpeciesEpithet = subSpeciesEpithet;
	}


	public String getInfraSubspecificName()
	{
		return infraSubspecificName;
	}


	public void setInfraSubspecificName(String infraSubspecificName)
	{
		this.infraSubspecificName = infraSubspecificName;
	}


	public String getAuthorTeamOriginalAndYear()
	{
		return authorTeamOriginalAndYear;
	}


	public void setAuthorTeamOriginalAndYear(String authorTeamOriginalAndYear)
	{
		this.authorTeamOriginalAndYear = authorTeamOriginalAndYear;
	}


	public String getTypeStatus()
	{
		return typeStatus;
	}


	public void setTypeStatus(String typeStatus)
	{
		this.typeStatus = typeStatus;
	}


	public String getNameAddendum()
	{
		return nameAddendum;
	}


	public void setNameAddendum(String nameAddendum)
	{
		this.nameAddendum = nameAddendum;
	}


	public String getIdentificationQualifier1()
	{
		return identificationQualifier1;
	}


	public void setIdentificationQualifier1(String identificationQualifier1)
	{
		this.identificationQualifier1 = identificationQualifier1;
	}


	public String getIdentificationQualifier2()
	{
		return identificationQualifier2;
	}


	public void setIdentificationQualifier2(String identificationQualifier2)
	{
		this.identificationQualifier2 = identificationQualifier2;
	}


	public String getIdentificationQualifier3()
	{
		return identificationQualifier3;
	}


	public void setIdentificationQualifier3(String identificationQualifier3)
	{
		this.identificationQualifier3 = identificationQualifier3;
	}


	public Specimen getSpecimen()
	{
		return specimen;
	}


	public void setSpecimen(Specimen specimen)
	{
		this.specimen = specimen;
	}

}
