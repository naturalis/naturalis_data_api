package nl.naturalis.nda.elasticsearch.load;

import java.util.List;

public interface CSVTransformer<T> {
	
	List<T> transform(CSVRecordInfo record);

}
