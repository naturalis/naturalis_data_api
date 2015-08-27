package nl.naturalis.nda.elasticsearch.load;

import java.util.List;

/**
 * Describes what a transformation component within an ETL program does.
 * Basically: transforming one type of object into another. Note, however, that
 * the {@link #transform(Object) transform} method takes one input object but
 * produces multiple output objects (a {@code List}). This is because it might
 * be possible to extract multiple objects from one and the same source object.
 * For example, a CSV record in Brahms may contain multiple images.
 * 
 * @author Ayco Holleman
 *
 * @param <T>
 *            The type of object that is input to the transformer
 * @param <U>
 *            The type of object that is output from the transformer
 */
public interface Transformer<T, U> {

	/**
	 * Transform the input object into one or more output objects.
	 * 
	 * @param input
	 * @return
	 */
	List<U> transform(T input);

}
