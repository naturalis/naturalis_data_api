package nl.naturalis.nba.dao.es.format;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import nl.naturalis.nba.dao.es.DocumentType;
import nl.naturalis.nba.dao.es.exception.DaoException;
import nl.naturalis.nba.dao.es.format.config.DataSetXmlConfig;
import nl.naturalis.nba.dao.es.format.config.DataSetsXmlConfig;

public class DataSetCollectionBuilder {

	private File configFile;
	private IDataSetFieldFactory fieldFactory;

	public DataSetCollectionBuilder(File configFile, IDataSetFieldFactory fieldFactory)
	{
		this.configFile = configFile;
		this.fieldFactory = fieldFactory;
	}

	public DataSetCollection build() throws DataSetConfigurationException
	{
		DataSetsXmlConfig root = parseConfigFile();
		DataSetCollection collection = new DataSetCollection();
		collection.setDocumentType(getDocumentType(root));
		for (DataSetXmlConfig dsxc : root.getDatasets()) {
			collection.addDataSet(new DataSetBuilder(dsxc).build());
		}
		return collection;
	}

	private static DocumentType<?> getDocumentType(DataSetsXmlConfig root)
			throws DataSetConfigurationException
	{
		if (root.getSource() == null)
			return null;
		try {
			return DocumentType.forName(root.getSource());
		}
		catch (DaoException e) {
			String msg = "Invalid value in <source> element. Please specify "
					+ "a valid Elasticsearch document type";
			throw new DataSetConfigurationException(msg);
		}
	}

	private DataSetsXmlConfig parseConfigFile() throws DataSetConfigurationException
	{
		if (!configFile.isFile()) {
			String msg = "Missing configuration file " + configFile.getPath();
			throw new DataSetConfigurationException(msg);
		}
		try {
			JAXBContext ctx = JAXBContext.newInstance(DataSetsXmlConfig.class);
			Unmarshaller unmarshaller = ctx.createUnmarshaller();
			DataSetsXmlConfig root;
			root = (DataSetsXmlConfig) unmarshaller.unmarshal(configFile);
			return root;
		}
		catch (JAXBException e) {
			throw new DataSetConfigurationException(e);
		}
	}

}
