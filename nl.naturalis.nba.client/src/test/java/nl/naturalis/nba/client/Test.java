package nl.naturalis.nba.client;

import nl.naturalis.nba.utils.http.SimpleHttpPost;

public class Test {

	public static void main(String[] args)
	{
		SimpleHttpPost post = new SimpleHttpPost();
		post.setBaseUrl("http://localhost:8080/v2/specimen/queryPOST");
		post.addFormParam("user", "ayco");
		post.setContentType("application/x-www-form-urlencoded");
		post.addFormParam("password", "strange");
		post.execute();
	}

}
