package nl.naturalis.nda.elasticsearch.load.brahms;

import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.load.CSVImporter;
import nl.naturalis.nda.elasticsearch.load.NDASchemaManager;

import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrahmsSpecimensImporter extends CSVImporter<Specimen> {

	public static void main(String[] args) throws Exception
	{
		IndexNative index = null;
		try {

			index = new IndexNative(NDASchemaManager.DEFAULT_NDA_INDEX_NAME);

			BrahmsSpecimensImporter importer = new BrahmsSpecimensImporter(index);
			importer.importCsv("");

		}
		finally {
			if (index != null) {
				index.getClient().close();
			}
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(BrahmsSpecimensImporter.class);
	private static final String LUCENE_TYPE = "Specimen";
	private static final String ID_PREFIX = "BRHMS-";


	public BrahmsSpecimensImporter(IndexNative index)
	{
		super(index, LUCENE_TYPE);
		setSpecifyId(true);
		setSpecifyParent(false);
	}


	@Override
	protected Specimen transfer(CSVRecord record) throws Exception
	{
		final Specimen specimen = new Specimen();
		specimen.setSourceSystem(SourceSystem.BRAHMS);
		String s = get(record, "barcode");
		if (s == null) {
			throw new Exception("Missing barcode");
		}
		specimen.setSourceSystemId(s);
		specimen.setUnitID(s);
		specimen.setRecordBasis("Preserved Specimen");
		specimen.setSex(get(record, "sex"));
		return specimen;
	}


	private static String get(CSVRecord record, String field)
	{
		String s = record.get(field).trim();
		return s.length() == 0 ? null : s;
	}
}
