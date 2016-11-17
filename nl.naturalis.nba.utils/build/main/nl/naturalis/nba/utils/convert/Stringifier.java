package nl.naturalis.nba.utils.convert;

import nl.naturalis.nba.utils.StringUtil;

/**
 * Callback interface for converting an object to a {@code String}
 * 
 */
public interface Stringifier<IN> extends Converter<IN, String> {

	/**
	 * Basic, null-safe stringifier that simply calls {@code toString()} on the
	 * specified object. When passing {@code null}, the {@code execute} method
	 * returns an empty string (<i>not</i> &#34;null&#34;!).
	 */
	public static final Stringifier<?> BASIC_STRINGIFIER = new Stringifier<Object>() {

		public String execute(Object object, Object... args)
		{
			if (object == null)
				return StringUtil.EMPTY;
			return object.toString();
		}
	};

	/**
	 * Basic, null-safe stringifier that simply calls {@code toString()} on the
	 * specified object. When passing {@code null}, the {@code execute} method
	 * returns the string &#34;null&#34;.
	 */
	public static final Stringifier<Object> JAVA_STRINGIFIER = new Stringifier<Object>() {

		public String execute(Object object, Object... args)
		{
			if (object == null)
				return "null";
			return object.toString();
		}
	};

}
