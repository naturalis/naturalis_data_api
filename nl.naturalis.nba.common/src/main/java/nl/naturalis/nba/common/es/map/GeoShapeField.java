package nl.naturalis.nba.common.es.map;

/**
 * A {@code GeoShapeField} is a {@link SimpleField} with Elasticsearch data type
 * {@link ESDataType#GEO_SHAPE geo_shape}.
 * 
 * @author Ayco Holleman
 *
 */
public class GeoShapeField extends SimpleField {

	public GeoShapeField()
	{
		super(ESDataType.GEO_SHAPE);
	}

}
