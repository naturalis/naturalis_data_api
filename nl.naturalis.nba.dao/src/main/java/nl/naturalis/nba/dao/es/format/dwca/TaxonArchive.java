package nl.naturalis.nba.dao.es.format.dwca;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import nl.naturalis.nba.dao.es.exception.DwcaCreationException;
import nl.naturalis.nba.dao.es.format.DataSet;
import nl.naturalis.nba.dao.es.format.DataSetEntity;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "archive")
class TaxonArchive extends Archive {

	TaxonArchive()
	{
		super();
	}

	TaxonArchive forDataSet(DataSet dataSet)
	{
		this.core = createCore(dataSet);
		this.extensions = createExtensions(dataSet);
		return this;
	}

	private static Core createCore(DataSet dataSet)
	{
		return new TaxonCore().forDataSet(dataSet);
	}

	private static List<Extension> createExtensions(DataSet ds)
	{
		DataSetEntity[] entities = ds.getDataSetCollection().getEntities();
		List<Extension> extensions = new ArrayList<>(entities.length - 1);
		LOOP: for (DataSetEntity entity : entities) {
			switch (entity.getName()) {
				case "taxa":
					continue LOOP;
				case "vernacular":
					extensions.add(new TaxonVernacularNameExtension().forDataSet(ds));
					break;
				case "reference":
					extensions.add(new TaxonReferenceExtension().forDataSet(ds));
					break;
				default:
					String fmt = "Extension %s not supported for data set %s";
					String msg = String.format(fmt, entity.getName(), ds.getName());
					throw new DwcaCreationException(msg);
			}
		}
		return extensions;
	}

}
