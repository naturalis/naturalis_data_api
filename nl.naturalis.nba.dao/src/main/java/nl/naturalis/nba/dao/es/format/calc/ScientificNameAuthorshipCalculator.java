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
 * Generates a value for the DarwinCore &#34;namePublishedIn&#34; term. Assumes
 * the entity object is a plain {@link Taxon} document (no entity path) or the
 * {@link Taxon#getSynonyms() synonyms} entity within a {@code Taxon} document.
 * 
 * @author Ayco Holleman
 *
 */
public class ScientificNameAuthorshipCalculator implements ICalculator {

	private static final Path authorPath = new Path("author");
	private static final Path yearPath = new Path("year");
	private static final Path verbatimPath = new Path("authorshipVerbatim");

	@Override
	public void initialize(LinkedHashMap<String, String> args) throws DataSetConfigurationException
	{
	}

	@Override
	public Object calculateValue(EntityObject entity) throws CalculatorException
	{
		String author = EMPTY_STRING;
		String year = EMPTY_STRING;
		Object value = authorPath.read(entity.getData());
		if (value != JsonUtil.MISSING_VALUE) {
			author = value.toString();
		}
		value = yearPath.read(entity.getData());
		if (value != JsonUtil.MISSING_VALUE) {
			year = value.toString();
		}
		if (author != EMPTY_STRING && year != EMPTY_STRING) {
			return author + ", " + year;
		}
		value = verbatimPath.read(entity.getData());
		if (value != JsonUtil.MISSING_VALUE) {
			return value.toString();
		}
		return EMPTY_STRING;
	}

}
