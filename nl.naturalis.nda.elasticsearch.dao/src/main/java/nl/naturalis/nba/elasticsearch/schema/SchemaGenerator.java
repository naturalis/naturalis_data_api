package nl.naturalis.nba.elasticsearch.schema;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import nl.naturalis.nba.annotations.NotAnalyzed;
import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;

import org.domainobject.util.ClassUtil;

public class SchemaGenerator {

	private static final String ES_STRING = "string";
	private static final String ES_INT = "integer";
	private static final String ES_LONG = "long";
	private static final String ES_BOOLEAN = "boolean";
	private static final String ES_DOUBLE = "double";
	private static final String ES_DATE = "date";

	private static final String INDENT = "\t";

	private static final Package PKG_DOMAIN = Specimen.class.getPackage();
	private static final Package PKG_ESTYPES = ESSpecimen.class.getPackage();

	private static HashMap<Class<?>, String> typeMap = new HashMap<>();

	static {
		typeMap.put(String.class, ES_STRING);
		typeMap.put(char.class, ES_STRING);
		typeMap.put(Character.class, ES_STRING);
		typeMap.put(int.class, ES_INT);
		typeMap.put(Integer.class, ES_INT);
		typeMap.put(long.class, ES_LONG);
		typeMap.put(Long.class, ES_LONG);
		typeMap.put(double.class, ES_DOUBLE);
		typeMap.put(Double.class, ES_DOUBLE);
		typeMap.put(boolean.class, ES_BOOLEAN);
		typeMap.put(Boolean.class, ES_BOOLEAN);
		typeMap.put(Date.class, ES_DATE);
	}

	public static void main(String[] args) throws Exception
	{
		PrintWriter pw = new PrintWriter(System.out);
		SchemaGenerator sg = new SchemaGenerator(pw);
		sg.generate(ESSpecimen.class);
	}

	private final PrintWriter pw;

	public SchemaGenerator(PrintWriter pw)
	{
		this.pw = pw;
	}

	public void generate(Class<?> cls)
	{
		println(0, "{");
		beginObject(1, cls.getSimpleName());
		printField(2, false, "dynamic", "strict");
		processClass(2, cls);
		endObject(1);
		println(0, "}");
		pw.flush();
		pw.close();
	}

	public void processClass(int level, Class<?> cls)
	{
		Package pkg = cls.getPackage();
		if (!PKG_DOMAIN.equals(pkg) && !PKG_ESTYPES.equals(pkg)) {
			throw new RuntimeException("Class not allowed/supported: " + cls.getName());
		}
		beginObject(level, "properties");
		Field[] fields = cls.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			processField(level + 1, i == fields.length - 1, fields[i]);
		}
		pw.println();
		endObject(level);
	}

	public void processField(int level, boolean last, Field f)
	{
		println(level, jsonVar(f.getName()), " {");
		Class<?> type = getType(f);
		String esType = typeMap.get(type);
		if (esType == null) {
			processClass(level + 1, type);
		}
		else {
			print(level + 1, jsonVar("type", esType));
			if (f.getAnnotation(NotAnalyzed.class) != null) {
				pw.println(',');
				println(level + 1, jsonVar("index", "not_analyzed"));
			}
			else {
				pw.println(",");
				beginObject(level + 1, "fields");
				beginObject(level + 2, "raw");
				printField(level + 3, false, "index", "not_analyzed");
				printField(level + 3, true, "type", esType);
				endObject(level + 2);
				endObject(level + 1);
			}
		}
		if (last)
			println(level, "}");
		else
			println(level, "},");
	}

	private void beginObject(int level, String name)
	{
		println(level, jsonVar(name), " {");
	}

	private void endObject(int level)
	{
		println(level, "}");
	}

	private void printField(int level, boolean last, String name, String value)
	{
		if (last)
			println(level, jsonVar(name, value));
		else
			println(level, jsonVar(name, value), ",");
	}

	private void println(int level, String... strings)
	{
		indent(level);
		for (String s : strings)
			pw.print(s);
		pw.println();
	}

	private void print(int level, String... strings)
	{
		indent(level);
		for (String s : strings)
			pw.print(s);
	}

	private void indent(int level)
	{
		for (int i = 0; i < level; ++i)
			pw.print(INDENT);
	}

	private static Class<?> getType(Field f)
	{
		Class<?> c = f.getType();
		if (c.isArray()) {
			return c.getComponentType();
		}
		if (ClassUtil.isA(c, Collection.class)) {
			return getClassForTypeArgument(f);
		}
		return c;
	}

	private static Class<?> getClassForTypeArgument(Field f)
	{
		String s = f.getGenericType().toString();
		int i = s.indexOf('<');
		s = s.substring(i + 1, s.length() - 1);
		try {
			return Class.forName(s);
		}
		catch (ClassNotFoundException e) {
			// TODO
			throw new RuntimeException(e);
		}
	}

	private static String jsonVar(String name, String value)
	{
		return jsonVar(name) + quote(value);
	}

	private static String jsonVar(String name)
	{
		return quote(name) + ": ";
	}

	private static String quote(String s)
	{
		return '"' + s + '"';
	}

}
