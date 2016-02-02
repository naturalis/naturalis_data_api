package nl.naturalis.nba.elasticsearch.schema;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.HashMap;

import nl.naturalis.nba.annotations.NotAnalyzed;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;

public class SchemaGenerator {

	private static final String INDENT = "\t";

	private static HashMap<Class<?>, String> typeMap = new HashMap<>();

	static {
		typeMap.put(String.class, "string");
		typeMap.put(int.class, "integer");
		typeMap.put(Integer.class, "integer");
		typeMap.put(boolean.class, "boolean");
		typeMap.put(Boolean.class, "boolean");
	}

	public static void main(String[] args)
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
		String pkg = cls.getPackage().getName();
		if (!pkg.equals("nl.naturalis.nda.elasticsearch.dao.estypes")
				&& !pkg.equals("nl.naturalis.nda.domain")) {
			// TODO: throw something
			return;
		}
		beginObject(level, "properties");
		Field[] fields = cls.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			if (i != 0) {
				pw.println(',');
			}
			processField(level, fields[i]);
		}
		endObject(level);
	}

	public void processField(int level, Field f)
	{
		println(level, jsonVar(f.getName()), " {");
		String esType = typeMap.get(f.getType());
		if (esType == null) {
			processClass(level + 1, f.getType());
			return;
		}
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
			printField(level + 3, false, "type", esType);
			endObject(level + 2);
			endObject(level + 1);
		}
		print(level, "}");
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
