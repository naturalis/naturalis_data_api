package nl.naturalis.nba.dao.es.map;

import static org.domainobject.util.ClassUtil.isA;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import nl.naturalis.nba.api.annotations.MappedProperty;
import nl.naturalis.nba.api.annotations.NGram;
import nl.naturalis.nba.api.annotations.NotAnalyzed;
import nl.naturalis.nba.api.annotations.NotIndexed;
import nl.naturalis.nba.api.annotations.NotNested;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.es.types.ESSpecimen;

/**
 * Generates Elasticsearch mappings from {@link Class} objects.
 * 
 * @author ayco
 *
 */
public class MappingFactory {

	private static final Package apiModelPackage = Specimen.class.getPackage();
	private static final Package esModelPackage = ESSpecimen.class.getPackage();
	private static final DataTypeMap dataTypeMap = DataTypeMap.getInstance();

	public MappingFactory()
	{
	}

	public Mapping getMapping(Class<?> forClass)
	{
		Mapping mapping = new Mapping();
		addFieldsToDocument(mapping, forClass);
		return mapping;
	}

	private static ESField createESField(Field field)
	{
		Class<?> realType = field.getType();
		Class<?> mapToType = mapType(realType, field.getGenericType());
		ESDataType esType = dataTypeMap.getESType(mapToType);
		if (esType == null) {
			/*
			 * The Java type does not map to a simple Elasticsearch type like
			 * "string" or "boolean". The Elastichsearch type must be "object"
			 * or "nested".
			 */
			return createDocument(field, mapToType);
		}
		return createSimpleField(field, esType);
	}

	private static ESField createESField(Method method)
	{
		Class<?> realType = method.getReturnType();
		Class<?> mapToType = mapType(realType, method.getGenericReturnType());
		ESDataType esType = dataTypeMap.getESType(mapToType);
		if (esType == null) {
			return createDocument(method, mapToType);
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

	private static Document createDocument(Field field, Class<?> mapToType)
	{
		checkPackage(mapToType);
		Class<?> realType = field.getType();
		Document document;
		if (realType.isArray() || isA(realType, Collection.class)) {
			if (field.getAnnotation(NotNested.class) == null) {
				document = new Document(ESDataType.NESTED);
			}
			else {
				document = new Document();
			}
		}
		else {
			document = new Document();
		}
		addFieldsToDocument(document, mapToType);
		return document;
	}

	private static Document createDocument(Method method, Class<?> mapToType)
	{
		checkPackage(mapToType);
		Class<?> realType = method.getReturnType();
		Document document;
		if (realType.isArray() || isA(realType, Collection.class)) {
			if (method.getAnnotation(NotNested.class) == null) {
				document = new Document(ESDataType.NESTED);
			}
			else {
				document = new Document();
			}
		}
		else {
			document = new Document();
		}
		addFieldsToDocument(document, mapToType);
		return document;
	}

	private static void addFieldsToDocument(Document document, Class<?> forClass)
	{
		for (Field f : getFields(forClass)) {
			document.addField(f.getName(), createESField(f));
		}
		for (Method m : getMappedProperties(forClass)) {
			String methodName = m.getName();
			String fieldName;
			if(methodName.startsWith("get")) {
				fieldName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
			}
			else {
				fieldName = Character.toLowerCase(methodName.charAt(2)) + methodName.substring(3);
			}			
			document.addField(fieldName, createESField(m));
		}
	}

	private static void checkPackage(Class<?> cls)
	{
		Package pkg = cls.getPackage();
		if (!apiModelPackage.equals(pkg) && !esModelPackage.equals(pkg)) {
			throw new MappingException("Class not allowed/supported: " + cls.getName());
		}
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

	/*
	 * Maps a class to the class that must be mapped in its place. For example,
	 * when mapping arrays, it's not the array that is mapped but the class of
	 * its elements.
	 */
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
			assert (false);
			return null;
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
		if (name.startsWith("get") && Character.isUpperCase(name.charAt(3))) {
			return null != m.getAnnotation(MappedProperty.class);
		}
		if (name.startsWith("is") && Character.isUpperCase(name.charAt(2))) {
			if (returnType == boolean.class || returnType == Boolean.class) {
				return null != m.getAnnotation(MappedProperty.class);
			}
		}
		return false;
	}

}
