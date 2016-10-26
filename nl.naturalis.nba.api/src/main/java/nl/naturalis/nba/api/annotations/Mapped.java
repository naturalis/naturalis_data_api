package nl.naturalis.nba.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import nl.naturalis.nba.api.model.IDocumentObject;

/**
 * Indicates that the field or property (getter method) decorated with this
 * annotation is mapped to a field in the document store. Fields are mapped by
 * default, so they don't need to be decorated with this annotation. Properties,
 * on the other hand, are not. If a getter method is mapped, it means you can
 * query it just like any other field (unless it happens to be also have the
 * {@link NotIndexed} annotation). In other words, you can create a
 * {@link Condition query condition} for it. Note that for a getter named (for
 * example) {@code getSummary()}, you would create a query on a field named
 * {@code summary}.
 * 
 * @see IDocumentObject
 * 
 * @author Ayco Holleman
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD })
public @interface Mapped {

}
