package nl.naturalis.nba.utils.convert;

/**
 * A callback interface defining the translation of one string into another.
 * 
 */
public interface Translator extends Manipulator<String> {

	@Override
	String execute(String input, Object... conversionArguments);

}
