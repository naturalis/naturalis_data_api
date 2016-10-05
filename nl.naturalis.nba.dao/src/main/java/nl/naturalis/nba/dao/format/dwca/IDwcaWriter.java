package nl.naturalis.nba.dao.format.dwca;

import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.dao.format.DataSetConfigurationException;
import nl.naturalis.nba.dao.format.DataSetWriteException;

/**
 * Manages the assembly and creation of DarwinCore archives. Instances of
 * {@code IDwcaWriter} can be obtained by calling
 * {@link DwcaConfig#getWriter(java.io.OutputStream) DwcaConfig.getWriter}.
 * 
 * @author Ayco Holleman
 *
 */
public interface IDwcaWriter {

	/**
	 * Writes a DarwinCore archive for a pre-defined data set.
	 * 
	 * @throws DataSetConfigurationException
	 * @throws DataSetWriteException
	 */
	void writeDwcaForDataSet() throws DataSetConfigurationException, DataSetWriteException;

	/**
	 * Writes a Darwincore archive for a &#34;live&#34; query.
	 * 
	 * @param querySpec
	 * @throws InvalidQueryException
	 * @throws DataSetConfigurationException
	 * @throws DataSetWriteException
	 */
	void writeDwcaForQuery(QuerySpec querySpec)
			throws InvalidQueryException, DataSetConfigurationException, DataSetWriteException;

}