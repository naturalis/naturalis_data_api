package nl.naturalis.nda.elasticsearch.load;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;

import nl.naturalis.nda.elasticsearch.util.SimpleElasticSearchClient;

import org.domainobject.util.FileUtil;

public class SchemaCreator {

	public static final String NDA_INDEX_NAME = "nda";

	private static final PrintStream err = new PrintStream(new OutputStream() {
		public void write(int b) throws IOException
		{
		}
	});


	public static void main(String[] args)
	{
		System.setErr(err);
		SimpleElasticSearchClient client = new SimpleElasticSearchClient(System.out);

		try {

			client.deleteAllIndices();

			client.createIndex(NDA_INDEX_NAME);

			URL url = SchemaCreator.class.getResource("/elasticsearch/specimen.type.json");
			String mappings = FileUtil.getContents(url);
			client.addType(NDA_INDEX_NAME, "specimen", mappings);

			client.showAllIndices();
			client.showIndex(NDA_INDEX_NAME);

		}
		catch (Throwable t) {
			t.printStackTrace(System.out);
		}
	}
}
