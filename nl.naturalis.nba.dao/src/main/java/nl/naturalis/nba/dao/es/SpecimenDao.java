package nl.naturalis.nba.dao.es;

import static nl.naturalis.nba.common.json.JsonUtil.toJson;
import static nl.naturalis.nba.dao.es.DocumentType.SPECIMEN;
import static nl.naturalis.nba.dao.es.format.dwca.DwcaUtil.findDataSetCollection;
import static nl.naturalis.nba.dao.es.format.dwca.DwcaUtil.getDocumentTypeDirectory;
import static org.elasticsearch.index.query.QueryBuilders.constantScoreQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static nl.naturalis.nba.dao.es.DaoUtil.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.Logger;
import static org.domainobject.util.FileUtil.*;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequestBuilder;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.index.query.ConstantScoreQueryBuilder;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.api.ISpecimenAccess;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.es.format.DataSetCollection;
import nl.naturalis.nba.dao.es.format.dwca.DwcaWriter;
import nl.naturalis.nba.dao.es.query.QuerySpecTranslator;
import nl.naturalis.nba.dao.es.transfer.SpecimenTransfer;
import nl.naturalis.nba.dao.es.types.ESSpecimen;

public class SpecimenDao implements ISpecimenAccess {

	private static final Logger logger;

	static {
		logger = DaoRegistry.getInstance().getLogger(SpecimenDao.class);
	}

	public SpecimenDao()
	{
	}

