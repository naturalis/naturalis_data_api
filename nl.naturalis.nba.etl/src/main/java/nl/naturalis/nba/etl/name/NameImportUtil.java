package nl.naturalis.nba.etl.name;

import nl.naturalis.nba.api.model.Name;

public class NameImportUtil {

	static long longHashCode(String s)
	{
		long h = 0;
		for (int i = 0; i < s.length(); i++) {
			h = 31 * h + s.charAt(i);
		}
		return h;
	}

	static Name findName(String name)
	{
		return null;
	}

	private NameImportUtil()
	{
	}

}
