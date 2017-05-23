package nl.naturalis.nba.dao.format.calc;

import static nl.naturalis.nba.common.json.JsonUtil.MISSING_VALUE;
import static nl.naturalis.nba.common.json.JsonUtil.readField;
import static nl.naturalis.nba.dao.format.FormatUtil.EMPTY_STRING;

import java.util.List;
import java.util.Map;

import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.dao.format.CalculationException;
import nl.naturalis.nba.dao.format.CalculatorInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.ICalculator;

public class SpecimenMultiMediaCalculator implements ICalculator {

	private static final Path path = new Path("associatedMultiMediaUris");

	@Override
	public void initialize(Map<String, String> args) throws CalculatorInitializationException
	{
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object calculateValue(EntityObject entity) throws CalculationException
	{
		Object value = readField(entity.getData(), path);
		if (value == MISSING_VALUE) {
			return EMPTY_STRING;
		}
		List<Map<String, Object>> serviceAccessPoints = (List<Map<String, Object>>) value;
		StringBuilder uris = new StringBuilder(100);
		int i = 0;
		for (Map<String, Object> sap : serviceAccessPoints) {
			if (i++ != 0) {
				uris.append('|');
			}
			uris.append(sap.get("accessUri"));
		}
		return uris.toString();
	}

}
