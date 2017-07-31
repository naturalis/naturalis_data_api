package nl.naturalis.nba.dao.format.calc;

import static nl.naturalis.nba.dao.format.FormatUtil.EMPTY_STRING;

import java.util.List;
import java.util.Map;

import nl.naturalis.nba.api.model.Reference;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.dao.format.CalculationException;
import nl.naturalis.nba.dao.format.CalculatorInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.ICalculator;

public class TaxonRemarksCalculator implements ICalculator {

	@Override
	public void initialize(Map<String, String> args) throws CalculatorInitializationException
	{
	}

	@Override
	public Object calculateValue(EntityObject entity) throws CalculationException
	{
		Specimen specimen = (Specimen) entity.getDocument();
		SpecimenIdentification si = specimen.getIdentifications().iterator().next();
		List<Reference> refs = si.getScientificName().getReferences();
		if (refs == null) {
			return EMPTY_STRING;
		}
		Reference ref = refs.iterator().next();
		if (ref.getAuthor() == null) {
			return EMPTY_STRING;
		}
		String fullName = ref.getAuthor().getFullName();
		if (fullName == null) {
			return EMPTY_STRING;
		}
		return fullName.replaceAll("[,\\[\\]]", "");
	}
}
