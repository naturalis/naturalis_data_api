package nl.naturalis.nba.dao.es.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.domainobject.util.ConfigObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.common.json.ObjectMapperLocator;
import nl.naturalis.nba.dao.es.Registry;
import nl.naturalis.nba.dao.es.exception.DaoException;
import nl.naturalis.nba.dao.es.exception.InitializationException;
import nl.naturalis.nba.dao.es.map.Mapping;
import nl.naturalis.nba.dao.es.map.MappingFactory;
import nl.naturalis.nba.dao.es.types.ESMultiMediaObject;
import nl.naturalis.nba.dao.es.types.ESSpecimen;
import nl.naturalis.nba.dao.es.types.ESTaxon;
import nl.naturalis.nba.dao.es.types.ESType;

public class DocumentType {

	// Must be defined BEFORE the DocumentType constants, because DocumentType
	// constructor does some logging.
	private static final Logger logger = Registry.getInstance().getLogger(DocumentType.class);

	public static final DocumentType SPECIMEN = new DocumentType("Specimen");
	public static final DocumentType TAXON = new DocumentType("Taxon");
	public static final DocumentType MULTI_MEDIA_OBJECT = new DocumentType("MultiMediaObject");

	static {
		try {
			for (ConfigObject cfg : getIndexSections()) {
				// This will set the new IndexInfo instance on the applicable
				// DocumentType instances defined above, and add the applicable
				// DocumentType instances to the new IndexInfo instance.
				new IndexInfo(cfg);
			}
		}
		catch (Throwable t) {
			/*
			 * No point in going on. We log the error and allow the exception to
			 * cause an ExceptionInInitializerError.
			 */
			logger.fatal("Error while retrieving index info", t);
			throw t;
		}
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
		throw new DaoException("There is no document type corresponding to " + cls);
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
		throw new DaoException("No document type exists with name \"" + name + '"');
	}

	IndexInfo indexInfo;

	private final String name;
	private final Class<? extends ESType> esType;
	private final Mapping mapping;
	private final ObjectMapper objectMapper;

	private DocumentType(String name)
	{
		logger.info("Retrieving info for document type {}", name);
		this.name = name;
		this.esType = getClassForDocumentType(name);
		this.mapping = new MappingFactory().getMapping(esType);
		this.objectMapper = ObjectMapperLocator.getInstance().getObjectMapper(esType);
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
		return objectMapper;
	}

	private static Class<? extends ESType> getClassForDocumentType(String type)
	{
		switch (type) {
			case "Specimen":
				return ESSpecimen.class;
			case "Taxon":
				return ESTaxon.class;
			case "MultiMediaObject":
				return ESMultiMediaObject.class;
			default:
				String fmt = "No such document type: \"%s\"";
				String msg = String.format(fmt, type);
				throw new InitializationException(msg);
		}
	}

	private static List<ConfigObject> getIndexSections()
	{
		ConfigObject cfg = Registry.getInstance().getConfiguration();
		List<ConfigObject> sections = new ArrayList<>();
		String cfgFile = Registry.getInstance().getConfigurationFile().getAbsolutePath();
		for (int i = 0;; i++) {
			String prefix = "elasticsearch.index." + i;
			ConfigObject section = cfg.getSection(prefix);
			if (section == null) {
				break;
			}
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
