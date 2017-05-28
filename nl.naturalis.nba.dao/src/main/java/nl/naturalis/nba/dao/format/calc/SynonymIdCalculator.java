package nl.naturalis.nba.dao.format.calc;

import java.util.Map;
import static nl.naturalis.nba.common.json.JsonUtil.readField;
import static nl.naturalis.nba.common.json.JsonUtil.MISSING_VALUE;

import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.dao.format.CalculationException;
import nl.naturalis.nba.dao.format.CalculatorInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.ICalculator;

public class SynonymIdCalculator implements ICalculator {

	private static final Path sourceSystemIdPath = new Path("sourceSystemId");
	private static final Path fsnPath = new Path("fullScientificName");
	private static final Path taxonomicStatusPath = new Path("taxonomicStatus");

	@Override
	public void initialize(Map<String, String> args)
			throws CalculatorInitializationException
	{
	}

	@Override
	public Object calculateValue(EntityObject entity) throws CalculationException
	{
		Object sourceSystemId = readField(entity.getDocument(), sourceSystemIdPath);
		Object fsn = readField(entity.getData(), fsnPath);
		Object taxonomicStatus = readField(entity.getData(), taxonomicStatusPath);
		long hash = sourceSystemId.hashCode();
		hash = (hash * 31) + (fsn == MISSING_VALUE ? 0 : fsn.hashCode());
		hash = (hash * 31)
				+ (taxonomicStatus == MISSING_VALUE ? 0 : taxonomicStatus.hashCode());
		return Long.toHexString(hash).toUpperCase();
	}

}
