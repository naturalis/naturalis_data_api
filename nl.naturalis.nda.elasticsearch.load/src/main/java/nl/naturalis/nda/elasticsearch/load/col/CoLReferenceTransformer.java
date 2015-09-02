package nl.naturalis.nda.elasticsearch.load.col;

import static nl.naturalis.nda.elasticsearch.load.CSVImportUtil.val;
import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_TAXON;
import static nl.naturalis.nda.elasticsearch.load.col.CoLReferenceCsvField.creator;
import static nl.naturalis.nda.elasticsearch.load.col.CoLReferenceCsvField.date;
import static nl.naturalis.nda.elasticsearch.load.col.CoLReferenceCsvField.description;
import static nl.naturalis.nda.elasticsearch.load.col.CoLReferenceCsvField.taxonID;
import static nl.naturalis.nda.elasticsearch.load.col.CoLReferenceCsvField.title;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nl.naturalis.nda.domain.Person;
import nl.naturalis.nda.domain.Reference;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.load.AbstractCSVTransformer;
import nl.naturalis.nda.elasticsearch.load.CSVRecordInfo;
import nl.naturalis.nda.elasticsearch.load.CSVTransformer;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;
import nl.naturalis.nda.elasticsearch.load.LoadConstants;
import nl.naturalis.nda.elasticsearch.load.Registry;
import nl.naturalis.nda.elasticsearch.load.TransferUtil;

import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;

/**
 * A subclass of {@link CSVTransformer} that transforms CSV records into
 * {@link ESTaxon} objects.
 * 
 * @author Ayco Holleman
 *
 */
class CoLReferenceTransformer extends AbstractCSVTransformer<ESTaxon> {

	static Logger logger = Registry.getInstance().getLogger(CoLReferenceTransformer.class);

	private final IndexNative index;
	private CoLTaxonLoader loader;

	CoLReferenceTransformer(ETLStatistics stats)
	{
		super(stats);
		this.index = Registry.getInstance().getNbaIndexManager();
	}

	void setLoader(CoLTaxonLoader loader)
	{
		this.loader = loader;
	}

	@Override
	public List<ESTaxon> transform(CSVRecordInfo recInf)
	{
		this.recInf = recInf;
		objectID = val(recInf.getRecord(), taxonID);
		// Not much can go wrong here, so:
		stats.recordsProcessed++;
		stats.recordsAccepted++;
		stats.objectsProcessed++;
		List<ESTaxon> result = null;
		try {
			String elasticID = LoadConstants.ES_ID_PREFIX_COL + objectID;
			boolean isNew = false;
			ESTaxon taxon = loader.findInQueue(elasticID);
			if (taxon == null) {
				isNew = true;
				taxon = index.get(LUCENE_TYPE_TAXON, elasticID, ESTaxon.class);
			}
			if (taxon == null) {
				stats.objectsRejected++;
				if (!suppressErrors) {
					error("Orphan reference: " + val(recInf.getRecord(), title));
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
					 * else the reference is added to a taxon that's already
					 * queued, so we're fine
					 */
				}
				else {
					stats.objectsRejected++;
					if (!suppressErrors) {
						error("Reference already exists: " + ref);
					}
				}
			}
		}
		catch (Throwable t) {
			stats.objectsRejected++;
			if (!suppressErrors)
				error(t.toString());
		}
		return result;
	}

	private Reference createReference()
	{
		CSVRecord record = recInf.getRecord();
		Reference ref = new Reference();
		ref.setTitleCitation(val(record, title));
		ref.setCitationDetail(val(record, description));
		String s;
		if ((s = val(record, date)) != null) {
			Date pubDate = TransferUtil.parseDate(s);
			ref.setPublicationDate(pubDate);
		}
		if ((s = val(record, creator)) != null) {
			ref.setAuthor(new Person(s));
		}
		return ref;
	}
}
