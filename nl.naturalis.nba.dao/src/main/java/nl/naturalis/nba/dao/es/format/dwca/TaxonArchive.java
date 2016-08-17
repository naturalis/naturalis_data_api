package nl.naturalis.nba.dao.es.format.dwca;

import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nba.dao.es.exception.DwcaCreationException;
import nl.naturalis.nba.dao.es.format.DataSet;
import nl.naturalis.nba.dao.es.format.DataSetEntity;

class TaxonArchive extends Archive {

	TaxonArchive(DataSet dataSet)
	{
		super(dataSet);
	}

	@Override
	Core createCore(DataSet dataSet)
	{
		DataSetEntity entity = dataSet.getDataSetCollection().getEntity("taxa");
		return new TaxonCore(entity);
	}

	@Override
	List<Extension> createExtensions(DataSet dataSet)
	{
		DataSetEntity[] entities = dataSet.getDataSetCollection().getEntities();
		List<Extension> extensions = new ArrayList<>(entities.length - 1);
		LOOP: for (DataSetEntity entity : entities) {
			switch (entity.getName()) {
				case "taxa":
					continue LOOP;
				case "vernacular":
					extensions.add(new TaxonVernacularNameExtension(entity));
					break;
				case "reference":
					extensions.add(new TaxonReferenceExtension(entity));
					break;
				default:
					String fmt = "Extension %s not supported for data set %s";
					String msg = String.format(fmt, entity.getName(), dataSet.getName());
					throw new DwcaCreationException(msg);
			}
		}
		return extensions;
	}

}
