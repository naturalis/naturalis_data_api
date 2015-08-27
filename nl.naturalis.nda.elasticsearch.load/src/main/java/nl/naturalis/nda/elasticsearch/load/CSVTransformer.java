package nl.naturalis.nda.elasticsearch.load;

import java.util.List;

/**
 * Describes what a CSV transformer does. Currently does not add new
 * functionality to {@link Transformer}.
 * 
 * @author Ayco Holleman
 *
 * @param <T>
 *            The type of object that is output from the transformer.
 */
public interface CSVTransformer<T> extends Transformer<CSVRecordInfo, T> {

	List<T> transform(CSVRecordInfo record);

}
