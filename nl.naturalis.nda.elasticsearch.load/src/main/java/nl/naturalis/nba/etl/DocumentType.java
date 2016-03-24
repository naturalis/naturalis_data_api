package nl.naturalis.nba.etl;

/**
 * Enumerates the three ElasticSearch document types within the NBA index.
 * 
 * @author Ayco Holleman
 *
 */
public enum DocumentType
{
	SPECIMEN, TAXON, MULTI_MEDIA_OBJECT;

	/**
	 * Returns the {@code DocumentType} corresponding to the specified string.
	 * Somewhat generous in ignoring case, and allowing both plural and singular
	 * versions. MEDIA, MULTIMEDIA and MULTI_MEDIA all map to
	 * {@link #MULTI_MEDIA_OBJECT}.
	 * 
	 * @param name
	 * @return
	 */
	public static DocumentType forName(String name)
	{
		if (name == null)
			return null;
		name = name.trim().toUpperCase();
		if (name.equals("SPECIMENS"))
			return SPECIMEN;
		if (name.equals("TAXA"))
			return TAXON;
		if (name.equals("MEDIA") || name.equals("MULTIMEDIA") || name.equals("MULTI_MEDIA"))
			return MULTI_MEDIA_OBJECT;
		return DocumentType.valueOf(name);
	}
}
