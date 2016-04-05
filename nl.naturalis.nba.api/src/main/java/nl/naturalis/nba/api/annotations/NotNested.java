package nl.naturalis.nba.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

/**
 * Indicates that the field decorated with this annotation is not mapped as a
 * nested object. By default arrays and {@link Collection} objects are mapped as
 * nested objects. This annotation signifies that the annotated field is an
 * exception to this rule. Fields that are neither arrays nor collections are
 * never mapped as nested objects, so they don't need the
 * <code>&#64;NotNested</code> annotation.
 * 
 * @author Ayco Holleman
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface NotNested {

}
