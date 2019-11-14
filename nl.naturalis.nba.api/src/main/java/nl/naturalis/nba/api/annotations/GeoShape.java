package nl.naturalis.nba.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.geojson.GeoJsonObject;

// ES7: "Field parameter [precision] is deprecated and will be removed in a future version."
// TODO: remove this class


/**
 * Provides information about the storage of geo shape data. This annotation can
 * only be applied to fields and methods (getters) of type
 * {@link GeoJsonObject}.
 * 
 * @author Ayco Holleman
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface GeoShape {

//	public static final String DEFAULT_PRECISION = "5km";

//	String precision() default DEFAULT_PRECISION;

//	boolean pointsOnly() default false;

}
