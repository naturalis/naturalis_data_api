package nl.naturalis.nba.dao.es.format;

import java.io.IOException;
import java.io.LineNumberReader;

import nl.naturalis.nba.dao.es.DocumentType;
import nl.naturalis.nba.dao.es.exception.DaoException;
import static nl.naturalis.nba.dao.es.map.ESDataType.*;
import nl.naturalis.nba.dao.es.map.ESField;
import nl.naturalis.nba.dao.es.map.MappingInfo;
import nl.naturalis.nba.dao.es.map.NoSuchFieldException;

/*
 * Reads the configuration file (wrapped by a LineNumberReader), extracts
 * the settings from it and sets them on an EntityConfiguration
 * instance.
 */
class SettingsParser {

	static final char SETTING_START_CHAR = '&';

	private LineNumberReader lnr;
	private DocumentType<?> dt;
	private EntityConfiguration cnf;

	SettingsParser(LineNumberReader lnr, DocumentType<?> dt, EntityConfiguration cnf)
	{
		this.lnr = lnr;
		this.dt = dt;
		this.cnf = cnf;
	}

	void parse() throws EntityConfigurationException
	{
		try {
			for (String line = lnr.readLine(); line != null; line = lnr.readLine()) {
				line = line.trim();
				if (line.length() == 0 || line.charAt(0) != SETTING_START_CHAR) {
					continue;
				}
				String[] chunks = line.split("=");
				if (chunks.length != 2) {
					throw new EntityConfigurationException("Missing delimiter \"=\"");
				}
				String setting = chunks[0].trim().substring(1);
				String value = chunks[1].trim();
				switch (setting) {
					case "entity":
						setPathToEntity(value);
						break;
					default:
						String msg = "Unknown setting: " + setting;
						throw new EntityConfigurationException(msg);
				}
			}
		}
		catch (IOException e) {
			throw new DaoException(e);
		}
	}

	private void setPathToEntity(String path) throws EntityConfigurationException
	{
		if (path.length() == 0)
			return;
		MappingInfo mappingInfo = new MappingInfo(dt.getMapping());
		ESField f;
		try {
			f = mappingInfo.getField(path);
			if (f.getType() != null && f.getType() != NESTED) {
				String fmt = "Invalid path specified for %sentity: %s (object expected)";
				String msg = String.format(fmt, SETTING_START_CHAR, path);
				throw new EntityConfigurationException(msg);
			}
			String[] pathElements = path.split("\\.");
			cnf.setPathToEntity(pathElements);
		}
		catch (NoSuchFieldException e) {
			String fmt = "Invalid path specified for %sentity: %s (%s)";
			String msg = String.format(fmt, SETTING_START_CHAR, path, e.getMessage());
			throw new EntityConfigurationException(msg);
		}
	}

}
