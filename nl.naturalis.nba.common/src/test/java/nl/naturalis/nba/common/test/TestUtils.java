package nl.naturalis.nba.common.test;

import java.io.InputStream;
import java.util.Map;

import nl.naturalis.nba.common.json.JsonUtil;

public class TestUtils {

	private TestUtils()
	{
	}

	/**
	 * Asserts that the specified JSON string is equal the JSON in the specified
	 * file. Both JSON strings are first read into a map so formatting
	 * differences don't play a role.
	 * 
	 * @param unitTestClass
	 * @param jsonString
	 * @param jsonFile
	 * @return
	 */
	public static boolean jsonEquals(Class<?> unitTestClass, String jsonString, String jsonFile)
	{
		InputStream is = unitTestClass.getResourceAsStream(jsonFile);
		Map<String, Object> expected = JsonUtil.deserialize(is);
		Map<String, Object> actual = JsonUtil.deserialize(jsonString);
		return actual.equals(expected);
	}
}
