package nl.naturalis.nba.api.model;

import static nl.naturalis.nba.api.annotations.Analyzer.CASE_INSENSITIVE;
import static nl.naturalis.nba.api.annotations.Analyzer.LIKE;

import org.geojson.GeoJsonObject;

import nl.naturalis.nba.api.annotations.Analyzers;
import nl.naturalis.nba.api.annotations.NotIndexed;

public class GeoArea extends NbaTraceableObject implements IDocumentObject {

	private String id;
	@Analyzers({ CASE_INSENSITIVE })
	private String areaType;
	@Analyzers({ CASE_INSENSITIVE, LIKE })
	private String locality;
	private GeoJsonObject shape;
	@NotIndexed
	private String source;
	@Analyzers({ CASE_INSENSITIVE })
	private String isoCode;
	@Analyzers({ CASE_INSENSITIVE, LIKE })
	private String countryNL;

	@Override
	public String getId()
	{
		return id;
	}

	@Override
	public void setId(String id)
	{
		this.id = id;
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

	public GeoJsonObject getShape()
	{
		return shape;
	}

	public void setShape(GeoJsonObject shape)
	{
		this.shape = shape;
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
