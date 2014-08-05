package nl.naturalis.nda.elasticsearch.load.col;

import java.io.IOException;

import nl.naturalis.nda.domain.systypes.CoLDistribution;
import nl.naturalis.nda.elasticsearch.client.Index;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.load.CSVImporter;
import nl.naturalis.nda.elasticsearch.load.NDASchemaManager;

import org.apache.commons.csv.CSVRecord;
import org.domainobject.util.StringUtil;

public class DistributionsImporter extends CSVImporter<CoLDistribution> {

	public static void main(String[] args) throws IOException
	{
		Index index = new IndexNative(NDASchemaManager.DEFAULT_NDA_INDEX_NAME);
		index.deleteType(LUCENE_TYPE);
		String mapping = StringUtil.getResourceAsString("/es-mappings/CoLDistribution.json");
		index.addType(LUCENE_TYPE, mapping);
		DistributionsImporter importer = new DistributionsImporter(index);
		importer.importCsv("C:/test/col-dwca/distribution.txt");
	}

	//@formatter:off
	private static enum CsvField {
		taxonID
		, locationID
		, locality
		, occurrenceStatus
		, establishmentMeans
	}	
	//@formatter:on

	public static final String LUCENE_TYPE = "CoLDistribution";


	public DistributionsImporter(Index index)
	{
		super(index, LUCENE_TYPE);
		setSpecifyId(false);
		setSpecifyParent(true);
	}


	@Override
	protected CoLDistribution transfer(CSVRecord record)
	{
		CoLDistribution distribution = new CoLDistribution();
		distribution.setEstablishmentMeans(record.get(CsvField.establishmentMeans.ordinal()));
		distribution.setLocality(record.get(CsvField.locality.ordinal()));
		distribution.setLocationID(record.get(CsvField.locationID.ordinal()));
		distribution.setOccurrenceStatus(record.get(CsvField.occurrenceStatus.ordinal()));
		distribution.setTaxonID(getInt(record, CsvField.taxonID.ordinal()));
		return distribution;
	}


	@Override
	protected String getParentId(CSVRecord record)
	{
		return record.get(CsvField.taxonID.ordinal());
	}

}
