package nl.naturalis.nda.elasticsearch.load;

import org.domainobject.util.StringUtil;

public class LoadUtil {

	public static String getLuceneType(Class<?> cls)
	{
		return cls.getSimpleName();
	}


	public static String getMapping(Class<?> cls)
	{
		return StringUtil.getResourceAsString("/es-mappings/" + getLuceneType(cls) + ".json");
	}

}
