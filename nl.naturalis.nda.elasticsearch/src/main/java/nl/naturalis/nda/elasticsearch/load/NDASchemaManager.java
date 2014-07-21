package nl.naturalis.nda.elasticsearch.load;

import java.net.URL;

import nl.naturalis.nda.elasticsearch.client.Index;
import nl.naturalis.nda.elasticsearch.client.IndexNative;

import org.domainobject.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for creating/managing/updating the ElasticSearch index for the
 * NDA.
 * 
 * @author ayco_holleman
 * 
 */
public class NDASchemaManager {

	public static void main(String[] args)
	{
		Index index = new IndexNative(DEFAULT_NDA_INDEX_NAME);
		NDASchemaManager nsm = new NDASchemaManager(index);
		nsm.bootstrap();
	}

	/**
	 * The default name of the ElasticSearch index for the Naturalis Data API.
	 */
	public static final String DEFAULT_NDA_INDEX_NAME = "nda";

	private static final Logger logger = LoggerFactory.getLogger(NDASchemaManager.class);

	private final Index index;


	public NDASchemaManager(Index index)
	{
		this.index = index;
	}


	/**
	 * Creates the NDA schema from scratch. Will delete the entire index
	 * (mappings and documents) and then re-create it. WATCH OUT!
	 */
	public void bootstrap()
	{
		logger.info("Bootstrapping NDA index!");
		try {
			index.delete();
			index.create();
			URL url = NDASchemaManager.class.getResource("/es-mappings/specimen.type.json");
			String mappings = FileUtil.getContents(url);
			index.addType("specimen", mappings);
			logger.info(index.describe());
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
		finally {
			if (index instanceof IndexNative) {
				((IndexNative) index).getClient().close();
			}
		}
	}
}
