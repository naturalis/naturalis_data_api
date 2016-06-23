package nl.naturalis.nba.dao.es.map;

import java.util.LinkedHashMap;

public class DocumentField extends IndexableField {

	private LinkedHashMap<String, MultiField> fields;

	public DocumentField(ESDataType type)
	{
		this.type = type;
	}

	public LinkedHashMap<String, MultiField> getFields()
	{
		return fields;
	}

	public void addMultiField(MultiField field)
	{
		if (fields == null) {
			fields = new LinkedHashMap<>(2);
		}
		fields.put(field.name, field);
	}

	public boolean hasMultiField(MultiField mf)
	{
		return fields != null && fields.containsKey(mf.name);
	}

}
