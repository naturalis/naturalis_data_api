package nl.naturalis.nba.dao;

import static nl.naturalis.nba.common.json.JsonUtil.readField;
import static nl.naturalis.nba.dao.DaoUtil.getLogger;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.search.SearchHit;

import nl.naturalis.nba.api.KeyValuePair;
import nl.naturalis.nba.api.NbaException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.dao.util.es.SearchHitHandler;

class GetGroupsSearchHitHandler implements SearchHitHandler {

	private static final Logger logger = getLogger(GetGroupsSearchHitHandler.class);

	private Path path;
	private int from;
	private int size;

	private int groupCounter;
	private int docCounter;

	private List<KeyValuePair<Object, Integer>> result;

	GetGroupsSearchHitHandler(String keyField, int from, int size)
	{
		this.path = new Path(keyField);
		this.from = from;
		this.size = size;
		result = new ArrayList<>(size);
	}

	@Override
	public boolean handle(SearchHit hit) throws NbaException
	{
		if (logger.isDebugEnabled() && ++docCounter % 100000 == 0) {
			logger.debug("Documents processed: {}", docCounter);
		}
		List<KeyValuePair<Object, Integer>> r;
		if ((r = result).size() == size && size != 0) {
			return false;
		}
		KeyValuePair<Object, Integer> last = r.size() == 0 ? null : r.get(r.size() - 1);
		Object val = readField(hit.getSource(), path);
		if (last == null && groupCounter++ >= from) {
			r.add(new KeyValuePair<>(val, 1));
		}
		else if (last.getKey().equals(val)) {
			last.setValue(last.getValue() + 1);
		}
		else if (groupCounter++ >= from) {
			last = new KeyValuePair<>(val, 1);
			r.add(last);
		}
		return true;
	}

	List<KeyValuePair<Object, Integer>> getGroups()
	{
		return result;
	}

}
