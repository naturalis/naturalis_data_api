/**
 *  Description: Create a new DwCA export tool for the NBA
 *  Date: January 28th 2015
 *  Developer: Reinier Kartowikromo
 *  
 */
package nl.naturalis.nda.export.dwca;

import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_SPECIMEN;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.lang.Object;

import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import nl.naturalis.nda.export.dwca.CSVWriter;

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
	public static final String CSVComma = ",";

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{

		logger.info("-----------------------------------------------------------------");
		logger.info("Start");
		IndexNative index = new IndexNative(LoadUtil.getESClient(), LoadUtil.getConfig().required(
				"elasticsearch.index.name"));
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
		String csvOutPutFile = "specimen.txt";
		// before we open the file check to see if it already exists
		boolean alreadyExists = new File(csvOutPutFile).exists();

		FileOutputStream fos = new FileOutputStream(csvOutPutFile);
		PrintWriter pw = new PrintWriter(fos, true);
		CSVWriter csv = new CSVWriter(pw, false, '\t', System.getProperty("line.separator"));
		csv.writeCommentln("Specimen csv-file");

		// index.getResultsMap(NDAIndexManager.LUCENE_TYPE_SPECIMEN, 100);
		List<ESSpecimen> list = index.getResultsList(LUCENE_TYPE_SPECIMEN, 5, ESSpecimen.class);
		BeanPrinter.out(list);

		boolean success = (new File(csvOutPutFile)).delete();
		if (success)
		{
			System.out.println("The file has been successfully deleted");
		}

		if (!alreadyExists)
		{
			csv.write("UnitID");
			csv.write("UnitGUID");
			csv.write("CollectorsFieldNumber");
			csv.writeln();
		}

		for (ESSpecimen specimen : list)
		{
			pw.println(getCSVRecordFromSpecimen(specimen));
		}
		pw.close();
	}

	private String getCSVRecordFromSpecimen(ESSpecimen specimen)
	{
		String result = null;
		result = specimen.getUnitID() + CSVComma + specimen.getUnitGUID() + CSVComma
				+ specimen.getCollectorsFieldNumber() + CSVComma + specimen.getAssemblageID() + CSVComma
				+ specimen.getSourceInstitutionID() + CSVComma + specimen.getSourceID() + CSVComma
				+ specimen.getOwner() + CSVComma + specimen.getLicenceType() + CSVComma
				+ specimen.getLicence() + CSVComma + specimen.getRecordBasis() + CSVComma
				+ specimen.getKindOfUnit() + CSVComma + specimen.getCollectionType() + CSVComma;
		return result;
	}

}
