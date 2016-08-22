package nl.naturalis.nba.dao.es.format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import nl.naturalis.nba.common.json.JsonUtil;

public class DocumentFlattener {

	public static final String PARENT_DOCUMENT_FIELD_NAME = "@";

	private String[] np;
	private int nr;

	public DocumentFlattener(String[] nestedPath, int numRecords)
	{
		if (nestedPath != null && nestedPath.length != 0) {
			this.np = nestedPath;
			this.nr = numRecords;
		}
	}

	public List<Map<String, Object>> flatten(Map<String, Object> document)
	{
		if (np == null) {
			return Collections.singletonList(document);
		}
		List<Map<String, Object>> records = new ArrayList<>(nr);
		flatten(np, document, records);
		return records;
	}

	@SuppressWarnings("unchecked")
	private static void flatten(String[] path, Map<String, Object> in,
			List<Map<String, Object>> out)
	{
		Object obj = JsonUtil.readField(in, new String[] { path[0] });
		if (obj == null || obj == JsonUtil.MISSING_VALUE)
			return;
		attachParentToChild(in, obj);
		if (path.length == 1) {
			if (obj instanceof List)
				out.addAll((List<Map<String, Object>>) obj);
			else
				out.add((Map<String, Object>) obj);
		}
		else {
			String[] nestedPath = new String[path.length - 1];
			System.arraycopy(path, 1, nestedPath, 0, path.length - 1);
			if (obj instanceof List) {
				List<Map<String, Object>> list = (List<Map<String, Object>>) obj;
				for (Map<String, Object> nestedObject : list) {
					flatten(nestedPath, nestedObject, out);
				}
			}
			else {
				Map<String, Object> nestedObject = (Map<String, Object>) obj;
				flatten(nestedPath, nestedObject, out);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void attachParentToChild(Map<String, Object> in, Object obj)
	{
		if (obj instanceof List) {
			List<Map<String, Object>> list = (List<Map<String, Object>>) obj;
			for (Map<String, Object> e : list) {
				e.put(PARENT_DOCUMENT_FIELD_NAME, in);
			}
		}
		else {
			((Map<String, Object>) obj).put(PARENT_DOCUMENT_FIELD_NAME, in);
		}
	}

}
