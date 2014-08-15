package nl.naturalis.nda.elasticsearch.mock;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import nl.naturalis.nda.domain.CommonName;
import nl.naturalis.nda.domain.DefaultClassification;
import nl.naturalis.nda.domain.Expert;
import nl.naturalis.nda.domain.Identification;
import nl.naturalis.nda.domain.Monomial;
import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.domain.SpecimenUnit;
import nl.naturalis.nda.domain.Reference;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.domain.Synonym;
import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.search.Link;
import nl.naturalis.nda.search.MatchInfo;
import nl.naturalis.nda.search.ResultGroup;
import nl.naturalis.nda.search.ResultGroupSet;
import nl.naturalis.nda.search.SearchResult;
import nl.naturalis.nda.search.SearchResultSet;
import nl.naturalis.nda.search.StringMatchInfo;

import org.domainobject.util.FileUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Mock {

	public static void main(String[] args) throws Exception
	{
		Mock mock = new Mock();
		//		mock.occurrenceWithIdentifications();
		//		mock.occurrenceWithIdentificationsWithTaxa();
		//		mock.occurrenceTheFullMonty();
		//		mock.taxaWithscientificNameAndClassification();
		//		mock.taxaTheFullMonty();
		//mock.groupTaxaByName();
		mock.groupSpecimenBySpecificName();
		System.out.println("Done");
	}

	private static final ObjectMapper om = new ObjectMapper();

	static {
		om.setDateFormat(new SimpleDateFormat("YYYY-MM-dd"));
	}

	private final Mocker mocker = new Mocker();
	private final Random random = new Random();


	public void occurrenceWithIdentifications() throws JsonProcessingException
	{
		SearchResultSet<SpecimenUnit> rs = new SearchResultSet<SpecimenUnit>();
		rs.setTotalSize(1386);
		rs.addLink("self", "http://nda.naturalis.nl/occurence/?country=Suriname&offset=150");
		rs.addLink("occurrence.extra-info", "http://nda.naturalis.nl/occurence/extra-info");
		rs.addLink("taxon.cool-taxon-data", "http://nda.naturalis.nl/taxon/cool-data");

		for (int i = 0; i < 20; ++i) {
			SpecimenUnit specimenUnit = mocker.createMock(SpecimenUnit.class, Identification.class);
			Link link0 = new Link("self", "http://nda.naturalis.nl/occurence/?unitId=" + i);
			Link link1 = new Link("occurrence.related-occurences", "http://nda.naturalis.nl/occurence/related/unitId=" + i);
			SearchResult<SpecimenUnit> result = new SearchResult<SpecimenUnit>(specimenUnit);
			List<MatchInfo<?>> matchInfos = new ArrayList<MatchInfo<?>>();
			if (i % 2 == 0) {
				StringMatchInfo inf = new StringMatchInfo();
				inf.setPath("country");
				inf.setValue("Nederland");
				inf.setValueHighlighted("Neder<em>land</em>");
				matchInfos.add(inf);
			}
			else {
				StringMatchInfo inf = new StringMatchInfo();
				inf.setPath("locality");
				inf.setValue("Nederland");
				inf.setValueHighlighted("Neder<em>land</em>");
				matchInfos.add(inf);
			}
			if (i % 5 == 0) {
				if (specimenUnit.getIdentifications().size() > 1) {
					StringMatchInfo inf = new StringMatchInfo();
					inf.setPath("identifications[1].remarks");
					inf.setValue("In Nederland is het altijd leuk");
					inf.setValueHighlighted("In Neder<em>land</em> is het altijd leuk");
					matchInfos.add(inf);
				}
				else {
					StringMatchInfo inf = new StringMatchInfo();
					inf.setPath("identifications[0].references");
					inf.setValue("Het Nederlands Insectenboek");
					inf.setValueHighlighted("Het Neder<em>land</em>s Insectenboek");
					matchInfos.add(inf);
				}
			}
			result.setMatchInfo(matchInfos);
			float score = Float.parseFloat("0." + random.nextInt(99));
			result.setScore(score);
			result.addLink(link0);
			result.addLink(link1);
			rs.addSearchResult(result);
		}
		String json = om.writerWithDefaultPrettyPrinter().writeValueAsString(rs);
		System.out.println(json);
		FileUtil.setContents("C:/test/nda/mock/occurrences-with-identifications.json", json);
	}


	public void occurrenceWithIdentificationsWithTaxa() throws JsonProcessingException
	{
		SearchResultSet<SpecimenUnit> rs = new SearchResultSet<SpecimenUnit>();
		rs.setTotalSize(1386);
		rs.addLink("self", "http://nda.naturalis.nl/occurence/?country=Suriname&offset=1200");
		rs.addLink("occurrence.extra-info", "http://nda.naturalis.nl/occurence/extra-info");
		rs.addLink("taxon.cool-taxon-data", "http://nda.naturalis.nl/taxon/cool-data");

		for (int i = 0; i < 20; ++i) {
			SpecimenUnit specimenUnit = mocker.createMock(SpecimenUnit.class, Identification.class, Taxon.class);
			Link link0 = new Link("self", "http://nda.naturalis.nl/occurence/?unitId=" + i);
			Link link1 = new Link("occurrence.related-occurences", "http://nda.naturalis.nl/occurence/related/unitId=" + i);
			SearchResult<SpecimenUnit> result = new SearchResult<SpecimenUnit>(specimenUnit);
			List<MatchInfo<?>> matchInfos = new ArrayList<MatchInfo<?>>();
			if (i % 2 == 0) {
				StringMatchInfo inf = new StringMatchInfo();
				inf.setPath("country");
				inf.setValue("Nederland");
				inf.setValueHighlighted("Neder<em>land</em>");
				matchInfos.add(inf);
			}
			else {
				StringMatchInfo inf = new StringMatchInfo();
				inf.setPath("locality");
				inf.setValue("Nederland");
				inf.setValueHighlighted("Neder<em>land</em>");
				matchInfos.add(inf);
			}
			if (i % 5 == 0) {
				if (specimenUnit.getIdentifications().size() > 1) {
					StringMatchInfo inf = new StringMatchInfo();
					inf.setPath("identifications[1].remarks");
					inf.setValue("In Nederland is het altijd leuk");
					inf.setValueHighlighted("In Neder<em>land</em> is het altijd leuk");
					matchInfos.add(inf);
				}
				else {
					StringMatchInfo inf = new StringMatchInfo();
					inf.setPath("identifications[0].references");
					inf.setValue("Het Nederlands Insectenboek");
					inf.setValueHighlighted("Het Neder<em>land</em>s Insectenboek");
					matchInfos.add(inf);
				}
			}
			result.setMatchInfo(matchInfos);
			float score = Float.parseFloat("0." + random.nextInt(99));
			result.setScore(score);
			result.addLink(link0);
			result.addLink(link1);
			rs.addSearchResult(result);
		}
		String json = om.writerWithDefaultPrettyPrinter().writeValueAsString(rs);
		System.out.println(json);
		FileUtil.setContents("C:/test/nda/mock/occurrences-with-identifications-with-taxa.json", json);
	}


	public void occurrenceTheFullMonty() throws JsonProcessingException
	{
		SearchResultSet<SpecimenUnit> rs = new SearchResultSet<SpecimenUnit>();
		rs.setTotalSize(1020);
		rs.addLink("self", "http://nda.naturalis.nl/occurence/?country=Suriname&offset=1200");
		rs.addLink("occurrence.extra-info", "http://nda.naturalis.nl/occurence/extra");
		rs.addLink("taxon.cool-taxon-data", "http://nda.naturalis.nl/taxon/cool-data");

		for (int i = 0; i < 20; ++i) {
			SpecimenUnit specimenUnit = mocker.createMock(SpecimenUnit.class, Identification.class, Taxon.class, ScientificName.class,
					DefaultClassification.class, Synonym.class);
			Link link0 = new Link("self", "http://nda.naturalis.nl/occurence/?unitId=" + i);
			Link link1 = new Link("occurrence.related-occurences", "http://nda.naturalis.nl/occurence/related/unitId=" + i);
			SearchResult<SpecimenUnit> result = new SearchResult<SpecimenUnit>(specimenUnit);
			List<MatchInfo<?>> matchInfos = new ArrayList<MatchInfo<?>>();
			if (i % 2 == 0) {
				StringMatchInfo inf = new StringMatchInfo();
				inf.setPath("country");
				inf.setValue("Nederland");
				inf.setValueHighlighted("Neder<em>land</em>");
				matchInfos.add(inf);
			}
			else {
				StringMatchInfo inf = new StringMatchInfo();
				inf.setPath("locality");
				inf.setValue("Nederland");
				inf.setValueHighlighted("Neder<em>land</em>");
				matchInfos.add(inf);
			}
			if (i % 5 == 0) {
				if (specimenUnit.getIdentifications().size() > 1) {
					StringMatchInfo inf = new StringMatchInfo();
					inf.setPath("identifications[1].remarks");
					inf.setValue("In Nederland is het altijd leuk");
					inf.setValueHighlighted("In Neder<em>land</em> is het altijd leuk");
					matchInfos.add(inf);
				}
				else {
					StringMatchInfo inf = new StringMatchInfo();
					inf.setPath("identifications[0].references");
					inf.setValue("Het Nederlands Insectenboek");
					inf.setValueHighlighted("Het Neder<em>land</em>s Insectenboek");
					matchInfos.add(inf);
				}
			}
			result.setMatchInfo(matchInfos);
			float score = Float.parseFloat("0." + random.nextInt(99));
			result.setScore(score);
			result.addLink(link0);
			result.addLink(link1);
			rs.addSearchResult(result);
		}
		String json = om.writerWithDefaultPrettyPrinter().writeValueAsString(rs);
		System.out.println(json);
		FileUtil.setContents("C:/test/nda/mock/occurrence-the-full-monty.json", json);
	}


	public void taxaWithscientificNameAndClassification() throws JsonProcessingException
	{
		SearchResultSet<Taxon> rs = new SearchResultSet<Taxon>();
		rs.setTotalSize(236);
		rs.addLink("self", "http://nda.naturalis.nl/taxon/?offset=200");

		for (int i = 0; i < 20; ++i) {
			Taxon taxon = mocker.createMock(Taxon.class, ScientificName.class, DefaultClassification.class);
			Link link0 = new Link("self", "http://nda.naturalis.nl/taxon/?taxonId=" + i);
			Link link1 = new Link("occurrence.list-all", "http://nda.naturalis.nl/occurence/taxonId=" + i);
			SearchResult<Taxon> result = new SearchResult<Taxon>(taxon);
			List<MatchInfo<?>> matchInfos = new ArrayList<MatchInfo<?>>();
			if (i % 3 == 0) {
				StringMatchInfo inf = new StringMatchInfo();
				inf.setPath("acceptedName.author");
				inf.setValue("Mariska van Dongen");
				inf.setValueHighlighted("M<em>aris</em>ka van Dongen");
				matchInfos.add(inf);
			}
			else if (i % 3 == 1) {
				StringMatchInfo inf = new StringMatchInfo();
				inf.setPath("acceptedName.fullScientificName");
				inf.setValue("Paris Major");
				inf.setValueHighlighted("P<em>aris</em> Major");
				matchInfos.add(inf);
			}
			else if (i % 3 == 2) {
				StringMatchInfo inf = new StringMatchInfo();
				inf.setPath("acceptedName.genusOrMonomial");
				inf.setValue("Paris");
				inf.setValueHighlighted("P<em>aris</em>");
				matchInfos.add(inf);
			}
			result.setMatchInfo(matchInfos);
			float score = Float.parseFloat("0." + random.nextInt(99));
			result.setScore(score);
			result.addLink(link0);
			result.addLink(link1);
			rs.addSearchResult(result);
		}
		String json = om.writerWithDefaultPrettyPrinter().writeValueAsString(rs);
		System.out.println(json);
		FileUtil.setContents("C:/test/nda/mock/taxa-with-scientific-names-and-classification.json", json);
	}


	public void taxaTheFullMonty() throws JsonProcessingException
	{
		SearchResultSet<Taxon> rs = new SearchResultSet<Taxon>();
		rs.setTotalSize(988);
		rs.addLink("self", "http://nda.naturalis.nl/taxon/?offset=200");

		for (int i = 0; i < 20; ++i) {
			Taxon taxon = mocker.createMock(Taxon.class, ScientificName.class, DefaultClassification.class, Synonym.class, CommonName.class,
					Reference.class, Expert.class, Monomial.class);
			Link link0 = new Link("self", "http://nda.naturalis.nl/taxon/?taxonId=" + i);
			Link link1 = new Link("occurrence.list-all", "http://nda.naturalis.nl/occurence/taxonId=" + i);
			SearchResult<Taxon> result = new SearchResult<Taxon>(taxon);
			List<MatchInfo<?>> matchInfos = new ArrayList<MatchInfo<?>>();
			if (i % 3 == 0) {
				StringMatchInfo inf = new StringMatchInfo();
				inf.setPath("acceptedName.author");
				inf.setValue("Mariska van Dongen");
				inf.setValueHighlighted("M<em>aris</em>ka van Dongen");
				matchInfos.add(inf);
			}
			else if (i % 3 == 1) {
				StringMatchInfo inf = new StringMatchInfo();
				inf.setPath("acceptedName.fullScientificName");
				inf.setValue("Paris Major");
				inf.setValueHighlighted("P<em>aris</em> Major");
				matchInfos.add(inf);
			}
			else if (i % 3 == 2) {
				StringMatchInfo inf = new StringMatchInfo();
				inf.setPath("synonyms[0].scientificName.fullScientificName");
				inf.setValue("Paris Major");
				inf.setValueHighlighted("P<em>aris</em> Major");
				matchInfos.add(inf);
			}
			result.setMatchInfo(matchInfos);
			float score = Float.parseFloat("0." + random.nextInt(99));
			result.setScore(score);
			result.addLink(link0);
			result.addLink(link1);
			rs.addSearchResult(result);
		}
		String json = om.writerWithDefaultPrettyPrinter().writeValueAsString(rs);
		System.out.println(json);
		FileUtil.setContents("C:/test/nda/mock/taxa-the-full-monty.json", json);
	}


	public void groupTaxaByName() throws JsonProcessingException
	{
		ResultGroupSet<Taxon, String> rs = new ResultGroupSet<Taxon, String>();
		rs.setTotalSize(988);
		rs.addLink("self", "http://nda.naturalis.nl/taxon/name-search/?term=Malus");

		// GROUP 1: Malus domestica Borkh. (accepted name)

		ResultGroup<Taxon, String> resultGroup = new ResultGroup<Taxon, String>();
		rs.addGroup(resultGroup);
		resultGroup.setSharedValue("Malus domestica Borkh.");
		resultGroup.addLink("taxon.detail", "http://nda.naturalis.nl/taxon/?acceptedName=Malus+domestica");

		Taxon taxon = mocker.createMock(Taxon.class, ScientificName.class, DefaultClassification.class, Synonym.class, CommonName.class,
				Reference.class);
		taxon.setSourceSystem(SourceSystem.COL);
		taxon.getAcceptedName().setFullScientificName("Malus domestica Borkh.");
		SearchResult<Taxon> result = new SearchResult<Taxon>(taxon);
		resultGroup.addSearchResult(result);
		StringMatchInfo matchInfo = new StringMatchInfo();
		matchInfo.setPath("acceptedName.fullScientificName");
		matchInfo.setValue("Malus domestica Borkh.");
		matchInfo.setValueHighlighted("<em>Malus</em> domestica Borkh.");
		result.setScore(0.88F);
		result.addMatchInfo(matchInfo);
		result.addLink("taxon.detail", "http://nda.naturalis.nl/taxon/?sourceSystem=COL&id=87985138");

		taxon = mocker.createMock(Taxon.class, ScientificName.class, DefaultClassification.class, Synonym.class, CommonName.class, Reference.class,
				Expert.class, Monomial.class);
		taxon.setSourceSystem(SourceSystem.NSR);
		taxon.getAcceptedName().setFullScientificName("Malus domestica Borkh.");
		result = new SearchResult<Taxon>(taxon);
		resultGroup.addSearchResult(result);
		matchInfo = new StringMatchInfo();
		matchInfo.setPath("acceptedName.fullScientificName");
		matchInfo.setValue("Malus domestica Borkh.");
		matchInfo.setValueHighlighted("<em>Malus</em> domestica Borkh.");
		result.setScore(0.75F);
		result.addMatchInfo(matchInfo);
		result.addLink("taxon.detail", "http://nda.naturalis.nl/taxon/?sourceSystem=NSR&id=967587987");

		// GROUP 2: Malus sylvestris (accepted name)

		resultGroup = new ResultGroup<Taxon, String>();
		rs.addGroup(resultGroup);
		resultGroup.setSharedValue("Malus sylvestris");
		resultGroup.addLink("taxon.detail", "http://nda.naturalis.nl/taxon/?acceptedName=Malus+sylvestris");

		taxon = mocker.createMock(Taxon.class, ScientificName.class, DefaultClassification.class, Synonym.class, CommonName.class, Reference.class);
		taxon.setSourceSystem(SourceSystem.COL);
		taxon.getAcceptedName().setFullScientificName("Malus sylvestris");
		result = new SearchResult<Taxon>(taxon);
		resultGroup.addSearchResult(result);
		matchInfo = new StringMatchInfo();
		matchInfo.setPath("acceptedName.fullScientificName");
		matchInfo.setValue("Malus sylvestris");
		matchInfo.setValueHighlighted("<em>Malus</em> sylvestris");
		result.setScore(0.67F);
		result.addMatchInfo(matchInfo);
		result.addLink("taxon.detail", "http://nda.naturalis.nl/taxon/?sourceSystem=COL&id=183847983");

		taxon = mocker.createMock(Taxon.class, ScientificName.class, DefaultClassification.class, Synonym.class, CommonName.class);
		taxon.setSourceSystem(SourceSystem.NSR);
		taxon.getAcceptedName().setFullScientificName("Malus sylvestris");
		result = new SearchResult<Taxon>(taxon);
		resultGroup.addSearchResult(result);
		matchInfo = new StringMatchInfo();
		matchInfo.setPath("acceptedName.fullScientificName");
		matchInfo.setValue("Malus sylvestris");
		matchInfo.setValueHighlighted("<em>Malus</em> sylvestris");
		result.setScore(0.91F);
		result.addMatchInfo(matchInfo);
		result.addLink("taxon.detail", "http://nda.naturalis.nl/taxon/?sourceSystem=NSR&id=4876487646");

		// GROUP 3: Afrikaans Malusaapje (common name)

		resultGroup = new ResultGroup<Taxon, String>();
		rs.addGroup(resultGroup);
		resultGroup.setSharedValue("Afrikaans Malusaapje");
		resultGroup.addLink("taxon.detail", "http://nda.naturalis.nl/taxon/?acceptedName=Malusmonkius+africanus");

		taxon = mocker.createMock(Taxon.class, ScientificName.class, DefaultClassification.class, Synonym.class, CommonName.class, Reference.class);
		taxon.setSourceSystem(SourceSystem.NSR);
		taxon.getAcceptedName().setFullScientificName("Malusmonkius africanus L.");
		taxon.getCommonNames().get(0).setName("Afrikaans Malusaapje");
		result = new SearchResult<Taxon>(taxon);
		resultGroup.addSearchResult(result);
		matchInfo = new StringMatchInfo();
		matchInfo.setPath("acceptedName.commonNames[0].name");
		matchInfo.setValue("Afrikaans Malusaapjes");
		matchInfo.setValueHighlighted("Afrikaans <em>Malus</em>aapje");
		result.setScore(0.80F);
		result.addMatchInfo(matchInfo);
		result.addLink("taxon.detail", "http://nda.naturalis.nl/taxon/?sourceSystem=NSR&id=243567419");

		// GROUP 3: Langstaart Maluskonijn (common name)

		resultGroup = new ResultGroup<Taxon, String>();
		rs.addGroup(resultGroup);
		resultGroup.setSharedValue("Langstaart Maluskonijn");
		resultGroup.addLink("taxon.detail", "http://nda.naturalis.nl/taxon/?acceptedName=Konijnus+langstaartis");

		taxon = mocker.createMock(Taxon.class, ScientificName.class, DefaultClassification.class, Synonym.class, CommonName.class, Reference.class);
		taxon.setSourceSystem(SourceSystem.COL);
		taxon.getAcceptedName().setFullScientificName("Konijnus langstaartis");
		taxon.getCommonNames().get(0).setName("Langstaart Maluskonijn");
		result = new SearchResult<Taxon>(taxon);
		resultGroup.addSearchResult(result);
		matchInfo = new StringMatchInfo();
		matchInfo.setPath("acceptedName.commonNames[0].name");
		matchInfo.setValue("Langstaart Maluskonijn");
		matchInfo.setValueHighlighted("Langstaart <em>Malus</em>konijn");
		result.setScore(0.56F);
		result.addMatchInfo(matchInfo);
		result.addLink("taxon.detail", "http://nda.naturalis.nl/taxon/?sourceSystem=COL&id=5933754");

		taxon = mocker.createMock(Taxon.class, ScientificName.class, DefaultClassification.class, Synonym.class, CommonName.class, Reference.class);
		taxon.setSourceSystem(SourceSystem.NSR);
		taxon.getAcceptedName().setFullScientificName("Konijnus langstaartis");
		taxon.getCommonNames().get(0).setName("Langstaart Maluskonijn");
		result = new SearchResult<Taxon>(taxon);
		resultGroup.addSearchResult(result);
		matchInfo = new StringMatchInfo();
		matchInfo.setPath("acceptedName.commonNames[0].name");
		matchInfo.setValue("Langstaart Maluskonijn");
		matchInfo.setValueHighlighted("Langstaart <em>Malus</em>konijn");
		result.setScore(0.82F);
		result.addMatchInfo(matchInfo);
		result.addLink("taxon.detail", "http://nda.naturalis.nl/taxon/?sourceSystem=NSR&id=7979624458");

		// GROUP 4: Pyrus Malus L. (synonym)

		resultGroup = new ResultGroup<Taxon, String>();
		rs.addGroup(resultGroup);
		resultGroup.setSharedValue("Pyrus Malus L.");
		resultGroup.addLink("taxon.detail", "http://nda.naturalis.nl/taxon/?acceptedName=Malus+Sylvestris");

		taxon = mocker.createMock(Taxon.class, ScientificName.class, DefaultClassification.class, Synonym.class, CommonName.class, Reference.class);
		taxon.setSourceSystem(SourceSystem.COL);
		taxon.getAcceptedName().setFullScientificName("Malus sylvestris");
		taxon.getSynonyms().get(0).getScientificName().setFullScientificName("Pyrus Malus L.");
		result = new SearchResult<Taxon>(taxon);
		resultGroup.addSearchResult(result);
		matchInfo = new StringMatchInfo();
		matchInfo.setPath("acceptedName.synonyms[0].scientificName.fullScientificName");
		matchInfo.setValue("Pyrus Malus L.");
		matchInfo.setValueHighlighted("Pyrus <em>Malus</em> L.");
		result.setScore(0.93F);
		result.addMatchInfo(matchInfo);
		result.addLink("taxon.detail", "http://nda.naturalis.nl/taxon/?sourceSystem=COL&id=243567419");

		String json = om.writerWithDefaultPrettyPrinter().writeValueAsString(rs);
		System.out.println(json);
		FileUtil.setContents("C:/test/nda/mock/group-taxa-by-name.json", json);

	}


	public void groupSpecimenBySpecificName() throws JsonProcessingException
	{
		ResultGroupSet<SpecimenUnit, String> rs = new ResultGroupSet<SpecimenUnit, String>();
		rs.setTotalSize(988);
		rs.addLink("self", "http://nda.naturalis.nl/specimen/name-search/?term=Malus");

		// GROUP 1: Malus Mill.

		ResultGroup<SpecimenUnit, String> resultGroup = new ResultGroup<SpecimenUnit, String>();
		rs.addGroup(resultGroup);
		resultGroup.setSharedValue("Malus Mill.");
		resultGroup.addLink("taxon-detail", "http://nda.naturalis.nl/taxon/?acceptedName=Malus+Mill.");

		SpecimenUnit su = mocker.createMock(SpecimenUnit.class, Identification.class, Specimen.class, ScientificName.class);
		su.setSourceSystem(SourceSystem.BRAHMS);
		su.getIdentifications().get(0).getScientificName().setFullScientificName("Malus Mill.");
		SearchResult<SpecimenUnit> result = new SearchResult<SpecimenUnit>(su);
		resultGroup.addSearchResult(result);
		StringMatchInfo matchInfo = new StringMatchInfo();
		result.addMatchInfo(matchInfo);
		matchInfo.setPath("indentifications[0].scientificName.fullScientificName");
		matchInfo.setValue("Malus Mill.");
		matchInfo.setValueHighlighted("<span>Malus</span> Mill.");
		result.setScore(0.97F);
		Link link = new Link("specimen-unit-detail", "http://nda.naturalis.nl/specimen/" + random.nextInt(10000));
		result.addLink(link);
		for (int i = 0; i < su.getSpecimens().size(); ++i) {
			link = new Link("specimen-detail." + i, "http://nda.naturalis.nl/specimen/part/" + random.nextInt(10000));
			result.addLink(link);
		}

		su = mocker.createMock(SpecimenUnit.class, Identification.class, Specimen.class, ScientificName.class);
		su.setSourceSystem(SourceSystem.BRAHMS);
		su.getSpecimens().get(1).getIdentifications().get(1).getScientificName().setFullScientificName("Malus Mill.");
		result = new SearchResult<SpecimenUnit>(su);
		resultGroup.addSearchResult(result);
		matchInfo = new StringMatchInfo();
		result.addMatchInfo(matchInfo);
		matchInfo.setPath("specimens[1].indentifications[1].scientificName.fullScientificName");
		matchInfo.setValue("Malus Mill.");
		matchInfo.setValueHighlighted("<span>Malus</span> Mill.");
		result.setScore(0.97F);
		link = new Link("specimen-unit-detail", "http://nda.naturalis.nl/specimen/" + random.nextInt(100000));
		result.addLink(link);
		for (int i = 0; i < su.getSpecimens().size(); ++i) {
			link = new Link("specimen-detail." + i, "http://nda.naturalis.nl/specimen/part/" + random.nextInt(10000));
			result.addLink(link);
		}

		// GROUP 2: Malus sapiens

		resultGroup = new ResultGroup<SpecimenUnit, String>();
		rs.addGroup(resultGroup);
		resultGroup.setSharedValue("Malus sapiens");
		resultGroup.addLink("taxon-detail", "http://nda.naturalis.nl/taxon/?acceptedName=Malus+sapiens");

		su = mocker.createMock(SpecimenUnit.class, Identification.class, ScientificName.class);
		su.setSourceSystem(SourceSystem.CRS);
		su.getIdentifications().get(0).getScientificName().setFullScientificName("Malus sapiens");
		result = new SearchResult<SpecimenUnit>(su);
		resultGroup.addSearchResult(result);
		matchInfo = new StringMatchInfo();
		result.addMatchInfo(matchInfo);
		matchInfo.setPath("indentifications[0].scientificName.fullScientificName");
		matchInfo.setValue("Malus sapiens");
		matchInfo.setValueHighlighted("<span>Malus</span> sapiens");
		result.setScore(0.91F);
		link = new Link("specimen-unit-detail", "http://nda.naturalis.nl/specimen/" + random.nextInt(10000));

		su = mocker.createMock(SpecimenUnit.class, Identification.class, ScientificName.class);
		su.setSourceSystem(SourceSystem.CRS);
		su.getIdentifications().get(0).getScientificName().setFullScientificName("Malus sapiens");
		result = new SearchResult<SpecimenUnit>(su);
		resultGroup.addSearchResult(result);
		matchInfo = new StringMatchInfo();
		result.addMatchInfo(matchInfo);
		matchInfo.setPath("indentifications[0].scientificName.fullScientificName");
		matchInfo.setValue("Malus sapiens");
		matchInfo.setValueHighlighted("<span>Malus</span> sapiens");
		result.setScore(0.91F);
		link = new Link("specimen-unit-detail", "http://nda.naturalis.nl/specimen/" + random.nextInt(10000));

		// GROUP 3: Malus astropurpea

		resultGroup = new ResultGroup<SpecimenUnit, String>();
		rs.addGroup(resultGroup);
		resultGroup.setSharedValue("Malus astropurpea");
		resultGroup.addLink("taxon-detail", "http://nda.naturalis.nl/taxon/?acceptedName=Malus+astropurpea");

		su = mocker.createMock(SpecimenUnit.class, Specimen.class, Identification.class, ScientificName.class);
		su.setSourceSystem(SourceSystem.BRAHMS);
		su.getIdentifications().get(1).getScientificName().setFullScientificName("Malus astropurpea");
		result = new SearchResult<SpecimenUnit>(su);
		resultGroup.addSearchResult(result);
		matchInfo = new StringMatchInfo();
		result.addMatchInfo(matchInfo);
		matchInfo.setPath("indentifications[1].scientificName.fullScientificName");
		matchInfo.setValue("Malus astropurpea");
		matchInfo.setValueHighlighted("<span>Malus</span> astropurpea");
		result.setScore(0.91F);
		link = new Link("specimen-unit-detail", "http://nda.naturalis.nl/specimen/" + random.nextInt(10000));
		for (int i = 0; i < su.getSpecimens().size(); ++i) {
			link = new Link("specimen-detail." + i, "http://nda.naturalis.nl/specimen/part/" + random.nextInt(10000));
			result.addLink(link);
		}

		su = mocker.createMock(SpecimenUnit.class, Specimen.class, Identification.class, ScientificName.class);
		su.setSourceSystem(SourceSystem.BRAHMS);
		su.getSpecimens().get(1).getIdentifications().get(1).getScientificName().setFullScientificName("Malus astropurpea");
		result = new SearchResult<SpecimenUnit>(su);
		resultGroup.addSearchResult(result);
		matchInfo = new StringMatchInfo();
		result.addMatchInfo(matchInfo);
		matchInfo.setPath("specimens[1].indentifications[1].scientificName.fullScientificName");
		matchInfo.setValue("Malus astropurpea");
		matchInfo.setValueHighlighted("<span>Malus</span> astropurpea");
		result.setScore(0.91F);
		link = new Link("specimen-unit-detail", "http://nda.naturalis.nl/specimen/" + random.nextInt(10000));
		for (int i = 0; i < su.getSpecimens().size(); ++i) {
			link = new Link("specimen-detail." + i, "http://nda.naturalis.nl/specimen/part/" + random.nextInt(10000));
			result.addLink(link);
		}

		String json = om.writerWithDefaultPrettyPrinter().writeValueAsString(rs);
		System.out.println(json);
		FileUtil.setContents("C:/test/nda/mock/groupSpecimenBySpecificName.json", json);

	}

}
