package nl.naturalis.nba.api.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GatheringEvent extends NBADomainObject {

	private String projectTitle;
	private String worldRegion;
	/*
	 * This is not strictly ABCD, but this information is provided by some
	 * Naturalis data sources, and the meaning and specifity of continent is
	 * rather more obvious than world region. If a data source provides a
	 * continent, but not a world region, {@code content} and {@code
	 * worldRegion} will both be set to the provided continent. If a data source
	 * provides a world region, but not a continent, only the {@code
	 * worldRegion} field will be set to the world region, unless a continent
	 * could be parsed out of the world region.
	 */
	private String continent;
	private String country;
	private String iso3166Code;
	private String provinceState;
	private String island;
	private String locality;
	/*
	 * This is not strictly ABCD, but it enables the generation of IPTC data
	 * ({@link Iptc4xmpExt} objects) from {@link GatheringEvent}s. If a
	 * Naturalis data source happens to provide a city but not a locality, both
	 * {@code city} and {@code locality} will be set to the provided locality.
	 * If a locality was provided but not a city, only the {@code locality}
	 * field will be set, unless a city could be parsed out of the locality.
	 */
	private String city;
	/*
	 * This is not strictly ABCD, but it enables the generation of IPTC data
	 * ({@link Iptc4xmpExt} objects) from {@link GatheringEvent}s.
	 */
	private String sublocality;
	private String localityText;
	private Date dateTimeBegin;
	private Date dateTimeEnd;
	private String method;
	private String altitude;
	private String altitudeUnifOfMeasurement;
	private String depth;
	private String depthUnitOfMeasurement;
	private List<Person> gatheringPersons;
	private List<Organization> gatheringOrganizations;
	private List<GatheringSiteCoordinates> siteCoordinates;
	private List<ChronoStratigraphy> chronoStratigraphy;
	private List<BioStratigraphy> bioStratigraphic;
	private List<LithoStratigraphy> lithoStratigraphy;

	public void addSiteCoordinates(GatheringSiteCoordinates coordinates)
	{
		if (siteCoordinates == null) {
			siteCoordinates = new ArrayList<GatheringSiteCoordinates>();
		}
		siteCoordinates.add(coordinates);
	}

	public void addSiteCoordinates(Double latitude, Double longitude)
	{
		addSiteCoordinates(new GatheringSiteCoordinates(latitude, longitude));
	}

	public String getProjectTitle()
	{
		return projectTitle;
	}

	public void setProjectTitle(String projectTitle)
	{
		this.projectTitle = projectTitle;
	}

	public String getWorldRegion()
	{
		return worldRegion;
	}

	public void setWorldRegion(String worldRegion)
	{
		this.worldRegion = worldRegion;
	}

	public String getContinent()
	{
		return continent;
	}

	public void setContinent(String continent)
	{
		this.continent = continent;
	}

	public String getCountry()
	{
		return country;
	}

	public void setCountry(String country)
	{
		this.country = country;
	}

	public String getIso3166Code()
	{
		return iso3166Code;
	}

	public void setIso3166Code(String iso3166Code)
	{
		this.iso3166Code = iso3166Code;
	}

	public String getProvinceState()
	{
		return provinceState;
	}

	public void setProvinceState(String provinceState)
	{
		this.provinceState = provinceState;
	}

	public String getIsland()
	{
		return island;
	}

	public void setIsland(String island)
	{
		this.island = island;
	}

	public String getLocality()
	{
		return locality;
	}

	public void setLocality(String locality)
	{
		this.locality = locality;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getSublocality()
	{
		return sublocality;
	}

	public void setSublocality(String sublocality)
	{
		this.sublocality = sublocality;
	}

	public String getLocalityText()
	{
		return localityText;
	}

	public void setLocalityText(String localityText)
	{
		this.localityText = localityText;
	}

	public Date getDateTimeBegin()
	{
		return dateTimeBegin;
	}

	public void setDateTimeBegin(Date dateTimeBegin)
	{
		this.dateTimeBegin = dateTimeBegin;
	}

	public Date getDateTimeEnd()
	{
		return dateTimeEnd;
	}

	public void setDateTimeEnd(Date dateTimeEnd)
	{
		this.dateTimeEnd = dateTimeEnd;
	}

	public String getMethod()
	{
		return method;
	}

	public void setMethod(String method)
	{
		this.method = method;
	}

	public String getAltitude()
	{
		return altitude;
	}

	public void setAltitude(String altitude)
	{
		this.altitude = altitude;
	}

	public String getAltitudeUnifOfMeasurement()
	{
		return altitudeUnifOfMeasurement;
	}

	public void setAltitudeUnifOfMeasurement(String altitudeUnifOfMeasurement)
	{
		this.altitudeUnifOfMeasurement = altitudeUnifOfMeasurement;
	}

	public String getDepth()
	{
		return depth;
	}

	public void setDepth(String depth)
	{
		this.depth = depth;
	}

	public String getDepthUnitOfMeasurement()
	{
		return depthUnitOfMeasurement;
	}

	public void setDepthUnitOfMeasurement(String depthUnitOfMeasurement)
	{
		this.depthUnitOfMeasurement = depthUnitOfMeasurement;
	}

	public List<Person> getGatheringPersons()
	{
		return gatheringPersons;
	}

	public void setGatheringPersons(List<Person> gatheringPersons)
	{
		this.gatheringPersons = gatheringPersons;
	}

	public List<Organization> getGatheringOrganizations()
	{
		return gatheringOrganizations;
	}

	public void setGatheringOrganizations(List<Organization> gatheringOrganizations)
	{
		this.gatheringOrganizations = gatheringOrganizations;
	}

	public List<GatheringSiteCoordinates> getSiteCoordinates()
	{
		return siteCoordinates;
	}

	public void setSiteCoordinates(List<GatheringSiteCoordinates> siteCoordinates)
	{
		this.siteCoordinates = siteCoordinates;
	}

	public List<ChronoStratigraphy> getChronoStratigraphy()
	{
		return chronoStratigraphy;
	}

	public void setChronoStratigraphy(List<ChronoStratigraphy> chronoStratigraphy)
	{
		this.chronoStratigraphy = chronoStratigraphy;
	}

	public List<BioStratigraphy> getBioStratigraphic()
	{
		return bioStratigraphic;
	}

	public void setBioStratigraphic(List<BioStratigraphy> bioStratigraphic)
	{
		this.bioStratigraphic = bioStratigraphic;
	}

	public List<LithoStratigraphy> getLithoStratigraphy()
	{
		return lithoStratigraphy;
	}

	public void setLithoStratigraphy(List<LithoStratigraphy> lithoStratigraphy)
	{
		this.lithoStratigraphy = lithoStratigraphy;
	}

}
