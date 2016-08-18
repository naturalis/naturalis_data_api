package nl.naturalis.nba.dao.es.format.dwca;

import static nl.naturalis.nba.common.json.JsonUtil.deserialize;
import static org.domainobject.util.FileUtil.newFile;
import static org.domainobject.util.FileUtil.newFileInputStream;

import java.io.File;
import java.io.InputStream;

import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.dao.es.DocumentType;
import nl.naturalis.nba.dao.es.exception.DwcaCreationException;
import nl.naturalis.nba.dao.es.format.DataSet;
import nl.naturalis.nba.dao.es.format.DataSetCollection;

/**
 * A DwCA-specific builder of {@link DataSet} instances.
 * 
 * @author Ayco Holleman
 *
 */
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
		File home = newFile(dsc.getHome(), name);
		dataSet.setHome(home);
		dataSet.setQuerySpec(getQuerySpec(home));
		return dataSet;
	}

	private QuerySpec getQuerySpec(File homeDir)
	{
		File f = newFile(homeDir, "queryspec.json");
		if (!f.isFile()) {
			String fmt = "Missing query specification (queryspec.json) for data set %s";
			String msg = String.format(fmt, name);
			throw new DwcaCreationException(msg);
		}
		InputStream is = newFileInputStream(f);
		return deserialize(is, QuerySpec.class);
	}

}
