/**
 * Utility library providing a simple, high-level API for interacting
 * with ElasticSearch. The other packages use this library, rather than
 * calling the ElasticSearch Java API directly. This library is in fact
 * not specific to the NBA import although it currently still has a
 * dependency on the {@link nl.naturalis.nba.etl.Registry}
 * class to get hold of {@link org.slf4j.Logger SLF4J loggers}.
 */
package nl.naturalis.nba.etl.elasticsearch;

