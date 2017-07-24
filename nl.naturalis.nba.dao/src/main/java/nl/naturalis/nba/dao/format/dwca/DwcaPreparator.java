package nl.naturalis.nba.dao.format.dwca;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXParseException;

import nl.naturalis.nba.dao.exception.DaoException;
import nl.naturalis.nba.dao.format.DataSetConfigurationException;
import nl.naturalis.nba.dao.format.DataSetWriteException;
import nl.naturalis.nba.dao.format.Entity;
import nl.naturalis.nba.dao.format.IField;
import nl.naturalis.nba.utils.xml.XmlFileUpdater;

/**
 * This class is used to prepare the generation of a DarwinCore archive
 * <i>before</i> actually sending the archive to client. Once we start writing
 * bytes to the HTTP outputstream we have basically lost the ability to inform
 * the client of anything that goes wrong from that point on. This turns out to
 * be rather frustrating, because we have no clue why we end up with an empty or
 * corrupt zip file. By preparing and checking as much as possible beforehand
 * (through this class) we provide for better error reporting to the client.
 * 
 * @author Ayco Holleman
 *
 */
class DwcaPreparator {

	private static final Logger logger = getLogger(DwcaPreparator.class);

	private DwcaConfig cfg;

	private ByteArrayOutputStream emlBuffer;
	private ByteArrayOutputStream metaXmlBuffer;

	DwcaPreparator(DwcaConfig cfg)
	{
		this.cfg = cfg;
	}

	/**
	 * Makes as many as possible preparations for generating the DwC archive.
	 * Call this method before commiting any bytes to the HTTP output stream.
	 * 
	 * @throws DataSetWriteException
	 * @throws DataSetConfigurationException
	 */
	void prepare() throws DataSetWriteException, DataSetConfigurationException
	{
		prepareEml();
		prepareMetaXml();
	}

	byte[] getEml()
	{
		return emlBuffer.toByteArray();
	}

	byte[] getMetaXml()
	{
		return metaXmlBuffer.toByteArray();
	}

	private void prepareEml() throws DataSetWriteException
	{
		logger.info("Preparing eml.xml");
		File f = cfg.getEmlFile();
		if (!f.isFile()) {
			throw new DataSetWriteException("Missing EML file: " + f.getAbsolutePath());
		}
		XmlFileUpdater emlUpdater = new XmlFileUpdater(f);
		try {
			emlUpdater.readFile();
		}
		catch (SAXParseException e) {
			throw new DaoException("Error while parsing EML file", e);
		}
		String now = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		emlUpdater.updateFirstElement("pubDate", now);
		emlBuffer = new ByteArrayOutputStream(8192);
		emlUpdater.save(emlBuffer);
	}

	private void prepareMetaXml() throws DataSetConfigurationException
	{
		logger.info("Preparing meta.xml");
		Archive archive = new Archive();
		Core core = new Core();
		archive.setCore(core);
		Entity coreEntity = cfg.getCoreEntity();
		core.setFiles(new Files(cfg.getCsvFileName(coreEntity)));
		core.setRowType(cfg.getRowtype(coreEntity));
		core.setFields(getMetaXmlFieldsForEntity(coreEntity));
		/*
		 * NB Multiple entities may get written to the same zip entry (e.g. taxa
		 * and synonyms are both written to taxa.txt). We must generate only one
		 * <extension> element per CSV file, NOT one <extension> element per
		 * entity.
		 */
		HashSet<String> fileNames = new HashSet<>();
		fileNames.add(cfg.getCsvFileName(coreEntity));
		for (Entity entity : cfg.getDataSet().getEntities()) {
			if (entity.getName().equals(coreEntity.getName())) {
				continue;
			}
			String fileName = cfg.getCsvFileName(entity);
			if (fileNames.contains(fileName)) {
				continue;
			}
			fileNames.add(fileName);
			Extension extension = new Extension();
			extension.setFiles(new Files(cfg.getCsvFileName(entity)));
			extension.setRowType(cfg.getRowtype(entity));
			extension.setFields(getMetaXmlFieldsForEntity(entity));
			archive.addExtension(extension);
		}
		MetaXmlWriter metaXmlWriter = new MetaXmlWriter(archive);
		metaXmlBuffer = new ByteArrayOutputStream(8192);
		metaXmlWriter.write(metaXmlBuffer);
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
