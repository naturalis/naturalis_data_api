package nl.naturalis.nda.domain;

/**
 * An {@code Expert} represents a person or organization who is regarded as an
 * authority with respect to a particular species. An expert is not to be
 * confused with the person who first described and named a species (for whom
 * the term "author" is reserved). {@code Expert} is a subclass {@link Person}
 * even though it has no properties of its known. This is because for an
 * {@code Expert}, his/her name may be unknown ({@code null}), which would be
 * dubious for a regular {@code Person}. With an {@code Expert} either his/her
 * name or his/her organization may be unknown as long as at least one is known.
 */
public class Expert extends Person {

}
