package nl.naturalis.nba.dao.format.calc;

import static nl.naturalis.nba.dao.format.FormatUtil.EMPTY_STRING;

import java.util.List;
import java.util.Map;

import nl.naturalis.nba.api.model.Expert;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.dao.format.CalculationException;
import nl.naturalis.nba.dao.format.CalculatorInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.ICalculator;

/**
 * A calculator for the expert field in a DarwinCore archive for taxa. Assumes
 * the {@link EntityObject entity object} either is a plain {@link Taxon}
 * document or a {@link Taxon#getSynonyms() synonym} or a
 * {@link Taxon#getVernacularNames() vernacular name} within a {@code Taxon}
 * document.
 * 
 * @author Ayco Holleman
 *
 */
public class TaxonomicExpertCalculator implements ICalculator {

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
		List<Expert> experts;
		if (type == ACCEPTED_NAME) {
			experts = taxon.getAcceptedName().getExperts();
		}
		else {
			experts = taxon.getExperts();
		}
		if (experts == null) {
			return EMPTY_STRING;
		}
		String name = experts.get(0).getFullName();
		String org = null;
		if (experts.get(0).getOrganization() != null) {
			org = experts.get(0).getOrganization().getName();
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
