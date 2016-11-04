package nl.naturalis.nba.etl;

import java.util.List;

import nl.naturalis.nba.api.model.IDocumentObject;

/**
 * Defines the capacities of a transformation component within the ETL process.
 * Basically: transforming one type of object into another. Note, however, that
 * the {@link #transform(Object) transform} method takes one input object but
 * produces multiple output objects (a {@code List}). This is because it might
 * be possible to extract multiple objects from one and the same source object.
 * For example, one CSV record in Brahms may contain multiple images.
 * 
 * @author Ayco Holleman
 *
 * @param <INPUT>
 *            The type of object serving as input to the transformer
 * @param <OUTPUT>
 *            The type of object that is output from the transformer
 */
public interface Transformer<INPUT, OUTPUT extends IDocumentObject> {

	/**
	 * Transform the input object into one or more output objects.
	 * 
	 * @param input
	 * @return
	 */
	List<OUTPUT> transform(INPUT input);

}
