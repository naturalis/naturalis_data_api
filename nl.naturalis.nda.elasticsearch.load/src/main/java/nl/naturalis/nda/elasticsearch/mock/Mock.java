package nl.naturalis.nda.elasticsearch.mock;

import java.text.SimpleDateFormat;

import nl.naturalis.nda.domain.CommonName;
import nl.naturalis.nda.domain.DefaultClassification;
import nl.naturalis.nda.domain.Expert;
import nl.naturalis.nda.domain.Identification;
import nl.naturalis.nda.domain.Monomial;
import nl.naturalis.nda.domain.Occurrence;
import nl.naturalis.nda.domain.Reference;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.Synonym;
import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.search.Link;
import nl.naturalis.nda.search.OccurrenceSearchResultSet;
import nl.naturalis.nda.search.SearchResult;
import nl.naturalis.nda.search.TaxonSearchResultSet;

import org.domainobject.util.FileUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Mock {

	public static void main(String[] args) throws Exception
	{
		Mock mock = new Mock();
		mock.occurrenceWithIdentifications();
		mock.occurrenceWithIdentificationsWithTaxa();
		mock.occurrenceTheFullMonty();
		mock.taxaWithscientificNameAndClassification();
		mock.taxaTheFullMonty();
		System.out.println("Done");
	}

	private static final ObjectMapper om = new ObjectMapper();

	static {
		om.setDateFormat(new SimpleDateFormat("YYYY-MM-dd"));
	}

	private final Mocker mocker = new Mocker();


	public void occurrenceWithIdentifications() throws JsonProcessingException
	{
		OccurrenceSearchResultSet rs = new OccurrenceSearchResultSet();
		rs.setSize(1386);
		rs.addLink("self", "http://nda.naturalis.nl/occurence/?country=Suriname&offset=150");
		rs.addLink("occurrence.extra-info", "http://nda.naturalis.nl/occurence/extra");
		rs.addLink("taxon.cool-taxon-data", "http://nda.naturalis.nl/taxon/cool-data");

		for (int i = 0; i < 20; ++i) {
			Occurrence occurrence = mocker.createMock(Occurrence.class, Identification.class);
			Link link0 = new Link("self", "http://nda.naturalis.nl/occurence/?unitId=" + i);
			Link link1 = new Link("occurrence.related-occurences", "http://nda.naturalis.nl/occurence/related/unitId=" + i);
			SearchResult<Occurrence> result = new SearchResult<Occurrence>(occurrence);
			result.addLink(link0);
			result.addLink(link1);
			rs.addResult(result);
		}
		String json = om.writerWithDefaultPrettyPrinter().writeValueAsString(rs);
		System.out.println(json);
		FileUtil.setContents("C:/test/nda/mock/occurrences-with-identifications.json", json);
	}

	public void occurrenceWithIdentificationsWithTaxa() throws JsonProcessingException
	{
		OccurrenceSearchResultSet rs = new OccurrenceSearchResultSet();
		rs.setSize(1386);
		rs.addLink("self", "http://nda.naturalis.nl/occurence/?country=Suriname&offset=1200");
		rs.addLink("occurrence.extra-info", "http://nda.naturalis.nl/occurence/extra");
		rs.addLink("taxon.cool-taxon-data", "http://nda.naturalis.nl/taxon/cool-data");

		for (int i = 0; i < 20; ++i) {
			Occurrence occurrence = mocker.createMock(Occurrence.class, Identification.class, Taxon.class);
			Link link0 = new Link("self", "http://nda.naturalis.nl/occurence/?unitId=" + i);
			Link link1 = new Link("occurrence.related-occurences", "http://nda.naturalis.nl/occurence/related/unitId=" + i);
			SearchResult<Occurrence> result = new SearchResult<Occurrence>(occurrence);
			result.addLink(link0);
			result.addLink(link1);
			rs.addResult(result);
		}
		String json = om.writerWithDefaultPrettyPrinter().writeValueAsString(rs);
		System.out.println(json);
		FileUtil.setContents("C:/test/nda/mock/occurrences-with-identifications-with-taxa.json", json);
	}

	public void occurrenceTheFullMonty() throws JsonProcessingException
	{
		OccurrenceSearchResultSet rs = new OccurrenceSearchResultSet();
		rs.setSize(1020);
		rs.addLink("self", "http://nda.naturalis.nl/occurence/?country=Suriname&offset=1200");
		rs.addLink("occurrence.extra-info", "http://nda.naturalis.nl/occurence/extra");
		rs.addLink("taxon.cool-taxon-data", "http://nda.naturalis.nl/taxon/cool-data");

		for (int i = 0; i < 20; ++i) {
			Occurrence occurrence = mocker.createMock(Occurrence.class, Identification.class, Taxon.class, ScientificName.class, DefaultClassification.class, Synonym.class);
			Link link0 = new Link("self", "http://nda.naturalis.nl/occurence/?unitId=" + i);
			Link link1 = new Link("occurrence.related-occurences", "http://nda.naturalis.nl/occurence/related/unitId=" + i);
			SearchResult<Occurrence> result = new SearchResult<Occurrence>(occurrence);
			result.addLink(link0);
			result.addLink(link1);
			rs.addResult(result);
		}
		String json = om.writerWithDefaultPrettyPrinter().writeValueAsString(rs);
		System.out.println(json);
		FileUtil.setContents("C:/test/nda/mock/occurrence-the-full-monty.json", json);
	}

	public void taxaWithscientificNameAndClassification() throws JsonProcessingException
	{
		TaxonSearchResultSet rs = new TaxonSearchResultSet();
		rs.setSize(236);
		rs.addLink("self", "http://nda.naturalis.nl/taxon/?offset=200");

		for (int i = 0; i < 20; ++i) {
			Taxon taxon = mocker.createMock(Taxon.class, ScientificName.class, DefaultClassification.class);
			Link link0 = new Link("self", "http://nda.naturalis.nl/taxon/?taxonId=" + i);
			Link link1 = new Link("occurrence.list-all", "http://nda.naturalis.nl/occurence/taxonId=" + i);
			SearchResult<Taxon> result = new SearchResult<Taxon>(taxon);
			result.addLink(link0);
			result.addLink(link1);
			rs.addResult(result);
		}
		String json = om.writerWithDefaultPrettyPrinter().writeValueAsString(rs);
		System.out.println(json);
		FileUtil.setContents("C:/test/nda/mock/taxa-with-scientific-names-and-classification.json", json);
	}

	public void taxaTheFullMonty() throws JsonProcessingException
	{
		TaxonSearchResultSet rs = new TaxonSearchResultSet();
		rs.setSize(988);
		rs.addLink("self", "http://nda.naturalis.nl/taxon/?offset=200");

		for (int i = 0; i < 20; ++i) {
			Taxon taxon = mocker.createMock(Taxon.class, ScientificName.class, DefaultClassification.class, Synonym.class, CommonName.class, Reference.class, Expert.class, Monomial.class);
			Link link0 = new Link("self", "http://nda.naturalis.nl/taxon/?taxonId=" + i);
			Link link1 = new Link("occurrence.list-all", "http://nda.naturalis.nl/occurence/taxonId=" + i);
			SearchResult<Taxon> result = new SearchResult<Taxon>(taxon);
			result.addLink(link0);
			result.addLink(link1);
			rs.addResult(result);
		}
		String json = om.writerWithDefaultPrettyPrinter().writeValueAsString(rs);
		System.out.println(json);
		FileUtil.setContents("C:/test/nda/mock/taxa-the-full-monty.json", json);
	}

}
