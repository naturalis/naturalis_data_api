package nl.naturalis.nda.export.dwca;

/*  
 *  Created by : Reinier.Kartowikromo 
 *  Date: 12-02-2015
 *  Description: String Utilites for the StringBuilder Class to Write data to a CSV file
 */


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
 
public class StringUtilities
{  
	final static int BUFFER = 2048;
	
    public static int indexOfFirstContainedCharacter(String s1, String s2) {
        if (s1 == null || s1.isEmpty())
            return -1;
        Set<Character> set = new HashSet<Character>();
        for (int i=0; i<s2.length(); i++) {
            set.add(s2.charAt(i)); // Build a constant-time lookup table.
        }
        for (int i=0; i<s1.length(); i++) {
            if (set.contains(s1.charAt(i))) {
                return i; // Found a character in s1 also in s2.
            }
        }
        return -1; // No matches.
    }
     

    /* Zip multiple files */
    public static void addToZipFile(String fileName, ZipOutputStream zos)throws FileNotFoundException, IOException
    {
    	System.out.println("Writing '" + fileName + "' to zip file");
    	
    	BufferedInputStream origin = null;
    	byte data[]  = new byte[BUFFER];
    	
    	File file = new File(fileName);
    	FileInputStream fis = new FileInputStream(file);
    	origin = new BufferedInputStream(fis, BUFFER);
    	ZipEntry zipEntry = new ZipEntry(fileName);
    	zos.putNextEntry(zipEntry);
    	
    	int length;
    	while ((length = origin.read(data, 0, BUFFER)) != -1)
    	{
    		zos.write(data, 0, length);
    		zos.flush();
    	}
    	origin.close();
    	zos.closeEntry();
    	zos.close();
    	fis.close();
    }
}