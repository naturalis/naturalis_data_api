package nl.naturalis.nba.api.model.metadata;

import java.util.Set;

import nl.naturalis.nba.api.ComparisonOperator;

/**
 * 
 * Encapsulates metadata about a field within a document.
 * 
 * @author Ayco Holleman
 *
 */
public class FieldInfo {

	private boolean indexed;
	private String type;
	private Set<ComparisonOperator> allowedOperators;

	public FieldInfo()
	{
	}

	public boolean isIndexed()
	{
		return indexed;
	}

	public void setIndexed(boolean indexed)
	{
		this.indexed = indexed;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public Set<ComparisonOperator> getAllowedOperators()
	{
		return allowedOperators;
	}

	public void setAllowedOperators(Set<ComparisonOperator> allowedOperators)
	{
		this.allowedOperators = allowedOperators;
	}

}
