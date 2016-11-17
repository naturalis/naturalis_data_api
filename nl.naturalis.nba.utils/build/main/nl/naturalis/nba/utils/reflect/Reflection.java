package nl.naturalis.nba.utils.reflect;

public class Reflection {

	private final Object obj;
	private final Accessor accessor;

	public Reflection(Object obj)
	{
		this.obj = obj;
		this.accessor = Accessor.forClass(obj.getClass());
	}

	public Object get(String field)
	{
		return accessor.get(obj, field);
	}

	public byte getByte(String field)
	{
		return accessor.getByte(obj, field);
	}

	public double getChar(String field)
	{
		return accessor.getChar(obj, field);
	}

	public double getDouble(String field)
	{
		return accessor.getDouble(obj, field);
	}

	public float getFloat(String field)
	{
		return accessor.getFloat(obj, field);
	}

	public int getInt(String field)
	{
		return accessor.getInt(obj, field);
	}

	public long getLong(String field)
	{
		return accessor.getLong(obj, field);
	}

	public short getShort(String field)
	{
		return accessor.getShort(obj, field);
	}

}