	@Override
	public Specimen find(String id)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("find(\"{}\")", id);
		}
		GetRequestBuilder request = client().prepareGet();
		String index = SPECIMEN.getIndexInfo().getName();
		String type = SPECIMEN.getName();
		request.setIndex(index);
		request.setType(type);
		request.setId(id);
		GetResponse response = request.execute().actionGet();
		if (!response.isExists()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Specimen with id \"{}\" not found", id);
			}
			return null;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Response:\n{}", response.getSourceAsString());
		}
		Map<String, Object> data = response.getSource();
		return createSpecimen(id, data);
	}

	@Override
	public Specimen[] find(String[] ids)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("find({})", toJson(ids));
		}
		String type = SPECIMEN.getName();
		SearchRequestBuilder request = newSearchRequest(SPECIMEN);
		IdsQueryBuilder query = QueryBuilders.idsQuery(type);
		query.ids(ids);
		request.setQuery(query);
		return processSearchRequest(request);
	}

	@Override
	public boolean exists(String unitID)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("exists(\"{}\")", unitID);
		}
		SearchRequestBuilder request = newSearchRequest(SPECIMEN);
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
		if (logger.isDebugEnabled()) {
			logger.debug("findByUnitID(\"{}\")", unitID);
		}
		SearchRequestBuilder request = newSearchRequest(SPECIMEN);
		TermQueryBuilder tqb = termQuery("unitID", unitID);
		ConstantScoreQueryBuilder csq = constantScoreQuery(tqb);
		request.setQuery(csq);
		return processSearchRequest(request);
	}

	// @Override
	@SuppressWarnings("static-method")
	public Specimen[] findByCollector(String name)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("findByCollector(\"{}\")", name);
		}
		TermQueryBuilder tq = termQuery("gatheringEvent.gatheringPersons.fullName", name);
		NestedQueryBuilder nq = nestedQuery("gatheringEvent.gatheringPersons", tq);
		ConstantScoreQueryBuilder csq = constantScoreQuery(nq);
		SearchRequestBuilder request = newSearchRequest(SPECIMEN);
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
		if (logger.isDebugEnabled()) {
			logger.debug("getUnitIDsInCollection(\"{}\")", collectionName);
		}
		TermQueryBuilder tq = termQuery("theme", collectionName);
		ConstantScoreQueryBuilder csq = constantScoreQuery(tq);
		SearchRequestBuilder request = newSearchRequest(SPECIMEN);
		request.setQuery(csq);
		request.setNoFields();
		if (logger.isDebugEnabled()) {
			logger.debug("Executing query:\n{}", request);
		}
		SearchResponse response = request.execute().actionGet();
		if (logger.isDebugEnabled()) {
			logger.debug("Processing response:\n{}", response);
		}
		SearchHit[] hits = response.getHits().getHits();
		String[] ids = new String[hits.length];
		for (int i = 0; i < hits.length; ++i) {
			ids[i] = hits[i].getId();
		}
		return ids;
	}

	@Override
	public Specimen[] query(QuerySpec spec) throws InvalidQueryException
	{
		QuerySpecTranslator qst = new QuerySpecTranslator(spec, SPECIMEN);
		return processSearchRequest(qst.translate());
	}

	@Override
	public void dwcaQuery(QuerySpec spec, ZipOutputStream out) throws InvalidQueryException
	{
		DataSetCollection dsc = new DataSetCollection(SPECIMEN, "dynamic");
		DwcaWriter writer = new DwcaWriter(dsc, out);
		writer.processDynamicQuery(spec);
	}

	@Override
	public void dwcaGetDataSet(String name, ZipOutputStream out) throws InvalidQueryException
	{
		DataSetCollection dsc = findDataSetCollection(SPECIMEN, name);
		DwcaWriter writer = new DwcaWriter(dsc, out);
		writer.processPredefinedQuery(name);
	}

	@Override
	public String[] dwcaGetDataSetNames()
	{
		File dir = getDocumentTypeDirectory(SPECIMEN);
		ArrayList<String> names = new ArrayList<>(32);
		for (File subdir : getSubdirectories(dir)) {
			if (subdir.getName().equals("dynamic"))
				continue; // Special directory for dynamic DwCA
			if (!containsFile(subdir, "fields.config"))
				continue; // Can't be a data set collection dir
			File[] dataSetDirs = getSubdirectories(subdir);
			if (dataSetDirs.length == 0) {
				if (containsFile(subdir, "eml.xml")) {
					names.add(subdir.getName());
				}
			}
			else {
				for (File dataSetDir : dataSetDirs) {
					if (containsFile(dataSetDir, "eml.xml")) {
						names.add(dataSetDir.getName());
					}
				}
			}
		}
		return names.toArray(new String[names.size()]);
	}

	@Override
	public String save(Specimen specimen, boolean immediate)
	{
		String id = specimen.getId();
		String index = SPECIMEN.getIndexInfo().getName();
		String type = SPECIMEN.getName();
		if (logger.isDebugEnabled()) {
			String pattern = "New save request (index={};type={};id={})";
			logger.debug(pattern, index, type, id);
		}
		IndexRequestBuilder request = client().prepareIndex(index, type, id);
		ESSpecimen esSpecimen = SpecimenTransfer.save(specimen);
		byte[] source = JsonUtil.serialize(esSpecimen);
		request.setSource(source);
		IndexResponse response = request.execute().actionGet();
		if (immediate) {
			IndicesAdminClient iac = client().admin().indices();
			RefreshRequestBuilder rrb = iac.prepareRefresh(index);
			rrb.execute().actionGet();
		}
		specimen.setId(response.getId());
		return response.getId();
	}

	public boolean delete(String id, boolean immediate)
	{
		String index = SPECIMEN.getIndexInfo().getName();
		String type = SPECIMEN.getName();
		DeleteRequestBuilder request = client().prepareDelete(index, type, id);
		DeleteResponse response = request.execute().actionGet();
		return response.isFound();
	}

	private static Specimen[] processSearchRequest(SearchRequestBuilder request)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Executing query:\n{}", request);
		}
		SearchResponse response = request.execute().actionGet();
		SearchHit[] hits = response.getHits().getHits();
		if (logger.isDebugEnabled()) {
			logger.debug("Processing response:\n{}", response);
		}
		Specimen[] specimens = new Specimen[hits.length];
		for (int i = 0; i < hits.length; ++i) {
			String id = hits[i].getId();
			Map<String, Object> data = hits[i].getSource();
			Specimen specimen = createSpecimen(id, data);
			specimens[i] = specimen;
		}
		return specimens;
	}

	private static Specimen createSpecimen(String id, Map<String, Object> data)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Creating Specimen instance with id {}", id);
		}
		ObjectMapper om = SPECIMEN.getObjectMapper();
		ESSpecimen esSpecimen = om.convertValue(data, ESSpecimen.class);
		return SpecimenTransfer.load(esSpecimen, id);
	}

	private static Client client()
	{
		return ESClientManager.getInstance().getClient();
	}

}
