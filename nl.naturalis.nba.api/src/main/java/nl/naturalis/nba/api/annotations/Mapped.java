package nl.naturalis.nba.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import nl.naturalis.nba.api.model.IDocumentObject;

/**
 * Indicates that the method decorated with this annotation is mapped to a field in the
 * Elasticsearch document store. The method must be a Java bean getter method conforming
 * to the Java bean naming conventions, for example: {@code getFullName()}. The
 * corresponding Elasticsearch field will be {@code fullName} and you must query it as
 * such. The class containing the method should not also have a field named
 * {@code fullName} since that would cause {@code fullName} to be doubly mapped.
 * 
 * @see IDocumentObject
 * 
 * @author Ayco Holleman
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Mapped {

}
