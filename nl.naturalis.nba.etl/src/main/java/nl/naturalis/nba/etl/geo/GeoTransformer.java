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

import org.geojson.GeoJsonObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.api.model.GeoArea;
import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.etl.AbstractCSVTransformer;
import nl.naturalis.nba.etl.ETLStatistics;

/**
 * The transformer component in the Geo ETL cycle.
 * 
 * @author Ayco Holleman
 * @author Tom Gilissen
 *
 */
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
		String loc = input.get(locality);
		if (logger.isDebugEnabled()) {
			logger.debug("Processing {}", loc);
		}
		String geoJson = input.get(geojson);
		if (geoJson == null) {
			stats.recordsRejected++;
			if (!suppressErrors) {
				error("Missing GeoJSON for locality {}", loc);
			}
			return null;
		}
		GeoJsonObject obj;
		try {
			obj = mapper.readValue(geoJson, GeoJsonObject.class);
		}
		catch (IOException e) {
			stats.recordsRejected++;
			if (!suppressErrors) {
				error("Error in JSON for locality {}: {}", loc, e.getMessage());
			}
			return null;
		}
		GeoArea area = new GeoArea();
		area.setId(objectID + "@" + SourceSystem.GEO.getCode());
		area.setLocality(loc);
		area.setSourceSystem(SourceSystem.GEO);
		area.setSourceSystemId(input.get(gid));
		area.setAreaType(input.get(type));
		area.setCountryNL(input.get(country_nl));
		area.setShape(obj);
		area.setIsoCode(input.get(iso));
		area.setSource(input.get(source));
		stats.recordsAccepted++;
		stats.objectsProcessed++;
		stats.objectsAccepted++;
		return Arrays.asList(area);
	}

}
