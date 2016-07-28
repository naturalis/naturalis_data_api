package nl.naturalis.nba.dao.es;

import static nl.naturalis.nba.dao.es.DocumentType.SPECIMEN;

import java.io.OutputStream;

import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.dao.es.format.DataSetCollection;
import nl.naturalis.nba.dao.es.format.dwca.DwcaWriter;

public class SpecimenDwcaDao {

	@SuppressWarnings("static-method")
	public void queryDynamic(QuerySpec spec, OutputStream out) throws InvalidQueryException
	{
		DataSetCollection dsc = new DataSetCollection(SPECIMEN, "dynamic");
		DwcaWriter writer = new DwcaWriter(dsc, out);
		writer.processDynamicQuery(spec);
	}

	@SuppressWarnings("static-method")
	public void queryStatic(String dataSetCollection, String dataSet, OutputStream out)
			throws InvalidQueryException
	{
		DataSetCollection dsc = new DataSetCollection(SPECIMEN, dataSetCollection);
		DwcaWriter writer = new DwcaWriter(dsc, out);
		writer.processPredefinedQuery(dataSet);
	}

}
