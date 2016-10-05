package nl.naturalis.nba.dao.format.calc;

import static nl.naturalis.nba.common.json.JsonUtil.MISSING_VALUE;
import static nl.naturalis.nba.dao.format.FormatUtil.EMPTY_STRING;

import java.util.Map;

import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.common.Path;
import nl.naturalis.nba.dao.format.CalculationException;
import nl.naturalis.nba.dao.format.CalculatorInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.ICalculator;

/**
 * Generates a value for the DarwinCore &#34;namePublishedIn&#34; term. Assumes
 * the entity object is a plain {@link Taxon} document or the
 * {@link Taxon#getSynonyms() synonyms} entity within a {@code Taxon} document.
 * 
 * @author Ayco Holleman
 *
 */
public class ScientificNameAuthorshipCalculator implements ICalculator {

	private Path authorPath;
	private Path yearPath;
	private Path verbatimPath;

	@Override
	public void initialize(Map<String, String> args) throws CalculatorInitializationException
	{
		String type = args.get("type");
		if (type == null) {
			String msg = "Missing required element <arg name=\"type\">";
			throw new CalculatorInitializationException(msg);
		}
		switch (type) {
			case "accepted name":
				authorPath = new Path("acceptedName.author");
				yearPath = new Path("acceptedName.year");
				verbatimPath = new Path("acceptedName.authorshipVerbatim");
				break;
			case "synonym":
				authorPath = new Path("author");
				yearPath = new Path("year");
				verbatimPath = new Path("authorshipVerbatim");
				break;
			default:
				String msg = "Contents of element <arg name=\"type\"> must be one "
						+ "of: \"accepted name\", \"synonym\"";
				throw new CalculatorInitializationException(msg);
		}
	}

	@Override
	public Object calculateValue(EntityObject entity) throws CalculationException
	{
		Object value = authorPath.read(entity.getData());
		if (value == MISSING_VALUE) {
			value = verbatimPath.read(entity.getData());
			if (value == MISSING_VALUE) {
				return EMPTY_STRING;
			}
			return value;
		}
		Object year = yearPath.read(entity.getData());
		if (year == MISSING_VALUE) {
			return value;
		}
		return value + ", " + year;
	}

}
