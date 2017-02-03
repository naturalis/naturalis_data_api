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

public class EqualsConditionTranslatorTest {

	private static MappingInfo<Specimen> mappingInfo;

	static {
		Mapping<Specimen> mapping = MappingFactory.getMapping(Specimen.class);
		mappingInfo = new MappingInfo<>(mapping);
	}

	@Test
	public void testWithStringValue_01() throws InvalidQueryException
	{
		SearchCondition condition = new SearchCondition("identifications.scientificName.genusOrMonomial", LIKE, "RGM.126");
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		SearchSpec ss = new SearchSpec();
		ss.addCondition(condition);
		ss.setNonScoring(true);
		SearchRequestBuilder query = new SearchSpecTranslator(ss, DocumentType.SPECIMEN).translate();
		System.out.println(query);
		
	}

}
