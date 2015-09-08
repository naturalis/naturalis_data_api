package nl.naturalis.nda.elasticsearch.load;

import java.util.List;

public interface XMLTransformer<T> extends Transformer<XMLRecordInfo, T> {

	List<T> transform(XMLRecordInfo recInf);

}
