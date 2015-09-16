/**
 * Classes that provide canonical values for found-in-the-wild equivalents.
 * These classes are used throughout the other packages to normalize (i.e. standardize)
 * the data from and within the various data sources. As an example: data sources tend
 * to be sloppy about how they specify a specimens sex (e.g. "F" or "fem." or "female").
 * The {@link nl.naturalis.nda.elasticsearch.load.normalize.SexNormalizer} maps all
 * these values to "female".
 */
package nl.naturalis.nda.elasticsearch.load.normalize;

