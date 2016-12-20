package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.util.es.DocumentIterator;
import nl.naturalis.nba.etl.ETLRegistry;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.LoadConstants.*;

public class SpecimenNameImporter {

//	static final Logger logger = ETLRegistry.getInstance().getLogger(SpecimenNameImporter.class);
//
//	private final int esBulkRequestSize;
//	
//	public SpecimenNameImporter()
//	{
//		String key = LoadConstants.SYSPROP_ES_BULK_REQUEST_SIZE;
//		String val = System.getProperty(key, "1000");
//		esBulkRequestSize = Integer.parseInt(val);
//	}
//	
//	public void importNames() {
//		long start = System.currentTimeMillis();
//		ETLStatistics stats = new ETLStatistics();
//		NameLoader loader = new NameLoader(esBulkRequestSize, stats);
//		DocumentIterator<Specimen> extractor = new DocumentIterator(SPECIMEN);
//		
//	}

}
