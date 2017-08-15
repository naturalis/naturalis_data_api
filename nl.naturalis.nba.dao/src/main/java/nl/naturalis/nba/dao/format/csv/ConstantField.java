package nl.naturalis.nba.dao.format.csv;

import java.net.URI;

import nl.naturalis.nba.dao.format.AbstractField;
import nl.naturalis.nba.dao.format.EntityObject;

/**
 * A {@code ConstantField} writes a constant value to a CSV field. In practice
 * this value is retrieved from a &lt;constant&gt; element within the XML
 * configuration file for a dataset.
 * 
 * @author Ayco Holleman
 *
 */
class ConstantField extends AbstractField {

	private String value;

	ConstantField(String name, URI term, String value)
	{
		super(name, term);
		this.value = value;
	}

	@Override
	public String getValue(EntityObject entity)
	{
		return value;
	}

}
