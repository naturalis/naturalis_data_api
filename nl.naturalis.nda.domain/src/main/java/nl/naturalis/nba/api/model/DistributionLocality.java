package nl.naturalis.nba.api.model;



public class DistributionLocality
{
	private String locality;

	public String getLocality()
	{
		return locality;
	}

	public void setLocality(String locality)
	{
		this.locality = locality;
	}

	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof DistributionLocality)) {
			return false;
		}
		DistributionLocality other = (DistributionLocality) obj;
		return locality.equals(other.locality);
	}
}
