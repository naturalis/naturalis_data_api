package nl.naturalis.nda.elasticsearch.load.col;

import java.io.IOException;

import nl.naturalis.nda.domain.systypes.CoLCommonName;
import nl.naturalis.nda.elasticsearch.client.Index;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.load.CSVImporter;
import nl.naturalis.nda.elasticsearch.load.NDASchemaManager;

import org.apache.commons.csv.CSVRecord;
import org.domainobject.util.StringUtil;

public class CommonNamesImporter extends CSVImporter<CoLCommonName> {

	public static void main(String[] args) throws IOException
	{
		Index index = new IndexNative(NDASchemaManager.DEFAULT_NDA_INDEX_NAME);
		index.deleteType(LUCENE_TYPE);
		String mapping = StringUtil.getResourceAsString("/es-mappings/CoLCommonName.json");
		index.addType(LUCENE_TYPE, mapping);
		CommonNamesImporter importer = new CommonNamesImporter(index);
		importer.importCsv("C:/test/col-dwca/vernacular.txt");
	}

	//@formatter:off
	private static enum CsvField {
		taxonID
		, vernacularName
		, language
		, countryCode
		, locality
		, transliteration
	}	
	//@formatter:on

	public static final String LUCENE_TYPE = "CoLCommonName";


	public CommonNamesImporter(Index index)
	{
		super(index, LUCENE_TYPE);
		setSpecifyId(false);
		setSpecifyParent(true);
	}


	@Override
	protected CoLCommonName transfer(CSVRecord record)
	{
		final CoLCommonName commonName = new CoLCommonName();
		commonName.setTaxonId(getInt(record, CsvField.taxonID.ordinal()));
		commonName.setVernacularName(record.get(CsvField.vernacularName.ordinal()));
		commonName.setCountryCode(record.get(CsvField.countryCode.ordinal()));
		commonName.setLocality(record.get(CsvField.locality.ordinal()));
		commonName.setTransliteration(record.get(CsvField.transliteration.ordinal()));
		return commonName;
	}


	@Override
	protected String getParentId(CSVRecord record)
	{
		return record.get(CsvField.taxonID.ordinal());
	}

}
