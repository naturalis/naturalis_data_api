package nl.naturalis.nba.common.es.map;

import static nl.naturalis.nba.common.es.map.ESDataType.GEO_SHAPE;
import static nl.naturalis.nba.common.es.map.ESDataType.NESTED;
import static nl.naturalis.nba.common.es.map.Index.NO;
import static nl.naturalis.nba.common.es.map.Index.NOT_ANALYZED;
import static nl.naturalis.nba.common.es.map.MappingUtil.extractFieldFromGetter;
import static nl.naturalis.nba.common.es.map.MappingUtil.getClassForTypeArgument;
import static nl.naturalis.nba.common.es.map.MappingUtil.getFields;
import static nl.naturalis.nba.common.es.map.MappingUtil.getMappedProperties;
import static nl.naturalis.nba.common.es.map.MultiField.DEFAULT_MULTIFIELD;
import static nl.naturalis.nba.common.es.map.MultiField.IGNORE_CASE_MULTIFIELD;
import static nl.naturalis.nba.common.es.map.MultiField.LIKE_MULTIFIELD;
import static org.domainobject.util.ClassUtil.isA;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import nl.naturalis.nba.api.annotations.Analyzer;
import nl.naturalis.nba.api.annotations.Analyzers;
import nl.naturalis.nba.api.annotations.GeoShape;
import nl.naturalis.nba.api.annotations.NotIndexed;
import nl.naturalis.nba.api.annotations.NotNested;

/**
 * Generates Elasticsearch type mappings from {@link Class} objects.
 * 
 * @author Ayco Holleman
 *
 */
public class MappingFactory {

	private static final HashMap<Class<?>, Mapping> cache = new HashMap<>();
	private static final DataTypeMap dataTypeMap = DataTypeMap.getInstance();

	/**
	 * Builds an Elasticsearch {@link Mapping} object for the specified class.
	 * 
	 * @param type
	 * @return
	 */
	public static Mapping getMapping(Class<?> type)
	{

		Mapping mapping = cache.get(type);
		if (mapping == null) {
			mapping = new Mapping(type);
			addFieldsToDocument(mapping, type, newTree(new HashSet<>(0), type));
			cache.put(type, mapping);
		}
		return mapping;
	}

	private static void addFieldsToDocument(Document document, Class<?> type,
			HashSet<Class<?>> ancestors)
	{
		for (Field javaField : getFields(type)) {
			ESField esField = createESField(javaField, ancestors);
			esField.setName(javaField.getName());
			esField.setParent(document);
			esField.setMultiValued(isMultiValued(javaField));
			document.addField(javaField.getName(), esField);
		}
		for (Method javaMethod : getMappedProperties(type)) {
			String methodName = javaMethod.getName();
			String fieldName = extractFieldFromGetter(methodName);
			ESField esField = createESField(javaMethod, ancestors);
			esField.setName(fieldName);
			esField.setParent(document);
			esField.setMultiValued(isMultiValued(javaMethod));
			document.addField(fieldName, esField);
		}
	}

	private static ESField createESField(Field field, HashSet<Class<?>> ancestors)
	{
		if (field.getAnnotation(GeoShape.class) != null) {
			return createSimpleField(field, GEO_SHAPE);
		}
		Class<?> realType = field.getType();
		Class<?> mapToType = mapType(realType, field.getGenericType());
		ESDataType esType = dataTypeMap.getESType(mapToType);
		if (esType == null) {
			/*
			 * Then the Java type does not map to a simple Elasticsearch type;
			 * the Elastichsearch type is either "object" or "nested".
			 */
			if (ancestors.contains(mapToType)) {
				throw new ClassCircularityException(field, mapToType);
			}
			return createDocument(field, mapToType, newTree(ancestors, mapToType));
		}
		return createSimpleField(field, esType);
	}

