package nl.naturalis.nba.dao.es.map;

import static nl.naturalis.nba.dao.es.map.MultiField.CI_ANALYZED;
import static nl.naturalis.nba.dao.es.map.MultiField.LIKE_ANALYZED;
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
import java.util.HashMap;
import java.util.List;

import nl.naturalis.nba.api.annotations.Analyzer;
import nl.naturalis.nba.api.annotations.Analyzers;
import nl.naturalis.nba.api.annotations.MappedProperty;
import nl.naturalis.nba.api.annotations.NotIndexed;
import nl.naturalis.nba.api.annotations.NotNested;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.es.types.ESSpecimen;
import nl.naturalis.nba.dao.es.types.ESType;

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

	private static final HashMap<Class<? extends ESType>, Mapping> cache = new HashMap<>();

	public MappingFactory()
	{
	}

	public Mapping getMapping(Class<? extends ESType> forClass)
	{
		Mapping mapping = cache.get(forClass);
		if (mapping == null) {
			mapping = new Mapping();
			addFieldsToDocument(mapping, forClass);
			cache.put(forClass, mapping);
		}
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
				fieldName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
			}
			else {
				fieldName = Character.toLowerCase(methodName.charAt(2)) + methodName.substring(3);
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
			df.setIndex(Index.NOT_ANALYZED);
			if (checkIndexed(df, fm)) {
				addAnalyzedFields(df, fm);
			}
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

	private static void checkPackage(Class<?> cls)
	{
		Package pkg = cls.getPackage();
		if (!apiModelPackage.equals(pkg) && !esModelPackage.equals(pkg)) {
			throw new MappingException("Class not allowed/supported: " + cls.getName());
		}
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
	 * annotation, or if its value was NONE. Otherwise it returns true.
	 */
	private static boolean addAnalyzedFields(DocumentField df, AnnotatedElement fm)
	{
		Analyzers annotation = fm.getAnnotation(Analyzers.class);
		if (annotation == null) {
			df.addMultiField("analyzed", MultiField.DEFAULT_ANALYZED);
			df.addMultiField("ci", CI_ANALYZED);
			return false;
		}
		List<Analyzer> value = Arrays.asList(annotation.value());
		EnumSet<Analyzer> analyzers = EnumSet.copyOf(value);
		if (analyzers.contains(Analyzer.NONE)) {
			if (analyzers.size() != 1) {
				String msg = "Analyzer NONE cannot be combined with other Analyzers";
				throw new MappingException(msg);
			}
			return false;
		}
		for (Analyzer a : analyzers) {
			switch (a) {
				case CASE_INSENSITIVE:
					df.addMultiField("like", LIKE_ANALYZED);
					break;
				case DEFAULT:
					df.addMultiField("analyzed", MultiField.DEFAULT_ANALYZED);
					break;
				case LIKE:
					df.addMultiField("ci", CI_ANALYZED);
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
	 * Elasticsearch; no array type exists or is required in Elasticsearch.
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
		if (name.startsWith("get")
				&& (name.charAt(3) == '_' || Character.isUpperCase(name.charAt(3)))) {
			return null != m.getAnnotation(MappedProperty.class);
		}
		if (name.startsWith("is")
				&& (name.charAt(2) == '_' || Character.isUpperCase(name.charAt(2)))) {
			if (returnType == boolean.class || returnType == Boolean.class) {
				return null != m.getAnnotation(MappedProperty.class);
			}
		}
		return false;
	}

}
