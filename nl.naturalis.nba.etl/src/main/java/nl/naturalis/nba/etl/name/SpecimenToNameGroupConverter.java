package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.etl.SummaryObjectUtil.copySpecimen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.ScientificNameGroup;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.api.model.TaxonomicIdentification;

class SpecimenToNameGroupConverter {

	private int numIdentifications;

	public SpecimenToNameGroupConverter()
	{
	}

	Collection<ScientificNameGroup> convert(Collection<Specimen> specimens)
	{
		// Assume about 3 identifications per specimen
		List<ScientificNameGroup> result = new ArrayList<>(specimens.size() * 3);
		for (Specimen specimen : specimens) {
			List<SpecimenIdentification> identifications = specimen.getIdentifications();
			if (identifications != null) {
				numIdentifications += identifications.size();
				sort(identifications);
				String prevName = null;
				for (TaxonomicIdentification si : identifications) {
					String name = si.getScientificName().getScientificNameGroup();
					if (prevName != null && name.equals(prevName)) {
						continue;
					}
					ScientificNameGroup nameGroup = new ScientificNameGroup(name);
					nameGroup.addSpecimen(copySpecimen(specimen, name));
					nameGroup.setSpecimenCount(nameGroup.getSpecimens().size());
					result.add(nameGroup);
					prevName = name;
				}
			}
		}
		return result;
	}

	int getNumIdentifications()
	{
		return numIdentifications;
	}

	private static void sort(List<SpecimenIdentification> identifications)
	{
		Collections.sort(identifications, new Comparator<SpecimenIdentification>() {

			@Override
			public int compare(SpecimenIdentification si1, SpecimenIdentification si2)
			{
				ScientificName sn1 = si1.getScientificName();
				ScientificName sn2 = si2.getScientificName();
				return sn1.getScientificNameGroup().compareTo(sn2.getScientificNameGroup());
			}
		});
	}

}
