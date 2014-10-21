package nl.naturalis.nda.elasticsearch.load;

import nl.naturalis.nda.elasticsearch.client.Index;
import nl.naturalis.nda.elasticsearch.client.IndexNative;

import org.domainobject.util.StringUtil;
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
		Index index = new IndexNative(LoadUtil.getDefaultClient(), DEFAULT_NDA_INDEX_NAME);
		NDASchemaManager nsm = new NDASchemaManager(index);
		nsm.bootstrap();
	}

	/**
	 * The default name of the ElasticSearch index for the Naturalis Data API.
	 */
	public static final String DEFAULT_NDA_INDEX_NAME = "nda";
	public static final String LUCENE_TYPE_TAXON = "Taxon";
	public static final String LUCENE_TYPE_SPECIMEN = "Specimen";
	public static final String LUCENE_TYPE_MULTIMEDIA_OBJECT = "MultiMediaObject";

	private static final Logger logger = LoggerFactory.getLogger(NDASchemaManager.class);

	private final Index index;


	public NDASchemaManager(Index index)
	{
		this.index = index;
	}


	/**
	 * Creates the NDA schema from scratch. Will delete the entire index
	 * (mappings and documents) and then re-create it. !!! WATCH OUT !!!
	 */
	public void bootstrap()
	{
		index.delete();
		String settings = StringUtil.getResourceAsString("/es-settings.json");
		index.create(settings);
		String mapping = StringUtil.getResourceAsString("/es-mappings/Taxon.json");
		index.addType(LUCENE_TYPE_TAXON, mapping);
		mapping = StringUtil.getResourceAsString("/es-mappings/Specimen.json");
		index.addType(LUCENE_TYPE_SPECIMEN, mapping);
		mapping = StringUtil.getResourceAsString("/es-mappings/MultiMediaObject.json");
		index.addType(LUCENE_TYPE_MULTIMEDIA_OBJECT, mapping);
		logger.info(index.describe());
	}
}
