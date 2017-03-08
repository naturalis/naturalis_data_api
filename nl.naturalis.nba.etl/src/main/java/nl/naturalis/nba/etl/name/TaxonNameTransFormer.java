package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.name.NameImportUtil.copyTaxon;
import static nl.naturalis.nba.etl.name.NameImportUtil.createName;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.ScientificNameGroup;
import nl.naturalis.nba.api.model.Taxon;

class TaxonNameTransformer {

	@SuppressWarnings("unused")
	private static final Logger logger = getLogger(TaxonNameTransformer.class);

	private ScientificNameGroup previousGroup;

	private int created;

	TaxonNameTransformer()
	{
	}

	public Collection<ScientificNameGroup> transform(Collection<Taxon> taxa)
	{
		ArrayList<Taxon> taxonList;
		if (taxa.getClass() == ArrayList.class) {
			taxonList = (ArrayList<Taxon>) taxa;
		}
		else {
			taxonList = new ArrayList<>(taxa);
		}
		ArrayList<ScientificNameGroup> groups = new ArrayList<>(taxa.size());
		if (previousGroup != null) {
			groups.add(previousGroup);
		}
		for (int i = 0; i < taxa.size(); i++) {
			Taxon taxon = taxonList.get(i);
			String name = createName(taxon);
			ScientificNameGroup group;
			if (previousGroup == null || !name.equals(previousGroup.getName())) {
				++created;
				group = new ScientificNameGroup(name);
			}
			else {
				group = previousGroup;
			}
			previousGroup = group;
			group.addTaxon(copyTaxon(taxon));
			if (i != taxa.size() - 1) {
				groups.add(group);
			}
		}
		return groups;
	}

	public ScientificNameGroup getLastNameGroup()
	{
		return previousGroup;
	}

	public int getNumCreated()
	{
		return created;
	}

}
