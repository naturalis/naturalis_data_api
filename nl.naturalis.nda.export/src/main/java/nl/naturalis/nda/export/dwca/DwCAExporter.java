/**
 *  Description: Create a new DwCA export tool for the NBA
 *  Date: January 28th 2015
 *  Developer: Reinier Kartowikromo
 *  
 */
package nl.naturalis.nda.export.dwca;

import nl.naturalis.nda.elasticsearch.load.LoadUtil;

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
	 */
	public static void main(String[] args)
	{
		logger.info("-----------------------------------------------------------------");
		logger.info("Start");
		
		
		
		
		logger.info("Ready");
		
		LoadUtil.getESClient();

	}

}
