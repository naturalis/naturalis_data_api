package nl.naturalis.nda.elasticsearch.mock;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Random;

import nl.naturalis.nda.domain.DefaultClassification;
import nl.naturalis.nda.domain.Expert;
import nl.naturalis.nda.domain.GatheringEvent;
import nl.naturalis.nda.domain.Monomial;
import nl.naturalis.nda.domain.MultiMediaObject;
import nl.naturalis.nda.domain.Reference;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.ServiceAccessPoint;
import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.domain.SpecimenIdentification;
import nl.naturalis.nda.domain.SpecimenMultiMediaObject;
import nl.naturalis.nda.domain.Synonym;
import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.domain.TaxonDescription;
import nl.naturalis.nda.domain.TaxonMultiMediaObject;
import nl.naturalis.nda.domain.VernacularName;
import nl.naturalis.nda.search.Link;
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
		//mock.taxaTheFullMonty();
		//mock.groupTaxaByName();
		//mock.groupSpecimenBySpecificName();
		//mock.getMediaObjects();
		//mock.getSpecimensForOtherSearchTerms();
		mock.specimenDetail();
		//mock.taxonDetail();
		//mock.taxonMediaDetail();
		//mock.specimenMediaDetail();
		System.out.println("Done");
	}

	private static final ObjectMapper om = new ObjectMapper();

	static {
		om.setDateFormat(new SimpleDateFormat("YYYY-MM-dd"));
	}

	private final Mocker mocker;
	private final Random random = new Random();


	public Mock()
	{
		mocker = new Mocker();
		mocker.setMaxListSize(SpecimenIdentification.class, 2);
	}


	public void groupTaxaByName() throws JsonProcessingException
	{
		ResultGroupSet<Taxon, String> rs = new ResultGroupSet<Taxon, String>();
		rs.setTotalSize(988);
		rs.addLink("self", "http://nda.naturalis.nl/taxon/name-search/?term=Malus");
		rs.setSearchTerms(Arrays.asList("Malus"));

		// GROUP 1: Malus domestica Borkh. (accepted name)

		ResultGroup<Taxon, String> resultGroup = new ResultGroup<Taxon, String>();
		rs.addGroup(resultGroup);
		resultGroup.setSharedValue("Malus domestica Borkh.");
		resultGroup.addLink("taxon.detail", "http://nda.naturalis.nl/taxon/?acceptedName=Malus+domestica");

		Taxon taxon = mocker.createMock(Taxon.class, ScientificName.class, DefaultClassification.class, Synonym.class, VernacularName.class,
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

		taxon = mocker.createMock(Taxon.class, ScientificName.class, DefaultClassification.class, Synonym.class, VernacularName.class,
				Reference.class, Expert.class, Monomial.class);
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

		taxon = mocker.createMock(Taxon.class, ScientificName.class, DefaultClassification.class, Synonym.class, VernacularName.class,
				Reference.class);
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

		taxon = mocker.createMock(Taxon.class, ScientificName.class, DefaultClassification.class, Synonym.class, VernacularName.class);
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

		taxon = mocker.createMock(Taxon.class, ScientificName.class, DefaultClassification.class, Synonym.class, VernacularName.class,
				Reference.class);
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

		taxon = mocker.createMock(Taxon.class, ScientificName.class, DefaultClassification.class, Synonym.class, VernacularName.class,
				Reference.class);
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

		taxon = mocker.createMock(Taxon.class, ScientificName.class, DefaultClassification.class, Synonym.class, VernacularName.class,
				Reference.class);
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

		taxon = mocker.createMock(Taxon.class, ScientificName.class, DefaultClassification.class, Synonym.class, VernacularName.class,
				Reference.class);
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
		ResultGroupSet<Specimen, String> rs = new ResultGroupSet<Specimen, String>();
		rs.setTotalSize(988);
		rs.addLink("self", "http://nda.naturalis.nl/specimen/name-search/?term=Malus");
		rs.setSearchTerms(Arrays.asList("Malus"));

		// GROUP 1: Malus Mill.

		ResultGroup<Specimen, String> resultGroup = new ResultGroup<Specimen, String>();
		rs.addGroup(resultGroup);
		resultGroup.setSharedValue("Malus Mill.");
		resultGroup.addLink("taxon-detail", "http://nda.naturalis.nl/taxon/?acceptedName=Malus+Mill.");

		Specimen specimen = mocker.createMock(Specimen.class, SpecimenIdentification.class, ScientificName.class);
		specimen.setSourceSystem(SourceSystem.BRAHMS);
		specimen.setSetID("0000000001");
		//BeanPrinter.out(specimen);
		specimen.getIdentifications().get(0).getScientificName().setFullScientificName("Malus Mill.");
		SearchResult<Specimen> result = new SearchResult<Specimen>(specimen);
		resultGroup.addSearchResult(result);
		StringMatchInfo matchInfo = new StringMatchInfo();
		result.addMatchInfo(matchInfo);
		matchInfo.setPath("identifications[0].scientificName.fullScientificName");
		matchInfo.setValue("Malus Mill.");
		matchInfo.setValueHighlighted("<span>Malus</span> Mill.");
		result.setScore(0.97F);
		Link link = new Link("specimen-detail", "http://nda.naturalis.nl/specimen/" + random.nextInt(10000));
		result.addLink(link);

		Specimen related = mocker.createMock(Specimen.class, SpecimenIdentification.class, ScientificName.class);
		related.setSourceSystem(SourceSystem.BRAHMS);
		related.setSetID("0000000001");
		specimen.addOtherSpecimenToSet(related);

		related = mocker.createMock(Specimen.class, SpecimenIdentification.class, ScientificName.class);
		related.setSourceSystem(SourceSystem.BRAHMS);
		related.setSetID("0000000001");
		specimen.addOtherSpecimenToSet(related);

		for (int i = 0; i < specimen.getOtherSpecimensInSet().size(); ++i) {
			link = new Link("specimen-detail.otherSpecimensInSet." + i, "http://nda.naturalis.nl/specimen/" + random.nextInt(10000));
			result.addLink(link);
		}

		specimen = mocker.createMock(Specimen.class, SpecimenIdentification.class, ScientificName.class);
		specimen.setSourceSystem(SourceSystem.BRAHMS);
		specimen.setSetID("0000000002");
		//BeanPrinter.out(specimen);
		specimen.getIdentifications().get(0).getScientificName().setFullScientificName("Malus Mill.");
		result = new SearchResult<Specimen>(specimen);
		resultGroup.addSearchResult(result);
		matchInfo = new StringMatchInfo();
		result.addMatchInfo(matchInfo);
		matchInfo.setPath("identifications[0].scientificName.fullScientificName");
		matchInfo.setValue("Malus Mill.");
		matchInfo.setValueHighlighted("<span>Malus</span> Mill.");
		result.setScore(0.97F);
		link = new Link("specimen-detail", "http://nda.naturalis.nl/specimen/" + random.nextInt(10000));
		result.addLink(link);

		related = mocker.createMock(Specimen.class, SpecimenIdentification.class, ScientificName.class);
		related.setSourceSystem(SourceSystem.BRAHMS);
		related.setSetID("0000000002");
		specimen.addOtherSpecimenToSet(related);

		related = mocker.createMock(Specimen.class, SpecimenIdentification.class, ScientificName.class);
		related.setSourceSystem(SourceSystem.BRAHMS);
		related.setSetID("0000000002");
		specimen.addOtherSpecimenToSet(related);

		for (int i = 0; i < specimen.getOtherSpecimensInSet().size(); ++i) {
			link = new Link("specimen-detail.otherSpecimensInSet." + i, "http://nda.naturalis.nl/specimen/" + random.nextInt(10000));
			result.addLink(link);
		}

		// GROUP 2: Malus silvestris

		resultGroup = new ResultGroup<Specimen, String>();
		rs.addGroup(resultGroup);
		resultGroup.setSharedValue("Malus silvestris");
		resultGroup.addLink("taxon-detail", "http://nda.naturalis.nl/taxon/?acceptedName=Malus+silvestris");

		specimen = mocker.createMock(Specimen.class, SpecimenIdentification.class, ScientificName.class);
		specimen.setSourceSystem(SourceSystem.BRAHMS);
		specimen.setSetID("0000000003");
		specimen.getIdentifications().get(0).getScientificName().setFullScientificName("Malus silvestris");
		result = new SearchResult<Specimen>(specimen);
		resultGroup.addSearchResult(result);
		matchInfo = new StringMatchInfo();
		result.addMatchInfo(matchInfo);
		matchInfo.setPath("identifications[0].scientificName.fullScientificName");
		matchInfo.setValue("Malus silvestris");
		matchInfo.setValueHighlighted("<span>Malus</span> silvestris");
		result.setScore(0.97F);
		link = new Link("specimen-detail", "http://nda.naturalis.nl/specimen/" + random.nextInt(10000));
		result.addLink(link);

		related = mocker.createMock(Specimen.class, SpecimenIdentification.class, ScientificName.class);
		related.setSourceSystem(SourceSystem.BRAHMS);
		related.setSetID("0000000003");
		specimen.addOtherSpecimenToSet(related);

		related = mocker.createMock(Specimen.class, SpecimenIdentification.class, ScientificName.class);
		related.setSourceSystem(SourceSystem.BRAHMS);
		related.setSetID("0000000003");
		specimen.addOtherSpecimenToSet(related);

		for (int i = 0; i < specimen.getOtherSpecimensInSet().size(); ++i) {
			link = new Link("specimen-detail.otherSpecimensInSet." + i, "http://nda.naturalis.nl/specimen/" + random.nextInt(10000));
			result.addLink(link);
		}

		// GROUP 3: Malus sapiens

		resultGroup = new ResultGroup<Specimen, String>();
		rs.addGroup(resultGroup);
		resultGroup.setSharedValue("Malus sapiens");
		resultGroup.addLink("taxon-detail", "http://nda.naturalis.nl/taxon/?acceptedName=Malus+sapiens");

		specimen = mocker.createMock(Specimen.class, SpecimenIdentification.class, ScientificName.class);
		specimen.setSourceSystem(SourceSystem.CRS);
		specimen.setSetID(null);
		specimen.getIdentifications().get(0).getScientificName().setFullScientificName("Malus sapiens");
		result = new SearchResult<Specimen>(specimen);
		resultGroup.addSearchResult(result);
		matchInfo = new StringMatchInfo();
		result.addMatchInfo(matchInfo);
		matchInfo.setPath("identifications[0].scientificName.fullScientificName");
		matchInfo.setValue("Malus sapiens");
		matchInfo.setValueHighlighted("<span>Malus</span> sapiens");
		result.setScore(0.97F);
		link = new Link("specimen-detail", "http://nda.naturalis.nl/specimen/" + random.nextInt(10000));
		result.addLink(link);

		// GROUP 4: Malus bombasticus

		resultGroup = new ResultGroup<Specimen, String>();
		rs.addGroup(resultGroup);
		resultGroup.setSharedValue("Malus bombasticus");
		resultGroup.addLink("taxon-detail", "http://nda.naturalis.nl/taxon/?acceptedName=Malus+bombasticus");

		specimen = mocker.createMock(Specimen.class, SpecimenIdentification.class, ScientificName.class);
		specimen.setSourceSystem(SourceSystem.CRS);
		specimen.setSetID(null);
		specimen.getIdentifications().get(0).getScientificName().setFullScientificName("Malus bombasticus");
		result = new SearchResult<Specimen>(specimen);
		resultGroup.addSearchResult(result);
		matchInfo = new StringMatchInfo();
		result.addMatchInfo(matchInfo);
		matchInfo.setPath("identifications[0].scientificName.fullScientificName");
		matchInfo.setValue("Malus bombasticus");
		matchInfo.setValueHighlighted("<span>Malus</span> bombasticus");
		result.setScore(0.97F);
		link = new Link("specimen-detail", "http://nda.naturalis.nl/specimen/" + random.nextInt(10000));
		result.addLink(link);

		String json = om.writerWithDefaultPrettyPrinter().writeValueAsString(rs);
		//System.out.println(json);
		FileUtil.setContents("C:/test/nda/mock/groupSpecimenBySpecificName.json", json);

	}

/*
	public void getMediaObjects() throws JsonProcessingException
	{
		SearchResultSet<MultiMediaObject> rs = new SearchResultSet<MultiMediaObject>();
		rs.setTotalSize(988);
		rs.addLink("self", "http://nda.naturalis.nl/multimedia/?term=Malus");
		rs.setSearchTerms(Arrays.asList("Malus"));

		TaxonMultiMediaObject tmmo = mocker.createMock(TaxonMultiMediaObject.class, ScientificName.class);
		tmmo.setCaption("De leuke Malus Mill.");
		tmmo.addServiceAccessPoint("http://medialib.naturalis.nl/NSR-1234578", "JPEG", ServiceAccessPoint.Variant.MEDIUM_QUALITY);
		Taxon taxon = mocker.createMock(Taxon.class, ScientificName.class);
		taxon.getAcceptedName().setFullScientificName("Malus Mill.");
		taxon.setSourceSystem(SourceSystem.NSR);
		tmmo.setTaxon(taxon);
		SearchResult<MultiMediaObject> result = new SearchResult<MultiMediaObject>(tmmo);
		rs.addSearchResult(result);
		result.addLink("multimedia-detail", "http://nda.naturalis.nl/multimedia/" + random.nextInt(10000));
		tmmo.getScientificNames().get(0).setFullScientificName("Malus Mill.");
		StringMatchInfo matchInfo = new StringMatchInfo();
		matchInfo.setPath("scientificNames[0].fullScientificName");
		matchInfo.setValue("Malus Mill.");
		matchInfo.setValueHighlighted("<span>Malus</span> Mill.");
		result.addMatchInfo(matchInfo);

		tmmo = mocker.createMock(TaxonMultiMediaObject.class, ScientificName.class);
		tmmo.setCaption("Een appeltje voor de dorst :-)");
		tmmo.addServiceAccessPoint("http://medialib.naturalis.nl/NSR-87398745", "JPEG", ServiceAccessPoint.Variant.THUMBNAIL);
		taxon = mocker.createMock(Taxon.class, ScientificName.class);
		taxon.getAcceptedName().setFullScientificName("Malus silvestris");
		taxon.setSourceSystem(SourceSystem.NSR);
		tmmo.setTaxon(taxon);
		result = new SearchResult<MultiMediaObject>(tmmo);
		rs.addSearchResult(result);
		result.addLink("multimedia-detail", "http://nda.naturalis.nl/multimedia/" + random.nextInt(10000));
		tmmo.getScientificNames().get(0).setFullScientificName("Malus silvestris");
		matchInfo = new StringMatchInfo();
		matchInfo.setPath("scientificNames[0].fullScientificName");
		matchInfo.setValue("Malus silvestris");
		matchInfo.setValueHighlighted("<span>Malus</span> silvestris");
		result.addMatchInfo(matchInfo);

		SpecimenMultiMediaObject smmo = mocker.createMock(SpecimenMultiMediaObject.class, ScientificName.class);
		smmo.setCaption("Malus bombasticus in al zijn glorie");
		smmo.addServiceAccessPoint("http://medialib.naturalis.nl/ZMA.RMNH.453563217.l", "JPEG", ServiceAccessPoint.Variant.MEDIUM_QUALITY);
		Specimen specimen = mocker.createMock(Specimen.class, SpecimenIdentification.class, ScientificName.class);
		specimen.getIdentifications().get(0).getScientificName().setFullScientificName("Malus bombasticus");
		specimen.setSourceSystem(SourceSystem.CRS);
		smmo.setSpecimen(specimen);
		result = new SearchResult<MultiMediaObject>(smmo);
		rs.addSearchResult(result);
		result.addLink("multimedia-detail", "http://nda.naturalis.nl/multimedia/" + random.nextInt(10000));
		smmo.getScientificNames().get(0).setFullScientificName("Malus bombasticus");
		matchInfo = new StringMatchInfo();
		matchInfo.setPath("scientificNames[0].fullScientificName");
		matchInfo.setValue("Malus Mill.");
		matchInfo.setValueHighlighted("<span>Malus</span> bombasticus");
		result.addMatchInfo(matchInfo);

		smmo = mocker.createMock(SpecimenMultiMediaObject.class, ScientificName.class);
		smmo.setCaption("Glaspreparaat van een Malus bombasticus");
		smmo.addServiceAccessPoint("http://medialib.naturalis.nl/ZMA.RMNH.123456", "JPEG", ServiceAccessPoint.Variant.THUMBNAIL);
		specimen = mocker.createMock(Specimen.class, SpecimenIdentification.class, ScientificName.class);
		specimen.getIdentifications().get(0).getScientificName().setFullScientificName("Malus bombasticus");
		specimen.setSourceSystem(SourceSystem.CRS);
		smmo.setSpecimen(specimen);
		result = new SearchResult<MultiMediaObject>(smmo);
		rs.addSearchResult(result);
		result.addLink("multimedia-detail", "http://nda.naturalis.nl/multimedia/" + random.nextInt(10000));
		smmo.getScientificNames().get(0).setFullScientificName("Malus bombasticus");
		matchInfo = new StringMatchInfo();
		matchInfo.setPath("scientificNames[0].fullScientificName");
		matchInfo.setValue("Malus Mill.");
		matchInfo.setValueHighlighted("<span>Malus</span> bombasticus");
		result.addMatchInfo(matchInfo);

		String json = om.writerWithDefaultPrettyPrinter().writeValueAsString(rs);
		//System.out.println(json);
		FileUtil.setContents("C:/test/nda/mock/getMediaObjects.json", json);
	}
*/

	public void getSpecimensForOtherSearchTerms() throws JsonProcessingException
	{
		SearchResultSet<Specimen> rs = new SearchResultSet<Specimen>();
		rs.setTotalSize(123);
		rs.addLink("self", "http://nda.naturalis.nl/specimen/?term=Malus");
		rs.setSearchTerms(Arrays.asList("Malus"));

		SearchResult<Specimen> result = new SearchResult<Specimen>();
		rs.addSearchResult(result);
		Specimen specimen = mocker.createMock(Specimen.class, SpecimenIdentification.class, ScientificName.class, DefaultClassification.class);
		specimen.setSourceSystem(SourceSystem.BRAHMS);
		result.setResult(specimen);
		specimen.getIdentifications().get(0).getDefaultClassification().setGenus("Malus");
		GatheringEvent gatheringEvent = new GatheringEvent();
		specimen.setGatheringEvent(gatheringEvent);
		gatheringEvent.setCountry("Alamalusia");
		String unitId = "ZMA.RMNH." + random.nextInt();
		specimen.setUnitID(unitId);
		result.addLink("specimen-detail", "http://nda.naturalis.nl/specimen/" + unitId);
		result.setScore(.7F);
		StringMatchInfo matchInfo = new StringMatchInfo();
		matchInfo.setPath("identifications[0].defaultClassification.genus");
		matchInfo.setValue("Malus");
		matchInfo.setValueHighlighted("<span>Malus</span>");
		result.addMatchInfo(matchInfo);
		matchInfo = new StringMatchInfo();
		matchInfo.setPath("gatheringEvent.country");
		matchInfo.setValue("Alamalusia");
		matchInfo.setValueHighlighted("Ala<span>malus</span>ia");
		result.addMatchInfo(matchInfo);

		result = new SearchResult<Specimen>();
		rs.addSearchResult(result);
		specimen = mocker.createMock(Specimen.class, SpecimenIdentification.class, ScientificName.class, DefaultClassification.class);
		specimen.setSourceSystem(SourceSystem.CRS);
		result.setResult(specimen);
		specimen.getIdentifications().get(0).getDefaultClassification().setGenus("Malus");
		unitId = "ZMA.RMNH." + random.nextInt();
		specimen.setUnitID(unitId);
		result.addLink("specimen-detail", "http://nda.naturalis.nl/specimen/" + unitId);
		result.setScore(.73F);
		matchInfo = new StringMatchInfo();
		matchInfo.setPath("title");
		matchInfo.setValue("Het malusje van alles");
		matchInfo.setValueHighlighted("Het <span>malus</span>je van alles");
		result.addMatchInfo(matchInfo);
		matchInfo = new StringMatchInfo();
		matchInfo.setPath("country");
		matchInfo.setValue("Alamalusia");
		matchInfo.setValueHighlighted("Ala<span>malus</span>ia");
		result.addMatchInfo(matchInfo);

		result = new SearchResult<Specimen>();
		rs.addSearchResult(result);
		specimen = mocker.createMock(Specimen.class, SpecimenIdentification.class, ScientificName.class, DefaultClassification.class);
		specimen.setSourceSystem(SourceSystem.CRS);
		result.setResult(specimen);
		gatheringEvent = new GatheringEvent();
		specimen.setGatheringEvent(gatheringEvent);
		gatheringEvent.setCountry("Malusonie");
		specimen.setTitle("In Malusonie komen veel tijgers voor");
		unitId = "ZMA.RMNH." + random.nextInt();
		specimen.setUnitID(unitId);
		result.addLink("specimen-detail", "http://nda.naturalis.nl/specimen/" + unitId);
		result.setScore(.78F);
		matchInfo = new StringMatchInfo();
		matchInfo.setPath("title");
		matchInfo.setValue("In Malusonie komen veel tijgers voor");
		matchInfo.setValueHighlighted("In <span>Malus</span>ionie komen veel tijgers voor");
		result.addMatchInfo(matchInfo);
		matchInfo = new StringMatchInfo();
		matchInfo.setPath("gatheringEvent.country");
		matchInfo.setValue("Malusonie");
		matchInfo.setValueHighlighted("<span>Malus</span>onie");
		result.addMatchInfo(matchInfo);

		result = new SearchResult<Specimen>();
		rs.addSearchResult(result);
		specimen = mocker.createMock(Specimen.class, SpecimenIdentification.class, ScientificName.class, DefaultClassification.class);
		specimen.setSourceSystem(SourceSystem.CRS);
		result.setResult(specimen);
		specimen.getIdentifications().get(0).getDefaultClassification().setGenus("Malus");
		unitId = "ZMA.RMNH." + random.nextInt();
		specimen.setUnitID(unitId);
		result.addLink("specimen-detail", "http://nda.naturalis.nl/specimen/" + unitId);
		result.setScore(.73F);
		matchInfo = new StringMatchInfo();
		matchInfo.setPath("title");
		matchInfo.setValue("Het malusje van alles");
		matchInfo.setValueHighlighted("Het <span>malus</span>je van alles");
		result.addMatchInfo(matchInfo);
		matchInfo = new StringMatchInfo();
		matchInfo.setPath("country");
		matchInfo.setValue("Alamalusia");
		matchInfo.setValueHighlighted("Ala<span>malus</span>ia");
		result.addMatchInfo(matchInfo);

		result = new SearchResult<Specimen>();
		rs.addSearchResult(result);
		specimen = mocker.createMock(Specimen.class, SpecimenIdentification.class, ScientificName.class, DefaultClassification.class);
		specimen.setSourceSystem(SourceSystem.CRS);
		result.setResult(specimen);
		gatheringEvent = new GatheringEvent();
		specimen.setGatheringEvent(gatheringEvent);
		gatheringEvent.setCountry("Malusonie");
		specimen.setTitle("In Malusonie komen veel tijgers voor");
		unitId = "ZMA.RMNH." + random.nextInt();
		specimen.setUnitID(unitId);
		result.addLink("specimen-detail", "http://nda.naturalis.nl/specimen/" + unitId);
		result.setScore(.78F);
		matchInfo = new StringMatchInfo();
		matchInfo.setPath("title");
		matchInfo.setValue("In Malusonie komen veel tijgers voor");
		matchInfo.setValueHighlighted("In <span>Malus</span>ionie komen veel tijgers voor");
		result.addMatchInfo(matchInfo);
		matchInfo = new StringMatchInfo();
		matchInfo.setPath("country");
		matchInfo.setValue("Malusonie");
		matchInfo.setValueHighlighted("<span>Malus</span>onie");
		result.addMatchInfo(matchInfo);

		String json = om.writerWithDefaultPrettyPrinter().writeValueAsString(rs);
		//System.out.println(json);
		FileUtil.setContents("C:/test/nda/mock/getSpecimensForOtherSearchTerms.json", json);
	}


	public void specimenDetail() throws JsonProcessingException
	{
		SearchResultSet<Specimen> rs = new SearchResultSet<Specimen>();
		rs.setTotalSize(1);
		int id = 2 + random.nextInt(100000);
		rs.addLink("self", "http://nda.naturalis.nl/specimen/ZMA.RMNH." + id);
		rs.addLink("prev", "http://nda.naturalis.nl/specimen/ZMA.RMNH." + (id - 1));
		rs.addLink("next", "http://nda.naturalis.nl/specimen/ZMA.RMNH." + (id + 1));
		rs.addLink("associated-taxa", "http://nda.naturalis.nl/taxon/accepted-name/Larus+fuscus");

		SearchResult<Specimen> result = new SearchResult<Specimen>();
		rs.addSearchResult(result);
		mocker.setMaxListSize(SpecimenIdentification.class, 1);
		Specimen specimen = mocker.createMock(Specimen.class, SpecimenIdentification.class, ScientificName.class, DefaultClassification.class);
		specimen.setUnitID("ZMA.RMNH." + id);
		specimen.setSourceSystem(SourceSystem.BRAHMS);
		specimen.getIdentifications().get(0).getScientificName().setFullScientificName("Larus fuscus");
		result.setResult(specimen);

		String json = om.writerWithDefaultPrettyPrinter().writeValueAsString(rs);
		//System.out.println(json);
		FileUtil.setContents("C:/test/nda/mock/specimen-detail.json", json);

	}


	public void taxonDetail() throws JsonProcessingException
	{
		SearchResultSet<Taxon> rs = new SearchResultSet<Taxon>();
		rs.setTotalSize(2);
		int id = 2 + random.nextInt(100000);
		rs.addLink("self", "http://nda.naturalis.nl/taxon/" + id);
		rs.addLink("prev", "http://nda.naturalis.nl/taxon/" + (id - 2));
		rs.addLink("next", "http://nda.naturalis.nl/taxon/" + (id + 2));
		rs.addLink("get-specimens", "http://nda.naturalis.nl/specimen/for-taxon/Larus+fuscus");

		SearchResult<Taxon> result = new SearchResult<Taxon>();
		rs.addSearchResult(result);
		mocker.setMaxListSize(Synonym.class, 3);
		mocker.setMaxListSize(TaxonDescription.class, 0);
		mocker.setMaxListSize(VernacularName.class, 3);
		Taxon taxon = mocker.createMock(Taxon.class, DefaultClassification.class, VernacularName.class, Synonym.class, TaxonDescription.class,
				ScientificName.class, Expert.class);
		taxon.setSourceSystem(SourceSystem.COL);
		taxon.setSourceSystemId("" + id);
		taxon.getAcceptedName().setFullScientificName("Larus Fuscus");
		result.setResult(taxon);

		result = new SearchResult<Taxon>();
		rs.addSearchResult(result);
		mocker.setMaxListSize(TaxonDescription.class, 3);
		mocker.setMaxListSize(VernacularName.class, 1);
		taxon = mocker.createMock(Taxon.class, DefaultClassification.class, VernacularName.class, Synonym.class, TaxonDescription.class,
				ScientificName.class, Expert.class);
		taxon.setSourceSystem(SourceSystem.NSR);
		taxon.setSourceSystemId("" + (id + 1));
		taxon.getAcceptedName().setFullScientificName("Larus Fuscus");
		result.setResult(taxon);

		String json = om.writerWithDefaultPrettyPrinter().writeValueAsString(rs);
		//System.out.println(json);
		FileUtil.setContents("C:/test/nda/mock/taxon-detail.json", json);

	}

/*
	public void taxonMediaDetail() throws JsonProcessingException
	{
		SearchResultSet<MultiMediaObject> rs = new SearchResultSet<MultiMediaObject>();
		rs.setTotalSize(2);
		int id = 2 + random.nextInt(100000);
		rs.addLink("self", "http://nda.naturalis.nl/media/" + id);
		rs.addLink("prev", "http://nda.naturalis.nl/media/" + (id - 2));
		rs.addLink("next", "http://nda.naturalis.nl/media/" + (id + 2));
		rs.addLink("taxon-detail", "http://nda.naturalis.nl/taxon/NSR-1234567");

		SearchResult<MultiMediaObject> result = new SearchResult<MultiMediaObject>();
		rs.addSearchResult(result);

		TaxonMultiMediaObject tmmo = mocker.createMock(TaxonMultiMediaObject.class, ServiceAccessPoint.class);
		tmmo.setCaption("De leuke Malus Mill.");
		tmmo.addServiceAccessPoint("http://medialib.naturalis.nl/NSR-123457", "JPEG", ServiceAccessPoint.Variant.MEDIUM_QUALITY);

		DefaultClassification dc = mocker.createMock(DefaultClassification.class);
		ScientificName sn = mocker.createMock(ScientificName.class);
		Taxon taxon = mocker.createMock(Taxon.class);
		tmmo.setTaxon(taxon);
		taxon.setAcceptedName(sn);
		taxon.setDefaultClassification(dc);
		tmmo.setScientificNames(Arrays.asList(sn));
		tmmo.setDefaultClassifications(Arrays.asList(dc));
		tmmo.getTaxon().setSourceSystem(SourceSystem.NSR);
		tmmo.getTaxon().setSourceSystemId("NSR-1234567");
		result.setResult(tmmo);

		String json = om.writerWithDefaultPrettyPrinter().writeValueAsString(rs);
		//System.out.println(json);
		FileUtil.setContents("C:/test/nda/mock/taxon-media-detail.json", json);
	}
*/

/*
	public void specimenMediaDetail() throws JsonProcessingException
	{
		SearchResultSet<MultiMediaObject> rs = new SearchResultSet<MultiMediaObject>();
		rs.setTotalSize(2);
		int id = 2 + random.nextInt(100000);
		rs.addLink("self", "http://nda.naturalis.nl/media/" + id);
		rs.addLink("prev", "http://nda.naturalis.nl/media/" + (id - 2));
		rs.addLink("next", "http://nda.naturalis.nl/media/" + (id + 2));
		rs.addLink("specimen-detail", "http://nda.naturalis.nl/specimen/ZMA.RMNH.453563217.l");

		SearchResult<MultiMediaObject> result = new SearchResult<MultiMediaObject>();
		rs.addSearchResult(result);
		
		SpecimenMultiMediaObject smmo = mocker.createMock(SpecimenMultiMediaObject.class, ScientificName.class);
		smmo.setCaption("Malus bombasticus in al zijn glorie");
		smmo.addServiceAccessPoint("http://medialib.naturalis.nl/ZMA.RMNH.453563217.l", "JPEG", ServiceAccessPoint.Variant.MEDIUM_QUALITY);
		Specimen specimen = mocker.createMock(Specimen.class, SpecimenIdentification.class, ScientificName.class);
		specimen.getIdentifications().get(0).getScientificName().setFullScientificName("Malus bombasticus");
		specimen.setSourceSystem(SourceSystem.CRS);
		smmo.setSpecimen(specimen);
		result = new SearchResult<MultiMediaObject>(smmo);
		rs.addSearchResult(result);
		smmo.getScientificNames().get(0).setFullScientificName("Malus bombasticus");

		String json = om.writerWithDefaultPrettyPrinter().writeValueAsString(rs);
		//System.out.println(json);
		FileUtil.setContents("C:/test/nda/mock/specimen-media-detail.json", json);
	}
*/
	
}
