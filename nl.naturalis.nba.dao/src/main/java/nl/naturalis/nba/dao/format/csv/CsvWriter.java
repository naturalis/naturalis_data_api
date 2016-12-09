package nl.naturalis.nba.dao.format.csv;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;

import java.io.OutputStream;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.NbaException;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.format.IField;
import nl.naturalis.nba.dao.util.es.Scroller;

public class CsvWriter<T extends IDocumentObject> {

	@SuppressWarnings("unused")
	private static final Logger logger = getLogger(CsvWriter.class);

	private final OutputStream out;
	private final DocumentType<T> dt;

	public CsvWriter(OutputStream out, DocumentType<T> dt)
	{
		this.out = out;
		this.dt = dt;
	}

	public void writeCsv(QuerySpec querySpec) throws InvalidQueryException
	{
		IField[] csvFields = getCsvFields(querySpec);
		CsvRecordWriter writer = new CsvRecordWriter(csvFields, out);
		writer.printBOM();
		writer.printHeader();
		CsvWriterSearchHitHandler handler = new CsvWriterSearchHitHandler(writer);
		Scroller scroller = new Scroller(querySpec, dt, handler);
		try {
			scroller.scroll();
		}
		catch (NbaException e) {
			// Won't happen (see CsvWriterSearchHitHandler.handle)
		}
	}

	private IField[] getCsvFields(QuerySpec querySpec)
	{
		String[] fields;
		if (querySpec.getFields() == null) {
			MappingInfo<T> mappingInfo = new MappingInfo<>(dt.getMapping());
			fields = mappingInfo.getPathStrings(false);
		}
		else {
			fields = querySpec.getFields().toArray(new String[querySpec.getFields().size()]);
		}
		IField[] csvFields = new IField[fields.length];
		for (int i = 0; i < fields.length; ++i) {
			csvFields[i] = new CsvField(fields[i]);
		}
		return csvFields;
	}

}
