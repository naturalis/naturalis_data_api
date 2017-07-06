package nl.naturalis.nba.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.api.model.GeoArea;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.api.model.ScientificNameGroup_old;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.common.es.map.Mapping;
import nl.naturalis.nba.common.es.map.MappingFactory;
import nl.naturalis.nba.common.json.ObjectMapperLocator;
import nl.naturalis.nba.dao.exception.DaoException;
import nl.naturalis.nba.dao.exception.InitializationException;
import nl.naturalis.nba.utils.ConfigObject;

/**
 * Provides information about an Elasticsearch document type. This class is very
 * much like an {@code enum}. You cannot instantiate this class. Each
 * {@code DocumentType} instance functions as a little cache of frequently-used,
 * heavy-weight objects associated with the Elasticsearch document type. For
 * example, although you can easily create {@link Mapping} objects yourself, it
 * is recommendable to {@link #getMapping() request} them from the appropriate
 * {@code DocumentType} instance.
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
	/**
	 * A {@code DocumentType} instance representing the ScientificNameGroup
	 * document type.
	 */
	public static final DocumentType<ScientificNameGroup_old> SCIENTIFIC_NAME_GROUP;

	private static final DocumentType<?>[] all;

	static {

		SPECIMEN = new DocumentType<>(Specimen.class);
		TAXON = new DocumentType<>(Taxon.class);
		MULTI_MEDIA_OBJECT = new DocumentType<>(MultiMediaObject.class);
		GEO_AREA = new DocumentType<>(GeoArea.class);
		SCIENTIFIC_NAME_GROUP = new DocumentType<>("ScientificNameGroup", ScientificNameGroup_old.class);

		all = new DocumentType[] { SPECIMEN, TAXON, MULTI_MEDIA_OBJECT, GEO_AREA,
				SCIENTIFIC_NAME_GROUP };

		try {
			for (ConfigObject cfg : getIndexSections()) {
				/*
				 * This will set the new IndexInfo instance on the applicable
				 * DocumentType instances defined above, and add the applicable
				 * DocumentType instances to the new IndexInfo instance.
				 */
				@SuppressWarnings("unused")
				IndexInfo dummy = new IndexInfo(cfg);
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

		for (DocumentType<?> one : all) {
			if (one.getIndexInfo() == null) {
				Logger logger = DaoRegistry.getInstance().getLogger(DocumentType.class);
				String fmt = "Missing configurarion for docment type %s in %s";
				String msg = String.format(fmt, one.name, DaoRegistry.CONFIG_FILE_NAME);
				logger.fatal(msg);
				throw new InitializationException(msg);
			}
		}

	}

	/**
	 * Returns a {@code DocumentType} instance for the specified name
	 */
	public static DocumentType<?> forName(String name)
	{
		for (DocumentType<?> dt : all) {
			if (dt.name.equalsIgnoreCase(name)) {
				return dt;
			}
		}
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
		for (DocumentType<?> dt : all) {
			if (dt.javaType == cls) {
				return dt;
			}
		}
		throw new DaoException("No document type corresponding to " + cls);
	}

	/**
	 * Returns all document types managed by and accessible through the NBA.
	 * 
	 * @return
	 */
	public static DocumentType<?>[] getAllDocumentTypes()
	{
		return all;
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
	 * Returns a {@link Mapping mapping object} representing the document type
	 * mapping.
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
		String defaultNumShards = cfg.get("elasticsearch.index.default.shards");
		String defaultNumReplicas = cfg.get("elasticsearch.index.default.replicas");
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
			if (defaultNumShards != null) {
				section.set("defaultNumShards", defaultNumShards);
			}
			if (defaultNumReplicas != null) {
				section.set("defaultNumReplicas", defaultNumReplicas);
			}
			sections.add(section);
		}
		if (sections.size() == 0) {
			String msg = "Missing required section \"elasticsearch.index.0\"";
			throw new InitializationException(msg);
		}
		return sections;
	}
}
