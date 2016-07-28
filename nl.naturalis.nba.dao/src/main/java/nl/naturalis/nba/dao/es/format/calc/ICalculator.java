package nl.naturalis.nba.dao.es.format.calc;

import java.util.Map;

public interface ICalculator {

	Object calculateValue(Map<String, Object> esDocumentAsMap);

}
