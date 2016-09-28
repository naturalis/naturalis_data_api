package nl.naturalis.nba.dao.es.format.calc;

import static nl.naturalis.nba.dao.es.format.FormatUtil.EMPTY_STRING;

import java.util.LinkedHashMap;

import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.common.Path;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.es.format.CalculatorException;
import nl.naturalis.nba.dao.es.format.DataSetConfigurationException;
import nl.naturalis.nba.dao.es.format.EntityObject;
import nl.naturalis.nba.dao.es.format.ICalculator;

/**
 * Generates a value for the &#34;expert&#34; field. Assumes the entity object
 * is a plain {@link Taxon} document (no entity path) or the
 * {@link Taxon#getSynonyms() synonyms} entity or
 * {@link Taxon#getVernacularNames() vernacularNames} entity within a
 * {@code Taxon} document.
 * 
 * @author Ayco Holleman
 *
 */
public class TaxonomicExpertCalculator implements ICalculator {

	private static final Path namePath = new Path("experts.0.fullName");
	private static final Path orgPath = new Path("experts.0.organization.name");

	@Override
	public void initialize(LinkedHashMap<String, String> args) throws DataSetConfigurationException
	{
	}

	@Override
	public Object calculateValue(EntityObject entity) throws CalculatorException
	{
		String name = EMPTY_STRING;
		String org = EMPTY_STRING;
		Object value = namePath.read(entity.getData());
		if (value != JsonUtil.MISSING_VALUE) {
			name = value.toString();
		}
		value = orgPath.read(entity.getData());
		if (value != JsonUtil.MISSING_VALUE) {
			org = value.toString();
		}
		if (name == EMPTY_STRING && org == EMPTY_STRING) {
			return EMPTY_STRING;
		}
		return name + " (" + org + ")";
	}

}
