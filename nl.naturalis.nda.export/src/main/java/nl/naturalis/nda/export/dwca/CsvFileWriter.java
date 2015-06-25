package nl.naturalis.nda.export.dwca;

/*  
 *  Created by : Reinier.Kartowikromo 
 *  Date: 12-02-2015
 *  Description: StringBuilder Class to Write data to a CSV file
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
 
 
public class CsvFileWriter extends BufferedWriter
{
	
	public static final String httpUrl = "http://data.biodiversitydata.nl/";
    /**
     * Parameterized constructor
     * @param fileName
     * @throws IOException
     */
    @SuppressWarnings("resource")
	public CsvFileWriter(String fileName) throws IOException{
        super(new FileWriter(fileName));
    }
    
    @SuppressWarnings("resource")
	public CsvFileWriter(File file) throws IOException{
        super(new FileWriter(file));
    }
    
    /**
     * Writes a single row to a CSV file.
     * @param row
     * @throws IOException
     */
    public void WriteRow(CsvRow row) throws IOException
    {
        StringBuilder builder = new StringBuilder();
        boolean firstColumn = true;
        for(String column : row ){
            if (!firstColumn)
                builder.append('\t');
            if(column != null)
            {
           		column = column.replace('\r', ' ');
           		column = column.replace('\n', ' ');
           		builder.append(column);
            	firstColumn = false;
            }
        }
        row.lineText = builder.toString();
        write(row.lineText);
        newLine();
        builder.setLength(0);
        builder.trimToSize();
    }
     
    /**
     *
     *  Class to store one CSV row.
     *
     */
    public class CsvRow extends  ArrayList<String>
    {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		String lineText = null;
         
        public String getlineText(){
            return lineText;
        }
         
        public void setLineText(String lineText){
            this.lineText = lineText;
        }
 
    }
}