	private static ESField createESField(Method method, HashSet<Class<?>> ancestors)
	{
		if (method.getAnnotation(GeoShape.class) != null) {
			return createSimpleField(method, GEO_SHAPE);
		}
		Class<?> realType = method.getReturnType();
		Class<?> mapToType = mapType(realType, method.getGenericReturnType());
		ESDataType esType = dataTypeMap.getESType(mapToType);
		if (esType == null) {
			if (ancestors.contains(mapToType)) {
				throw new ClassCircularityException(method, mapToType);
			}
			return createDocument(method, mapToType, newTree(ancestors, mapToType));
		}
		return createSimpleField(method, esType);
	}

	private static DocumentField createSimpleField(AnnotatedElement fm, ESDataType esType)
	{
		if (esType == GEO_SHAPE) {
			return new GeoShapeField();
		}
		NotIndexed annotation = fm.getAnnotation(NotIndexed.class);
		if (annotation == null) {
			if (esType.isAnalyzable()) {
				AnalyzableField field = new AnalyzableField(esType);
				field.setIndex(NOT_ANALYZED);
				addMultiFields(field, fm);
				return field;
			}
			return new DocumentField(esType);
		}
		DocumentField field = new DocumentField(esType);
		field.setIndex(NO);
		return field;
	}

	private static Document createDocument(Field field, Class<?> mapToType,
			HashSet<Class<?>> ancestors)
	{
		Class<?> realType = field.getType();
		Document document;
		if (realType.isArray() || isA(realType, Collection.class)) {
			if (field.getAnnotation(NotNested.class) == null) {
				document = new Document(NESTED);
			}
			else {
				document = new Document();
			}
		}
		else {
			document = new Document();
		}
		addFieldsToDocument(document, mapToType, ancestors);
		return document;
	}

	private static Document createDocument(Method method, Class<?> mapToType,
			HashSet<Class<?>> ancestors)
	{
		Class<?> realType = method.getReturnType();
		Document document;
		if (realType.isArray() || isA(realType, Collection.class)) {
			if (method.getAnnotation(NotNested.class) == null) {
				document = new Document(NESTED);
			}
			else {
				document = new Document();
			}
		}
		else {
			document = new Document();
		}
		addFieldsToDocument(document, mapToType, ancestors);
		return document;
	}

	/*
	 * Returns false if the Java field does not contain the @Analyzers
	 * annotation. Otherwise it returns true.
	 */
	private static boolean addMultiFields(AnalyzableField af, AnnotatedElement fm)
	{
		Analyzers annotation = fm.getAnnotation(Analyzers.class);
		if (annotation == null) {
			af.addMultiField(DEFAULT_MULTIFIELD);
			af.addMultiField(IGNORE_CASE_MULTIFIELD);
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
					af.addMultiField(IGNORE_CASE_MULTIFIELD);
					break;
				case DEFAULT:
					af.addMultiField(DEFAULT_MULTIFIELD);
					break;
				case LIKE:
					af.addMultiField(LIKE_MULTIFIELD);
					break;
				default:
					break;
			}
		}
		return true;
	}

	/*
	 * Maps a Java class to another Java class that must be used in its place.
	 * The latter class is then mapped to an Elasticsearch data type. For
	 * example, when mapping arrays, it's not the array that is mapped but the
	 * class of its elements. No array type exists or is required in
	 * Elasticsearch; fields are intrinsically multi-valued.
	 */
	private static Class<?> mapType(Class<?> type, Type typeArg)
	{
		if (type.isArray())
			return type.getComponentType();
		if (isA(type, Collection.class))
			return getClassForTypeArgument(typeArg);
		if (isA(type, Enum.class))
			return String.class;
		return type;
	}

	private static boolean isMultiValued(Field f)
	{
		if (f.getType().isArray())
			return true;
		if (isA(f.getType(), Collection.class))
			return true;
		return false;
	}

	private static boolean isMultiValued(Method m)
	{
		if (m.getReturnType().isArray())
			return true;
		if (isA(m.getReturnType(), Collection.class))
			return true;
		return false;
	}

	private static HashSet<Class<?>> newTree(HashSet<Class<?>> ancestors, Class<?> newType)
	{
		HashSet<Class<?>> set = new HashSet<>(6);
		set.addAll(ancestors);
		set.add(newType);
		return set;
	}

}
