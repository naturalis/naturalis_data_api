package nl.naturalis.nda.elasticsearch.load.crs;

import java.util.HashMap;
import java.util.HashSet;

import nl.naturalis.nda.elasticsearch.load.HarvestException;

import org.domainobject.util.FileUtil;
import org.domainobject.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CRSMap {

	static final String NOT_MAPPED = "@NOT_MAPPED@";

	private static final Logger logger = LoggerFactory.getLogger(CRSMap.class);

	private final HashMap<String, String> map;
	private final HashSet<String> determinationElements;


	public CRSMap()
	{
		map = loadMap();
		determinationElements = loadDeterminationElements();
	}


	public String get(String tag)
	{
		String field = map.get(tag);
		return field == null ? NOT_MAPPED : field;
	}


	public boolean isIncludedInMap(String tag)
	{
		return map.containsKey(tag);
	}


	public boolean isDeterminationElement(String tag)
	{
		return determinationElements.contains(tag);
	}


	private HashMap<String, String> loadMap()
	{
		logger.info("Loading CRS-to-NDA map (crs-mapping.txt)");
		String contents = FileUtil.getContents(getClass().getResource("/config/crs/crs-mapping.txt"));
		contents = StringUtil.trim(contents, " \t\r\n,");
		String[] elements = contents.split(",");
		if ((elements.length % 2) != 0) {
			throw new HarvestException("Not an even number of identifiers in crs-mapping.txt");
		}
		HashMap<String, String> map = new HashMap<String, String>((elements.length + 8) / 2, 1.0F);
		for (int i = 0; i < elements.length; i += 2) {
			String key = elements[i].trim();
			key = key.equals(NOT_MAPPED) ? NOT_MAPPED : key;
			String val = elements[i + 1].trim();
			val = val.equals(NOT_MAPPED) ? NOT_MAPPED : val;
			logger.debug(key + " -> " + val);
			map.put(key, val);
		}
		return map;
	}


	private HashSet<String> loadDeterminationElements()
	{
		logger.info("Loading CRS determination elements (crs-determination-elements.txt)");
		String contents = FileUtil.getContents(getClass().getResource("/config/crs/crs-determination-elements.txt"));
		contents = StringUtil.trim(contents, " \t\r\n,");
		String[] elements = contents.split(",");
		HashSet<String> set = new HashSet<String>(elements.length);
		for (int i = 0; i < elements.length; ++i) {
			String element = elements[i].trim();
			element = element.equals(NOT_MAPPED) ? NOT_MAPPED : element;
			logger.debug(element);
			set.add(element);
		}
		return set;
	}
}
