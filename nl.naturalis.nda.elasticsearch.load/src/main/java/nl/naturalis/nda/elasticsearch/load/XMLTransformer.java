package nl.naturalis.nda.elasticsearch.load;

import java.util.List;

/**
 * An extension of the {@link Transformer} interface that narrows the input of
 * the transformer to XML records. No new methods are added to this interface;
 * it just serves to make things more specific.
 * 
 * @author Ayco Holleman
 *
 * @param <T>
 *            The type of object that is output by the transformer.
 */
public interface XMLTransformer<T> extends Transformer<XMLRecordInfo, T> {

	List<T> transform(XMLRecordInfo recInf);

}
