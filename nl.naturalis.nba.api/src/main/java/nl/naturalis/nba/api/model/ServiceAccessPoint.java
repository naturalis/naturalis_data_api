package nl.naturalis.nba.api.model;

import java.net.URI;

import nl.naturalis.nba.api.annotations.Analyzers;
import nl.naturalis.nba.api.annotations.NotIndexed;

public class ServiceAccessPoint implements INbaModelObject {

	@NotIndexed
	private URI accessUri;
	@Analyzers({})
	private String format;
	@Analyzers({})
	private String variant;

	public ServiceAccessPoint()
	{
		// Commentaar
	}

	public ServiceAccessPoint(URI uri, String format, String variant)
	{
		this.accessUri = uri;
		this.format = format;
		this.variant = variant;
	}

	public ServiceAccessPoint(String uri, String format, String variant)
	{
		this.accessUri = URI.create(uri);
		this.format = format;
		this.variant = variant;
	}

	public URI getAccessUri()
	{
		return accessUri;
	}

	public void setAccessUri(URI accessUri)
	{
		this.accessUri = accessUri;
	}

	public String getFormat()
	{
		return format;
	}

	public void setFormat(String format)
	{
		this.format = format;
	}

	public String getVariant()
	{
		return variant;
	}

	public void setVariant(String variant)
	{
		this.variant = variant;
	}

}
