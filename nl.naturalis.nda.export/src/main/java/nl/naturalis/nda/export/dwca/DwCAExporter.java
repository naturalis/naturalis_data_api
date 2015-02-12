/**
 *  Description: Create a new DwCA export tool for the NBA
 *  Date: January 28th 2015
 *  Developer: Reinier Kartowikromo
 *  
 */
package nl.naturalis.nda.export.dwca;



import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_SPECIMEN;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;

import org.domainobject.util.debug.BeanPrinter;
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
		FileOutputStream fos = new FileOutputStream("C:/tmp/taxa.txt");
		PrintWriter pw = new PrintWriter(fos, true);
	   // index.getResultsMap(NDAIndexManager.LUCENE_TYPE_SPECIMEN, 100);
       List<ESSpecimen> list = index.getResultsList(LUCENE_TYPE_SPECIMEN, 5, ESSpecimen.class);
       BeanPrinter.out(list);
       for(ESSpecimen specimen : list) {
    	   pw.println(getCSVRecordFromSpecimen(specimen));
       }
       pw.close();
	}



	private String getCSVRecordFromSpecimen(ESSpecimen specimen)
	{
		String result = null;
		result = specimen.getUnitID();
		// TODO Auto-generated method stub
		return result;
	}

	

}
