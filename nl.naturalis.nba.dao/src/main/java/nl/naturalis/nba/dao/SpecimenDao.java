package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;
import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;
import static nl.naturalis.nba.dao.util.es.ESUtil.newSearchRequest;
import static nl.naturalis.nba.utils.debug.DebugUtil.printCall;
import static org.elasticsearch.index.query.QueryBuilders.constantScoreQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.io.File;
import java.io.FileFilter;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.ConstantScoreQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import nl.naturalis.nba.api.GroupByScientificNameQueryResult;
import nl.naturalis.nba.api.GroupByScientificNameQuerySpec;
import nl.naturalis.nba.api.ISpecimenAccess;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.NoSuchDataSetException;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.exception.DaoException;
import nl.naturalis.nba.dao.format.DataSetConfigurationException;
import nl.naturalis.nba.dao.format.DataSetWriteException;
import nl.naturalis.nba.dao.format.dwca.DwcaConfig;
import nl.naturalis.nba.dao.format.dwca.DwcaDataSetType;
import nl.naturalis.nba.dao.format.dwca.DwcaUtil;
import nl.naturalis.nba.dao.format.dwca.IDwcaWriter;
import nl.naturalis.nba.dao.util.GroupSpecimensByScientificNameHelper;

public class SpecimenDao extends NbaDao<Specimen> implements ISpecimenAccess {

	private static Logger logger = getLogger(SpecimenDao.class);

	public SpecimenDao()
	{
		super(SPECIMEN);
	}

	@Override
	public boolean exists(String unitID)
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("exists", unitID));
		}
		SearchRequest request = newSearchRequest(SPECIMEN);
		TermQueryBuilder tqb = termQuery("unitID", unitID);
		ConstantScoreQueryBuilder csq = constantScoreQuery(tqb);
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(csq);
		sourceBuilder.size(0);
		request.source(sourceBuilder);
		SearchResponse response = executeSearchRequest(request);
		return response.getHits().getTotalHits().value != 0;
	}

	@Override
	public Specimen[] findByUnitID(String unitID)
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("findByUnitID", unitID));
		}
		SearchRequest request = newSearchRequest(SPECIMEN);
    TermQueryBuilder tqb = termQuery("unitID", unitID);
    ConstantScoreQueryBuilder csq = constantScoreQuery(tqb);
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.query(csq);
    request.source(sourceBuilder);
    return processSearchRequest(request);
	}

	private static String[] namedCollections;

	@Override
	public String[] getNamedCollections()
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("getNamedCollections"));
		}
		if (namedCollections == null) {
			try {
				Set<String> themes = getDistinctValues("theme", null).keySet();
				namedCollections = themes.toArray(new String[themes.size()]);
			}
			catch (InvalidQueryException e) {
				assert (false);
				return null;
			}
		}
		return namedCollections;
	}

	@Override
	public String[] getIdsInCollection(String collectionName)
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("getIdsInCollection", collectionName));
		}
    TermQueryBuilder tq = termQuery("theme", collectionName);
    ConstantScoreQueryBuilder csq = constantScoreQuery(tq);
    SearchRequest request = newSearchRequest(SPECIMEN);
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.query(csq);
    sourceBuilder.fetchSource(false);
    request.source(sourceBuilder);
    SearchResponse response = executeSearchRequest(request);
    SearchHit[] hits = response.getHits().getHits();
    String[] ids = new String[hits.length];
    for (int i = 0; i < hits.length; ++i) {
      ids[i] = hits[i].getId();
    }
    return ids;
	}

	@Override
	public void dwcaQuery(QuerySpec querySpec, OutputStream out) throws InvalidQueryException
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("dwcaQuery", querySpec, out));
		}
		try {
			DwcaConfig config = DwcaConfig.getDynamicDwcaConfig(DwcaDataSetType.SPECIMEN);
			IDwcaWriter writer = config.getWriter(out);
			writer.writeDwcaForQuery(querySpec);
		}
		catch (DataSetConfigurationException | DataSetWriteException e) {
			throw new DaoException(e);
		}
	}

	@Override
	public void dwcaGetDataSet(String name, OutputStream out) throws NoSuchDataSetException
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("dwcaGetDataSet", name, out));
		}
		try {
			DwcaConfig config = new DwcaConfig(name, DwcaDataSetType.SPECIMEN);
			IDwcaWriter writer = config.getWriter(out);
			writer.writeDwcaForDataSet();
		}
		catch (DataSetConfigurationException | DataSetWriteException e) {
			throw new DaoException(e);
		}
	}

	@Override
	public String[] dwcaGetDataSetNames()
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("dwcaGetDataSetNames"));
		}
		File dir = DwcaUtil.getDwcaConfigurationDirectory(DwcaDataSetType.SPECIMEN);
		File[] files = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				if (f.getName().startsWith("dynamic")) {
					return false;
				}
				if (f.isFile() && f.getName().endsWith(DwcaConfig.CONF_FILE_EXTENSION)) {
					return true;
				}
				return false;
			}
		});
		String[] names = new String[files.length];
		for (int i = 0; i < files.length; i++) {
			String name = files[i].getName();
			names[i] = name.substring(0, name.indexOf('.'));
		}
		Arrays.sort(names);
		return names;
	}

	@Override
	public GroupByScientificNameQueryResult groupByScientificName(
			GroupByScientificNameQuerySpec sngQuery) throws InvalidQueryException
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("groupByScientificName", sngQuery));
		}
		return GroupSpecimensByScientificNameHelper.groupByScientificName(sngQuery);
	}

	@Override
	Specimen[] createDocumentObjectArray(int length)
	{
		return new Specimen[length];
	}

}