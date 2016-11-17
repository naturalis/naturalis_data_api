package nl.naturalis.nba.utils.convert;

/**
 * A callback interface defining the manipulation of object, without morhping it into
 * another <i>type</i> of object. In goes an object, out comes another object of the same
 * type, or the same object in another state.
 * 
 * @param <T>
 *            The type of the object to be manipulated
 */
public interface Manipulator<T> extends Converter<T, T> {

}
