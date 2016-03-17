package nl.naturalis.nba.elasticsearch.map;

import java.lang.reflect.Field;
import java.util.Collection;

import nl.naturalis.nba.annotations.NGram;
import nl.naturalis.nba.annotations.NotAnalyzed;
import nl.naturalis.nba.annotations.NotIndexed;
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
public class MappingFactory {

	public static void main(String[] args) throws Exception
	{
		MappingFactory mg = new MappingFactory();
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
	private static final TypeMap typeMap = TypeMap.getInstance();

	public MappingFactory()
	{
	}

	public Mapping getMapping(Class<?> forClass)
	{
		Mapping mapping = new Mapping();
		addFields(forClass, mapping);
		return mapping;
	}

	private static void addFields(Class<?> cls, ESObject esObject)
	{
		for (Field f : cls.getDeclaredFields()) {
			esObject.addField(f.getName(), processField(f));
		}
	}

	private static ESField processField(Field f)
	{
		Class<?> type = getType(f);
		Type esType = typeMap.getESType(type);
		if (esType == null) {
			return getESObject(type);
		}
		return getSimpleField(f, esType);
	}

	private static ESSimpleField getSimpleField(Field field, Type esType)
	{
		ESSimpleField sf = new ESSimpleField(esType);
		if (esType == Type.STRING) {
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
				Type type = Type.parse(ngram.type());
				ESScalar scalar = new ESScalar(type);
				scalar.setAnalyzer(ngram.value());
				sf.addToFields("ngram", scalar);
			}
		}
		return sf;
	}

	private static ESField getESObject(Class<?> cls)
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
