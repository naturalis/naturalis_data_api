package nl.naturalis.nba.dao.es.format.dwca;

import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.dao.es.format.DataSetConfigurationException;
import nl.naturalis.nba.dao.es.format.DataSetWriteException;

public interface IDwcaWriter {

	void writeDwcaForDataSet() throws DataSetConfigurationException, DataSetWriteException;

	void writeDwcaForQuery(QuerySpec querySpec)
			throws InvalidQueryException, DataSetConfigurationException, DataSetWriteException;

}