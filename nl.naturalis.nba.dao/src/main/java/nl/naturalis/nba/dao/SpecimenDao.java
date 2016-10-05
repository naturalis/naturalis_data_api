package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;
import static org.elasticsearch.index.query.QueryBuilders.constantScoreQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.ConstantScoreQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;

import nl.naturalis.nba.api.ISpecimenAccess;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.dao.transfer.ITransferObject;
import nl.naturalis.nba.dao.transfer.SpecimenTransferObject;
import nl.naturalis.nba.dao.types.ESSpecimen;

public class SpecimenDao extends AbstractDao<Specimen, ESSpecimen> implements ISpecimenAccess {

	private static Logger logger = DaoRegistry.getInstance().getLogger(SpecimenDao.class);

	public SpecimenDao()
	{
		super(SPECIMEN);
	}

	@Override
	public boolean exists(String unitID)
	{
		if (logger.isDebugEnabled())
			logger.debug("exists(\"{}\")", unitID);
		SearchRequestBuilder request = newSearchRequest();
		TermQueryBuilder tqb = termQuery("unitID", unitID);
		ConstantScoreQueryBuilder csq = constantScoreQuery(tqb);
		request.setQuery(csq);
		request.setSize(0);
		SearchResponse response = request.execute().actionGet();
		return response.getHits().getTotalHits() != 0;
	}

	@Override
	public Specimen[] findByUnitID(String unitID)
	{
		if (logger.isDebugEnabled())
			logger.debug("findByUnitID(\"{}\")", unitID);
		SearchRequestBuilder request = newSearchRequest();
		TermQueryBuilder tqb = termQuery("unitID", unitID);
		ConstantScoreQueryBuilder csq = constantScoreQuery(tqb);
		request.setQuery(csq);
		return processSearchRequest(request);
	}

	// @Override
	public Specimen[] findByCollector(String name)
	{
		if (logger.isDebugEnabled())
			logger.debug("findByCollector(\"{}\")", name);
		TermQueryBuilder tq = termQuery("gatheringEvent.gatheringPersons.fullName", name);
		NestedQueryBuilder nq = nestedQuery("gatheringEvent.gatheringPersons", tq);
		ConstantScoreQueryBuilder csq = constantScoreQuery(nq);
		SearchRequestBuilder request = newSearchRequest();
		request.setQuery(csq);
		return processSearchRequest(request);
	}

	@Override
	public String[] getNamedCollections()
	{
		return new String[] { "Living Dinos", "Strange Plants" };
	}

	@Override
	public String[] getIdsInCollection(String collectionName)
	{
		if (logger.isDebugEnabled())
			logger.debug("getUnitIDsInCollection(\"{}\")", collectionName);
		TermQueryBuilder tq = termQuery("theme", collectionName);
		ConstantScoreQueryBuilder csq = constantScoreQuery(tq);
		SearchRequestBuilder request = newSearchRequest();
		request.setQuery(csq);
		request.setNoFields();
		if (logger.isDebugEnabled())
			logger.debug("Executing query:\n{}", request);
		SearchResponse response = request.execute().actionGet();
		if (logger.isDebugEnabled())
			logger.debug("Processing response:\n{}", response);
		SearchHit[] hits = response.getHits().getHits();
		String[] ids = new String[hits.length];
		for (int i = 0; i < hits.length; ++i) {
			ids[i] = hits[i].getId();
		}
		return ids;
	}

	@Override
	public void dwcaQuery(QuerySpec spec, ZipOutputStream out) throws InvalidQueryException
	{
//		DataSetCollectionConfiguration dsc = new DataSetCollectionConfiguration(SPECIMEN, "dynamic");
//		DwcaWriter writer = new DwcaWriter(dsc, out);
//		writer.processDynamicQuery(spec);
	}

	@Override
	public void dwcaGetDataSet(String name, ZipOutputStream out) throws InvalidQueryException
	{
	}

	@Override
	public String[] dwcaGetDataSetNames()
	{
		return null;
//		File dir = getDocumentTypeDirectory(SPECIMEN);
//		ArrayList<String> names = new ArrayList<>(32);
//		for (File subdir : getSubdirectories(dir)) {
//			if (subdir.getName().equals("dynamic"))
//				continue; // Special directory for dynamic DwCA
//			if (!containsFile(subdir, "fields.config"))
//				continue; // Can't be a data set collection dir
//			File[] dataSetDirs = getSubdirectories(subdir);
//			if (dataSetDirs.length == 0) {
//				if (containsFile(subdir, "eml.xml")) {
//					names.add(subdir.getName());
//				}
//			}
//			else {
//				for (File dataSetDir : dataSetDirs) {
//					if (containsFile(dataSetDir, "eml.xml")) {
//						names.add(dataSetDir.getName());
//					}
//				}
//			}
//		}
//		return names.toArray(new String[names.size()]);
	}

	@Override
	ITransferObject<Specimen, ESSpecimen> getTransferObject()
	{
		return new SpecimenTransferObject();
	}

	@Override
	Specimen[] createApiObjectArray(int length)
	{
		return new Specimen[length];
	}

}
