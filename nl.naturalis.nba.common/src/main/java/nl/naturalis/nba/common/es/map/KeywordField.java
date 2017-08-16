package nl.naturalis.nba.common.es.map;

import java.util.LinkedHashMap;

import nl.naturalis.nba.api.ComparisonOperator;
import nl.naturalis.nba.api.annotations.Analyzers;
import nl.naturalis.nba.api.model.Specimen;

/**
 * A {@code KeywordField} is a {@link SimpleField} with Elasticsearch data type
 * {@link ESDataType#KEYWORD keyword}. String fields in Java model classes like
 * {@link Specimen} will always be mapped to Elasticsearch document fields of
 * type "keyword". Therefore, you can <i>always</i> use operator
 * {@link ComparisonOperator#EQUALS EQUALS} to query a string field. In
 * addition, a String field in a Java model class can be decorated with the
 * {@link Analyzers} annotation, which causes the corresponding Elasticsearch
 * field to be analyzed according to the value of the {@link Analyzers}
 * annotation.
 * 
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
