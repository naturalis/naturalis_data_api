package nl.naturalis.nba.api.model;

import java.net.URI;

public class ServiceAccessPoint {

	public static enum Variant
	{
		THUMBNAIL, TRAILER, LOWER_QUALITY, MEDIUM_QUALITY, GOOD_QUALITY, BEST_QUALITY, OFFLINE
	}

	private URI accessUri;
	private String format;
	private Variant variant;


	public ServiceAccessPoint()
	{
		// Commentaar
	}


	public ServiceAccessPoint(URI uri, String format, Variant variant)
	{
		this.accessUri = uri;
		this.format = format;
		this.variant = variant;
	}

	public ServiceAccessPoint(String uri, String format, Variant variant)
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


	public Variant getVariant()
	{
		return variant;
	}


	public void setVariant(Variant variant)
	{
		this.variant = variant;
	}

}
