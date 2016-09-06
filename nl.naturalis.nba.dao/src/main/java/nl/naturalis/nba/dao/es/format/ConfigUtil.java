package nl.naturalis.nba.dao.es.format;

import static nl.naturalis.nba.dao.es.format.config.SourceTypeXmlConfig.DOCUMENT;
import static nl.naturalis.nba.dao.es.format.config.SourceTypeXmlConfig.GENERIC;

import nl.naturalis.nba.common.es.map.Mapping;
import nl.naturalis.nba.common.es.map.MappingFactory;
import nl.naturalis.nba.dao.es.DocumentType;
import nl.naturalis.nba.dao.es.exception.DaoException;
import nl.naturalis.nba.dao.es.format.config.SourceXmlConfig;

class ConfigUtil {

	private ConfigUtil()
	{
	}
	
	static Mapping getSource(SourceXmlConfig sxc) throws DataSetConfigurationException
	{
		if (sxc == null || sxc.getType() == GENERIC) {
			return null;
		}
		if (sxc.getType() == null || sxc.getType() == DOCUMENT) {
			try {
				DocumentType<?> dt = DocumentType.forName(sxc.getValue());
				return dt.getMapping();
			}
			catch (DaoException e) {
				String msg = "Invalid value in <source> element. Please specify "
						+ "a valid Elasticsearch document type";
				throw new DataSetConfigurationException(msg);
			}
		}
		try {
			Class<?> cls = Class.forName(sxc.getValue());
			return MappingFactory.getMapping(cls);
		}
		catch (ClassNotFoundException e) {
			String msg = "Invalid value in <source> element. Please specify "
					+ "a valid java class";
			throw new DataSetConfigurationException(msg);
		}
	}	

}
