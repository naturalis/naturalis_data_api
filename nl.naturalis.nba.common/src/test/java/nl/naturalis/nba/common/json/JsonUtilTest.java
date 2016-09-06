package nl.naturalis.nba.common.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import nl.naturalis.nba.common.json.JsonUtil;

@SuppressWarnings("static-method")
public class JsonUtilTest {

	@Test
	public void testDeserialize()
	{
		try (InputStream is = JsonUtilTest.class.getResourceAsStream("JsonUtilTest.json")) {
			Map<String, Object> map = JsonUtil.deserialize(is);
			assertEquals("01", map.get("firstName"), "John");
			assertEquals("02", map.get("lastName"), "Smith");
			assertTrue("03", map.containsKey("hobbies"));
			assertNull("04", map.get("hobbies"));
			assertEquals("05", map.get("age"), 36);
			@SuppressWarnings("unchecked")
			Map<String, Object> address = (Map<String, Object>) map.get("address");
			assertEquals("06", address.get("number"), 1429);
			List<?> kids = (List<?>) map.get("kids");
			assertEquals("07", 3, kids.size());
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testReadField_01()
	{
		try (InputStream is = JsonUtilTest.class.getResourceAsStream("JsonUtilTest.json")) {
			String s = (String) JsonUtil.readField(is, "address.country.name");
			assertEquals("01", "U.S.A", s);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testReadField_02()
	{
		try (InputStream is = JsonUtilTest.class.getResourceAsStream("JsonUtilTest.json")) {
			int i = (int) JsonUtil.readField(is, "address.number");
			assertEquals("01", 1429, i);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testReadField_03()
	{
		try (InputStream is = JsonUtilTest.class.getResourceAsStream("JsonUtilTest.json")) {
			List<?> kids = (List<?>) JsonUtil.readField(is, "kids");
			assertEquals("01", 3, kids.size());
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testReadField_04()
	{
		try (InputStream is = JsonUtilTest.class.getResourceAsStream("JsonUtilTest.json")) {
			String kid = (String) JsonUtil.readField(is, "kids.0");
			assertEquals("01", "Mary", kid);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testReadField_05()
	{
		try (InputStream is = JsonUtilTest.class.getResourceAsStream("JsonUtilTest.json")) {
			String kid = (String) JsonUtil.readField(is, "kids.1");
			assertEquals("01", "Lisa", kid);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testReadField_06()
	{
		try (InputStream is = JsonUtilTest.class.getResourceAsStream("JsonUtilTest.json")) {
			String kid = (String) JsonUtil.readField(is, "kids.2");
			assertEquals("01", "Junior", kid);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testReadField_07()
	{
		try (InputStream is = JsonUtilTest.class.getResourceAsStream("JsonUtilTest.json")) {
			Object value = JsonUtil.readField(is, "kids.3");
			assertTrue("01", value == JsonUtil.MISSING_VALUE);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testReadField_08()
	{
		try (InputStream is = JsonUtilTest.class.getResourceAsStream("JsonUtilTest.json")) {
			Object value =  JsonUtil.readField(is, "bla");
			assertTrue("01", value == JsonUtil.MISSING_VALUE);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testReadField_09()
	{
		try (InputStream is = JsonUtilTest.class.getResourceAsStream("JsonUtilTest.json")) {
			Object value =  JsonUtil.readField(is, "bla.0");
			assertTrue("01", value == JsonUtil.MISSING_VALUE);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testReadField_10()
	{
		try (InputStream is = JsonUtilTest.class.getResourceAsStream("JsonUtilTest.json")) {
			Object value =  JsonUtil.readField(is, "bla.bla");
			assertTrue("01", value == JsonUtil.MISSING_VALUE);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
