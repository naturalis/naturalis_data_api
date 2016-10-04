package nl.naturalis.nba.dao.es.format.dwca;

import static nl.naturalis.nba.dao.es.DaoUtil.getLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.Logger;
import org.domainobject.util.FileUtil;
import org.domainobject.util.IOUtil;

import nl.naturalis.nba.dao.es.DaoRegistry;
import nl.naturalis.nba.dao.es.format.DataSetConfigurationException;
import nl.naturalis.nba.dao.es.format.Entity;
import nl.naturalis.nba.dao.es.format.IField;

/**
 * Utility class for the DwCA creation process.
 * 
 * @author Ayco Holleman
 *
 */
public class DwcaUtil {

	private static Logger logger = getLogger(DwcaUtil.class);

	private DwcaUtil()
	{
	}

	/**
	 * Returns the directory containing the configuration files for the
	 * specified type of data sets.
	 */
	public static File getDwcaConfigurationDirectory(DwcaDataSetType dataSetType)
	{
		File root = DaoRegistry.getInstance().getConfigurationDirectory();
		String dirName = dataSetType.name().toLowerCase();
		return FileUtil.newFile(root, "dwca/" + dirName);
	}

	static void writeEmlXml(DwcaConfig dwcaConfig, ZipOutputStream zos)
			throws DataSetConfigurationException, IOException
	{
		logger.info("Adding eml.xml ({})", dwcaConfig.getEmlFile());
		zos.putNextEntry(new ZipEntry("eml.xml"));
		FileInputStream fis = null;
		fis = new FileInputStream(dwcaConfig.getEmlFile());
		IOUtil.pipe(fis, zos, 2048);
		fis.close();
	}

	static void writeMetaXml(DwcaConfig dwcaConfig, ZipOutputStream zos)
			throws DataSetConfigurationException, IOException
	{
		logger.info("Adding meta.xml");
		zos.putNextEntry(new ZipEntry("meta.xml"));
		Archive archive = new Archive();
		Core core = new Core();
		archive.setCore(core);
		Entity coreEntity = dwcaConfig.getCoreEntity();
		core.setFiles(new Files(dwcaConfig.getCsvFileName(coreEntity)));
		core.setRowType(dwcaConfig.getRowtype(coreEntity));
		core.setFields(getMetaXmlFieldsForEntity(coreEntity));
		for (Entity entity : dwcaConfig.getDataSet().getEntities()) {
			if (entity.getName().equals(coreEntity.getName()))
				continue;
			Extension extension = new Extension();
			extension.setFiles(new Files(dwcaConfig.getCsvFileName(entity)));
			extension.setRowType(dwcaConfig.getRowtype(entity));
			extension.setFields(getMetaXmlFieldsForEntity(entity));
			archive.addExtension(extension);
		}
		MetaXmlWriter metaXmlWriter = new MetaXmlWriter(archive);
		metaXmlWriter.write(zos);
		zos.flush();
	}

	private static List<Field> getMetaXmlFieldsForEntity(Entity entity)
			throws DataSetConfigurationException
	{
		List<IField> entityFields = entity.getFields();
		List<Field> metaXmlFields = new ArrayList<>(entityFields.size());
		for (int i = 0; i < entityFields.size(); i++) {
			IField entityField = entityFields.get(i);
			URI term = entityField.getTerm();
			if (term == null) {
				String fmt = "Entity %s, field %s: term attribute required for DwCA files";
				String msg = String.format(fmt, entity.getName(), entityField.getName());
				throw new DataSetConfigurationException(msg);
			}
			metaXmlFields.add(new Field(i, term));
		}
		return metaXmlFields;
	}

}
