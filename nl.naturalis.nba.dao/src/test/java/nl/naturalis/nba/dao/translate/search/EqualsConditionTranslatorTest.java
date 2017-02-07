package nl.naturalis.nba.dao.translate.search;

import static nl.naturalis.nba.api.ComparisonOperator.*;
import static nl.naturalis.nba.dao.translate.search.ConditionTranslatorFactory.getTranslator;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.junit.Test;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.SearchCondition;
import nl.naturalis.nba.api.SearchSpec;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.common.es.map.Mapping;
import nl.naturalis.nba.common.es.map.MappingFactory;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.dao.DocumentType;

@SuppressWarnings("static-method")
public class EqualsConditionTranslatorTest {

	private static MappingInfo<Specimen> mappingInfo;

	static {
		Mapping<Specimen> mapping = MappingFactory.getMapping(Specimen.class);
		mappingInfo = new MappingInfo<>(mapping);
	}

	@Test
	public void testWithStringValue_01() throws InvalidQueryException
	{
		SearchCondition condition1 = new SearchCondition("identifications.scientificName.genusOrMonomial", LIKE, "erica");
		SearchCondition condition2 = new SearchCondition("identifications.scientificName.specificEpithet", EQUALS, "benthamiana");
		ConditionTranslator ct = getTranslator(condition1, mappingInfo);
		SearchSpec ss = new SearchSpec();
		ss.addCondition(condition1);
		ss.addCondition(condition2);
		ss.setNonScoring(true);
		SearchRequestBuilder query = new SearchSpecTranslator(ss, DocumentType.SPECIMEN).translate();
		System.out.println(query);
		
	}

}
