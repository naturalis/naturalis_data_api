package nl.naturalis.nba.dao.es.map;

import static nl.naturalis.nba.dao.es.map.ESDataType.NESTED;
import static nl.naturalis.nba.dao.es.map.ESDataType.STRING;
import static nl.naturalis.nba.dao.es.map.Index.NO;
import static nl.naturalis.nba.dao.es.map.Index.NOT_ANALYZED;
import static nl.naturalis.nba.dao.es.map.MultiField.DEFAULT_MULTIFIELD;
import static nl.naturalis.nba.dao.es.map.MultiField.IGNORE_CASE_MULTIFIELD;
import static nl.naturalis.nba.dao.es.map.MultiField.LIKE_MULTIFIELD;
import static nl.naturalis.nba.dao.es.map.Util.extractFieldFromGetter;
import static nl.naturalis.nba.dao.es.map.Util.getClassForTypeArgument;
import static nl.naturalis.nba.dao.es.map.Util.getFields;
import static nl.naturalis.nba.dao.es.map.Util.getMappedProperties;
import static org.domainobject.util.ClassUtil.isA;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import nl.naturalis.nba.api.annotations.Analyzer;
import nl.naturalis.nba.api.annotations.Analyzers;
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
	 * @param type
	 * @return
	 */
	public Mapping getMapping(Class<?> type)
	{
		Mapping mapping = new Mapping(type);
		addFieldsToDocument(mapping, type);
		return mapping;
	}

	private static void addFieldsToDocument(Document document, Class<?> type)
	{
		for (Field f : getFields(type)) {
			ESField esField = createESField(f);
			esField.setName(f.getName());
			esField.setParent(document);
			document.addField(f.getName(), esField);
		}
		for (Method m : getMappedProperties(type)) {
			String methodName = m.getName();
			String fieldName = extractFieldFromGetter(methodName);
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
		NotIndexed annotation = fm.getAnnotation(NotIndexed.class);
		if (esType == STRING) {
			df.setIndex(NOT_ANALYZED);
		}
		if (annotation == null) {
			if (esType == STRING) {
				addAnalyzedFields(df, fm);
			}
		}
		else {
			df.setIndex(NO);
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
				document = new Document(NESTED);
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
				document = new Document(NESTED);
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
	 * Maps a Java class to another Java class that must be used in its place.
	 * The latter class is then mapped to an Elasticsearch data type. For
	 * example, when mapping arrays, it's not the array that is mapped but the
	 * class of its elements, because fields are intrinsically multi-valued in
	 * Elasticsearch. No array type exists or is required in Elasticsearch.
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

}
