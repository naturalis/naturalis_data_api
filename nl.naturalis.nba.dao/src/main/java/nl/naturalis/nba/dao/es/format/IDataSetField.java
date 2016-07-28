package nl.naturalis.nba.dao.es.format;

import java.util.Map;

public interface IDataSetField {

	String getName();

	String getValue(Map<String, Object> esDocumentAsMap);

}
