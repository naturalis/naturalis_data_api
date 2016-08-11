package nl.naturalis.nba.dao.es.types;

import nl.naturalis.nba.dao.es.map.Mapping;
import nl.naturalis.nba.dao.es.map.MappingFactory;

/**
 * Marker interface indicating that the implementing class represents an
 * Elasticsearch document type. The class is structured exactly like the
 * document type. In fact, the Elasticsearch type mapping is generated from the
 * class through reflection. See {@link Mapping} and {@link MappingFactory}.
 * 
 * @author Ayco Holleman
 *
 */
public interface ESType {

}
