package nl.naturalis.nba.elasticsearch.map;

import static org.domainobject.util.ClassUtil.isA;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import nl.naturalis.nba.annotations.MappedProperty;
import nl.naturalis.nba.annotations.NGram;
import nl.naturalis.nba.annotations.NotAnalyzed;
import nl.naturalis.nba.annotations.NotIndexed;
import nl.naturalis.nba.annotations.NotNested;
import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;

/**
 * Generates Elasticsearch mappings from {@link Class} objects.
 * 
 * @author ayco
 *
 */
public class MappingFactory {

	private static final Package PKG_DOMAIN = Specimen.class.getPackage();
	private static final Package PKG_ESTYPES = ESSpecimen.class.getPackage();
	private static final DataTypeMap dataTypeMap = DataTypeMap.getInstance();

	public MappingFactory()
	{
	}

	public Mapping getMapping(Class<?> forClass)
	{
		Mapping mapping = new Mapping();
		ArrayList<Field> javaFields = getFields(forClass);
		for (Field f : javaFields) {
			mapping.addField(f.getName(), createESField(f));
		}
		return mapping;
	}

	private static ESField createESField(Field field)
	{
		Class<?> realType = field.getType();
		Class<?> mapToType = mapType(field.getType(), field.getGenericType());
		ESDataType esType = dataTypeMap.getESType(mapToType);
		if (esType == null) {
			boolean nested = realType.isArray() || isA(realType, Collection.class);
			return createDocument(field, nested, mapToType);
		}
		return createSimpleField(field, esType);
	}

	private static ESField createESField(Method method)
	{
		Class<?> realType = method.getReturnType();
		Class<?> mapToType = mapType(realType, method.getGenericReturnType());
		ESDataType esType = dataTypeMap.getESType(mapToType);
		if (esType == null) {
			boolean nested = realType.isArray() || isA(realType, Collection.class);
			return createDocument(method, nested, mapToType);
		}
		return createSimpleField(method, esType);
	}

	private static DocumentField createSimpleField(AnnotatedElement fm, ESDataType esType)
	{
		DocumentField df = new DocumentField(esType);
		if (esType == ESDataType.STRING) {
			if (!processNotIndexedAnnotation(df, fm))
				processNotAnalyzedAnnotation(df, fm);
			processNGramAnnotation(df, fm);
		}
		return df;
	}

	private static boolean processNotIndexedAnnotation(DocumentField df, AnnotatedElement fm)
	{
		NotIndexed notIndexed = fm.getAnnotation(NotIndexed.class);
		if (notIndexed == null) {
			return false;
		}
		df.setIndex(Index.NO);
		return true;
	}

	private static void processNotAnalyzedAnnotation(DocumentField df, AnnotatedElement fm)
	{
		NotAnalyzed notAnalyzed = fm.getAnnotation(NotAnalyzed.class);
		if (notAnalyzed == null) {
			df.addRawField();
		}
		else {
			df.setIndex(Index.NOT_ANALYZED);
		}
	}

	private static void processNGramAnnotation(DocumentField df, AnnotatedElement fm)
	{
		NGram ngram = fm.getAnnotation(NGram.class);
		if (ngram != null) {
			ESDataType dt = ESDataType.parse(ngram.type());
			ESScalar scalar = new ESScalar(dt);
			scalar.setAnalyzer(ngram.value());
			df.addToFields("ngram", scalar);
		}
	}

	private static Document createDocument(AnnotatedElement fm, boolean nested, Class<?> cls)
	{
		Package pkg = cls.getPackage();
		if (!PKG_DOMAIN.equals(pkg) && !PKG_ESTYPES.equals(pkg)) {
			throw new RuntimeException("Class not allowed/supported: " + cls.getName());
		}
		Document document;
		if (nested) {
			if (fm.getAnnotation(NotNested.class) == null) {
				document = new Document(ESDataType.NESTED);
			}
			else {
				document = new Document();
			}
		}
		else {
			document = new Document();
		}
		ArrayList<Field> javaFields = getFields(cls);
		for (Field f : javaFields) {
			document.addField(f.getName(), createESField(f));
		}
		for (Method m : getMappedProperties(cls)) {
			document.addField(m.getName(), createESField(m));
		}
		return document;
	}

	private static Class<?> mapType(Class<?> realType, Type typeArg)
	{
		if (realType.isArray())
			return realType.getComponentType();
		if (isA(realType, Enum.class))
			return String.class;
		if (isA(realType, Collection.class))
			return getClassForTypeArgument(typeArg);
		return realType;
	}

	private static Class<?> getClassForTypeArgument(Type t)
	{
		String s = t.toString();
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

	private static ArrayList<Field> getFields(Class<?> cls)
	{
		ArrayList<Class<?>> hierarchy = new ArrayList<>(3);
		do {
			hierarchy.add(cls);
			cls = cls.getSuperclass();
		} while (cls != Object.class);
		ArrayList<Field> allFields = new ArrayList<>();
		for (int i = hierarchy.size() - 1; i >= 0; i--) {
			cls = hierarchy.get(i);
			Field[] fields = cls.getDeclaredFields();
			for (Field f : fields) {
				if (Modifier.isStatic(f.getModifiers()))
					continue;
				allFields.add(f);
			}
		}
		return allFields;
	}

	private static ArrayList<Method> getMappedProperties(Class<?> cls)
	{
		ArrayList<Class<?>> hierarchy = new ArrayList<>(3);
		do {
			hierarchy.add(cls);
			cls = cls.getSuperclass();
		} while (cls != Object.class);
		ArrayList<Method> props = new ArrayList<>(4);
		for (int i = hierarchy.size() - 1; i >= 0; i--) {
			cls = hierarchy.get(i);
			Method[] methods = cls.getDeclaredMethods();
			for (Method m : methods) {
				if (isMappedProperty(m)) {
					props.add(m);
				}
			}
		}
		return props;
	}

	private static boolean isMappedProperty(Method m)
	{
		if (Modifier.isStatic(m.getModifiers()))
			return false;
		if (m.getParameters().length != 0)
			return false;
		Class<?> returnType = m.getReturnType();
		if (returnType == void.class)
			return false;
		String name = m.getName();
		if (name.startsWith("get") && Character.isUpperCase(name.charAt(3)))
			return null != m.getAnnotation(MappedProperty.class);
		if (name.startsWith("is") && Character.isUpperCase(name.charAt(2))) {
			if (returnType == boolean.class || returnType == Boolean.class) {
				return null != m.getAnnotation(MappedProperty.class);
			}
		}
		return false;
	}

}
