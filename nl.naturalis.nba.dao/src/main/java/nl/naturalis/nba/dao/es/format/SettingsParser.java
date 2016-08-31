package nl.naturalis.nba.dao.es.format;

import static nl.naturalis.nba.dao.es.map.ESDataType.NESTED;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import org.domainobject.util.IOUtil;

import nl.naturalis.nba.dao.es.DocumentType;
import nl.naturalis.nba.dao.es.exception.DaoException;
import nl.naturalis.nba.dao.es.map.ESField;
import nl.naturalis.nba.dao.es.map.Mapping;
import nl.naturalis.nba.dao.es.map.MappingInfo;
import nl.naturalis.nba.dao.es.map.NoSuchFieldException;

/**
 * Parses those lines in an entity configuration file (wrapped into a
 * {@link LineNumberReader} that specify general configuration settings (rather
 * than fields).
 * 
 * @author Ayco Holleman
 *
 */
class SettingsParser {

	static final char SETTING_START_CHAR = '&';

	private File entityConfigFile;

	SettingsParser(File entityConfigFile)
	{
		this.entityConfigFile = entityConfigFile;
	}

	void parse(EntityConfiguration dse) throws EntityConfigurationException
	{
		setDocumentType(dse);
		setOtherSettings(dse);
	}

	private void setDocumentType(EntityConfiguration dse) throws EntityConfigurationException
	{
		LineNumberReader lnr = getLineNumberReader();
		try {
			LOOP: for (String line = lnr.readLine(); line != null; line = lnr.readLine()) {
				line = line.trim();
				if (line.length() == 0 || line.charAt(0) != SETTING_START_CHAR) {
					continue;
				}
				String[] chunks = line.split("=");
				if (chunks.length != 2) {
					throw new EntityConfigurationException("Missing delimiter \"=\"");
				}
				String setting = chunks[0].substring(1).trim();
				if (setting.equals("document")) {
					try {
						DocumentType<?> dt = DocumentType.forName(chunks[1].trim());
						dse.setDocumentType(dt);
					}
					catch (DaoException e) {
						throw new EntityConfigurationException(e.getMessage());
					}
					break LOOP;
				}
			}
		}
		catch (IOException e) {
			throw new EntityConfigurationException(e);
		}
		finally {
			IOUtil.close(lnr);
		}
	}

	void setOtherSettings(EntityConfiguration dse) throws EntityConfigurationException
	{
		LineNumberReader lnr = getLineNumberReader();
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
				String setting = chunks[0].substring(1).trim();
				String value = chunks[1].trim();
				switch (setting) {
					case "document":
						break;
					case "entity":
						setPathToEntity(dse, value);
						break;
					default:
						String msg = "Unknown setting: " + setting;
						throw new EntityConfigurationException(msg);
				}
			}
		}
		catch (IOException e) {
			throw new EntityConfigurationException(e);
		}
		finally {
			IOUtil.close(lnr);
		}
	}

	private static void setPathToEntity(EntityConfiguration dse, String path) throws EntityConfigurationException
	{
		if (path.length() == 0)
			return;
		String[] pathElements = path.split("\\.");
		dse.setPathToEntity(pathElements);
		if (dse.getDocumentType() != null) {
			Mapping mapping = dse.getDocumentType().getMapping();
			MappingInfo mappingInfo = new MappingInfo(mapping);
			ESField f;
			try {
				f = mappingInfo.getField(path);
				if (f.getType() != null && f.getType() != NESTED) {
					String fmt = "Invalid path specified for %sentity: %s (object expected)";
					String msg = String.format(fmt, SETTING_START_CHAR, path);
					throw new EntityConfigurationException(msg);
				}
			}
			catch (NoSuchFieldException e) {
				String fmt = "Invalid path specified for %sentity: %s (%s)";
				String msg = String.format(fmt, SETTING_START_CHAR, path, e.getMessage());
				throw new EntityConfigurationException(msg);
			}
		}
	}

	private LineNumberReader getLineNumberReader() throws EntityConfigurationException
	{
		try {
			return new LineNumberReader(new FileReader(entityConfigFile));
		}
		catch (FileNotFoundException e) {
			String fmt = "Entity configuration file not found: %s";
			String msg = String.format(fmt, entityConfigFile.getAbsolutePath());
			throw new EntityConfigurationException(msg);
		}
	}

}
