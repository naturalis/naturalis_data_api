package nl.naturalis.nda.service.rest.resource;

import static javax.ejb.ConcurrencyManagementType.BEAN;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.elasticsearch.client.Client;

import com.fasterxml.jackson.databind.ObjectMapper;

@Singleton
@Startup
@ConcurrencyManagement(BEAN)
public class Registry {

	private Client esClient;
	private ObjectMapper objectMapper;


	public Client getESClient()
	{
		if (esClient == null) {
			esClient = nodeBuilder().node().client();
		}
		return esClient;
	}


	public ObjectMapper getObjectMapper()
	{
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();
		}
		return objectMapper;
	}


	@PostConstruct
	public void init()
	{

	}


	@PreDestroy
	public void destroy()
	{

	}

}
