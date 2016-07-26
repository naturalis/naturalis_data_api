package nl.naturalis.nba.dao.es.dwca;

import java.io.File;

import org.domainobject.util.FileUtil;

import nl.naturalis.nba.dao.es.DAORegistry;
import nl.naturalis.nba.dao.es.DocumentType;

public class DwcaUtil {

	private DwcaUtil()
	{
	}

	static File getFieldsConfigFile(DocumentType dt, String setType)
	{
		File nbaConfDir = DAORegistry.getInstance().getConfigurationDirectory();
		String path = dt.toString().toLowerCase() + '/' + setType + "/fields.config";
		return FileUtil.newFile(nbaConfDir, path);
	}

}
