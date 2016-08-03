package nl.naturalis.nba.dao.es.format.dwca;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import nl.naturalis.nba.dao.es.exception.DwcaCreationException;
import nl.naturalis.nba.dao.es.format.IDataSetField;

/**
 * Abstract base class for generating the meta&#46;xml file of a DwC archive.
 * 
 * @author Ayco Holleman
 *
 */
public abstract class MetaXmlGenerator {

	private IDataSetField[] fields;

	public MetaXmlGenerator(IDataSetField[] fields)
	{
		this.fields = fields;
	}

	/**
	 * Writes the XML to the specified output stream.
	 * 
	 * @param out
	 */
	public void generateMetaXml(OutputStream out)
	{
		Files files = new Files();
		files.setLocation(getLocation());
		Id id = new Id();
		id.setIndex(getIndexOfIdField());
		Core core = new Core();
		core.setFiles(files);
		core.setId(id);
		core.setEncoding("UTF-8");
		core.setFieldsEnclosedBy("");
		core.setFieldsTerminatedBy(",");
		core.setLinesTerminatedBy("\n");
		core.setIgnoreHeaderLines("1");
		core.setRowtype(getRowType());
		core.setField(getFields());
		Archive archive = new Archive();
		archive.setMetadata("eml.xml");
		archive.setXmlnsxsi("http://www.w3.org/2001/XMLSchema-instance");
		archive.setXmlnstdwg("http://rs.tdwg.org/dwc/text/");
		archive.add(core);
		try {
			JAXBContext context = JAXBContext.newInstance(Archive.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			m.marshal(archive, out);
		}
		catch (JAXBException e) {
			throw new DwcaCreationException(e);
		}
	}

	abstract String getLocation();

	abstract String getRowType();

	private int getIndexOfIdField()
	{
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].getName().equals("id"))
				return i;
		}
		return -1;
	}

	private List<Field> getFields()
	{
		String base = "http://rs.tdwg.org/dwc/terms/";
		List<Field> list = new ArrayList<>(fields.length);
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].getName().equals("id"))
				continue;
			Field field = new Field();
			field.setIndex(String.valueOf(i));
			field.setTerm(base + fields[i].getName());
			list.add(field);
		}
		return list;
	}

}
