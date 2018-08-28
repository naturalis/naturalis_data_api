package nl.naturalis.nba.dao.format;

import java.util.Map;
import nl.naturalis.nba.api.model.IDocumentObject;

/**
 * An {@code ICalculator} is used to determine the value for a calculated field
 * in a data set. This type of field does not obtain its value directly from an
 * Elasticsearch document. Instead, it applies some logic, with or without using
 * the data in the Elasticsearch document, to arrive at the value for the field.
 * {@code ICalculator} implementations <b>must</b> provide a no-arg constructor.
 * 
 * @author Ayco Holleman
 *
 */
public interface ICalculator {

	/**
	 * Initializes the calculator with the specified values. The keys of the map
	 * are parameter names, the values of the map are parameter values. This
	 * method is called just once, right after instantiation of the calculator.
	 */
	/*
	 * Although this interface is oblivious to it, in practice these
	 * initialization values come from <arg> elements within a <calculator>
	 * element within a DwCA configuration file.
	 */
	void initialize(Map<String, String> args) throws CalculatorInitializationException;
	
	/**
	 * Initialiser for a calculator that needs to be aware of the document type.
	 * @param cls
	 * @param args
	 * @throws CalculatorInitializationException
	 */
	default void initialize(Class<? extends IDocumentObject> cls, Map<String, String> args) throws CalculatorInitializationException {};

	/**
	 * Calculates a values. The specified entity object may or may not be used
	 * to calculate the value. <i>This method must never return null.</i> Fields
	 * using an {@code ICalculator} will blindly call {@code toString()} on the
	 * returned value. Recommended practice to indicate that a value was
	 * {@code null} is to return {@link FormatUtil#EMPTY_STRING}.
	 * 
	 * @param entity
	 * @return
	 */
	Object calculateValue(EntityObject entity) throws CalculationException;

}
