package nl.naturalis.nba.api;

/**
 * Java bean encapsulating a key-value pair.
 * 
 * @author Ayco Holleman
 *
 * @param <T>
 *            The type of the key
 * @param <U>
 *            The type of the value
 */
public class KeyValuePair<T, U> {

	private T key;
	private U value;

	public KeyValuePair()
	{
	}

	public KeyValuePair(T key, U value)
	{
		this.key = key;
		this.value = value;
	}

	/**
	 * Returns the key of the key-value pair.
	 * 
	 * @return
	 */
	public T getKey()
	{
		return key;
	}

	/**
	 * Sets the key of the key-value pair.
	 * 
	 * @param key
	 */
	public void setKey(T key)
	{
		this.key = key;
	}

	/**
	 * Returns the key of the key-value pair.
	 * 
	 * @return
	 */
	public U getValue()
	{
		return value;
	}

	/**
	 * Sets the value of the key-value pair.
	 * 
	 * @param value
	 */
	public void setValue(U value)
	{
		this.value = value;
	}

}
