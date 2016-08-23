package nl.naturalis.nba.dao.es.format;

import java.util.Map;

public class DocumentNode {

	private Map<String, Object> document;
	private DocumentNode parent;

	DocumentNode(Map<String, Object> document, DocumentNode parent)
	{
		this.document = document;
		this.parent = parent;
	}

	public Map<String, Object> getRoot()
	{
		if (parent == null)
			return document;
		DocumentNode dn;
		for (dn = parent; dn.parent != null; dn = dn.parent)
			;
		return dn.document;
	}

	public Map<String, Object> getEntity()
	{
		return document;
	}


}
