package nl.naturalis.nba.common.es.map;

import nl.naturalis.nba.common.es.ESDateInput;

/**
 * A {@code DateField} is a {@link SimpleField} with Elasticsearch data type
 * {@link ESDataType#DATE date}.
 * 
 * @author Ayco Holleman
 *
 */
public class DateField extends SimpleField {

	private String format = ESDateInput.ES_DATE_FORMAT;

	public DateField()
	{
		super(ESDataType.DATE);
	}

	public String getFormat()
	{
		return format;
	}

	public void setFormat(String format)
	{
		this.format = format;
	}

}
