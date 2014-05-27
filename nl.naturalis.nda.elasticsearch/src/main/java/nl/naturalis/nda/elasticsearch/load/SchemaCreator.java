package nl.naturalis.nda.elasticsearch.load;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;

import nl.naturalis.nda.elasticsearch.client.ElasticSearchHttpClient;

import org.domainobject.util.FileUtil;

public class SchemaCreator {

	public static final String NDA_INDEX_NAME = "nda";

	public static void main(String[] args)
	{
		ElasticSearchHttpClient client = new ElasticSearchHttpClient(NDA_INDEX_NAME);

		try {

			client.deleteAllIndices();

			client.createIndex();

			URL url = SchemaCreator.class.getResource("/elasticsearch/specimen.type.json");
			String mappings = FileUtil.getContents(url);
			client.addType("specimen", mappings);

			client.describeAllIndices();
			client.describe();

		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
