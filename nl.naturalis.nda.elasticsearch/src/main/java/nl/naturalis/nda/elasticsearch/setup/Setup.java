package nl.naturalis.nda.elasticsearch.setup;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;

import org.domainobject.util.FileUtil;

import nl.naturalis.nda.elasticsearch.util.SimpleElasticSearchClient;

public class Setup {

	private static final PrintStream err = new PrintStream(new OutputStream() {
		public void write(int b) throws IOException
		{
		}
	});


	public static void main(String[] args)
	{
		// Get rid of those annoying specimen.v1.type.json
		System.setErr(err);
		SimpleElasticSearchClient client = new SimpleElasticSearchClient(System.out);

		try {
			client.deleteAllIndices();

			URL url = Setup.class.getResource("/specimen.v1.type.json");
			String contents = FileUtil.getContents(url);
			client.createIndex("nda_v1_0", contents);
			
			client.showIndices();
		}
		catch (Throwable t) {
			t.printStackTrace(System.out);
		}
	}
}
