package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.utils.debug.DebugUtil.printCall;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.ComparisonOperator;
import nl.naturalis.nba.api.INbaMetaData;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.common.es.map.ComplexField;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.NoSuchFieldException;
import nl.naturalis.nba.common.es.map.SimpleField;
import nl.naturalis.nba.dao.exception.DaoException;
import nl.naturalis.nba.dao.translate.OperatorValidator;

abstract class MetaDataDao<T extends IDocumentObject> implements INbaMetaData<T> {

	private static final Logger logger = getLogger(MetaDataDao.class);

	private final DocumentType<T> dt;

	MetaDataDao(DocumentType<T> dt)
	{
		this.dt = dt;
	}

	@Override
	public Map<String, Set<ComparisonOperator>> getAllowedOperators(String... fields)
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("getAllowedOperators", fields));
		}
		if (fields == null || (fields.length == 1 && fields[0].equals("*"))) {
			fields = getPaths(true);
		}
		int mapSize = ((int) (fields.length / .75) + 1);
		Map<String, Set<ComparisonOperator>> result = new LinkedHashMap<>(mapSize);
		for (String field : fields) {
			EnumSet<ComparisonOperator> allowed = EnumSet.noneOf(ComparisonOperator.class);
			for (ComparisonOperator op : ComparisonOperator.values()) {
				try {
					if (OperatorValidator.isOperatorAllowed(field, op, dt)) {
						allowed.add(op);
					}
				}
				catch (NoSuchFieldException e) {
					throw new DaoException(e);
				}
				result.put(field, allowed);
			}
		}
		return result;
	}

	@Override
	public boolean isOperatorAllowed(String field, ComparisonOperator operator)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("isOperatorAllowed(\"{}\",\"{}\")", field, operator);
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
		List<String> paths = new ArrayList<>(100);
		LinkedHashMap<String, ESField> properties = dt.getMapping().getProperties();
		for (Map.Entry<String, ESField> property : properties.entrySet()) {
			addPath(paths, null, property.getKey(), property.getValue());
		}
		String[] result = paths.toArray(new String[paths.size()]);
		if (sorted) {
			Arrays.sort(result);
		}
		return result;
	}

	private static void addPath(List<String> paths, String parent, String child, ESField field)
	{
		String path = parent == null ? child : parent + '.' + child;
		if (field instanceof SimpleField) {
			paths.add(path);
		}
		else {
			ComplexField cf = (ComplexField) field;
			LinkedHashMap<String, ESField> fields = cf.getProperties();
			for (Entry<String, ESField> e : fields.entrySet()) {
				addPath(paths, path, e.getKey(), e.getValue());
			}
		}
	}

}
