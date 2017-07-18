package nl.naturalis.nba.dao.format.dwca;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXParseException;

import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.dao.exception.DaoException;
import nl.naturalis.nba.dao.format.DataSetConfigurationException;
import nl.naturalis.nba.dao.format.Entity;
import nl.naturalis.nba.dao.format.IField;
import nl.naturalis.nba.utils.FileUtil;
import nl.naturalis.nba.utils.xml.XmlFileUpdater;

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

	static void writeEmlXml(DwcaConfig dwcaConfig, ZipOutputStream zos) throws IOException
	{
		logger.info("Adding eml.xml ({})", dwcaConfig.getEmlFile());
		zos.putNextEntry(new ZipEntry("eml.xml"));
		XmlFileUpdater emlUpdater = new XmlFileUpdater(dwcaConfig.getEmlFile());
		try {
			emlUpdater.readFile();
		}
		catch (SAXParseException e) {
			throw new DaoException("Error while parsing EML file", e);
		}
		String now = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		emlUpdater.updateFirstElement("pubDate", now);
		emlUpdater.save(zos);
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
		/*
		 * NB Multiple entities may get written to the same zip entry (e.g. taxa
		 * and synonyms are both written to taxa.txt). We must generate only one
		 * <extension> element per CSV file, NOT one <extension> element per
		 * entity.
		 */
		HashSet<String> fileNames = new HashSet<>();
		fileNames.add(dwcaConfig.getCsvFileName(coreEntity));
		for (Entity entity : dwcaConfig.getDataSet().getEntities()) {
			if (entity.getName().equals(coreEntity.getName())) {
				continue;
			}
			String fileName = dwcaConfig.getCsvFileName(entity);
			if (fileNames.contains(fileName)) {
				continue;
			}
			fileNames.add(fileName);
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
		IField[] entityFields = entity.getFields();
		List<Field> metaXmlFields = new ArrayList<>(entityFields.length);
		for (int i = 0; i < entityFields.length; i++) {
			IField entityField = entityFields[i];
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
