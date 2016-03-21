package nl.naturalis.nba.elasticsearch.map;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;

import nl.naturalis.nba.annotations.NGram;
import nl.naturalis.nba.annotations.NotAnalyzed;
import nl.naturalis.nba.annotations.NotIndexed;
import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;

import org.domainobject.util.ClassUtil;

/**
 * Generates an Elasticsearch mappings from {@link Class} objects.
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
		ArrayList<Field> javaFields = getAllFieldsInHierarchy(forClass);
		for (Field f : javaFields) {
			mapping.addField(f.getName(), processField(f));
		}
		return mapping;
	}

	private static ArrayList<Field> getAllFieldsInHierarchy(Class<?> cls)
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

	private static ESField processField(Field f)
	{
		Class<?> type = getType(f);
		ESDataType esType = dataTypeMap.getESType(type);
		if (esType == null) {
			return createDocument(type);
		}
		return getSimpleField(f, esType);
	}

	private static DocumentField getSimpleField(Field field, ESDataType esType)
	{
		DocumentField sf = new DocumentField(esType);
		if (esType == ESDataType.STRING) {
			NotIndexed notIndexed = field.getAnnotation(NotIndexed.class);
			if (notIndexed == null) {
				NotAnalyzed notAnalyzed = field.getAnnotation(NotAnalyzed.class);
				if (notAnalyzed == null) {
					sf.addRawField();
				}
				else {
					sf.setIndex(Index.NOT_ANALYZED);
				}
			}
			else {
				sf.setIndex(Index.NO);
			}
			NGram ngram = field.getAnnotation(NGram.class);
			if (ngram != null) {
				ESDataType eSDataType = ESDataType.parse(ngram.type());
				ESScalar scalar = new ESScalar(eSDataType);
				scalar.setAnalyzer(ngram.value());
				sf.addToFields("ngram", scalar);
			}
		}
		return sf;
	}

	private static Document createDocument(Class<?> cls)
	{
		Package pkg = cls.getPackage();
		if (!PKG_DOMAIN.equals(pkg) && !PKG_ESTYPES.equals(pkg)) {
			throw new RuntimeException("Class not allowed/supported: " + cls.getName());
		}
		Document document = new Document();
		ArrayList<Field> javaFields = getAllFieldsInHierarchy(cls);
		for (Field f : javaFields) {
			document.addField(f.getName(), processField(f));
		}
		return document;
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
