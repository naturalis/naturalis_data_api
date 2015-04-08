package nl.naturalis.nda.elasticsearch.load;

public enum DocumentType
{
	SPECIMEN, TAXON, MULTI_MEDIA_OBJECT;

	public static DocumentType forName(String name)
	{
		if (name == null) {
			return null;
		}
		name = name.trim().toUpperCase();
		if (name.equals("MEDIA") || name.equals("MULTIMEDIA")) {
			return MULTI_MEDIA_OBJECT;
		}
		return DocumentType.valueOf(name);
	}
}
