package nl.naturalis.nba.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the field decorated with this annotation is not indexed
 * (i&#46;e&#46; it is not searchable). By default all fields are searchable. If
 * a field is not searchable, it is by definition not analyzed. Therefore it
 * makes no sense to combine the {@link NotIndexed @NotIndexed} annotation with the
 * {@link Analyzers @Analyzers} annotation, although no exception is thrown if you do.
 * 
 * @author Ayco Holleman
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface NotIndexed {

}
