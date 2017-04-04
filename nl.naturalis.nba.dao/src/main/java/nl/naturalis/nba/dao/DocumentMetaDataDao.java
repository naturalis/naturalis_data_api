package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.utils.debug.DebugUtil.printCall;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.ComparisonOperator;
import nl.naturalis.nba.api.IDocumentMetaData;
import nl.naturalis.nba.api.NoSuchFieldException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.metadata.FieldInfo;
import nl.naturalis.nba.api.model.metadata.NbaSetting;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.SimpleField;
import nl.naturalis.nba.dao.exception.DaoException;
import nl.naturalis.nba.dao.translate.OperatorValidator;

abstract class DocumentMetaDataDao<T extends IDocumentObject> implements IDocumentMetaData<T> {

	private static final Logger logger = getLogger(DocumentMetaDataDao.class);

	private final DocumentType<T> dt;

	DocumentMetaDataDao(DocumentType<T> dt)
	{
		this.dt = dt;
	}

	@Override
	public Map<String, FieldInfo> getFieldInfo(String... fields) throws NoSuchFieldException
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("getAllowedOperators", fields));
		}
		if (fields == null || (fields.length == 1 && fields[0].equals("*"))) {
			fields = getPaths(true);
		}
		int mapSize = ((int) (fields.length / .75) + 1);
		Map<String, FieldInfo> result = new LinkedHashMap<>(mapSize);
		MappingInfo<T> mappingInfo = new MappingInfo<>(dt.getMapping());
		for (String field : fields) {
			Path path = new Path(field);
			ESField esField = mappingInfo.getField(path);
			if (!(esField instanceof SimpleField)) {
				throw new NoSuchFieldException(path);
			}
			SimpleField sf = (SimpleField) esField;
			FieldInfo info = new FieldInfo();
			info.setIndexed(sf.getIndex() != Boolean.FALSE);
			info.setType(esField.getType().toString());
			EnumSet<ComparisonOperator> allowed = EnumSet.noneOf(ComparisonOperator.class);
			for (ComparisonOperator op : ComparisonOperator.values()) {
				if (OperatorValidator.isOperatorAllowed(sf, op)) {
					allowed.add(op);
				}
			}
			info.setAllowedOperators(allowed);
			result.put(field, info);
		}
		return result;
	}

	@Override
	public boolean isOperatorAllowed(String field, ComparisonOperator operator)
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("isOperatorAllowed", field, operator));
		}
		try {
			return OperatorValidator.isOperatorAllowed(field, operator, dt);
		}
		catch (NoSuchFieldException e) {
			throw new DaoException(e);
		}
	}

	@Override
	public String[] getPaths(boolean sorted)
	{
		return new MappingInfo<>(dt.getMapping()).getPathStrings(sorted);
	}

	@Override
	public Map<NbaSetting, Object> getSettings()
	{
		return null;
	}

}
