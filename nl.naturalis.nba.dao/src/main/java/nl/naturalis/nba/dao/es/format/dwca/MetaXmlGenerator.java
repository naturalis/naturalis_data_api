package nl.naturalis.nba.dao.es.format.dwca;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import nl.naturalis.nba.dao.es.exception.DwcaCreationException;
import nl.naturalis.nba.dao.es.format.IDataSetField;

public abstract class MetaXmlGenerator {

	private final IDataSetField[] columns;

	public MetaXmlGenerator(IDataSetField[] columns)
	{
		this.columns = columns;
	}

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
		for (int i = 0; i < columns.length; i++) {
			if (columns[i].getName().equals("id"))
				return i;
		}
		return -1;
	}

	private List<Field> getFields()
	{
		String base = "http://rs.tdwg.org/dwc/terms/";
		List<Field> fields = new ArrayList<>(columns.length);
		for (int i = 0; i < columns.length; i++) {
			if (columns[i].getName().equals("id"))
				continue;
			Field field = new Field();
			field.setIndex(String.valueOf(i));
			field.setTerm(base + columns[i].getName());
			fields.add(field);
		}
		return fields;
	}

}
