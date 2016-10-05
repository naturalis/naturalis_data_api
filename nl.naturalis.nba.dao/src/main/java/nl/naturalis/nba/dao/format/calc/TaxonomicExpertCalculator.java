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
 * Generates a value for the &#34;expert&#34; field. Assumes the entity object
 * is a plain {@link Taxon} document or the {@link Taxon#getSynonyms() synonyms}
 * entity or {@link Taxon#getVernacularNames() vernacularNames} entity within a
 * {@code Taxon} document.
 * 
 * @author Ayco Holleman
 *
 */
public class TaxonomicExpertCalculator implements ICalculator {

	private Path namePath;
	private Path orgPath;

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
				namePath = new Path("acceptedName.experts.0.fullName");
				orgPath = new Path("acceptedName.experts.0.organization.name");
				break;
			case "synonym":
			case "vernacular name":
				namePath = new Path("experts.0.fullName");
				orgPath = new Path("experts.0.organization.name");
				break;
			default:
				String msg = "Contents of element <arg name=\"type\"> must be one "
						+ "of: \"accepted name\", \"synonym\", \"vernacular name\"";
				throw new CalculatorInitializationException(msg);
		}
	}

	@Override
	public Object calculateValue(EntityObject entity) throws CalculationException
	{
		String name = null;
		String org = null;
		Object value = namePath.read(entity.getData());
		if (value != MISSING_VALUE) {
			name = value.toString();
		}
		value = orgPath.read(entity.getData());
		if (value != MISSING_VALUE) {
			org = value.toString();
		}
		if (name == null) {
			if (org == null) {
				return EMPTY_STRING;
			}
			return '(' + org + ')';
		}
		if (org == null) {
			return name;
		}
		return name + " (" + org + ")";
	}

}
