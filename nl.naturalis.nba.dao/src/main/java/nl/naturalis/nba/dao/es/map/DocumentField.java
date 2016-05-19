package nl.naturalis.nba.dao.es.map;

import java.util.LinkedHashMap;

public class DocumentField extends IndexableField {

	private LinkedHashMap<String, MultiField> fields;

	public DocumentField(ESDataType esDataType)
	{
		super(esDataType);
	}

	public LinkedHashMap<String, MultiField> getFields()
	{
		return fields;
	}

	public void addMultiField(String name, MultiField field)
	{
		if (fields == null) {
			fields = new LinkedHashMap<>(2);
		}
		fields.put(name, field);
	}

	public boolean hasMultiField(String name)
	{
		return fields != null && fields.containsKey(name);
	}

}
