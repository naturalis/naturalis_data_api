package nl.naturalis.nba.dao.es.types;

import nl.naturalis.nba.dao.es.map.MappingFactory;

/**
 * Marker interface indicating that the implementing class corresponds
 * one-to-one with an Elasticsearch type. (In fact, the Elasticsearch type is
 * generated from the class through reflection and annotation processing. See
 * {@link MappingFactory}.)
 * 
 * @author Ayco Holleman
 *
 */
public interface ESType {

}
