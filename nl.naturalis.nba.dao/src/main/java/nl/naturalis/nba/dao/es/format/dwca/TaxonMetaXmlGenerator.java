package nl.naturalis.nba.dao.es.format.dwca;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import nl.naturalis.nba.dao.es.exception.DwcaCreationException;
import nl.naturalis.nba.dao.es.format.DataSet;
import nl.naturalis.nba.dao.es.format.DataSetEntity;
import nl.naturalis.nba.dao.es.format.IDataSetField;

/**
 * @author Ayco Holleman
 *
 */
public abstract class TaxonMetaXmlGenerator {

	private DataSet dataSet;

	public TaxonMetaXmlGenerator(DataSet dataSet)
	{
		this.dataSet = dataSet;
	}

	/**
	 * Writes the XML to the specified output stream.
	 * 
	 * @param out
	 */
	public void generateMetaXml(OutputStream out)
	{
		Archive archive = new Archive();
		archive.setXmlnsxsi("http://www.w3.org/2001/XMLSchema-instance");
		archive.setXmlnstdwg("http://rs.tdwg.org/dwc/text/");
		archive.setCore(createCore());
		try {
			JAXBContext context = JAXBContext.newInstance(Archive.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.setProperty(Marshaller.JAXB_ENCODING, DwcaConstants.ENCODING);
			m.marshal(archive, out);
		}
		catch (JAXBException e) {
			throw new DwcaCreationException(e);
		}
	}

	private Core createCore()
	{
		DataSetEntity entity = dataSet.getDataSetCollection().getEntity("taxa");
		Files files = new Files();
		files.setLocation("taxa.txt");
		Core core = new Core();
		core.setFiles(files);
		core.setRowType("http://rs.tdwg.org/dwc/terms/Taxon");
		core.setFields(getFields(entity));
		return core;
	}
	
	private void addExtensions(Archive archive) {
		DataSetEntity[] entities = dataSet.getDataSetCollection().getEntities();
		for(DataSetEntity entity : entities) {
			Extension ext = new Extension();
		}
	}

	private static List<Field> getFields(DataSetEntity entity)
	{
		IDataSetField[] fields = entity.getFields();
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
