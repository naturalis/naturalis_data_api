package nl.naturalis.nba.api.model;

import nl.naturalis.nba.api.ISpecimenAccess;

/**
 * Interface indicating that each instance of a class implementing this
 * interface corresponds to a single document in the document store underlying
 * the NBA. Only instances of classes implementing this interface can be
 * retrieved using the API's {@code find()} methods (e.g.
 * {@link ISpecimenAccess#find(String)}). N.B. Contrast this with
 * {@link NBATraceableObject}. Instances of these classes can be traced back to
 * <i>source system</i> records.
 * 
 * @author Ayco Holleman
 *
 */
public interface IDocumentObject {

	/**
	 * Returns the Elasticsearch system ID of the document corresponding to this
	 * instance.
	 * 
	 * @return
	 */
	public String getId();

}
