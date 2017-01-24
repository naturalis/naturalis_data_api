package nl.naturalis.nba.common.es.map;

import java.util.LinkedHashMap;

import nl.naturalis.nba.api.ComparisonOperator;
import nl.naturalis.nba.api.annotations.Analyzer;

/**
 * An {@code StringField} is a {@link SimpleField} with data type
 * {@link ESDataType#KEYWORD}. This type of field can be indexed in multiple
 * ways through {@link Analyzer analyzers}. The field itself always remains
 * unanalyzed (meaning you can always create {@link ComparisonOperator#EQUALS}
 * queries for it. Other indexes are specified through a virtual
 * {@link MultiField multi-field} underneath it.
 * 
 * @author Ayco Holleman
 *
 */
public class KeywordField extends SimpleField {

	protected String analyzer;
	private LinkedHashMap<String, MultiField> fields;

	public KeywordField()
	{
		super(ESDataType.KEYWORD);
	}

	public LinkedHashMap<String, MultiField> getMultiFields()
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
