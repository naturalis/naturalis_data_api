package nl.naturalis.nda.export.dwca;

/*  
 *  Created by : Reinier.Kartowikromo 
 *  Date: 12-02-2015
 *  Description: String Utilites for the StringBuilder Class to Write data to a CSV file
 */


import java.util.HashSet;
import java.util.Set;
 
public class StringUtilities
{  
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
     
    public static boolean isNumeric(String str) 
    { 
      try 
      { 
        double d = Double.parseDouble(str); 
      } 
      catch(NumberFormatException nfe) 
      { 
        return false; 
      } 
      return true; 
    }
}