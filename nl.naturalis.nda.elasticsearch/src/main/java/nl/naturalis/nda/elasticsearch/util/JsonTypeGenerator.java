package nl.naturalis.nda.elasticsearch.util;

public class JsonTypeGenerator {

	private final Class<?> forClass;


	public JsonTypeGenerator(Class<?> forClass)
	{
		this.forClass = forClass;
	}


	public void generate()
	{
		StringBuilder sb = new StringBuilder(1024);
	}

}
