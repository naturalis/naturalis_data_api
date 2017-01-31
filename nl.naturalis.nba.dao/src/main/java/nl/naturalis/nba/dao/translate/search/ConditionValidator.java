package nl.naturalis.nba.dao.translate.search;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.SearchCondition;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.NoSuchFieldException;
import nl.naturalis.nba.common.es.map.SimpleField;
import static nl.naturalis.nba.api.ComparisonOperator.*;

import nl.naturalis.nba.api.IllegalOperatorException;

/**
 * Ensures the a {@link SearchCondition} specifies an existing field and that it
 * is a primitive field (not an object).
 * 
 * @author Ayco Holleman
 *
 */
class ConditionValidator {

	private SearchCondition condition;
	private MappingInfo<?> mappingInfo;

	ConditionValidator(SearchCondition condition, MappingInfo<?> mappingInfo)
	{
		this.condition = condition;
		this.mappingInfo = mappingInfo;
	}

	void validateCondition() throws InvalidConditionException
	{
		if (condition.getFields() == null || condition.getFields().isEmpty()) {
			String msg = "Missing field in search condition";
			throw new InvalidConditionException(msg);
		}
		if (condition.getOperator() == null) {
			String msg = "Missing operator in search condition";
			throw new InvalidConditionException(msg);
		}
		for (Path path : condition.getFields()) {
			ESField field;
			try {
				field = mappingInfo.getField(path);
			}
			catch (NoSuchFieldException e) {
				throw new InvalidConditionException(e.getMessage());
			}
			if (!(field instanceof SimpleField)) {
				String fmt = "Field %s cannot be queried: field is an object";
				String msg = String.format(fmt, path);
				throw new InvalidConditionException(msg);
			}
			SimpleField sf = (SimpleField) field;
			if (sf.getIndex() == Boolean.FALSE) {
				String fmt = "Field %s cannot be queried: field is not indexed";
				String msg = String.format(fmt, path);
				throw new InvalidConditionException(msg);
			}
		}
		if (condition.getOperator() != MATCHES) {
			if (condition.getFields().size() != 1) {
				String fmt = "Only one field allowed in search condition with operator %s";
				String msg = String.format(fmt, condition.getOperator());
				throw new InvalidConditionException(msg);
			}
			Path path = condition.getFields().iterator().next();
			SimpleField sf = TranslatorUtil.getESField(path, mappingInfo);
			if (!OperatorValidator.isOperatorAllowed(sf, condition.getOperator())) {
				throw new IllegalOperatorException(path, condition.getOperator());
			}
		}
	}

}
