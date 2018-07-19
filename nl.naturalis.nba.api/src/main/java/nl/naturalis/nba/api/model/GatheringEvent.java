package nl.naturalis.nba.api.model;

import static nl.naturalis.nba.api.annotations.Analyzer.CASE_INSENSITIVE;
import static nl.naturalis.nba.api.annotations.Analyzer.DEFAULT;
import static nl.naturalis.nba.api.annotations.Analyzer.LIKE;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nba.api.annotations.Analyzers;

public class GatheringEvent implements INbaModelObject {

	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String projectTitle;
	@Analyzers({ DEFAULT, CASE_INSENSITIVE, LIKE })
	private String worldRegion;
	@Analyzers({ DEFAULT, CASE_INSENSITIVE, LIKE })
	private String continent;
	@Analyzers({ DEFAULT, CASE_INSENSITIVE, LIKE })
	private String country;
	private String iso3166Code;
	@Analyzers({ DEFAULT, CASE_INSENSITIVE, LIKE })
	private String provinceState;
	@Analyzers({ DEFAULT, CASE_INSENSITIVE, LIKE })
	private String island;
	@Analyzers({ DEFAULT, CASE_INSENSITIVE, LIKE })
	private String locality;
	@Analyzers({ DEFAULT, CASE_INSENSITIVE, LIKE })
	private String city;
	@Analyzers({ DEFAULT, CASE_INSENSITIVE, LIKE })
	private String sublocality;
	@Analyzers({ DEFAULT, CASE_INSENSITIVE, LIKE })
	private String localityText;
	private OffsetDateTime dateTimeBegin;
	private OffsetDateTime dateTimeEnd;
	private String dateText;
	private String method;
	private String altitude;
	private String altitudeUnifOfMeasurement;
	private String behavior;
	private String biotopeText;
	private String depth;
	private String depthUnitOfMeasurement;
	
	private List<Person> gatheringPersons;
	private List<Organization> gatheringOrganizations;
	private List<GatheringSiteCoordinates> siteCoordinates;
	private List<NamedArea> namedAreas;
  private List<AssociatedTaxon> associatedTaxa;

	private List<ChronoStratigraphy> chronoStratigraphy;
	private List<BioStratigraphy> bioStratigraphy;
	private List<LithoStratigraphy> lithoStratigraphy;

	public void addSiteCoordinates(GatheringSiteCoordinates coordinates)
	{
		if (siteCoordinates == null) {
			siteCoordinates = new ArrayList<>();
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

	/**
	 * This is not strictly ABCD, but this information is provided by some
	 * Naturalis data sources, and the meaning and specifity of continent is
	 * rather more obvious than world region. If a data source provides a
	 * continent, but not a world region, {@code content} and {@code
	 * worldRegion} will both be set to the provided continent. If a data source
	 * provides a world region, but not a continent, only the {@code
	 * worldRegion} field will be set to the world region, unless a continent
	 * could be parsed out of the world region.
	 */
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

	/**
	 * This is not strictly ABCD, but it enables the generation of IPTC data
	 * ({@link Iptc4xmpExt} objects) from {@link GatheringEvent}s. If a
	 * Naturalis data source happens to provide a city but not a locality, both
	 * {@code city} and {@code locality} will be set to the provided locality.
	 * If a locality was provided but not a city, only the {@code locality}
	 * field will be set, unless a city could be parsed out of the locality.
	 */
	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	/**
	 * This is not strictly ABCD, but it enables the generation of IPTC data
	 * ({@link Iptc4xmpExt} objects) from {@link GatheringEvent}s.
	 */
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

	public OffsetDateTime getDateTimeBegin()
	{
		return dateTimeBegin;
	}

	public void setDateTimeBegin(OffsetDateTime dateTimeBegin)
	{
		this.dateTimeBegin = dateTimeBegin;
	}

	public OffsetDateTime getDateTimeEnd()
	{
		return dateTimeEnd;
	}

	public void setDateTimeEnd(OffsetDateTime dateTimeEnd)
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
	
	public List<NamedArea> getNamedAreas()
	{
	  return namedAreas;
	}
	
	public void setNamedAreas(List<NamedArea> namedAreas)
	{
	  this.namedAreas = namedAreas;
	}
	
	public List<AssociatedTaxon> getAssociatedTaxa()
	{
	  return associatedTaxa;
	}
	
	public void setAssociatedTaxa(List<AssociatedTaxon> associatedTaxa)
	{
	  this.associatedTaxa = associatedTaxa;
	}
	
	public String getBiotopeText() 
	{
	  return biotopeText;
	}
	
	public void setBiotopeText(String biotopeText)
	{
	  this.biotopeText = biotopeText;
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
		return bioStratigraphy;
	}

	public void setBioStratigraphy(List<BioStratigraphy> bioStratigraphic)
	{
		this.bioStratigraphy = bioStratigraphic;
	}

	public List<LithoStratigraphy> getLithoStratigraphy()
	{
		return lithoStratigraphy;
	}

	public void setLithoStratigraphy(List<LithoStratigraphy> lithoStratigraphy)
	{
		this.lithoStratigraphy = lithoStratigraphy;
	}

  public String getDateText() {
    return dateText;
  }

  public void setDateText(String dateText) {
    this.dateText = dateText;
  }

  public String getBehavior() {
    return behavior;
  }

  public void setBehavior(String behavior) {
    this.behavior = behavior;
  }

}
