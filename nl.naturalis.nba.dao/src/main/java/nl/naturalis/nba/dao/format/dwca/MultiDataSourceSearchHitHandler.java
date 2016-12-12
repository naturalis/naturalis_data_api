package nl.naturalis.nba.dao.format.dwca;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.search.SearchHit;

import nl.naturalis.nba.api.NbaException;
import nl.naturalis.nba.common.Path;
import nl.naturalis.nba.dao.format.DocumentFlattener;
import nl.naturalis.nba.dao.format.Entity;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.IEntityFilter;
import nl.naturalis.nba.dao.format.csv.CsvRecordWriter;
import nl.naturalis.nba.dao.util.es.SearchHitHandler;

class MultiDataSourceSearchHitHandler implements SearchHitHandler {

	@SuppressWarnings("unused")
	private static final Logger logger = getLogger(MultiDataSourceSearchHitHandler.class);

	private final Entity entity;
	private final DocumentFlattener flattener;
	private final CsvRecordWriter printer;

	private int processed;

	MultiDataSourceSearchHitHandler(Entity entity, CsvRecordWriter printer)
	{
		this.entity = entity;
		this.printer = printer;
		Path path = entity.getDataSource().getPath();
		flattener = new DocumentFlattener(path);
	}

	@Override
	public void handle(SearchHit hit) throws NbaException
	{
		List<EntityObject> eos = flattener.flatten(hit.getSource());
		LOOP: for (EntityObject eo : eos) {
			for (IEntityFilter filter : entity.getFilters()) {
				if (!filter.accept(eo)) {
					continue LOOP;
				}
			}
			printer.printRecord(eo);
		}
		if (++processed % 10000 == 0) {
			printer.flush();
		}
	}

}
