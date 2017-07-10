package nl.naturalis.nba.dao.util.es;

import java.util.Iterator;
import java.util.List;

import nl.naturalis.nba.api.model.IDocumentObject;

/**
 * An interface specifying behaviour for iterating over large sets of documents.
 * 
 * @author Ayco Holleman
 *
 */

public interface IDocumentIterator<T extends IDocumentObject> extends Iterator<T>, Iterable<T> {

	/**
	 * Returns the total number of documents to iterate over.
	 * 
	 * @return
	 */
	long size();

	/**
	 * Returns the number of documents iterated over thus far.
	 */
	long getDocCounter();

	/**
	 * Returns the next batch of documents. This method must be implemented such
	 * that {@link Iterator#next() Iterator.next} and {@link Iterator#hasNext()}
	 * keep working as expected both before and after calling this method.
	 * 
	 * @return
	 */
	public List<T> nextBatch();

}