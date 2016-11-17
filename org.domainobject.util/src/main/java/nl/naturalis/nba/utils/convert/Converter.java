package nl.naturalis.nba.utils.convert;

/**
 * A callback interface defining the conversion of one object into another object.
 * 
 * @param <IN>
 *            The type of the object going into the conversion
 * @param <OUT>
 *            The type of the object coming out of the conversion
 */
public interface Converter<IN, OUT> {

	/**
	 * Converts an instance of type T to an instance of type U using the specified
	 * arguments.
	 * 
	 * @param obj
	 *            The object to convert
	 * @param conversionArguments
	 *            Extra arguments driving the conversion (optional)
	 * 
	 * @return The object resulting from the conversion
	 */
	OUT execute(IN obj, Object... conversionArguments);

}
