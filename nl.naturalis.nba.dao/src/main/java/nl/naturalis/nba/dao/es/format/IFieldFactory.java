package nl.naturalis.nba.dao.es.format;

import nl.naturalis.nba.dao.es.format.calc.ICalculator;

/**
 * A {@code ITypedFieldFactory} produces format-specific versions of data set
 * fields. For each format (CSV, XML, etc.) a concrete implementation must be
 * provided that produces {@link IField} instances that format and escape values
 * as appropriate for that format.
 * 
 * @author Ayco Holleman
 *
 */
public interface IFieldFactory {

	/**
	 * Returns an {@link IField} instance that provides a default value for the
	 * specified field.
	 * 
	 * @param name
	 * @param constant
	 * @return
	 */
	IField createConstantField(String name, String constant);

	/**
	 * Returns an {@link IField} instance that uses an {@link ICalculator}
	 * instance to calculate a value for the specified field.
	 * 
	 * @param name
	 * @param calculator
	 * @return
	 */
	IField createdCalculatedField(String name, ICalculator calculator);

}