package nl.naturalis.nba.common.es.map;

import static nl.naturalis.nba.common.es.map.ESDataType.GEO_SHAPE;
import static nl.naturalis.nba.common.es.map.ESDataType.NESTED;
import static nl.naturalis.nba.common.es.map.ESDataType.STRING;
import static nl.naturalis.nba.common.es.map.Index.NO;
import static nl.naturalis.nba.common.es.map.Index.NOT_ANALYZED;
import static nl.naturalis.nba.common.es.map.MappingUtil.extractFieldFromGetter;
import static nl.naturalis.nba.common.es.map.MappingUtil.getClassForTypeArgument;
import static nl.naturalis.nba.common.es.map.MappingUtil.getFields;
import static nl.naturalis.nba.common.es.map.MappingUtil.getMappedProperties;
import static nl.naturalis.nba.common.es.map.MultiField.DEFAULT_MULTIFIELD;
import static nl.naturalis.nba.common.es.map.MultiField.IGNORE_CASE_MULTIFIELD;
import static nl.naturalis.nba.common.es.map.MultiField.LIKE_MULTIFIELD;
import static nl.naturalis.nba.utils.ClassUtil.isA;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import nl.naturalis.nba.api.annotations.Analyzer;
import nl.naturalis.nba.api.annotations.Analyzers;
import nl.naturalis.nba.api.annotations.GeoShape;
import nl.naturalis.nba.api.annotations.NotIndexed;
import nl.naturalis.nba.api.annotations.NotNested;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.query.Condition;

/**
 * <p>
 * Generates Elasticsearch type mappings from {@link Class} objects. For each
 * instance field in the Java class a counterpart will be created in the
 * Elasticsearch document type, unless it is annotated with {@link JsonIgnore}.
 * Getters are ignored unless they are annotated with {@link JsonProperty}.
 * Furthermore, fields and mapped getters can be annotated with NBA-specific
 * annotations that specify or fine-tune the indexing behaviour for the field.
 * For example: {@link NotIndexed} or {@link Analyzers}. {@link Mapping} objects
 * play an important role not only during data imports (data is guaranteed to
 * fit the document type because the document type was generated from the very
 * class that contains the data); it also plays an important role when querying
 * data. For example, if a field is annotated with {@link NotIndexed}, the query
 * mechanism knows beforehand that a {@link Condition query condition} on that
 * field will fail and won't even bother sending the query to Elasticsearch.
 * </p>
 * <p>
 * {@code Mapping} objects are best consulted through a {@link MappingInfo}
 * instance, which wraps and decorates the {@code Mapping} object with useful
 * extra functionality. Contrary to the {@code Mapping} objects themselves,
 * {@code MappingInfo} objects have negligable instantion costs. {@code Mapping}
 * objects are expensive to create. However, a {@code MappingFactory} caches the
 * {@code Mapping} objects it creates so asking twice for the same
 * {@code Mapping} object is cheap.
 * </p>
 * <p>
 * Note that a {@code Mapping} object <b>is</b> the Elasticsearch document type
 * mapping. That is, if you serialize it to JSON, you have an Elasticsearch
 * document type mapping.
 * </p>
 * 
 * @author Ayco Holleman
 *
 */
public class MappingFactory {

	private static final HashMap<Class<? extends IDocumentObject>, Mapping<? extends IDocumentObject>> cache = new HashMap<>();
	private static final DataTypeMap dataTypeMap = DataTypeMap.getInstance();

	/**
	 * Builds an Elasticsearch {@link Mapping} object for the specified class.
	 * 
	 * @param type
	 * @return
	 */
	public static <T extends IDocumentObject> Mapping<T> getMapping(Class<T> type)
	{

		@SuppressWarnings("unchecked")
		Mapping<T> mapping = (Mapping<T>) cache.get(type);
		if (mapping == null) {
			mapping = new Mapping<T>(type);
			addFieldsToDocument(mapping, type, newTree(new HashSet<>(0), type));
			cache.put(type, mapping);
		}
		return mapping;
	}

	private static void addFieldsToDocument(ComplexField document, Class<?> type,
			HashSet<Class<?>> ancestors)
	{
		for (Field javaField : getFields(type)) {
			ESField esField = createESField(javaField, ancestors);
			esField.setName(javaField.getName());
			esField.setParent(document);
			esField.setArray(isMultiValued(javaField));
			document.addField(javaField.getName(), esField);
		}
		for (Method javaMethod : getMappedProperties(type)) {
			String methodName = javaMethod.getName();
			String fieldName = extractFieldFromGetter(methodName);
			ESField esField = createESField(javaMethod, ancestors);
			esField.setName(fieldName);
			esField.setParent(document);
			esField.setArray(isMultiValued(javaMethod));
			document.addField(fieldName, esField);
		}
	}

	private static ESField createESField(Field field, HashSet<Class<?>> ancestors)
	{
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

	private static SimpleField createSimpleField(AnnotatedElement fm, ESDataType esType)
	{
		SimpleField sf;
		switch (esType) {
			case GEO_SHAPE:
				sf = new GeoShapeField();
				if (fm.getAnnotation(GeoShape.class) != null) {
					GeoShape annotation = fm.getAnnotation(GeoShape.class);
					GeoShapeField gsf = (GeoShapeField) sf;
					gsf.setPrecision(annotation.precision());
					gsf.setPoints_only(annotation.pointsOnly());
				}
				break;
			case STRING:
				sf = new StringField();
				break;
			default:
				sf = new SimpleField(esType);
		}
		if (fm.getAnnotation(NotIndexed.class) == null) {
			if (esType == STRING) {
				sf.setIndex(NOT_ANALYZED);
				addMultiFields((StringField) sf, fm);
			}
		}
		else {
			sf.setIndex(NO);
		}
		return sf;
	}

	private static ComplexField createDocument(Field field, Class<?> mapToType,
			HashSet<Class<?>> ancestors)
	{
		Class<?> realType = field.getType();
		ComplexField document;
		if (realType.isArray() || isA(realType, Collection.class)) {
			if (field.getAnnotation(NotNested.class) == null) {
				document = new ComplexField(NESTED);
			}
			else {
				document = new ComplexField();
			}
		}
		else {
			document = new ComplexField();
		}
		addFieldsToDocument(document, mapToType, ancestors);
		return document;
	}

	private static ComplexField createDocument(Method method, Class<?> mapToType,
			HashSet<Class<?>> ancestors)
	{
		Class<?> realType = method.getReturnType();
		ComplexField document;
		if (realType.isArray() || isA(realType, Collection.class)) {
			if (method.getAnnotation(NotNested.class) == null) {
				document = new ComplexField(NESTED);
			}
			else {
				document = new ComplexField();
			}
		}
		else {
			document = new ComplexField();
		}
		addFieldsToDocument(document, mapToType, ancestors);
		return document;
	}

	/*
	 * Returns false if the Java field does not contain the @Analyzers
	 * annotation. Otherwise it returns true.
	 */
	private static boolean addMultiFields(StringField af, AnnotatedElement fm)
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

	// Disallow instantiation
	private MappingFactory()
	{
	}

}
