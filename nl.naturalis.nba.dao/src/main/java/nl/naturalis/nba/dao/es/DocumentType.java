package nl.naturalis.nba.dao.es;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.domainobject.util.ConfigObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.common.json.ObjectMapperLocator;
import nl.naturalis.nba.dao.es.exception.DaoException;
import nl.naturalis.nba.dao.es.exception.InitializationException;
import nl.naturalis.nba.dao.es.map.Mapping;
import nl.naturalis.nba.dao.es.map.MappingFactory;
import nl.naturalis.nba.dao.es.types.ESGeoArea;
import nl.naturalis.nba.dao.es.types.ESMultiMediaObject;
import nl.naturalis.nba.dao.es.types.ESSpecimen;
import nl.naturalis.nba.dao.es.types.ESTaxon;
import nl.naturalis.nba.dao.es.types.ESType;

public enum DocumentType
{

	SPECIMEN("Specimen", ESSpecimen.class),
	TAXON("Taxon", ESTaxon.class),
	MULTI_MEDIA_OBJECT("MultiMediaObject", ESMultiMediaObject.class),
	GEO_AREA("GeoArea", ESGeoArea.class);

	static {
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
			Logger logger = DAORegistry.getInstance().getLogger(DocumentType.class);
			logger.fatal("Error while retrieving index info", t);
			throw t;
		}
	}

	public static DocumentType forName(String name)
	{
		if (SPECIMEN.name.equals(name)) {
			return SPECIMEN;
		}
		if (TAXON.name.equals(name)) {
			return TAXON;
		}
		if (MULTI_MEDIA_OBJECT.name.equals(name)) {
			return MULTI_MEDIA_OBJECT;
		}
		if (GEO_AREA.name.equals(name)) {
			return GEO_AREA;
		}
		throw new DaoException("There is no document type with name \"" + name + '"');
	}

	public static DocumentType forClass(Class<? extends ESType> cls)
	{
		if (SPECIMEN.esType == cls) {
			return SPECIMEN;
		}
		if (TAXON.esType == cls) {
			return TAXON;
		}
		if (MULTI_MEDIA_OBJECT.esType == cls) {
			return MULTI_MEDIA_OBJECT;
		}
		if (GEO_AREA.esType == cls) {
			return GEO_AREA;
		}
		throw new DaoException("There is no document type corresponding to " + cls);
	}

	IndexInfo indexInfo;

	private final Logger logger;
	private final String name;
	private final Class<? extends ESType> esType;
	private final ObjectMapper objMapper;
	private final Mapping mapping;

	private DocumentType(String name, Class<? extends ESType> esType)
	{
		logger = DAORegistry.getInstance().getLogger(DocumentType.class);
		logger.info("Retrieving info for document type {}", name);
		this.name = name;
		this.esType = esType;
		this.mapping = MappingFactory.getMapping(esType);
		this.objMapper = ObjectMapperLocator.getInstance().getObjectMapper(esType);
	}

	public String getName()
	{
		return name;
	}

	public IndexInfo getIndexInfo()
	{
		return indexInfo;
	}

	public Class<? extends ESType> getESType()
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
	public String toString()
	{
		return name;
	}

	private static List<ConfigObject> getIndexSections()
	{
		DAORegistry registry = DAORegistry.getInstance();
		ConfigObject cfg = registry.getConfiguration();
		List<ConfigObject> sections = new ArrayList<>();
		for (int i = 0;; i++) {
			String prefix = "elasticsearch.index." + i;
			ConfigObject section = cfg.getSection(prefix);
			if (section == null) {
				break;
			}
			Logger logger = DAORegistry.getInstance().getLogger(DocumentType.class);
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
