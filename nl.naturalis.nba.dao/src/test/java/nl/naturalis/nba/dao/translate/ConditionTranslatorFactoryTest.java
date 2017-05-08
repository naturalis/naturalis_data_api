package nl.naturalis.nba.dao.translate;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.geojson.LngLatAlt;
import org.geojson.MultiPolygon;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;

import nl.naturalis.nba.api.ComparisonOperator;
import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.DocumentType;

@SuppressWarnings("static-method")
public class ConditionTranslatorFactoryTest {

	@Test
	public void test_01() throws InvalidConditionException
	{
		QueryCondition qc = new QueryCondition();
		qc.setField(new Path("gatheringEvent.siteCoordinates.geoShape"));
		qc.setOperator(ComparisonOperator.IN);
		MultiPolygon polygon = new MultiPolygon();
		TypeReference<List<List<List<LngLatAlt>>>> typeRef = new TypeReference<List<List<List<LngLatAlt>>>>() {};
		String s = "[ [ [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ], [ [100.35, 0.35], [100.65, 0.35], [100.65, 0.65], [100.35, 0.65], [100.35, 0.35] ] ] ]";
		List<List<List<LngLatAlt>>> coords = JsonUtil.deserialize(s, typeRef);
		polygon.setCoordinates(coords);
		qc.setValue(polygon);
		ConditionTranslator translator = ConditionTranslatorFactory.getTranslator(qc, DocumentType.SPECIMEN);
		assertEquals("01",ShapeInShapeConditionTranslator.class, translator.getClass());
	}

}
