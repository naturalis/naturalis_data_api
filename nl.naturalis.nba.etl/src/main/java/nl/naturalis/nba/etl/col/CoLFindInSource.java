package nl.naturalis.nba.etl.col;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import nl.naturalis.nba.dao.es.Registry;

/**
 * Utility class for finding values in the CoL data sources (taxa.txt,
 * reference.txt, vernacular.txt).
 * 
 * @author Ayco Holleman
 *
 */
public class CoLFindInSource {
	
	public static void main(String[] args) throws IOException
	{
		try {
			CoLFindInSource colfindinsource = new CoLFindInSource();
			String dwcaDir = Registry.getInstance().getConfiguration().required("col.csv_dir");
			colfindinsource.ReadValueFromCsv(dwcaDir + "/vernacular.txt");
		}
		finally {
		}
	}

	public class Vernacular {

		private int taxonID;
		private String vernacularName;
		private String language;
		private String countryCode;
		private String locality;
		private String transliteration;

		public int getId()
		{
			return taxonID;
		}

		public void setId(int taxonID)
		{
			this.taxonID = taxonID;
		}

		public String getVernacularName()
		{
			return vernacularName;
		}

		public void setName(String vernacularname)
		{
			this.vernacularName = vernacularname;
		}

		public String getLanguage()
		{
			return language;
		}

		public void setLanguage(String language)
		{
			this.language = language;
		}

		public String getcountryCode()
		{
			return countryCode;
		}

		public void setCountryCode(String countryCode)
		{
			this.countryCode = countryCode;
		}

		public String getLocality()
		{
			return locality;
		}

		public void setLocality(String locality)
		{
			this.locality = locality;
		}

		public String getTransliteratio()
		{
			return transliteration;
		}

		public void setTransliteration(String transliteration)
		{
			this.transliteration = transliteration;
		}

		@Override
		public String toString()
		{
			return "\n Vernacular:" + "\n ID=" + getId() + "\n VernacularName:" + getVernacularName() + "\n Language:" + getLanguage()
					+ " \n Countrycode:" + getcountryCode() + "\n Locality:" + getLocality() + "\n Transliteration:" + getTransliteratio()
					+ "\n ................................................." + "\n file vernacular.txt [record ]" + getId();
		}
	}

	public void ReadValueFromCsv(String path) throws IOException
	{
		LineNumberReader lnr = new LineNumberReader(new FileReader(path));
		int indexed = 0;
		List<Vernacular> vernlist = new ArrayList<>();
		String line;
		try {
			lnr.readLine();
			while ((line = lnr.readLine()) != null) {
				Vernacular vern = new Vernacular();
				Scanner scanner = new Scanner(line);
				scanner.useDelimiter("\t");
				while (scanner.hasNext()) {
					String data = scanner.next();
					if (indexed == 0)
						vern.setId(Integer.parseInt(data));
					else if (indexed == 1)
						vern.setName(data);
					else if (indexed == 2)
						vern.setLanguage(data);
					else if (indexed == 3)
						vern.setCountryCode(data);
					else if (indexed == 4)
						vern.setLocality(data);
					else if (indexed == 5)
						vern.setTransliteration(data);
					else
						System.out.println("Invalid data: " + data);
					indexed++;
				}
				indexed = 0;
				vernlist.add(vern);
				System.out.println(vernlist);
				scanner.close();
			}
		}
		finally {
			lnr.close();
		}
	}
}
