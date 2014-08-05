package nl.naturalis.nda.elasticsearch.load.col;

import java.io.IOException;

import nl.naturalis.nda.domain.systypes.CoLReference;
import nl.naturalis.nda.elasticsearch.client.Index;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.load.CSVImporter;
import nl.naturalis.nda.elasticsearch.load.NDASchemaManager;

import org.apache.commons.csv.CSVRecord;
import org.domainobject.util.StringUtil;

public class ReferencesImporter extends CSVImporter<CoLReference> {

	public static void main(String[] args) throws IOException
	{
		Index index = new IndexNative(NDASchemaManager.DEFAULT_NDA_INDEX_NAME);
		index.deleteType(LUCENE_TYPE);
		String mapping = StringUtil.getResourceAsString("/es-mappings/CoLReference.json");
		index.addType(LUCENE_TYPE, mapping);
		ReferencesImporter importer = new ReferencesImporter(index);
		importer.importCsv("C:/test/col-dwca/reference.txt");
	}

	//@formatter:off
	static enum CsvField {
		taxonID
		, creator
		, date
		, title
		, description
		, identifier
		, type
	}	
	//@formatter:on

	private static final String LUCENE_TYPE = "CoLReference";


	public ReferencesImporter(Index index)
	{
		super(index, LUCENE_TYPE);
		setSpecifyId(false);
		setSpecifyParent(true);
	}


	@Override
	protected CoLReference transfer(CSVRecord record)
	{
		final CoLReference reference = new CoLReference();
		reference.setCreator(record.get(CsvField.creator.ordinal()));
		reference.setDate(record.get(CsvField.date.ordinal()));
		reference.setDescription(record.get(CsvField.description.ordinal()));
		reference.setIdentifier(record.get(CsvField.identifier.ordinal()));
		reference.setTaxonID(getInt(record, CsvField.taxonID.ordinal()));
		reference.setTitle(record.get(CsvField.title.ordinal()));
		reference.setType(record.get(CsvField.type.ordinal()));
		return reference;
	}


	@Override
	protected String getId(CSVRecord record)
	{
		return null;
	}


	@Override
	protected String getParentId(CSVRecord record)
	{
		return record.get(CsvField.taxonID.ordinal());
	}

}
