package nl.naturalis.nba.dao.format.calc;

import java.util.Map;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.dao.format.CalculationException;
import nl.naturalis.nba.dao.format.CalculatorInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.ICalculator;

/**
 * A calculator for the taxonID field in a DarwinCore archive for taxa <i>if</i>
 * the CSV record being written is a synonym rather than an accepted name.
 * Assumes the {@link EntityObject entity object} is a plain {@link Taxon}
 * document.
 * 
 * @author Ayco Holleman
 *
 */
public class SynonymIdCalculator implements ICalculator {

  @Override
  public void initialize(Class<? extends IDocumentObject> docType, Map<String, String> args) throws CalculatorInitializationException
  {
  }

	@Override
	public Object calculateValue(EntityObject entity) throws CalculationException
	{
		Taxon taxon = (Taxon) entity.getDocument();
		ScientificName synonym = (ScientificName) entity.getEntity();
		long hash = taxon.getSourceSystemId().hashCode();
		hash = (hash * 31) + hash(synonym.getFullScientificName());
		// NOTE: Enum uses the hashCode method of Object. This means the hash will
		// change. To get a stable hash code we use the ordinal instead.
		if (synonym.getTaxonomicStatus() != null) {
		  hash = (hash * 31) + synonym.getTaxonomicStatus().ordinal() + 1;		  
		}
		return Long.toHexString(hash).toUpperCase();
	}

	private static int hash(Object obj)
	{
		return obj == null ? 0 : obj.hashCode();
	}

}
