package nl.naturalis.nba.dao.format.calc;

import static nl.naturalis.nba.dao.format.FormatUtil.EMPTY_STRING;

import java.util.Date;
import java.util.List;
import java.util.Map;

import nl.naturalis.nba.api.model.Reference;
import nl.naturalis.nba.api.model.Taxon;
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

	private static final int ACCEPTED_NAME = 0;
	private static final int SYNONYM = 1;
	private static final int VERNACULAR_NAME = 2;

	private int type;

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
				this.type = ACCEPTED_NAME;
				break;
			case "synonym":
				this.type = SYNONYM;
				break;
			case "vernacular name":
				this.type = VERNACULAR_NAME;
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

		Taxon taxon = (Taxon) entity.getEntity();
		List<Reference> references;
		if (type == ACCEPTED_NAME) {
			references = taxon.getAcceptedName().getReferences();
		}
		else {
			references = taxon.getReferences();
		}
		if (references == null) {
			return EMPTY_STRING;
		}
		Reference ref = references.get(0);
		String title = ref.getTitleCitation();
		String author = null;
		if (ref.getAuthor() != null) {
			author = ref.getAuthor().getFullName();
		}
		Date date = ref.getPublicationDate();
		if (title == null && author == null && date == null) {
			return EMPTY_STRING;
		}
		StringBuilder sb = new StringBuilder(32);
		if (title != null) {
			sb.append(title);
		}
		if (author != null || date != null) {
			if (title != null) {
				sb.append(' ');
			}
			sb.append('(');
			sb.append(author);
			if (author == null) {
				if (date != null) {
					sb.append(FormatUtil.formatDate(date));
				}
				sb.append(date);
			}
			else if (date != null) {
				sb.append(", ");
				sb.append(FormatUtil.formatDate(date));
			}
			sb.append(')');
		}
		return sb.toString();
	}

}
