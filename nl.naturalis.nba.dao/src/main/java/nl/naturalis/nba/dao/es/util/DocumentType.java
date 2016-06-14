package nl.naturalis.nba.dao.es.util;

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

	public static final DocumentType SPECIMEN = new DocumentType("Specimen");
	public static final DocumentType TAXON = new DocumentType("Taxon");
	public static final DocumentType MULTI_MEDIA_OBJECT = new DocumentType("MultiMediaObject");

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

	private final String name;
	private final IndexInfo indexInfo;
	private final Class<? extends ESType> esType;
	private final Mapping mapping;
	private final ObjectMapper objectMapper;

	private DocumentType(String name)
	{
		this.name = name;
		this.indexInfo = getIndexInfoForDocumentType(name);
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

	private static IndexInfo getIndexInfoForDocumentType(String documentType)
	{
		String section = "elasticsearch.index." + documentType;
		ConfigObject cfg = Registry.getInstance().getConfiguration().getSection(section);
		if (cfg == null) {
			section = "elasticsearch.index.default";
			cfg = Registry.getInstance().getConfiguration().getSection(section);
		}
		if (cfg == null) {
			String fmt = "Missing section \"elasticsearch.index.default\" in %s";
			String msg = String.format(fmt, Registry.CONFIG_FILE_NAME);
			throw new InitializationException(msg);
		}
		return new IndexInfo(cfg);
	}

	private static Class<? extends ESType> getClassForDocumentType(String documentType)
	{
		switch (documentType) {
			case "Specimen":
				return ESSpecimen.class;
			case "Taxon":
				return ESTaxon.class;
			case "MultiMediaObject":
				return ESMultiMediaObject.class;
			default:
				String fmt = "No such document type: \"%s\"";
				String msg = String.format(fmt, documentType);
				throw new InitializationException(msg);
		}
	}
}
