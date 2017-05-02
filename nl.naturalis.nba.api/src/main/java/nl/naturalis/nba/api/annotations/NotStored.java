package nl.naturalis.nba.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import nl.naturalis.nba.api.model.IDocumentObject;

/**
 * Indicates that the field decorated with this annotation is not part of the
 * Elasticsearch document type mapping. Its value is not stored in, or retrieved
 * from an Elasticsearch index. It is a field whose value is calculated and set
 * by the NBA itself. For implementations of {@link IDocumentObject} the id
 * field will and must always be annotated with this annotation. This field
 * contains the Elasticsearch document id, which is not part of the document
 * itself.
 * 
 * @author Ayco Holleman
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface NotStored {

}
