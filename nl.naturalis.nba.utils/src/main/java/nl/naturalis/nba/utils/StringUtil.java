package nl.naturalis.nba.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class StringUtil {

	/**
	 * The empty, zero-length {@code String}.
	 */
	public static final String EMPTY = "";

	public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

	/**
	 * Extract a slice from {@code string}. If {@code from} is negative, count
	 * from the end of the string. For example, {@code substr("Hello", -2)}
	 * returns {@code "lo"}. If {@code string} is {@code null} this method
	 * returns {@code null}. If {@code from} is greater than the string's
	 * length, this method returns {@link #EMPTY}.
	 * 
	 * @param string
	 *            The {@code String} to extract a slice from
	 * @param from
	 *            The start index of the slice within {@code string} (may be
	 *            negative)
	 * 
	 * @return The slice
	 */
	public static String substr(String string, int from)
	{
		if (string == null) {
			return null;
		}
		if (from > string.length()) {
			return EMPTY;
		}
		if (from < 0) {
			from = Math.max(0, string.length() + from);
		}
		return string.substring(from);
	}

	/**
	 * Extract a slice from {@code string}. If {@code from} is negative, count
	 * from the end of the string. For example, {@code substr("Hello", -4, 3)}
	 * returns {@code "ell"}. If {@code string} is {@code null} this method
	 * returns {@code null}. If {@code from} is greater than the string's
	 * length, this method returns {@link #EMPTY}. If {@code length} is less
	 * then 1, this method returns {@link #EMPTY}.
	 * 
	 * @param string
	 *            The {@code String} to extract a slice from
	 * @param from
	 *            The start index of the slice within {@code string} (may be
	 *            negative)
	 * @param length
	 *            The length the slice (must be positive or you will receive an
	 *            empty {@code String}).
	 * @return The slice
	 */
	public static String substr(String string, int from, int length)
	{
		if (string == null) {
			return null;
		}
		if (from > string.length() || length < 1) {
			return EMPTY;
		}
		if (from < 0) {
			from = Math.max(0, string.length() + from);
		}
		int to = Math.min(string.length(), from + length);
		return string.substring(from, to);
	}

	public static String zpad(Object obj, int width, String separator)
	{
		return lpad(obj, width, '0', separator);
	}

	public static String zpad(Object obj, int width)
	{
		return lpad(obj, width, '0');
	}

	public static String lpad(Object obj, int width, String separator)
	{
		return lpad(obj, width, ' ', separator);
	}

	public static String pad(Object obj, int width)
	{
		return pad(obj, width, ' ', EMPTY);
	}

	public static String pad(Object obj, int width, char padChar)
	{
		return pad(obj, width, padChar, EMPTY);
	}

	public static String pad(Object obj, int width, char padChar, String separator)
	{
		String s;
		if (obj == null)
			s = EMPTY;
		else {
			s = obj instanceof String ? (String) obj : obj.toString();
			if (s.length() >= width) {
				return s + separator;
			}
		}
		int left = (width - s.length()) / 2;
		int right = width - left - s.length();
		StringBuilder sb = new StringBuilder(width + separator.length());
		for (int i = 0; i < left; ++i)
			sb.append(padChar);
		sb.append(s);
		for (int i = 0; i < right; ++i)
			sb.append(padChar);
		sb.append(separator);
		return sb.toString();
	}

	public static String lpad(Object obj, int width)
	{
		return lpad(obj, width, ' ', EMPTY);
	}

	public static String lpad(Object obj, int width, char padChar)
	{
		return lpad(obj, width, padChar, EMPTY);
	}

	public static String lpad(Object obj, int width, char padChar, String separator)
	{
		String s;
		if (obj == null)
			s = EMPTY;
		else {
			s = obj instanceof String ? (String) obj : obj.toString();
			if (s.length() >= width) {
				return s + separator;
			}
		}
		StringBuilder sb = new StringBuilder(width + separator.length());
		for (int i = s.length(); i < width; ++i) {
			sb.append(padChar);
		}
		sb.append(s);
		sb.append(separator);
		return sb.toString();
	}

	public static String rpad(Object obj, int width, String separator)
	{
		return rpad(obj, width, ' ', separator);
	}

	public static String rpad(Object obj, int width)
	{
		return rpad(obj, width, ' ', EMPTY);
	}

	public static String rpad(Object obj, int width, char padChar)
	{
		return rpad(obj, width, padChar, EMPTY);
	}

	public static String rpad(Object obj, int width, char padChar, String separator)
	{
		String s;
		if (obj == null)
			s = EMPTY;
		else {
			s = obj instanceof String ? (String) obj : obj.toString();
			if (s.length() >= width) {
				return s + separator;
			}
		}
		StringBuilder sb = new StringBuilder(width + separator.length());
		sb.append(s);
		for (int i = s.length(); i < width; ++i) {
			sb.append(padChar);
		}
		sb.append(separator);
		return sb.toString();
	}

	/**
	 * Extract a slice from {@code string}. If {@code from} is negative, the
	 * start of the slice is counted from the end of the string.If {@code to} is
	 * negative, the end of the slice is counted from the end of the string. For
	 * example, {@code substr("Hello", -4, -1)} returns {@code "ell"}. If
	 * {@code string} is {@code null} this method returns {@code null}. If
	 * {@code from} is greater than the string's length, this method returns
	 * {@link #EMPTY}. If {@code length} is less then 1, this method returns
	 * {@link #EMPTY}.
	 * 
	 * @param string
	 *            The {@code String} to extract a slice from
	 * @param from
	 *            The start index of the slice (may be negative)
	 * @param to
	 *            The end index (not inclusive) of the slice (may be negative)
	 * 
	 * @return The slice
	 */
	public static String substring(String string, int from, int to)
	{
		if (string == null) {
			return null;
		}
		if (from < 0) {
			from = Math.max(0, string.length() + from);
		}
		if (to < 0) {
			to = Math.max(0, string.length() + to);
		}
		else {
			to = Math.min(string.length(), to);
		}
		if (from > string.length() || to < from) {
			return EMPTY;
		}
		return string.substring(from, to);
	}

	/**
	 * Null-save equivalent of
	 * {@code new String(bytes, Charset.forName("UTF-8"))}.
	 * 
	 * @param bytes
	 *            The byte array to convert
	 * @return The {@code String} created from the bytes or {@code null} is
	 *         {@code bytes} is {@code null}
	 */
	public static String toString(byte[] bytes)
	{
		if (bytes == null) {
			return null;
		}
		return new String(bytes, CHARSET_UTF8);
	}

	/**
	 * Chop {@code word} from the left of {@code string} and repeat until
	 * {@code string} does not start with {@code word}.
	 * 
	 * @param string
	 * @param word
	 * @return
	 */
	public static String lchop(String string, String word)
	{
		if (string == null) {
			return null;
		}
		if (word == null || word.length() == 0 || word.length() > string.length()) {
			return string;
		}
		int i = 0;
		while (string.regionMatches(i, word, 0, word.length()))
			i += word.length();
		return i == string.length() ? EMPTY : string.substring(i);
	}

	/**
	 * Chop {@code word} from the right of {@code string} and repeat until
	 * {@code string} does not end with {@code word}.
	 * 
	 * @param string
	 * @param word
	 * @return
	 */
	public static String rchop(String string, String word)
	{
		if (string == null)
			return null;
		if (word == null)
			return string;
		while (string.endsWith(word))
			string = string.substring(0, string.length() - word.length());
		return string;
	}

	/**
	 * Null-safe split method hat does not interpret the delimiter as a regular
	 * expression.
	 * 
	 * @param s
	 * @param delim
	 * @return
	 */
	public static String[] split(String s, String delim)
	{
		return split(s, delim, 8);
	}

	/**
	 * Null-safe split method that does not interpret the delimiter as a regular
	 * expression.
	 * 
	 * @param s
	 *            The string to split
	 * @param delim
	 *            The delimiter around which to split
	 * @param numParts
	 *            The expected number of parts (might end up larger)
	 * @return
	 */
	public static String[] split(String s, String delim, int numParts)
	{
		if (s == null)
			return null;
		ArrayList<String> chunks = new ArrayList<>(numParts);
		int from = 0;
		int to = 0;
		while (from < s.length()) {
			to = s.indexOf(delim, from);
			if (to == -1) {
				chunks.add(s.substring(from));
				break;
			}
			chunks.add(s.substring(from, to));
			from = to + delim.length();
		}
		return chunks.toArray(new String[chunks.size()]);
	}

	/**
	 * Trim the specified character off both sides of the specified
	 * {@code String}. This method returns an empty String if the specified
	 * {@code String} is null.
	 * 
	 * @param s
	 *            The {@code String} to trim
	 * @param c
	 *            The character to trim off the {@code String}
	 * 
	 * @return The trimmed {@code String}.
	 * 
	 */
	public static String trim(String s, char c)
	{
		return ltrim(rtrim(s, c), c);
	}

	/**
	 * Trim the specified character off the beginning of the specified
	 * {@code String}. This method returns an empty String if the specified
	 * {@code String} is null.
	 * 
	 * @param s
	 *            The {@code String} to trim
	 * @param c
	 *            The character to trim off the {@code String}
	 * 
	 * @return The trimmed {@code String}.
	 * 
	 */
	public static String ltrim(String s, char c)
	{
		if (s == null)
			return EMPTY;
		int i;
		for (i = 0; i < s.length() && s.charAt(i) == c; ++i) {}
		return i == s.length() ? EMPTY : s.substring(i);
	}

	public static String trim(String s, char... chars)
	{
		if (s == null)
			return EMPTY;
		int i = 0;
		while (ArrayUtil.in(s.charAt(i), chars)) {
			i++;
		}
		int j = s.length() - 1;
		while (ArrayUtil.in(s.charAt(j), chars)) {
			j--;
		}
		return s.substring(i, j + 1);
	}

	public static String trim(String s, String chars)
	{
		return trim(s, chars.toCharArray());
	}

	/**
	 * Trim the specified character off the end of the specified {@code String}.
	 * This method returns an empty String if the specified {@code String} is
	 * null.
	 * 
	 * @param s
	 *            The {@code String} to trim
	 * @param c
	 *            The character to trim off the {@code String}
	 * 
	 * @return The trimmed {@code String}.
	 * 
	 */
	public static String rtrim(String s, char c)
	{
		if (s == null)
			return EMPTY;
		int i;
		for (i = s.length() - 1; i != -1 && s.charAt(i) == c; --i) {}
		return i == -1 ? EMPTY : s.substring(0, i + 1);
	}

	/**
	 * Equivalent to {@code isTrueValue(value, true)}. In other words
	 * {@code null} and empty {@code String}s are considered to be true!
	 * 
	 * @param value
	 *            The {@code String} to convert to a boolean
	 * @return
	 * 
	 * @see #isTrueValue(String, boolean)
	 */
	public static boolean isTrue(String s)
	{
		return ConfigObject.isTrueValue(s);
	}

	/**
	 * Convert the specified {@code String} to a boolean value. Returns
	 * {@code dfault} if the string is {@code null} or empty. Returns
	 * {@code true} if the specified value is is one of
	 * {@code ["true", "1", "yes", "on"]}. Otherwise this method returns
	 * {@code false}. The string is trimmed before being compared to these
	 * values.
	 * 
	 * @param value
	 *            The {@code String} to convert to a boolean
	 * @param dfault
	 *            The value to return if {@code value} is null or empty
	 * @return
	 */
	public static boolean isTrue(String s, boolean dfault)
	{
		return ConfigObject.isTrueValue(s, dfault);
	}

	/**
	 * Returns the contents of the specified resource as a {@code String}.
	 * 
	 * @param path
	 * @return
	 */
	public static String getResourceAsString(String path)
	{
		InputStream is = StringUtil.class.getResourceAsStream(path);
		if (is == null) {
			throw new RuntimeException(String.format("Invalid path: \"%s\"", path));
		}
		String s = fromInputStream(is);
		try {
			is.close();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		return s;
	}

	/**
	 * Convert the specified {@code String} to an {@code InputStream}.
	 * 
	 * @param s
	 *            The {@code String} to convert
	 * 
	 * @return The resulting {@code InputStream}
	 */
	public static InputStream toInputStream(String s)
	{
		return new ByteArrayInputStream(s.getBytes(CHARSET_UTF8));
	}

	/**
	 * Convert the specified {@code String} to an {@code InputStream} using the
	 * specified character encoding.
	 * 
	 * @param s
	 *            The {@code String} to convert
	 * @param charset
	 *            The character encoding to apply
	 * 
	 * @return The resulting {@code InputStream}
	 */
	public static InputStream toInputStream(String s, String charset)
	{
		return new ByteArrayInputStream(s.getBytes(Charset.forName(charset)));
	}

	public static String fromInputStream(InputStream is)
	{
		return fromInputStream(is, "UTF-8");
	}

	public static String fromInputStream(InputStream is, String charset)
	{
		return fromInputStream(is, charset, 2048);
	}

	public static String fromInputStream(InputStream is, String charset, int bufsize)
	{
		ByteArrayOutputStream buffer = new ByteArrayOutputStream(bufsize);
		int n;
		byte[] data = new byte[bufsize];
		try {
			while ((n = is.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, n);
			}
			buffer.flush();
		}
		catch (IOException e) {
			throw ExceptionUtil.smash(e);
		}
		return new String(buffer.toByteArray(), Charset.forName(charset));
	}

	public static int toInt(String s)
	{
		return s == null || s.equals(EMPTY) ? 0 : Integer.parseInt(s);
	}

}
