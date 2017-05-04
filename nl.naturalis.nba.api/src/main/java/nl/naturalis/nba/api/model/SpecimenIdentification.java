package nl.naturalis.nba.api.model;

import static nl.naturalis.nba.api.annotations.Analyzer.CASE_INSENSITIVE;
import static nl.naturalis.nba.api.annotations.Analyzer.DEFAULT;
import static nl.naturalis.nba.api.annotations.Analyzer.LIKE;

import nl.naturalis.nba.api.annotations.Analyzers;

public class SpecimenIdentification extends TaxonomicIdentification {

	private boolean preferred;
	private String verificationStatus;
	private String rockType;
	private String associatedFossilAssemblage;
	private String rockMineralUsage;
	private String associatedMineralName;
	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String remarks;

	public boolean isPreferred()
	{
		return preferred;
	}

	public void setPreferred(boolean preferred)
	{
		this.preferred = preferred;
	}

	public String getVerificationStatus()
	{
		return verificationStatus;
	}

	public void setVerificationStatus(String verificationStatus)
	{
		this.verificationStatus = verificationStatus;
	}

	public String getRockType()
	{
		return rockType;
	}

	public void setRockType(String rockType)
	{
		this.rockType = rockType;
	}

	public String getAssociatedFossilAssemblage()
	{
		return associatedFossilAssemblage;
	}

	public void setAssociatedFossilAssemblage(String associatedFossilAssemblage)
	{
		this.associatedFossilAssemblage = associatedFossilAssemblage;
	}

	public String getRockMineralUsage()
	{
		return rockMineralUsage;
	}

	public void setRockMineralUsage(String rockMineralUsage)
	{
		this.rockMineralUsage = rockMineralUsage;
	}

	public String getAssociatedMineralName()
	{
		return associatedMineralName;
	}

	public void setAssociatedMineralName(String associatedMineralName)
	{
		this.associatedMineralName = associatedMineralName;
	}

	public String getRemarks()
	{
		return remarks;
	}

	public void setRemarks(String remarks)
	{
		this.remarks = remarks;
	}

}
