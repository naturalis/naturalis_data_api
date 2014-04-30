package nl.naturalis.nda.elasticsearch.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

import org.domainobject.util.http.SimpleHttpDelete;
import org.domainobject.util.http.SimpleHttpGet;
import org.domainobject.util.http.SimpleHttpPut;
import org.domainobject.util.http.SimpleHttpRequest;

public class SimpleElasticSearchClient {

	//@formatter:off
	private static class NullWriter extends Writer {
		public void write(char[] cbuf, int off, int len) throws IOException {}
		public void flush() throws IOException {}
		public void close() throws IOException {}
	};
	//@formatter:on

	public static final String LOCAL_CLUSTER = "http://localhost:9200/";

	private final SimpleHttpGet get = new SimpleHttpGet();
	private final SimpleHttpPut put = new SimpleHttpPut();
	private final SimpleHttpDelete del = new SimpleHttpDelete();

	private final PrintWriter out;

	private SimpleHttpRequest lastRequest;


	public SimpleElasticSearchClient()
	{
		this(LOCAL_CLUSTER);
	}


	public SimpleElasticSearchClient(String clusterUrl)
	{
		this(clusterUrl, new PrintWriter(new NullWriter()));
	}


	public SimpleElasticSearchClient(OutputStream os)
	{
		this(LOCAL_CLUSTER, os);
	}


	public SimpleElasticSearchClient(Writer writer)
	{
		this(LOCAL_CLUSTER, writer);
	}


	public SimpleElasticSearchClient(String clusterUrl, OutputStream os)
	{
		this(clusterUrl, new PrintWriter(os, true));
	}


	public SimpleElasticSearchClient(String clusterUrl, Writer writer)
	{
		this.out = writer instanceof PrintWriter ? (PrintWriter) writer : new PrintWriter(writer, true);
		get.setBaseUrl(clusterUrl);
		put.setBaseUrl(clusterUrl);
		del.setBaseUrl(clusterUrl);
	}


	public void showIndices()
	{
		out.println("-> showIndices");
		lastRequest = get;
		get.setPath("_aliases");
		execute();
	}
	
	public void createIndex(String name, String mapping)
	{
		out.println("-> createIndex [name=" + name + "] , [mapping=" + mapping + "]");
		lastRequest = put;
		put.setPath(name);
		put.setBody(mapping);
	}


	public void deleteIndex(String name)
	{
		out.println("-> deleteIndex [name=" + name + "]");
		lastRequest = del;
		del.setPath(name);
		execute();
	}


	public void deleteAllIndices()
	{
		out.println("-> deleteAllIndices");
		lastRequest = del;
		del.setPath("_all");
		execute();
	}


	private void execute()
	{
		try {
			lastRequest.execute();
			if (lastRequest.isOK()) {
				out.println(lastRequest.getResponse());
			}
			else {
				out.println(String.format("[ERROR] code: %s; message: %s", lastRequest.getErrorCode(), lastRequest.getError()));
			}
		}
		catch (Throwable t) {
			t.printStackTrace(out);
		}
		finally {
			out.println();
		}
	}

}
