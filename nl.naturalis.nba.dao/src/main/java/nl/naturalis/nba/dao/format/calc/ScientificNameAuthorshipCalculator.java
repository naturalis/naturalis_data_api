package nl.naturalis.nba.dao.format.calc;

import static nl.naturalis.nba.dao.format.FormatUtil.EMPTY_STRING;

import java.util.Map;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.dao.format.CalculationException;
import nl.naturalis.nba.dao.format.CalculatorInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.ICalculator;

/**
 * A calculator for the scientificNameAuthorship field in a DarwinCore archive
 * for taxa. Assumes the {@link EntityObject entity object} either is a plain
 * {@link Taxon} document or a {@link Taxon#getSynonyms() synonym} or a
 * {@link Taxon#getVernacularNames() vernacular name} within a {@code Taxon}
 * document.
 * 
 * @author Ayco Holleman
 *
 */
public class ScientificNameAuthorshipCalculator implements ICalculator {

	private static final int ACCEPTED_NAME = 0;
	private static final int SYNONYM = 1;

	private int type;

  @Override
  public void initialize(Class<? extends IDocumentObject> docType, Map<String, String> args) throws CalculatorInitializationException
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
			default:
				String msg = "Contents of element <arg name=\"type\"> must be one "
						+ "of: \"accepted name\", \"synonym\"";
				throw new CalculatorInitializationException(msg);
		}
	}

	@Override
	public Object calculateValue(EntityObject entity) throws CalculationException
	{
		String author;
		String year;
		String verbatim;
		if (type == ACCEPTED_NAME) {
			Taxon taxon = (Taxon) entity.getEntity();
			author = taxon.getAcceptedName().getAuthor();
			year = taxon.getAcceptedName().getYear();
			verbatim = taxon.getAcceptedName().getAuthorshipVerbatim();
		}
		else {
			ScientificName synonym = (ScientificName) entity.getEntity();
			author = synonym.getAuthor();
			year = synonym.getYear();
			verbatim = synonym.getAuthorshipVerbatim();
		}
		if (author == null) {
			if (verbatim == null) {
				return EMPTY_STRING;
			}
			return verbatim;
		}
		if (year == null) {
			return author;
		}
		return author + ", " + year;
	}

}
