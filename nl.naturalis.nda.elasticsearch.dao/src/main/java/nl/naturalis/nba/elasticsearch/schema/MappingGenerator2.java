package nl.naturalis.nba.elasticsearch.schema;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import nl.naturalis.nba.annotations.NGram;
import nl.naturalis.nba.annotations.NotAnalyzed;
import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;

import org.domainobject.util.ClassUtil;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Generates an Elasticsearch mappings from {@link Class} objects.
 * 
 * @author ayco
 *
 */
public class MappingGenerator2 {

	public static void main(String[] args) throws Exception
	{
		MappingGenerator2 mg = new MappingGenerator2();
		Mapping m = mg.getMapping(ESSpecimen.class);
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		om.setSerializationInclusion(Include.NON_NULL);
		om.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
		String s = om.writerWithDefaultPrettyPrinter().writeValueAsString(m);
		System.out.println(s);
	}

	private static final Package PKG_DOMAIN = Specimen.class.getPackage();
	private static final Package PKG_ESTYPES = ESSpecimen.class.getPackage();

	private static HashMap<Class<?>, Type> typeMap = new HashMap<>();

	static {
		typeMap.put(String.class, Type.STRING);
		typeMap.put(char.class, Type.STRING);
		typeMap.put(Character.class, Type.STRING);
		typeMap.put(URI.class, Type.STRING);
		typeMap.put(Enum.class, Type.STRING);
		typeMap.put(byte.class, Type.BYTE);
		typeMap.put(Byte.class, Type.BYTE);
		typeMap.put(short.class, Type.SHORT);
		typeMap.put(Short.class, Type.BOOLEAN);
		typeMap.put(int.class, Type.INTEGER);
		typeMap.put(Integer.class, Type.INTEGER);
		typeMap.put(long.class, Type.LONG);
		typeMap.put(Long.class, Type.LONG);
		typeMap.put(float.class, Type.FLOAT);
		typeMap.put(Float.class, Type.FLOAT);
		typeMap.put(double.class, Type.DOUBLE);
		typeMap.put(Double.class, Type.DOUBLE);
		typeMap.put(boolean.class, Type.BOOLEAN);
		typeMap.put(Boolean.class, Type.BOOLEAN);
		typeMap.put(Date.class, Type.DATE);
	}

	private final Ngram defaultNgram;

	public MappingGenerator2()
	{
		defaultNgram = new Ngram("nda_ngram_analyzer");
	}

	public Mapping getMapping(Class<?> forClass)
	{
		Mapping mapping = new Mapping();
		addFields(forClass, mapping);
		return mapping;
	}

	private void addFields(Class<?> cls, ESObject esObject)
	{
		for (Field f : cls.getDeclaredFields()) {
			esObject.addField(f.getName(), processField(f));
		}
	}

	private ESField processField(Field f)
	{
		Class<?> type = getType(f);
		Type esType = typeMap.get(type);
		if (esType == null) {
			return getESObject(type);
		}
		return getESScalar(f, esType);
	}

	private ESScalar getESScalar(Field field, Type esType)
	{
		ESScalar sf = new ESScalar(esType);
		if (esType == Type.STRING) {
			NotAnalyzed notAnalyzed = field.getAnnotation(NotAnalyzed.class);
			NGram ngram = field.getAnnotation(NGram.class);
			if (notAnalyzed != null) {
				sf.setIndex(Index.NOT_ANALYZED);
			}
			else {
				sf.setFields(Fields.DEFAULT);
			}
			if (ngram != null) {
				sf.setNgram(defaultNgram);
			}
		}
		return sf;
	}

	private ESField getESObject(Class<?> cls)
	{
		Package pkg = cls.getPackage();
		if (!PKG_DOMAIN.equals(pkg) && !PKG_ESTYPES.equals(pkg)) {
			throw new RuntimeException("Class not allowed/supported: " + cls.getName());
		}
		ESObject field = new ESObject();
		addFields(cls, field);
		return field;
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

}
