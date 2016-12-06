package nl.naturalis.nba.dao;

import java.io.IOException;
import java.io.InputStream;

import org.geojson.GeoJsonObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.api.model.GeoArea;
import nl.naturalis.nba.api.model.SourceSystem;

public class TestGeoAreas {

	private TestGeoAreas()
	{
	}

	static GeoArea Aalten()
	{
		GeoArea area = new GeoArea();
		area.setSourceSystem(SourceSystem.GEO);
		area.setSourceSystemId("001");
		area.setLocality("Aalten");
		area.setShape(loadShape("aalten.geojson.txt"));
		return area;
	}

	static GeoArea NoordHolland()
	{
		GeoArea area = new GeoArea();
		area.setSourceSystem(SourceSystem.GEO);
		area.setSourceSystemId("002");
		area.setLocality("Noord-Holland");
		area.setShape(loadShape("noord-holland.geojson.txt"));
		return area;
	}

	private static GeoJsonObject loadShape(String resource)
	{
		InputStream is = TestGeoAreas.class.getResourceAsStream(resource);
		try {
			return new ObjectMapper().readValue(is, GeoJsonObject.class);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
