package nl.naturalis.nda.elasticsearch.load;

import java.util.List;

/**
 * An extension of the {@link Transformer} interface that narrows the input of
 * the transformer to CSV records. No new methods are added to this interface;
 * it just serves to make things more specific.
 * 
 * @author Ayco Holleman
 *
 * @param <T>
 *            The type of object that is output by the transformer.
 */
public interface CSVTransformer<T> extends Transformer<CSVRecordInfo, T> {

	List<T> transform(CSVRecordInfo record);

}
