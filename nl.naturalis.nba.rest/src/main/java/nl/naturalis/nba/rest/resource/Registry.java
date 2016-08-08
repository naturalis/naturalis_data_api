package nl.naturalis.nba.rest.resource;

import static javax.ejb.ConcurrencyManagementType.BEAN;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import nl.naturalis.nba.rest.util.NDA;

import com.fasterxml.jackson.databind.ObjectMapper;

@Singleton
@Startup
@ConcurrencyManagement(BEAN)
public class Registry {
//
//	private ObjectMapper objectMapper;
//	private NDA nda;
//
//
//	public NDA getNDA()
//	{
//		return nda;
//	}
//
//
//	public TaxonDao getTaxonDao(String baseUrl)
//	{
//		return new TaxonDao(nda.getESClient(), nda.getIndexName(), baseUrl);
//	}
//
//
//	public BioportalTaxonDao getBioportalTaxonDao(String baseUrl)
//	{
//		return new BioportalTaxonDao(nda.getESClient(), nda.getIndexName(), baseUrl);
//	}
//
//
//	public SpecimenDao getSpecimenDao(String baseUrl)
//	{
//		return new SpecimenDao(nda.getESClient(), nda.getIndexName(), getTaxonDao(baseUrl), baseUrl);
//	}
//
//
//	public BioportalSpecimenDao getBioportalSpecimenDao(String baseUrl)
//	{
//		return new BioportalSpecimenDao(nda.getESClient(), nda.getIndexName(), getBioportalTaxonDao(baseUrl), getTaxonDao(baseUrl), baseUrl);
//	}
//
//
//	public MultiMediaObjectDao getMultiMediaObjectDao(String baseUrl)
//	{
//		return new MultiMediaObjectDao(nda.getESClient(), nda.getIndexName(), baseUrl);
//	}
//
//
//	public BioportalMultiMediaObjectDao getBioportalMultiMediaObjectDao(String baseUrl)
//	{
//		return new BioportalMultiMediaObjectDao(nda.getESClient(), nda.getIndexName(), getBioportalTaxonDao(baseUrl), getTaxonDao(baseUrl),
//				getSpecimenDao(baseUrl), baseUrl);
//	}
//
//
//	public ObjectMapper getObjectMapper()
//	{
//		if (objectMapper == null) {
//			objectMapper = new ObjectMapper();
//		}
//		return objectMapper;
//	}
//
//
//	@PostConstruct
//	public void init()
//	{
//		LogUtil.configureLogging();
//		nda = new NDA();
//	}
//
//
//	@PreDestroy
//	public void destroy()
//	{
//		if (nda != null && nda.getESClient() != null) {
//			nda.getESClient().close();
//		}
//	}
//
}
