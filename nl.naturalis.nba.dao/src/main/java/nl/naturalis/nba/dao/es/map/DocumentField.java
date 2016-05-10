package nl.naturalis.nba.dao.es.map;

import java.util.LinkedHashMap;

public class DocumentField extends IndexableField {

	private LinkedHashMap<String, IndexableField> fields;
	
	public DocumentField(ESDataType esDataType)
	{
		super(esDataType);
	}

	public LinkedHashMap<String, IndexableField> getFields()
	{
		return fields;
	}

	public void addMultiField(String name, IndexableField field)
	{
		if (fields == null) {
			fields = new LinkedHashMap<>(2);
		}
		fields.put(name, field);
	}

}
