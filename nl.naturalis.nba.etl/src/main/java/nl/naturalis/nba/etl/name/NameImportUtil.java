package nl.naturalis.nba.etl.name;

class NameImportUtil {

	static long longHashCode(String s)
	{
		long h = 0;
		for (int i = 0; i < s.length(); i++) {
			h = 31 * h + s.charAt(i);
		}
		return h;
	}

	private NameImportUtil()
	{
	}

}
