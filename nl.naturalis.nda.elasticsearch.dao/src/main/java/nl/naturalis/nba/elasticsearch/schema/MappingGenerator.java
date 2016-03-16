package nl.naturalis.nba.elasticsearch.schema;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import nl.naturalis.nba.annotations.NGram;
import nl.naturalis.nba.annotations.NotAnalyzed;
import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;

import org.domainobject.util.ClassUtil;

/**
 * Generates an Elasticsearch mappings from {@link Class} objects.
 * 
 * @author ayco
 *
 */
public class MappingGenerator {

	public static void main(String[] args) throws Exception
	{
		StringWriter sw = new StringWriter(2048);
		PrintWriter pw = new PrintWriter(sw);
		MappingGenerator sg = new MappingGenerator(pw);
		sg.printMapping(ESSpecimen.class);
		System.out.println(sw.toString());
	}

	static final String ES_STRING = "string";
	static final String ES_BYTE = "byte";
	static final String ES_SHORT = "short";
	static final String ES_INT = "integer";
	static final String ES_LONG = "long";
	static final String ES_FLOAT = "float";
	static final String ES_DOUBLE = "double";
	static final String ES_BOOLEAN = "boolean";
	static final String ES_DATE = "date";

	private static final String INDENT = "\t";

	private static final Package PKG_DOMAIN = Specimen.class.getPackage();
	private static final Package PKG_ESTYPES = ESSpecimen.class.getPackage();

	private static HashMap<Class<?>, String> typeMap = new HashMap<>();

	static {
		typeMap.put(String.class, ES_STRING);
		typeMap.put(char.class, ES_STRING);
		typeMap.put(Character.class, ES_STRING);
		typeMap.put(URI.class, ES_STRING);
		typeMap.put(Enum.class, ES_STRING);
		typeMap.put(byte.class, ES_BYTE);
		typeMap.put(Byte.class, ES_BYTE);
		typeMap.put(short.class, ES_SHORT);
		typeMap.put(Short.class, ES_BOOLEAN);
		typeMap.put(int.class, ES_INT);
		typeMap.put(Integer.class, ES_INT);
		typeMap.put(long.class, ES_LONG);
		typeMap.put(Long.class, ES_LONG);
		typeMap.put(float.class, ES_FLOAT);
		typeMap.put(Float.class, ES_FLOAT);
		typeMap.put(double.class, ES_DOUBLE);
		typeMap.put(Double.class, ES_DOUBLE);
		typeMap.put(boolean.class, ES_BOOLEAN);
		typeMap.put(Boolean.class, ES_BOOLEAN);
		typeMap.put(Date.class, ES_DATE);
	}

	private PrintWriter pw;

	public MappingGenerator()
	{
		this.pw = null;
	}

	public MappingGenerator(Writer writer)
	{
		writeTo(writer);
	}

	public void writeTo(Writer writer)
	{
		if (writer instanceof PrintWriter) {
			pw = (PrintWriter) writer;
		}
		else {
			pw = new PrintWriter(writer);
		}
	}

	public void printMapping(Class<?> cls)
	{
		if (pw == null) {
			pw = new PrintWriter(System.out);
		}
		println(0, "{");
		printField(1, false, "dynamic", "strict");
		processFields(1, cls);
		println(0, "}");
		pw.flush();
		pw.close();
	}

	public String getMapping(Class<?> forClass)
	{
		PrintWriter old = pw;
		StringWriter sw = new StringWriter(4096);
		pw = new PrintWriter(sw);
		try {
			printMapping(forClass);
		}
		finally {
			pw = old;
		}
		return sw.toString();
	}

	public void processClass(int level, Class<?> cls)
	{
		Package pkg = cls.getPackage();
		if (!PKG_DOMAIN.equals(pkg) && !PKG_ESTYPES.equals(pkg)) {
			throw new RuntimeException("Class not allowed/supported: " + cls.getName());
		}
		processFields(level, cls);
	}

	private void processFields(int level, Class<?> cls)
	{
		beginObject(level, "properties");
		Field[] fields = cls.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			processField(level + 1, i == fields.length - 1, fields[i]);
		}
		endObject(level, true);
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
			NotAnalyzed notAnalyzed = f.getAnnotation(NotAnalyzed.class);
			NGram ngram = f.getAnnotation(NGram.class);
			print(level + 1, jsonVar("type", esType));
			if (esType == ES_STRING) {
				if (notAnalyzed != null) {
					pw.println(',');
					println(level + 1, jsonVar("index", "not_analyzed"));
				}
				else {
					pw.println(",");
					beginObject(level + 1, "fields");
					beginObject(level + 2, "raw");
					printField(level + 3, false, "index", "not_analyzed");
					printField(level + 3, true, "type", "string");
					endObject(level + 2, true);
					endObject(level + 1, ngram == null);
				}
			}
			if (ngram != null) {
				beginObject(level + 1, "ngram");
				printField(level + 2, false, "type", ngram.type());
				printField(level + 2, true, "index_analyzer", ngram.value());
				endObject(level + 1, true);
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

	private void endObject(int level, boolean last)
	{
		if (last)
			println(level, "}");
		else
			println(level, "},");
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
		if (ClassUtil.isA(c, Enum.class)) {
			return String.class;
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
