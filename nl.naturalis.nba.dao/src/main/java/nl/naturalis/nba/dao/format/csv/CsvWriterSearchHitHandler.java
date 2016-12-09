package nl.naturalis.nba.dao.format.csv;

import java.util.List;

import org.elasticsearch.search.SearchHit;

import nl.naturalis.nba.api.NbaException;
import nl.naturalis.nba.dao.format.DocumentFlattener;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.util.es.SearchHitHandler;

class CsvWriterSearchHitHandler implements SearchHitHandler {

	private final CsvRecordWriter printer;
	private final DocumentFlattener flattener;

	CsvWriterSearchHitHandler(CsvRecordWriter printer)

	{
		this.printer = printer;
		this.flattener = new DocumentFlattener();
	}

	@Override
	public void handle(SearchHit hit) throws NbaException
	{
		List<EntityObject> eos = flattener.flatten(hit.getSource());
		for (EntityObject eo : eos) {
			printer.printRecord(eo);
		}
	}

}
