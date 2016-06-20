package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.dao.es.util.DocumentType.TAXON;
import static nl.naturalis.nba.etl.col.CoLReferenceCsvField.creator;
import static nl.naturalis.nba.etl.col.CoLReferenceCsvField.date;
import static nl.naturalis.nba.etl.col.CoLReferenceCsvField.description;
import static nl.naturalis.nba.etl.col.CoLReferenceCsvField.taxonID;
import static nl.naturalis.nba.etl.col.CoLReferenceCsvField.title;
import static nl.naturalis.nba.dao.es.util.ESUtil.*;
import static nl.naturalis.nba.api.model.SourceSystem.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nl.naturalis.nba.api.model.Person;
import nl.naturalis.nba.api.model.Reference;
import nl.naturalis.nba.dao.es.types.ESTaxon;
import nl.naturalis.nba.etl.*;
import nl.naturalis.nba.etl.elasticsearch.IndexManagerNative;

/**
 * Subclass of {@link CSVTransformer} that transforms CSV records into
 * {@link ESTaxon} objects.
 * 
 * @author Ayco Holleman
 *
 */
class CoLReferenceTransformer extends AbstractCSVTransformer<CoLReferenceCsvField, ESTaxon> {

	private final IndexManagerNative index;
	private final CoLTaxonLoader loader;

	CoLReferenceTransformer(ETLStatistics stats, CoLTaxonLoader loader)
	{
		super(stats);
		this.index = ETLRegistry.getInstance().getIndexManager(TAXON);
		this.loader = loader;
	}

	@Override
	protected String getObjectID()
	{
		return input.get(taxonID);
	}

	@Override
	protected List<ESTaxon> doTransform()
	{
		stats.recordsAccepted++;
		stats.objectsProcessed++;
		List<ESTaxon> result = null;
		try {
			String id = getElasticsearchId(COL, objectID);
			boolean isNew = false;
			ESTaxon taxon = loader.findInQueue(id);
			if (taxon == null) {
				isNew = true;
				taxon = index.get(TAXON.getName(), id, ESTaxon.class);
			}
			if (taxon == null) {
				stats.objectsRejected++;
				if (!suppressErrors) {
					error("Orphan reference: " + input.get(title));
				}
			}
			else {
				Reference ref = createReference();
				if (taxon.getReferences() == null || !taxon.getReferences().contains(ref)) {
					stats.objectsAccepted++;
					taxon.addReference(ref);
					if (isNew) {
						result = Arrays.asList(taxon);
					}
					/*
					 * else we have added the reference to a taxon that's
					 * already queued for indexing, so we're fine
					 */
				}
				else {
					stats.objectsRejected++;
					if (!suppressErrors) {
						error("Duplicate reference for taxon: " + ref);
					}
				}
			}
		}
		catch (Throwable t) {
			handleError(t);
		}
		return result;
	}

	/**
	 * Removes all literature references from the taxon specified in the CSV
	 * record. Not part of the {@link Transformer} API, but used by the
	 * {@link CoLReferenceCleaner} to clean up taxa before starting the
	 * {@link CoLReferenceImporter}.
	 * 
	 * @param recInf
	 * @return
	 */
	public List<ESTaxon> clean(CSVRecordInfo<CoLReferenceCsvField> recInf)
	{
		this.input = recInf;
		objectID = input.get(taxonID);
		// Not much can go wrong here, so:
		stats.recordsProcessed++;
		stats.recordsAccepted++;
		stats.objectsProcessed++;
		List<ESTaxon> result = null;
		try {
			String id = getElasticsearchId(COL, objectID);
			ESTaxon taxon = loader.findInQueue(id);
			if (taxon == null) {
				taxon = index.get(TAXON.getName(), id, ESTaxon.class);
				if (taxon != null && taxon.getReferences() != null) {
					stats.objectsAccepted++;
					taxon.setReferences(null);
					result = Arrays.asList(taxon);
				}
				else {
					stats.objectsSkipped++;
				}
			}
			else {
				stats.objectsSkipped++;
			}
		}
		catch (Throwable t) {
			handleError(t);
		}
		return result;
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
