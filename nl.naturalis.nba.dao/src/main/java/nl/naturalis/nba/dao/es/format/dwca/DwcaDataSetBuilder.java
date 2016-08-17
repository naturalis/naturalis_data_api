package nl.naturalis.nba.dao.es.format.dwca;

import static org.domainobject.util.FileUtil.newFile;

import nl.naturalis.nba.dao.es.DocumentType;
import nl.naturalis.nba.dao.es.format.DataSet;
import nl.naturalis.nba.dao.es.format.DataSetCollection;

public class DwcaDataSetBuilder {

	private DocumentType<?> dt;
	private String name;

	public DwcaDataSetBuilder(DocumentType<?> dt, String name)
	{
		this.dt = dt;
		this.name = name;
	}

	public DataSet build()
	{
		DataSetCollection dsc = new DwcaDataSetCollectionBuilder(dt, name).build();
		DataSet dataSet = new DataSet();
		dataSet.setDataSetCollection(dsc);
		dataSet.setName(name);
		dataSet.setHome(newFile(dsc.getHome(), name));
		return dataSet;
	}

}
