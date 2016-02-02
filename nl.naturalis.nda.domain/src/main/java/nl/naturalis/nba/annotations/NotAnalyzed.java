package nl.naturalis.nba.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the field decorated with this annotation is a
 * &#34;not_analyzed&#34; field. By default (i&#46;e&#46; if you do not use this
 * annotation), <i>every</i> field will be an &#34;analyzed&#34; field
 * <i>and</i> it will be accompanied by a &#34;raw&#34; field which is
 * &#34;not_analyzed&#34;.
 * 
 * @author Ayco Holleman
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface NotAnalyzed {

}
