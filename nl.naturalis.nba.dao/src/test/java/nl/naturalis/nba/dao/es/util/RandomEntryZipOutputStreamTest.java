package nl.naturalis.nba.dao.es.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Test;

@SuppressWarnings("static-method")
public class RandomEntryZipOutputStreamTest {

	@Test
	public void testFinish_01() throws IOException
	{
		String tmpDir = System.getProperty("java.io.tmpdir");
		File zipFile = new File(tmpDir + "/SprayableZipOutputStreamTest.zip");
		if (zipFile.exists()) {
			zipFile.delete();
		}
		FileOutputStream fos = new FileOutputStream(zipFile);
		RandomEntryZipOutputStream spray = new RandomEntryZipOutputStream(fos, "test1.txt");
		spray.addEntry("test2.txt");
		spray.addEntry("test3.txt");
		PrintStream ps = new PrintStream(spray);
		spray.setActiveEntry("test1.txt");
		ps.println("This is a line for test1.txt");
		spray.setActiveEntry("test2.txt");
		ps.println("This is a line for test2.txt");
		spray.setActiveEntry("test3.txt");
		ps.println("This is a line for test3.txt");
		ps.close();
	}

}
