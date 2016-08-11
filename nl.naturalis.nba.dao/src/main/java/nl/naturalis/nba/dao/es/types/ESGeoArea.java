package nl.naturalis.nba.dao.es.types;

import static nl.naturalis.nba.api.annotations.Analyzer.CASE_INSENSITIVE;
import static nl.naturalis.nba.api.annotations.Analyzer.LIKE;

import org.geojson.Geometry;

import nl.naturalis.nba.api.annotations.Analyzers;
import nl.naturalis.nba.api.annotations.NotIndexed;

public class ESGeoArea implements ESType {

	private int areaId;
	@Analyzers({ CASE_INSENSITIVE })
	private String areaType;
	@Analyzers({ CASE_INSENSITIVE, LIKE })
	private String locality;
	private Geometry<?> geoJson;
	@NotIndexed
	private String source;
	@Analyzers({ CASE_INSENSITIVE })
	private String isoCode;
	@Analyzers({ CASE_INSENSITIVE, LIKE })
	private String countryNL;

	public int getAreaId()
	{
		return areaId;
	}

	public void setAreaId(int areaId)
	{
		this.areaId = areaId;
	}

	public String getAreaType()
	{
		return areaType;
	}

	public void setAreaType(String areaType)
	{
		this.areaType = areaType;
	}

	public String getLocality()
	{
		return locality;
	}

	public void setLocality(String locality)
	{
		this.locality = locality;
	}

	public Geometry<?> getGeoJson()
	{
		return geoJson;
	}

	public void setGeoJson(Geometry<?> geoJson)
	{
		this.geoJson = geoJson;
	}

	public String getSource()
	{
		return source;
	}

	public void setSource(String source)
	{
		this.source = source;
	}

	public String getIsoCode()
	{
		return isoCode;
	}

	public void setIsoCode(String isoCode)
	{
		this.isoCode = isoCode;
	}

	public String getCountryNL()
	{
		return countryNL;
	}

	public void setCountryNL(String countryNL)
	{
		this.countryNL = countryNL;
	}

}
