package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.SummaryObjectUtil.copyTaxon;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.ScientificNameGroup_old;
import nl.naturalis.nba.api.model.Taxon;

class TaxonNameTransformer {

	@SuppressWarnings("unused")
	private static final Logger logger = getLogger(TaxonNameTransformer.class);
	private static final ScientificNameGroup_old DUMMY = new ScientificNameGroup_old("__dummy__");

	private ScientificNameGroup_old previousGroup = DUMMY;

	private int created;
	private int updated;

	TaxonNameTransformer()
	{
	}

	Collection<ScientificNameGroup_old> transform(Collection<Taxon> batch)
	{
		ArrayList<Taxon> taxa;
		if (batch.getClass() == ArrayList.class) {
			taxa = (ArrayList<Taxon>) batch;
		}
		else {
			taxa = new ArrayList<>(batch);
		}
		ArrayList<ScientificNameGroup_old> groups = new ArrayList<>(batch.size());
		if (previousGroup != DUMMY) {
			groups.add(previousGroup);
		}
		ScientificNameGroup_old group;
		String name;
		for (int i = 0; i < batch.size(); i++) {
			Taxon taxon = taxa.get(i);
			name = taxon.getAcceptedName().getScientificNameGroup();
			if (name.equals(previousGroup.getName())) {
				++updated;
				group = previousGroup;
			}
			else {
				++created;
				group = new ScientificNameGroup_old(name);
			}
			group.addTaxon(copyTaxon(taxon));
			group.setTaxonCount(group.getTaxa().size());
			/*
			 * Do not add the last group in the batch; it will be added as the
			 * first group in the next batch. This way, if the last name in the
			 * current batch happens to be the same as the first in the next, no
			 * duplicate group will be created.
			 */
			if (i != batch.size() - 1) {
				groups.add(group);
			}
			previousGroup = group;
		}
		return groups;
	}

	ScientificNameGroup_old getLastGroup()
	{
		return previousGroup;
	}

	int getNumCreated()
	{
		return created;
	}

	int getNumUpdated()
	{
		return updated;
	}

}
