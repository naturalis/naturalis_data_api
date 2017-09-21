package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;

import java.util.Map;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.metadata.NbaSetting;
import nl.naturalis.nba.utils.ConfigObject;

public class SpecimenMetaDataDao extends NbaDocumentMetaDataDao<Specimen> {

	public SpecimenMetaDataDao()
	{
		super(SPECIMEN);
	}

	@Override
	public Map<NbaSetting, Object> getSettings()
	{
		Map<NbaSetting, Object> settings = super.getSettings();
		ConfigObject cfg = DaoRegistry.getInstance().getConfiguration();
		String property = "nl.naturalis.nba.specimen.groupByScientificName.maxNumBuckets";
		Integer value = cfg.required(property, Integer.class);
		settings.put(NbaSetting.SPECIMEN_GROUP_BY_SCIENTIFIC_NAME_MAX_NUM_BUCKETS, value);
		return settings;
	}
}
