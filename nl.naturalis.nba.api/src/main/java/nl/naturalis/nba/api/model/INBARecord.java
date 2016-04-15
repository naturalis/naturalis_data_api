package nl.naturalis.nba.api.model;

/**
 * Interface indicating that the implementing class corresponds to a single
 * document in the document store underlying the NBA. Only instances of classes
 * implementing this interface can by retrieved using the NBA's
 * {@code findById()} methods. N.B. Contrast this with
 * {@link NBATraceableObject}. Instances of these classes can be traced back to
 * <i>source system</i> records.
 * 
 * @author Ayco Holleman
 *
 */
public interface INBARecord {

	public String getId();

}
