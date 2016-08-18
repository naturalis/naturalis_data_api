package nl.naturalis.nba.dao.es.format.dwca;

import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import nl.naturalis.nba.dao.es.exception.DwcaCreationException;

/**
 * Generates the meta&#46;xml file of a DarwinCore archive.
 * 
 * @author Ayco Holleman
 *
 */
public class MetaXmlGenerator {

	private Archive archive;

	public MetaXmlGenerator(Archive archive)
	{
		this.archive = archive;
	}

	/**
	 * Writes the XML to the specified output stream.
	 * 
	 * @param out
	 */
	public void generateMetaXml(OutputStream out)
	{
		try {
			JAXBContext context = JAXBContext.newInstance(archive.getClass());
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.setProperty(Marshaller.JAXB_ENCODING, DwcaConstants.ENCODING);
			m.marshal(archive, out);
		}
		catch (JAXBException e) {
			throw new DwcaCreationException(e);
		}
	}
}
