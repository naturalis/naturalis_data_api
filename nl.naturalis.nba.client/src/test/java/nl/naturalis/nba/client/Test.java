package nl.naturalis.nba.client;

import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.utils.http.SimpleHttpPost;
import nl.naturalis.nba.utils.http.SimpleHttpRequest;

public class Test {

	public static void main(String[] args)
	{
		SimpleHttpPost post = new SimpleHttpPost();
		post.setBaseUrl("http://localhost:8080/v2/specimen/query2");
		QuerySpec qs = new QuerySpec();
		qs.setFrom(12);
		qs.setSize(16);
		post.setRequestBody(JsonUtil.toJson(qs),SimpleHttpRequest.CT_APPLICATION_JSON);
		post.execute();
	}

}
