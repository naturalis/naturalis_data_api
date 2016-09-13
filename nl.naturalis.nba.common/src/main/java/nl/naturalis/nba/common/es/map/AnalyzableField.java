package nl.naturalis.nba.common.es.map;

import java.util.LinkedHashMap;

/**
 * An {@code IndexableField} is a {@link ESField} that lets you specify if and
 * how it is indexed. A {@code Field} can be either a top-level
 * {@link PrimitiveField} or a virtual "{@link MultiField multi-field}"
 * underneath it.
 * 
 * @author Ayco Holleman
 *
 */
public class AnalyzableField extends PrimitiveField implements IAnalyzable {

	protected String analyzer;
	private LinkedHashMap<String, MultiField> fields;

	public AnalyzableField(ESDataType type)
	{
		super(type);
	}

	public String getAnalyzer()
	{
		return analyzer;
	}

	public void setAnalyzer(String analyzer)
	{
		this.analyzer = analyzer;
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
