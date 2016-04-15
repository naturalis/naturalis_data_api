package nl.naturalis.nba.dao.es.util;

import java.util.Base64;

import org.apache.commons.compress.utils.Charsets;

public class ESUtil {

	private ESUtil()
	{
	}

	public static String base64Encode(String s)
	{
		byte[] bytes = s.getBytes(Charsets.UTF_8);
		bytes = Base64.getEncoder().encode(bytes);
		return new String(bytes,Charsets.UTF_8);
	}

}
