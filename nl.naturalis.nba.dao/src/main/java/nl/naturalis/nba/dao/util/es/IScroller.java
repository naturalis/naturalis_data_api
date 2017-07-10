package nl.naturalis.nba.dao.util.es;

import nl.naturalis.nba.api.NbaException;

/**
 * An interface specifying behaviour for iterating over large sets of documents.
 * Contrary to the {@link IDocumentIterator} class, instances of this interface
 * do not return the documents they iterate over. Rather the documents are
 * processed using a callback interface ({@link SearchHitHandler}) passed to the
 * {@link #scroll(SearchHitHandler) scroll} method.
 * 
 * @author Ayco Holleman
 *
 */
public interface IScroller {

	/**
	 * Iterates over the documents, passing each of them to the specified
	 * {@link SearchHitHandler}.
	 * 
	 * @throws NbaException
	 */
	void scroll(SearchHitHandler handler) throws NbaException;

}