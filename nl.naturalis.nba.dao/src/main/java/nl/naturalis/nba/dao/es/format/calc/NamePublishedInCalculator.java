package nl.naturalis.nba.dao.es.format.calc;

import static nl.naturalis.nba.dao.es.format.FormatUtil.EMPTY_STRING;

import java.util.LinkedHashMap;

import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.common.Path;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.es.format.CalculatorException;
import nl.naturalis.nba.dao.es.format.DataSetConfigurationException;
import nl.naturalis.nba.dao.es.format.EntityObject;
import nl.naturalis.nba.dao.es.format.FormatUtil;
import nl.naturalis.nba.dao.es.format.ICalculator;

/**
 * Generates a value for the DarwinCore &#34;namePublishedIn&#34; term. Assumes
 * the entity object is a plain {@link Taxon} document (no entity path) or the
 * {@link Taxon#getSynonyms() synonyms} entity or
 * {@link Taxon#getVernacularNames() vernacularNames} entity within a
 * {@code Taxon} document.
 * 
 * @author Ayco Holleman
 *
 */
public class NamePublishedInCalculator implements ICalculator {

	private static final Path titlePath = new Path("references.0.titleCitation");
	private static final Path authorPath = new Path("references.0.author.fullName");
	private static final Path datePath = new Path("references.0.publicationDate");

	@Override
	public void initialize(LinkedHashMap<String, String> args) throws DataSetConfigurationException
	{
	}

	@Override
	public Object calculateValue(EntityObject entity) throws CalculatorException
	{
		String title = EMPTY_STRING;
		String author = EMPTY_STRING;
		String date = EMPTY_STRING;
		Object value = titlePath.read(entity.getData());
		if (value != JsonUtil.MISSING_VALUE) {
			title = value.toString();
		}
		value = authorPath.read(entity.getData());
		if (value != JsonUtil.MISSING_VALUE) {
			author = value.toString();
		}
		value = datePath.read(entity.getData());
		if (value != JsonUtil.MISSING_VALUE) {
			date = FormatUtil.formatDate(value.toString());
		}
		if (title == EMPTY_STRING && author == EMPTY_STRING && date == EMPTY_STRING) {
			return EMPTY_STRING;
		}
		return title + " (" + author + "," + date + ")";
	}

}
