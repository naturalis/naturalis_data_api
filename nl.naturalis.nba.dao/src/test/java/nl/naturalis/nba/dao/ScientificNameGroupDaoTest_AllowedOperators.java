package nl.naturalis.nba.dao;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import nl.naturalis.nba.api.NoSuchFieldException;
import nl.naturalis.nba.api.model.metadata.FieldInfo;

public class ScientificNameGroupDaoTest_AllowedOperators {

	/*
	 * Tests if allowed operators for fields in SCIENTIFIC_NAME_GROUP match their
	 * respective counterparts in TAXON and SPECIMEN documents
	 */
	@Test
	public void test_01() throws NoSuchFieldException {

		ScientificNameGroupMetaDataDao sngdao = new ScientificNameGroupMetaDataDao();
		SpecimenMetaDataDao mdao = new SpecimenMetaDataDao();
		TaxonMetaDataDao tdao = new TaxonMetaDataDao();

		Map<String, FieldInfo> sngFieldInfos = sngdao.getFieldInfo(sngdao.getPaths(true));
		Map<String, FieldInfo> specimenFieldInfos = mdao.getFieldInfo(mdao.getPaths(true));
		Map<String, FieldInfo> taxonFieldInfos = tdao.getFieldInfo(tdao.getPaths(true));

		// iterate over each field of the Scientific name group and check
		// whether it is also present in a taxon or specimen metadata
		Iterator<Entry<String, FieldInfo>> it = sngFieldInfos.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, FieldInfo> entry = it.next();
			String field = entry.getKey();
			// check for prefix 'specimens.' and 'taxa.'. If present, the field
			// *might be* borrowed from SPECIMEN or TAXA
			Map<String, FieldInfo> currentFieldInfos = null;
			if (field.matches("^specimens\\..+")) {
				currentFieldInfos = specimenFieldInfos;
			} else if (field.matches("^taxa\\..+")) {
				currentFieldInfos = taxonFieldInfos;
			} else {
				// do not process other fields than the ones from specimen and taxa
				continue;			
			}

			// strip prefix
			field = field.replaceFirst("^(specimens\\.|taxa\\.)", "");
			// 'matchingIdentifications' and 'otherIdentifications' map to 'identifications'
			field = field.replaceAll("(matchingIdentifications|otherIdentifications)\\.", "identifications\\.");
			if (currentFieldInfos.containsKey(field)) {
				FieldInfo fi1 = entry.getValue();
				FieldInfo fi2 = currentFieldInfos.get(field);
				assertEquals(fi1.getAllowedOperators(), fi2.getAllowedOperators());
			}
		}
	}
																		
}
