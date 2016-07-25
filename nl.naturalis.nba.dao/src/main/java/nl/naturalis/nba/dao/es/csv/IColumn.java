package nl.naturalis.nba.dao.es.csv;

import java.util.Map;

public interface IColumn {

	String getHeader();

	String getValue(Map<String, Object> esDocumentAsMap);

}
