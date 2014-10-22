package nl.naturalis.nda.service.rest.resource;

import static javax.ejb.ConcurrencyManagementType.BEAN;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import nl.naturalis.nda.elasticsearch.dao.dao.BioportalTaxonDao;
import nl.naturalis.nda.elasticsearch.dao.dao.TaxonDao;
import nl.naturalis.nda.service.rest.util.NDA;

import org.elasticsearch.client.Client;

import com.fasterxml.jackson.databind.ObjectMapper;

@Singleton
@Startup
@ConcurrencyManagement(BEAN)
public class Registry {

	private Client esClient;
	private ObjectMapper objectMapper;
	private NDA nda;


	public NDA getNDA()
	{
		return nda;
	}


	public TaxonDao getTaxonDao()
	{
		return new TaxonDao(nda.getESClient(), nda.getIndexName());
	}


	public BioportalTaxonDao getBioportalTaxonDao()
	{
		return new BioportalTaxonDao(nda.getESClient(), nda.getIndexName());
	}


	public Client getESClient()
	{
		if (esClient == null) {
			//esClient = NDA.getESClient();
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
		nda = new NDA();
	}


	@PreDestroy
	public void destroy()
	{
		if (nda != null && nda.getESClient() != null) {
			nda.getESClient().close();
		}
	}

}
