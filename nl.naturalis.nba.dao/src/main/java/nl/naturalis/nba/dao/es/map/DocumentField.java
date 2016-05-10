package nl.naturalis.nba.dao.es.map;

import java.util.LinkedHashMap;

public class DocumentField extends ESScalar {

	private LinkedHashMap<String, ESScalar> fields;
	
	public DocumentField(ESDataType esDataType)
	{
		super(esDataType);
	}

	public LinkedHashMap<String, ESScalar> getFields()
	{
		return fields;
	}

	public void addMultiField(String name, ESScalar field)
	{
		if (fields == null) {
			fields = new LinkedHashMap<>(2);
		}
		fields.put(name, field);
	}

}
