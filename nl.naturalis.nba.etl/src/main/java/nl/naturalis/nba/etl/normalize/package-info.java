/**
 * Classes that provide canonical equivalents for found-in-the-wild values.
 * These classes are used throughout the other packages to normalize (i.e. standardize)
 * the data from and within the various data sources. As an example: data sources tend
 * to be sloppy about how they specify a specimen's sex (e.g. "F" or "fem." or "female").
 * The {@link nl.naturalis.nba.etl.normalize.SexNormalizer} maps all
 * these values to "female".
 */
package nl.naturalis.nba.etl.normalize;

