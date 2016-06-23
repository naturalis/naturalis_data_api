package nl.naturalis.nba.dao.es.map;

import static java.lang.Character.isUpperCase;
import static java.lang.Character.toLowerCase;
import static nl.naturalis.nba.dao.es.map.MultiField.DEFAULT_MULTIFIELD;
import static nl.naturalis.nba.dao.es.map.MultiField.IGNORE_CASE_MULTIFIELD;
import static nl.naturalis.nba.dao.es.map.MultiField.LIKE_MULTIFIELD;
import static org.domainobject.util.ClassUtil.isA;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import nl.naturalis.nba.api.annotations.Analyzer;
import nl.naturalis.nba.api.annotations.Analyzers;
import nl.naturalis.nba.api.annotations.MappedProperty;
import nl.naturalis.nba.api.annotations.NotIndexed;
import nl.naturalis.nba.api.annotations.NotNested;
import nl.naturalis.nba.dao.es.DocumentType;

/**
 * Generates Elasticsearch mappings from {@link Class} objects.
 * 
 * @author Ayco Holleman
 *
 */
public class MappingFactory {

	/**
	 * Builds an Elasticsearch {@link Mapping} object for the specified class.
	 * This is a rather heavy-weight operation. In principle you should always
	 * retrieve {@link Mapping} instances through
	 * {@link DocumentType#getMapping() DocumentType.getMapping()}.
	 * 
	 * @param forClass
	 * @return
	 */
	public Mapping getMapping(Class<?> type)
	{
		Mapping mapping = new Mapping();
		addFieldsToDocument(mapping, type);
		return mapping;
	}

	private static void addFieldsToDocument(Document document, Class<?> forClass)
	{
		for (Field f : getFields(forClass)) {
			ESField esField = createESField(f);
			esField.setName(f.getName());
			esField.setParent(document);
			document.addField(f.getName(), esField);
		}
		for (Method m : getMappedProperties(forClass)) {
			String methodName = m.getName();
			String fieldName;
			if (methodName.startsWith("get")) {
				fieldName = toLowerCase(methodName.charAt(3)) + methodName.substring(4);
			}
			else {
				fieldName = toLowerCase(methodName.charAt(2)) + methodName.substring(3);
			}
			ESField esField = createESField(m);
			esField.setName(fieldName);
			esField.setParent(document);
			document.addField(fieldName, esField);
		}
	}

	private static ESField createESField(Field field)
	{
		Class<?> realType = field.getType();
		Class<?> mapToType = mapType(realType, field.getGenericType());
		ESDataType esType = DataTypeMap.getInstance().getESType(mapToType);
		if (esType == null) {
			/*
			 * Then the Java type does not map to a simple Elasticsearch type
			 * like "string" or "boolean". The Elastichsearch type must be
			 * "object" or "nested".
			 */
			return createDocument(field, mapToType);
		}
		return createSimpleField(field, esType);
	}

	private static ESField createESField(Method method)
	{
		Class<?> realType = method.getReturnType();
		Class<?> mapToType = mapType(realType, method.getGenericReturnType());
		ESDataType esType = DataTypeMap.getInstance().getESType(mapToType);
		if (esType == null) {
			return createDocument(method, mapToType);
		}
		return createSimpleField(method, esType);
	}

	private static DocumentField createSimpleField(AnnotatedElement fm, ESDataType esType)
	{
		DocumentField df = new DocumentField(esType);
		if (esType == ESDataType.STRING) {
			df.setIndex(Index.NOT_ANALYZED);
			if (checkIndexed(df, fm)) {
				addAnalyzedFields(df, fm);
			}
		}
		return df;
	}

	private static Document createDocument(Field field, Class<?> mapToType)
	{
		checkClass(mapToType);
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
		checkClass(mapToType);
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

	private static void checkClass(Class<?> cls)
	{
		//
	}

	/*
	 * Check whether or not the field is indexed and, if not, set the "index"
	 * property of the field to "no".
	 */
	private static boolean checkIndexed(DocumentField df, AnnotatedElement fm)
	{
		NotIndexed annotation = fm.getAnnotation(NotIndexed.class);
		if (annotation == null) {
			return true;
		}
		df.setIndex(Index.NO);
		return false;
	}

	/*
	 * Returns false if the Java field does not contain the @Analyzers
	 * annotation. Otherwise it returns true.
	 */
	private static boolean addAnalyzedFields(DocumentField df, AnnotatedElement fm)
	{
		Analyzers annotation = fm.getAnnotation(Analyzers.class);
		if (annotation == null) {
			df.addMultiField(DEFAULT_MULTIFIELD);
			df.addMultiField(IGNORE_CASE_MULTIFIELD);
			return false;
		}
		if (annotation.value().length == 0) {
			return true;
		}
		List<Analyzer> value = Arrays.asList(annotation.value());
		EnumSet<Analyzer> analyzers = EnumSet.copyOf(value);
		for (Analyzer a : analyzers) {
			switch (a) {
				case CASE_INSENSITIVE:
					df.addMultiField(IGNORE_CASE_MULTIFIELD);
					break;
				case DEFAULT:
					df.addMultiField(DEFAULT_MULTIFIELD);
					break;
				case LIKE:
					df.addMultiField(LIKE_MULTIFIELD);
					break;
				default:
					break;
			}
		}
		return true;
	}

	/*
	 * Maps a class to the class that must be used in its place. For example,
	 * when mapping arrays, it's not the array that is mapped but the class of
	 * its elements, because fields are intrinsically multi-valued in
	 * Elasticsearch. No array type exists or is required in Elasticsearch.
	 */
	private static Class<?> mapType(Class<?> realType, Type typeArg)
	{
		if (realType.isArray())
			return realType.getComponentType();
		if (isA(realType, Collection.class))
			return getClassForTypeArgument(typeArg);
		if (isA(realType, Enum.class))
			return String.class;
		return realType;
	}

	/*
	 * Returns the type argument for a generic type (e.g. Person for
	 * List<Person>)
	 */
	private static Class<?> getClassForTypeArgument(Type t)
	{
		String s = t.toString();
		int i = s.indexOf('<');
		s = s.substring(i + 1, s.length() - 1);
		try {
			return Class.forName(s);
		}
		catch (ClassNotFoundException e) {
			throw new MappingException(e);
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
		if (name.startsWith("get") && (name.charAt(3) == '_' || isUpperCase(name.charAt(3)))) {
			return null != m.getAnnotation(MappedProperty.class);
		}
		if (name.startsWith("is") && (name.charAt(2) == '_' || isUpperCase(name.charAt(2)))) {
			if (returnType == boolean.class || returnType == Boolean.class) {
				return null != m.getAnnotation(MappedProperty.class);
			}
		}
		return false;
	}

}
