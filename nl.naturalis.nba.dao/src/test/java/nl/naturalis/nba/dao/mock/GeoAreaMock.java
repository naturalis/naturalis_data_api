package nl.naturalis.nba.dao.mock;

import java.io.IOException;
import java.io.InputStream;

import org.geojson.GeoJsonObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.api.model.GeoArea;
import nl.naturalis.nba.api.model.SourceSystem;

public class GeoAreaMock {

	private GeoAreaMock()
	{
	}

	public static GeoArea Aalten()
	{
		GeoArea area = new GeoArea();
		area.setSourceSystem(SourceSystem.GEO);
		area.setSourceSystemId("001");
		area.setLocality("Aalten");
		area.setShape(loadShape("aalten.geojson.txt"));
		return area;
	}

	public static GeoArea Uitgeest()
	{
		GeoArea area = new GeoArea();
		area.setSourceSystem(SourceSystem.GEO);
		area.setSourceSystemId("002");
		area.setLocality("Uitgeest");
		area.setShape(loadShape("uitgeest.geojson.txt"));
		return area;
	}

	public static GeoArea NoordHolland()
	{
		GeoArea area = new GeoArea();
		area.setSourceSystem(SourceSystem.GEO);
		area.setSourceSystemId("003");
		area.setLocality("Noord-Holland");
		area.setShape(loadShape("noord-holland.geojson.txt"));
		return area;
	}

	public static GeoArea Netherlands()
	{
		GeoArea area = new GeoArea();
		area.setSourceSystem(SourceSystem.GEO);
		area.setSourceSystemId("004");
		area.setLocality("Netherlands");
		area.setShape(loadShape("netherlands.geojson.txt"));
		return area;
	}
	
	public static GeoArea Vatican() {
		GeoArea area = new GeoArea();
		area.setSourceSystem(SourceSystem.GEO);
		area.setSourceSystemId("005");
		area.setLocality("Vatican");
		area.setShape(loadShape("vatican.geojson.txt"));
		return area;
	}

	private static GeoJsonObject loadShape(String resource)
	{
		InputStream is = GeoAreaMock.class.getResourceAsStream(resource);
		try {
			return new ObjectMapper().readValue(is, GeoJsonObject.class);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
