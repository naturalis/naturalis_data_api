/**
 *  Description: Create a new DwCA export tool for the NBA
 *  Date: January 28th 2015
 *  Developer: Reinier Kartowikromo
 *  
 */
package nl.naturalis.nda.export.dwca;



import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import nl.naturalis.nda.elasticsearch.load.NDAIndexManager;
import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_SPECIMEN;











import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class DwCAExporter
{
	static final Logger logger = LoggerFactory.getLogger(DwCAExporter.class);

	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		
		logger.info("-----------------------------------------------------------------");
		logger.info("Start");
		IndexNative index = new IndexNative(LoadUtil.getESClient(), LoadUtil.getConfig().required("elasticsearch.index.name"));
		try
		{
			DwCAExporter exp = new DwCAExporter(index);
		    exp.ExportDwca();
		} finally
		{
			index.getClient().close();
		}
		
		
		logger.info("Ready");
	}
	
	
	
	public DwCAExporter(IndexNative index)
	{
		this.index = index;
	}
	
	private final IndexNative index;


	public void ExportDwca() throws IOException
	{
	   Path output = Paths.get("Specimens.txt");
       index.getResultsMap(NDAIndexManager.LUCENE_TYPE_SPECIMEN, 100);
	}

	

}
