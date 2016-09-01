package nl.naturalis.nba.dao.es.format;

import java.io.File;

import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.dao.es.DocumentType;

/**
 * Class capturing the information necessary to generate a data set. A data set
 * is a file or collection of files containing formatted data (e.g. CSV
 * records). Use a {@link DataSetConfigurationBuilder} to get hold of a
 * {@code DataSetConfiguration} instance.
 * 
 * @author Ayco Holleman
 *
 */
public class DataSetConfiguration {

	private String name;
	private QuerySpec querySpec;

}
