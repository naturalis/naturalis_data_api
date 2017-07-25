package nl.naturalis.nba.dao.format.calc;

import static nl.naturalis.nba.dao.format.FormatUtil.EMPTY_STRING;

import java.util.Map;

import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.common.PathValueReader;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.format.CalculationException;
import nl.naturalis.nba.dao.format.CalculatorInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.FormatUtil;
import nl.naturalis.nba.dao.format.ICalculator;

/**
 * Generates a value for the DarwinCore &#34;namePublishedIn&#34; term. Assumes
 * the entity object is a plain {@link Taxon} document or the
 * {@link Taxon#getSynonyms() synonyms} entity or
 * {@link Taxon#getVernacularNames() vernacularNames} entity within a
 * {@code Taxon} document.
 * 
 * @author Ayco Holleman
 *
 */
public class NamePublishedInCalculator implements ICalculator {

	private PathValueReader titlePath;
	private PathValueReader authorPath;
	private PathValueReader datePath;

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
				titlePath = new PathValueReader("acceptedName.references.0.titleCitation");
				authorPath = new PathValueReader("acceptedName.references.0.author.fullName");
				datePath = new PathValueReader("acceptedName.references.0.publicationDate");
				break;
			case "synonym":
			case "vernacular name":
				titlePath = new PathValueReader("references.0.titleCitation");
				authorPath = new PathValueReader("references.0.author.fullName");
				datePath = new PathValueReader("references.0.publicationDate");
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
		String title = EMPTY_STRING;
		String author = EMPTY_STRING;
		String date = EMPTY_STRING;
		Object value = titlePath.read(entity.getEntity());
		if (value != JsonUtil.MISSING_VALUE) {
			title = value.toString();
		}
		value = authorPath.read(entity.getEntity());
		if (value != JsonUtil.MISSING_VALUE) {
			author = value.toString();
		}
		value = datePath.read(entity.getEntity());
		if (value != null) {
			date = FormatUtil.formatDate(value.toString());
		}
		if (title == EMPTY_STRING && author == EMPTY_STRING && date == EMPTY_STRING) {
			return EMPTY_STRING;
		}
		StringBuilder sb = new StringBuilder(32);
		sb.append(title);
		if (author != EMPTY_STRING || date != EMPTY_STRING) {
			if (title != EMPTY_STRING) {
				sb.append(' ');
			}
			sb.append('(');
			sb.append(author);
			if (author == EMPTY_STRING) {
				sb.append(date);
			}
			else if (date != EMPTY_STRING) {
				sb.append(", ");
				sb.append(date);
			}
			sb.append(')');
		}
		return sb.toString();
	}

}
