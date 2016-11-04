package nl.naturalis.nba.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.domainobject.util.ConfigObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.api.model.GeoArea;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.common.es.map.Mapping;
import nl.naturalis.nba.common.es.map.MappingFactory;
import nl.naturalis.nba.common.json.ObjectMapperLocator;
import nl.naturalis.nba.dao.exception.DaoException;
import nl.naturalis.nba.dao.exception.InitializationException;

/**
 * Provides information about an Elasticsearch document type. This class is very
 * much like an {@code enum} of the four document types managed by the NBA
 * (Specimen, Taxon, MultiMediaObject and GeoArea). You cannot instantiate this
 * class. There are four {@code public static final} instances of it, for each
 * of the document types just mentioned. Each {@code DocumentType} instance
 * functions as a little cache of frequently-used, heavy-weight objects
 * associated with the document type. For example, although you can easily
 * create {@link Mapping} objects yourself, it is recommendable to
 * {@link #getMapping() request} them from the appropriate {@code DocumentType}
 * instance.
 * 
 * @author Ayco Holleman
 *
 */
public class DocumentType<T extends IDocumentObject> {

	/**
	 * A {@code DocumentType} instance representing the Specimen document type.
	 */
	public static final DocumentType<Specimen> SPECIMEN;
	/**
	 * A {@code DocumentType} instance representing the Taxon document type.
	 */
	public static final DocumentType<Taxon> TAXON;
	/**
	 * A {@code DocumentType} instance representing the MultiMediaObject
	 * document type.
	 */
	public static final DocumentType<MultiMediaObject> MULTI_MEDIA_OBJECT;
	/**
	 * A {@code DocumentType} instance representing the GeoArea document type.
	 */
	public static final DocumentType<GeoArea> GEO_AREA;

	static {

		SPECIMEN = new DocumentType<>(Specimen.class);
		TAXON = new DocumentType<>(Taxon.class);
		MULTI_MEDIA_OBJECT = new DocumentType<>(MultiMediaObject.class);
		GEO_AREA = new DocumentType<>(GeoArea.class);

		try {
			for (ConfigObject cfg : getIndexSections()) {
				/*
				 * This will set the new IndexInfo instance on the applicable
				 * DocumentType instances defined above, and add the applicable
				 * DocumentType instances to the new IndexInfo instance.
				 */
				new IndexInfo(cfg);
			}
		}
		catch (Throwable t) {
			/*
			 * No point in going on. We log the error and allow the exception to
			 * cause an ExceptionInInitializerError.
			 */
			Logger logger = DaoRegistry.getInstance().getLogger(DocumentType.class);
			logger.fatal("Error while retrieving index info", t);
			throw t;
		}
	}

	/**
	 * Returns a {@code DocumentType} instance for the specified name
	 */
	public static DocumentType<?> forName(String name)
	{
		if (SPECIMEN.name.equalsIgnoreCase(name))
			return SPECIMEN;
		if (TAXON.name.equalsIgnoreCase(name))
			return TAXON;
		if (MULTI_MEDIA_OBJECT.name.equalsIgnoreCase(name))
			return MULTI_MEDIA_OBJECT;
		if (GEO_AREA.name.equalsIgnoreCase(name))
			return GEO_AREA;
		throw new DaoException("No such document type: \"" + name + '"');
	}

	/**
	 * Returns the {@code DocumentType} instance corresponding to the specified
	 * Java class. For aach Elasticsearch document type there is one Java class
	 * structured just like it. In fact, the document type is generated through
	 * reflection from the Java class.
	 */
	public static DocumentType<?> forClass(Class<? extends IDocumentObject> cls)
	{
		if (SPECIMEN.javaType == cls)
			return SPECIMEN;
		if (TAXON.javaType == cls)
			return TAXON;
		if (MULTI_MEDIA_OBJECT.javaType == cls)
			return MULTI_MEDIA_OBJECT;
		if (GEO_AREA.javaType == cls)
			return GEO_AREA;
		throw new DaoException("No document type corresponding to " + cls);
	}

	IndexInfo indexInfo;

	private final Logger logger;
	private final String name;
	private final Class<T> javaType;
	private final ObjectMapper objMapper;
	private final Mapping<T> mapping;

	private DocumentType(Class<T> javaType)
	{
		this(javaType.getSimpleName(), javaType);
	}

	private DocumentType(String name, Class<T> javaType)
	{
		logger = DaoRegistry.getInstance().getLogger(DocumentType.class);
		logger.info("Retrieving info for document type \"{}\"", name);
		this.name = name;
		this.javaType = javaType;
		this.mapping = MappingFactory.getMapping(javaType);
		ObjectMapperLocator oml = ObjectMapperLocator.getInstance();
		this.objMapper = oml.getObjectMapper(javaType);
	}

	/**
	 * Returns the name of the document type.
	 * 
	 * @return
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns information about the index hosting the document type.
	 * 
	 * @return
	 */
	public IndexInfo getIndexInfo()
	{
		return indexInfo;
	}

	/**
	 * Returns the Java class reflecting the document type.
	 * 
	 * @return
	 */
	public Class<T> getJavaType()
	{
		return javaType;
	}

	/**
	 * Returns a {@link Mapping mapping object} representing the document type mapping.
	 * 
	 * @return
	 */
	public Mapping<T> getMapping()
	{
		return mapping;
	}

	/**
	 * Returns a Jackson Object mapper for the document type.
	 * 
	 * @return
	 */
	public ObjectMapper getObjectMapper()
	{
		return objMapper;
	}

	@Override
	public boolean equals(Object obj)
	{
		return this == obj;
	}

	@Override
	public int hashCode()
	{
		return System.identityHashCode(this);
	}

	@Override
	public String toString()
	{
		return name;
	}

	private static List<ConfigObject> getIndexSections()
	{
		DaoRegistry registry = DaoRegistry.getInstance();
		ConfigObject cfg = registry.getConfiguration();
		List<ConfigObject> sections = new ArrayList<>();
		for (int i = 0;; i++) {
			String prefix = "elasticsearch.index." + i;
			ConfigObject section = cfg.getSection(prefix);
			if (section == null) {
				break;
			}
			Logger logger = DaoRegistry.getInstance().getLogger(DocumentType.class);
			String cfgFile = registry.getConfigurationFile().getAbsolutePath();
			logger.info("Processing section {} of {}", prefix, cfgFile);
			sections.add(section);
		}
		if (sections.size() == 0) {
			String msg = "Missing required section \"elasticsearch.index.0\"";
			throw new InitializationException(msg);
		}
		return sections;
	}
}
