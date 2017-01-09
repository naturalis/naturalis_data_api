package nl.naturalis.nba.client;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class Test {

	public static void main(String[] args) throws UnsupportedEncodingException
	{
		long l = 1L;
	    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
	    buffer.putLong(l);
	    byte[] b0 = buffer.array();
	    String s = new String(b0, Charset.forName("US-ASCII"));
	    buffer = ByteBuffer.allocate(Long.BYTES);
	    buffer.put(s.getBytes("US-ASCII"));
	    buffer.flip();//need flip 
		System.out.println("XXX: " + buffer.getLong());
		
//		SimpleHttpPost post = new SimpleHttpPost();
//		post.setBaseUrl("http://localhost:8080/v2/specimen/query2");
//		QuerySpec qs = new QuerySpec();
//		qs.setFrom(12);
//		qs.setSize(16);
//		post.setRequestBody(JsonUtil.toJson(qs),SimpleHttpRequest.CT_APPLICATION_JSON);
//		post.execute();
	}

}
