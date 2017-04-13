package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.api.model.SourceSystem.COL;
import static nl.naturalis.nba.dao.DocumentType.TAXON;
import static nl.naturalis.nba.dao.util.es.ESUtil.getElasticsearchId;
import static nl.naturalis.nba.etl.ETLUtil.getTestGenera;
import static nl.naturalis.nba.etl.col.CoLReferenceCsvField.creator;
import static nl.naturalis.nba.etl.col.CoLReferenceCsvField.date;
import static nl.naturalis.nba.etl.col.CoLReferenceCsvField.description;
import static nl.naturalis.nba.etl.col.CoLReferenceCsvField.taxonID;
import static nl.naturalis.nba.etl.col.CoLReferenceCsvField.title;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nl.naturalis.nba.api.model.Person;
import nl.naturalis.nba.api.model.Reference;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.AbstractCSVTransformer;
import nl.naturalis.nba.etl.CSVTransformer;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.TransformUtil;

/**
 * Subclass of {@link CSVTransformer} that transforms CSV records into {@link Taxon}
 * objects.
 * 
 * @author Ayco Holleman
 *
 */
class CoLReferenceTransformer
		extends AbstractCSVTransformer<CoLReferenceCsvField, Taxon> {

	private final CoLTaxonLoader loader;
	private int orphans;
	private String[] testGenera;

	CoLReferenceTransformer(ETLStatistics stats, CoLTaxonLoader loader)
	{
		super(stats);
		this.loader = loader;
		testGenera = getTestGenera();
	}

	@Override
	protected String getObjectID()
	{
		return input.get(taxonID);
	}

	@Override
	protected List<Taxon> doTransform()
	{
		stats.recordsAccepted++;
		stats.objectsProcessed++;
		try {
			String id = getElasticsearchId(COL, objectID);
			Taxon taxon = loader.findInQueue(id);
			if (taxon != null) {
				Reference reference = createReference();
				if (!taxon.getReferences().contains(reference)) {
					stats.objectsAccepted++;
					taxon.addReference(reference);
				}
				else {
					stats.objectsRejected++;
					if (!suppressErrors) {
						error("Duplicate reference: " + reference);
					}
				}
				return null;
			}
			taxon = ESUtil.find(TAXON, id);
			Reference reference = createReference();
			if (taxon == null) {
				++orphans;
				if (!suppressErrors && testGenera == null) {
					error("Orphan reference: " + reference);
				}
			}
			else {
				if (taxon.getReferences() == null
						|| !taxon.getReferences().contains(reference)) {
					stats.objectsAccepted++;
					taxon.addReference(reference);
					return Arrays.asList(taxon);
				}
				if (!suppressErrors) {
					error("Duplicate reference: " + reference);
				}
			}
			stats.objectsRejected++;
			return null;
		}
		catch (Throwable t) {
			handleError(t);
			return null;
		}
	}

	public int getNumOrphans()
	{
		return orphans;
	}

	private Reference createReference()
	{
		Reference ref = new Reference();
		ref.setTitleCitation(input.get(title));
		ref.setCitationDetail(input.get(description));
		String s;
		if ((s = input.get(date)) != null) {
			Date pubDate = TransformUtil.parseDate(s);
			ref.setPublicationDate(pubDate);
		}
		if ((s = input.get(creator)) != null) {
			ref.setAuthor(new Person(s));
		}
		return ref;
	}
}
