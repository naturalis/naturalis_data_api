package nl.naturalis.nba.dao.query;

import static nl.naturalis.nba.api.query.ComparisonOperator.BETWEEN;
import static nl.naturalis.nba.api.query.ComparisonOperator.EQUALS;
import static nl.naturalis.nba.api.query.ComparisonOperator.EQUALS_IC;
import static nl.naturalis.nba.api.query.ComparisonOperator.GT;
import static nl.naturalis.nba.api.query.ComparisonOperator.GTE;
import static nl.naturalis.nba.api.query.ComparisonOperator.IN;
import static nl.naturalis.nba.api.query.ComparisonOperator.LIKE;
import static nl.naturalis.nba.api.query.ComparisonOperator.LT;
import static nl.naturalis.nba.api.query.ComparisonOperator.LTE;
import static nl.naturalis.nba.api.query.ComparisonOperator.NOT_BETWEEN;
import static nl.naturalis.nba.api.query.ComparisonOperator.NOT_EQUALS;
import static nl.naturalis.nba.api.query.ComparisonOperator.NOT_EQUALS_IC;
import static nl.naturalis.nba.api.query.ComparisonOperator.NOT_IN;
import static nl.naturalis.nba.api.query.ComparisonOperator.NOT_LIKE;
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
import static nl.naturalis.nba.common.es.map.ESDataType.STRING;
import static nl.naturalis.nba.common.es.map.MultiField.IGNORE_CASE_MULTIFIELD;
import static nl.naturalis.nba.common.es.map.MultiField.LIKE_MULTIFIELD;

import java.util.EnumMap;
import java.util.EnumSet;

import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.query.ComparisonOperator;
import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.common.es.map.ESDataType;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.NoSuchFieldException;
import nl.naturalis.nba.common.es.map.SimpleField;
import nl.naturalis.nba.common.es.map.StringField;
import nl.naturalis.nba.dao.DocumentType;

/**
 * Checks whether the {@link ComparisonOperator operator} used in a
 * {@link Condition query condition} is valid given the type of the field being
 * queried and given the analyzers on that field.
 * 
 * @author Ayco Holleman
 *
 */
public class OperatorCheck {

	/* A type-to-operator map */
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
		t2o.put(STRING, EnumSet.of(EQUALS, NOT_EQUALS, EQUALS_IC, NOT_EQUALS_IC, IN, NOT_IN, LIKE,
				NOT_LIKE));
		t2o.put(GEO_POINT, EnumSet.of(IN, NOT_IN));
		t2o.put(GEO_SHAPE, EnumSet.of(IN, NOT_IN));
	}
	
	public static boolean isOperatorAllowed(SimpleField field, ComparisonOperator operator)
	{
		if (isOperatorAllowed(field.getType(), operator)) {
			if (operator == LIKE || operator == NOT_LIKE) {
				if (field instanceof StringField) {
					return ((StringField) field).hasMultiField(LIKE_MULTIFIELD);
				}
				return false;
			}
			if (operator == EQUALS_IC || operator == NOT_EQUALS_IC) {
				if (field instanceof StringField) {
					return ((StringField) field).hasMultiField(IGNORE_CASE_MULTIFIELD);
				}
				// Otherwise (e.g. with numbers) EQUALS_IC is treated like EQUALS
			}
			return true;
		}
		return false;
	}

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
