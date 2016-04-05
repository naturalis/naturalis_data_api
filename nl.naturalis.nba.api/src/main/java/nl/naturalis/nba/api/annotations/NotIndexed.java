package nl.naturalis.nba.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the field decorated with this annotation is not indexed
 * (i&#46;e&#46; it is not searchable). By default all fields are searchable. If
 * a field is not searchable, it is by definition also {@link NotAnalyzed}.
 * Therefore it makes no sense to use both annotations to the same field,
 * although no exception is thrown if you do.
 * 
 * @author Ayco Holleman
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface NotIndexed {

}
