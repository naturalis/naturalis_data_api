package nl.naturalis.nba.dao.es;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.domainobject.util.ConfigObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.common.es.map.Mapping;
import nl.naturalis.nba.common.es.map.MappingFactory;
import nl.naturalis.nba.common.json.ObjectMapperLocator;
import nl.naturalis.nba.dao.es.exception.DaoException;
import nl.naturalis.nba.dao.es.exception.InitializationException;
import nl.naturalis.nba.dao.es.types.ESGeoArea;
import nl.naturalis.nba.dao.es.types.ESMultiMediaObject;
import nl.naturalis.nba.dao.es.types.ESSpecimen;
import nl.naturalis.nba.dao.es.types.ESTaxon;
import nl.naturalis.nba.dao.es.types.ESType;

/**
 * An enumeration of the four document types managed by the NBA. Each
 * {@code DocumentType} instance functions as a little cache of oft-used,
 * potentially heavy-weight objects associated with the document type. For
 * example, although you can easily create {@link Mapping} objects yourself, it
 * is recommendable to {@link #getMapping() request} them from the appropriate
 * {@code DocumentType} instance.
 * 
 * @author Ayco Holleman
 *
 */
public class DocumentType<T extends ESType> {

	public static final DocumentType<ESSpecimen> SPECIMEN;
	public static final DocumentType<ESTaxon> TAXON;
	public static final DocumentType<ESMultiMediaObject> MULTI_MEDIA_OBJECT;
	public static final DocumentType<ESGeoArea> GEO_AREA;

	static {

		SPECIMEN = new DocumentType<>(ESSpecimen.class);
		TAXON = new DocumentType<>(ESTaxon.class);
		MULTI_MEDIA_OBJECT = new DocumentType<>(ESMultiMediaObject.class);
		GEO_AREA = new DocumentType<>(ESGeoArea.class);

		try {
			for (ConfigObject cfg : getIndexSections()) {
				/*
				 * This will set the new IndexInfo instance on the applicable
				 * DocumentType instances defined above, and add the applicable
				 * DocumentType instances to the new IndexInfo instance.
				 */
				@SuppressWarnings("unused")
				IndexInfo indexInfo = new IndexInfo(cfg);
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
	 * Returns the {@code DocumentType} instance for the specified document
	 * type.
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
	 * Elasticsearch model object,
	 */
	public static DocumentType<?> forClass(Class<? extends ESType> cls)
	{
		if (SPECIMEN.esType == cls)
			return SPECIMEN;
		if (TAXON.esType == cls)
			return TAXON;
		if (MULTI_MEDIA_OBJECT.esType == cls)
			return MULTI_MEDIA_OBJECT;
		if (GEO_AREA.esType == cls)
			return GEO_AREA;
		throw new DaoException("No document type corresponding to " + cls);
	}

	IndexInfo indexInfo;

	private final Logger logger;
	private final String name;
	private final Class<T> esType;
	private final ObjectMapper objMapper;
	private final Mapping mapping;

	private DocumentType(Class<T> esType)
	{
		this(esType.getSimpleName().substring(2), esType);
	}

	private DocumentType(String name, Class<T> esType)
	{
		logger = DaoRegistry.getInstance().getLogger(DocumentType.class);
		logger.info("Retrieving info for document type {}", name);
		this.name = name;
		this.esType = esType;
		this.mapping = MappingFactory.getMapping(esType);
		ObjectMapperLocator oml = ObjectMapperLocator.getInstance();
		this.objMapper = oml.getObjectMapper(esType);
	}

	public String getName()
	{
		return name;
	}

	public IndexInfo getIndexInfo()
	{
		return indexInfo;
	}

	public Class<T> getESType()
	{
		return esType;
	}

	public Mapping getMapping()
	{
		return mapping;
	}

	public ObjectMapper getObjectMapper()
	{
		return objMapper;
	}

	@Override
	public boolean equals(Object obj)
	{
		return this == obj;
	}

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
