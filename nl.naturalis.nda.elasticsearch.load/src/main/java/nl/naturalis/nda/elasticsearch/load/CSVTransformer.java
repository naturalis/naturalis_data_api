package nl.naturalis.nda.elasticsearch.load;

import java.util.List;

/**
 * Defines the capacities transformers that take commons-csv objects as their
 * input an have an arbitrary type of objects as their output.
 * 
 * @author Ayco Holleman
 *
 * @param <T>
 *            The type of object that is output from the transformer.
 */
public interface CSVTransformer<T> extends Transformer<CSVRecordInfo, T> {

	List<T> transform(CSVRecordInfo record);

}
