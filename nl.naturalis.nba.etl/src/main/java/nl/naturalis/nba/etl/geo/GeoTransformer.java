package nl.naturalis.nba.etl.geo;

import static nl.naturalis.nba.etl.geo.GeoCsvField.country_nl;
import static nl.naturalis.nba.etl.geo.GeoCsvField.geojson;
import static nl.naturalis.nba.etl.geo.GeoCsvField.gid;
import static nl.naturalis.nba.etl.geo.GeoCsvField.iso;
import static nl.naturalis.nba.etl.geo.GeoCsvField.locality;
import static nl.naturalis.nba.etl.geo.GeoCsvField.source;
import static nl.naturalis.nba.etl.geo.GeoCsvField.type;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.geojson.Geometry;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.api.model.GeoArea;
import nl.naturalis.nba.etl.AbstractCSVTransformer;
import nl.naturalis.nba.etl.ETLStatistics;

class GeoTransformer extends AbstractCSVTransformer<GeoCsvField, GeoArea> {
	
	private ObjectMapper mapper = new ObjectMapper();

	GeoTransformer(ETLStatistics stats)
	{
		super(stats);
	}

	@Override
	protected String getObjectID()
	{
		return input.get(gid);
	}

	@Override
	protected List<GeoArea> doTransform()
	{
		stats.recordsAccepted++;
		stats.objectsProcessed++;
		GeoArea area = new GeoArea();
		area.setAreaId(Integer.parseInt(input.get(gid)));
		area.setAreaType(input.get(type));
		area.setCountryNL(input.get(country_nl));
		String s = input.get(geojson);
		Geometry<?> obj;
		try {
			obj = mapper.readValue(s, Geometry.class);
		}
		catch (IOException e) {
			logger.error(e.getMessage());
			return null;
		}
		area.setGeoJson(obj);
		area.setIsoCode(input.get(iso));
		area.setLocality(input.get(locality));
		area.setSource(input.get(source));
		return Arrays.asList(area);
	}

}
