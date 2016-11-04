package nl.naturalis.nba.etl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.Client;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.common.json.ObjectMapperLocator;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.etl.elasticsearch.IndexManagerNative;

/**
 * Class providing centralized access to core services such as logging and
 * elasticsearch. If anything goes wrong while configuring those services an
 * {@link InitializationException} is thrown and it probably doesn't make much
 * sense to let the program continue. Therefore one of the first things an
 * import program should do is retrieve an instance of the {@code Registry}
 * class.
 * 
 * @author Ayco Holleman
 *
 */
public class ETLRegistry {

	private static ETLRegistry instance;

	/**
	 * Return a {@code Registry} instance. Will call {@link #initialize()}
	 * first.
	 * 
	 * @return A {@code Registry} instance.
	 */
	public static ETLRegistry getInstance()
	{
		if (instance == null) {
			instance = new ETLRegistry();
		}
		return instance;
	}

	private ETLRegistry()
	{
	}

	/**
	 * Get a logger for the specified class.
	 * 
	 * @param cls
	 * @return
	 */
	public Logger getLogger(Class<?> cls)
	{
		/*
		 * We currently do nothing special to set up or hand out loggers. But
		 * given the constantly recurring configuration hazzles of whatever
		 * logging library you use, all classes must get their logger through
		 * this method, in stead of calling LogManager.getLogger directly. This
		 * way any special logging requirements turning up later, can be coded
		 * for in one place (here).
		 */
		return LogManager.getLogger(cls);
	}

	/**
	 * Get an index manager for the NBA index.
	 * 
	 * @return
	 */
	public IndexManagerNative getIndexManager(DocumentType documentType)
	{
		Client client = ESClientManager.getInstance().getClient();
		String index = documentType.getIndexInfo().getName();
		IndexManagerNative idxMgr = new IndexManagerNative(client, index);
		ObjectMapperLocator oml = ObjectMapperLocator.getInstance();
		ObjectMapper om = oml.getObjectMapper(documentType.getJavaType());
		idxMgr.setObjectMapper(om);
		return new IndexManagerNative(client, index);
	}

}
