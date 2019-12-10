package nl.naturalis.nba.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.geojson.GeoJsonObject;

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
public @interface GeoShape {}
