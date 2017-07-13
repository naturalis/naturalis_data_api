package nl.naturalis.nba.utils.xml;

import java.io.File;
import java.net.URL;

import org.junit.Test;
import org.xml.sax.SAXParseException;

public class XmlFileUpdaterTest {

	@Test
	public void testReadFile()
	{
	}

	@Test
	public void testUpdateFirstElement() throws SAXParseException
	{
		URL resource = getClass().getResource("eml.xml");
		File f = new File(resource.getFile());
		XmlFileUpdater updater = new XmlFileUpdater(f);
		updater.readFile();
		updater.updateFirstElement("pubDate", "2017-08-08T00:00:00");
	}

	@Test
	public void testSave() throws SAXParseException
	{
		URL resource = getClass().getResource("eml.xml");
		File f = new File(resource.getFile());
		XmlFileUpdater updater = new XmlFileUpdater(f);
		updater.readFile();
		updater.updateFirstElement("pubDate", "2017-08-08T00:00:00");
		updater.save(new File("/tmp/XmlFileUpdaterTest.xml"));
	}

}
