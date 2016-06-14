package nl.naturalis.nba.dao.es.util;

import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.compress.utils.Charsets;
import org.domainobject.util.ConfigObject;

import nl.naturalis.nba.dao.es.Registry;
import nl.naturalis.nba.dao.es.exception.DaoException;
import nl.naturalis.nba.dao.es.types.ESMultiMediaObject;
import nl.naturalis.nba.dao.es.types.ESSpecimen;
import nl.naturalis.nba.dao.es.types.ESTaxon;
import nl.naturalis.nba.dao.es.types.ESType;

public class ESUtil {

	private ESUtil()
	{
	}

	public static String base64Encode(String s)
	{
		byte[] bytes = s.getBytes(Charsets.UTF_8);
		bytes = Base64.getEncoder().encode(bytes);
		return new String(bytes, Charsets.UTF_8);
	}

	public static Set<IndexInfo> getDistinctIndices()
	{
		Set<IndexInfo> result = new HashSet<>(3);
		result.add(DocumentType.SPECIMEN.getIndexInfo());
		result.add(DocumentType.TAXON.getIndexInfo());
		result.add(DocumentType.MULTI_MEDIA_OBJECT.getIndexInfo());
		return result;
	}

	/**
	 * Returns the name of the Elasticsearch document type corresponding to the
	 * specified Java type.
	 * 
	 * @param cls
	 * @return
	 */
	public static String getDocumentTypeName(Class<? extends ESType> cls)
	{
		if (cls == ESSpecimen.class) {
			return "Specimen";
		}
		if (cls == ESTaxon.class) {
			return "Taxon";
		}
		if (cls == ESMultiMediaObject.class) {
			return "MultiMediaObject";
		}
		String fmt = "There is no Elasticsearch document type corresponding to class %s";
		String msg = String.format(fmt, cls.getName());
		throw new DaoException(msg);
	}

	/**
	 * Returns an Elasticsearch {@link IndexInfo} instance for the specified
	 * class.
	 * 
	 * @param cls
	 * @return
	 */
	public static IndexInfo getIndexInfoForClass(Class<? extends ESType> cls)
	{
		String esName = getDocumentTypeName(cls);
		return getIndexInfoForDocumentType(esName);
	}

	/**
	 * Returns an Elasticsearch {@link IndexInfo} instance for the specified
	 * document type.
	 * 
	 * @param documentType
	 * @return
	 */
	public static IndexInfo getIndexInfoForDocumentType(String documentType)
	{
		String section = "elasticsearch.index." + documentType;
		ConfigObject cfg = Registry.getInstance().getConfiguration().getSection(section);
		if (cfg == null) {
			section = "elasticsearch.index.default";
			cfg = Registry.getInstance().getConfiguration().getSection(section);
		}
		return new IndexInfo(cfg);
	}

	/**
	 * Returns the class corresponding to the specified Elasticsearch document
	 * type.
	 * 
	 * @param esType
	 * @return
	 */
	public static Class<? extends ESType> getClassForDocumentType(String esType)
	{
		switch (esType) {
			case "Specimen":
				return ESSpecimen.class;
			case "Taxon":
				return ESTaxon.class;
			case "MultiMediaObject":
				return ESMultiMediaObject.class;
			default:
				String fmt = "There is no Elasticsearch document type named \"%s\"";
				String msg = String.format(fmt, esType);
				throw new DaoException(msg);
		}
	}

}
