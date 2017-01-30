package nl.naturalis.nba.dao.translate.query;

import static nl.naturalis.nba.api.ComparisonOperator.BETWEEN;
import static nl.naturalis.nba.api.ComparisonOperator.EQUALS;
import static nl.naturalis.nba.api.ComparisonOperator.EQUALS_IC;
import static nl.naturalis.nba.api.ComparisonOperator.GT;
import static nl.naturalis.nba.api.ComparisonOperator.GTE;
import static nl.naturalis.nba.api.ComparisonOperator.IN;
import static nl.naturalis.nba.api.ComparisonOperator.LIKE;
import static nl.naturalis.nba.api.ComparisonOperator.LT;
import static nl.naturalis.nba.api.ComparisonOperator.LTE;
import static nl.naturalis.nba.api.ComparisonOperator.MATCHES;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_BETWEEN;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_EQUALS;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_EQUALS_IC;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_IN;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_LIKE;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_MATCHES;
import static nl.naturalis.nba.common.es.map.ESDataType.BOOLEAN;
import static nl.naturalis.nba.common.es.map.ESDataType.BYTE;
import static nl.naturalis.nba.common.es.map.ESDataType.DATE;
import static nl.naturalis.nba.common.es.map.ESDataType.DOUBLE;
import static nl.naturalis.nba.common.es.map.ESDataType.FLOAT;
import static nl.naturalis.nba.common.es.map.ESDataType.GEO_POINT;
import static nl.naturalis.nba.common.es.map.ESDataType.GEO_SHAPE;
import static nl.naturalis.nba.common.es.map.ESDataType.INTEGER;
import static nl.naturalis.nba.common.es.map.ESDataType.LONG;
import static nl.naturalis.nba.common.es.map.ESDataType.SHORT;
import static nl.naturalis.nba.common.es.map.ESDataType.KEYWORD;
import static nl.naturalis.nba.common.es.map.MultiField.IGNORE_CASE_MULTIFIELD;
import static nl.naturalis.nba.common.es.map.MultiField.LIKE_MULTIFIELD;

import java.util.EnumMap;
import java.util.EnumSet;

import nl.naturalis.nba.api.ComparisonOperator;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.common.es.map.ESDataType;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.NoSuchFieldException;
import nl.naturalis.nba.common.es.map.SimpleField;
import nl.naturalis.nba.common.es.map.KeywordField;
import nl.naturalis.nba.dao.DocumentType;

/**
 * Checks whether the {@link ComparisonOperator operator} used in a
 * {@link QueryCondition query condition} is valid given the type of the field being
 * queried and given the analyzers on that field.
 * 
 * @author Ayco Holleman
 *
 */
public class OperatorCheck {

	/*
	 * A type-to-operator map. For each data type it maintains a set of allowed
	 * operators.
	 */
	private static final EnumMap<ESDataType, EnumSet<ComparisonOperator>> t2o = new EnumMap<>(
			ESDataType.class);

	static {
		t2o.put(BOOLEAN, EnumSet.of(EQUALS, NOT_EQUALS, EQUALS_IC, NOT_EQUALS_IC, IN, NOT_IN));
		t2o.put(BYTE, EnumSet.of(EQUALS, NOT_EQUALS, EQUALS_IC, NOT_EQUALS_IC, IN, NOT_IN, LT, LTE,
				GT, GTE, BETWEEN, NOT_BETWEEN));
		t2o.put(SHORT, EnumSet.of(EQUALS, NOT_EQUALS, EQUALS_IC, NOT_EQUALS_IC, IN, NOT_IN, LT, LTE,
				GT, GTE, BETWEEN, NOT_BETWEEN));
		t2o.put(INTEGER, EnumSet.of(EQUALS, NOT_EQUALS, EQUALS_IC, NOT_EQUALS_IC, IN, NOT_IN, LT,
				LTE, GT, GTE, BETWEEN, NOT_BETWEEN));
		t2o.put(LONG, EnumSet.of(EQUALS, NOT_EQUALS, EQUALS_IC, NOT_EQUALS_IC, IN, NOT_IN, LT, LTE,
				GT, GTE, BETWEEN, NOT_BETWEEN));
		t2o.put(FLOAT, EnumSet.of(EQUALS, NOT_EQUALS, EQUALS_IC, NOT_EQUALS_IC, IN, NOT_IN, LT, LTE,
				GT, GTE, BETWEEN, NOT_BETWEEN));
		t2o.put(DOUBLE, EnumSet.of(EQUALS, NOT_EQUALS, EQUALS_IC, NOT_EQUALS_IC, IN, NOT_IN, LT,
				LTE, GT, GTE, BETWEEN, NOT_BETWEEN));
		t2o.put(DATE, EnumSet.of(EQUALS, NOT_EQUALS, EQUALS_IC, NOT_EQUALS_IC, IN, NOT_IN, LT, LTE,
				GT, GTE, BETWEEN, NOT_BETWEEN));
		t2o.put(KEYWORD, EnumSet.of(EQUALS, NOT_EQUALS, EQUALS_IC, NOT_EQUALS_IC, IN, NOT_IN, LIKE,
				NOT_LIKE, MATCHES, NOT_MATCHES));
		t2o.put(GEO_POINT, EnumSet.of(IN, NOT_IN));
		t2o.put(GEO_SHAPE, EnumSet.of(IN, NOT_IN));
	}

	/**
	 * Checks whether the specified operator can be used for query conditions on
	 * the specified field.
	 * 
	 * @param field
	 * @param operator
	 * @return
	 */
	public static boolean isOperatorAllowed(SimpleField field, ComparisonOperator operator)
	{
		if (isOperatorAllowed(field.getType(), operator)) {
			if (operator == LIKE || operator == NOT_LIKE) {
				if (field instanceof KeywordField) {
					return ((KeywordField) field).hasMultiField(LIKE_MULTIFIELD);
				}
				return false;
			}
			if (operator == EQUALS_IC || operator == NOT_EQUALS_IC) {
				if (field instanceof KeywordField) {
					return ((KeywordField) field).hasMultiField(IGNORE_CASE_MULTIFIELD);
				}
				// Otherwise (e.g. with numbers) EQUALS_IC is treated like EQUALS
			}
			return true;
		}
		return false;
	}

	/**
	 * Checks whether the specified operator can be used for query conditions on
	 * the specified field.
	 * 
	 * @param field
	 *            The full path of the field
	 * @param operator
	 *            The operator to check
	 * @param dt
	 *            The document type containing the field
	 * @return
	 * @throws NoSuchFieldException
	 */
	public static <T extends IDocumentObject> boolean isOperatorAllowed(String field,
			ComparisonOperator operator, DocumentType<T> dt) throws NoSuchFieldException
	{
		MappingInfo<T> mappingInfo = new MappingInfo<>(dt.getMapping());
		ESField esField = mappingInfo.getField(field);
		if (!(esField instanceof SimpleField)) {
			return false;
		}
		return isOperatorAllowed((SimpleField) esField, operator);
	}

	private static boolean isOperatorAllowed(ESDataType dataType, ComparisonOperator operator)
	{
		EnumSet<ComparisonOperator> ops = t2o.get(dataType);
		if (ops == null) {
			/*
			 * Data type is OBJECT or NESTED and no condition/comparison is
			 * allowed using these types of fields, whatever the operator.
			 */
			return false;
		}
		return ops.contains(operator);
	}
}
