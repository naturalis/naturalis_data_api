package nl.naturalis.nba.common.es.map;

/**
 * Models an Elasticsearch date field definition. For this type field you can
 * specify one or more date formats that Elasticsearch should use to parse date
 * string (using || to separate the date formats). By default, however, only one
 * format is allowed: "yyyy-MM-dd'T'HH:mm:ss.SSSZ". It is left to the NBA to
 * parse date strings and report parse errors before queries are even sent to
 * Elasticsearch.
 * 
 * @author Ayco Holleman
 *
 */
public class DateField extends SimpleField {

	public static final String DEFAULT_DATE_FORMATS = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

	private String format = DEFAULT_DATE_FORMATS;

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
