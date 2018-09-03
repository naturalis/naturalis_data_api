package nl.naturalis.nba.dao.format.calc;

import static nl.naturalis.nba.dao.format.FormatUtil.EMPTY_STRING;

import java.util.List;
import java.util.Map;

import nl.naturalis.nba.api.model.GatheringEvent;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.Person;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.format.CalculationException;
import nl.naturalis.nba.dao.format.CalculatorInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.ICalculator;

/**
 * A calculator for the recordedBy field in a DarwinCore archive for specimens.
 * Assumes the {@link EntityObject entity object} is a plain {@link Specimen}
 * document.
 * 
 * @author Ayco Holleman
 *
 */
public class RecordedByCalculator implements ICalculator {

  @Override
  public void initialize(Class<? extends IDocumentObject> docType, Map<String, String> args) throws CalculatorInitializationException
  {
  }

	@Override
	public Object calculateValue(EntityObject entity) throws CalculationException
	{
		Specimen specimen = (Specimen) entity.getDocument();
		GatheringEvent ge = specimen.getGatheringEvent();
		if (ge == null) {
			return EMPTY_STRING;
		}
		List<Person> persons = ge.getGatheringPersons();
		if (persons == null) {
			return EMPTY_STRING;
		}
		Person person = persons.iterator().next();
		if (person.getFullName() == null) {
			return EMPTY_STRING;
		}
		String fullName = person.getFullName();
		if (fullName == null) {
			return EMPTY_STRING;
		}
		return fullName.replaceAll("[,\\[\\]]", "");
	}
}
