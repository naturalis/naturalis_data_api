package nl.naturalis.nba.dao.format.dwca;

import static nl.naturalis.nba.api.ComparisonOperator.EQUALS_IC;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.junit.Test;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.SearchCondition;
import nl.naturalis.nba.api.SearchSpec;
import nl.naturalis.nba.dao.format.DataSetConfigurationException;
import nl.naturalis.nba.dao.format.DataSetWriteException;

@SuppressWarnings("static-method")
public class DwcaWriterTest {

	@Test
	public void testWriteDwcaForQuery_01() throws DataSetConfigurationException,
			InvalidQueryException, FileNotFoundException, DataSetWriteException
	{
		DwcaConfig config = DwcaConfig.getDynamicDwcaConfig(DwcaDataSetType.TAXON);
		SearchSpec qs = new SearchSpec();
		qs.addCondition(new SearchCondition("defaultClassification.genus", EQUALS_IC, "LARUS"));
		FileOutputStream fos = new FileOutputStream(
				"/home/ayco/tmp/DwcaWriterTest.testWriteDwcaForQuery_01.zip");
		MultiDataSourceDwcaWriter writer = new MultiDataSourceDwcaWriter(config, fos);
		writer.writeDwcaForQuery(qs);
	}

	//@Test
	public void testWriteDwcaForDataSet()
	{
		fail("Not yet implemented");
	}

}
