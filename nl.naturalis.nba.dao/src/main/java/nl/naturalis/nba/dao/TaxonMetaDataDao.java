package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.DocumentType.TAXON;

import java.util.Map;

import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.metadata.NbaSetting;
import nl.naturalis.nba.utils.ConfigObject;

public class TaxonMetaDataDao extends NbaDocumentMetaDataDao<Taxon> {

	public TaxonMetaDataDao()
	{
		super(TAXON);
	}


	@Override
	public Map<NbaSetting, Object> getSettings()
	{
		Map<NbaSetting, Object> settings = super.getSettings();
		ConfigObject cfg = DaoRegistry.getInstance().getConfiguration();
		String property = "nl.naturalis.nba.taxon.groupByScientificName.maxNumBuckets";
		Integer value = cfg.required(property, Integer.class);
		settings.put(NbaSetting.TAXON_GROUP_BY_SCIENTIFIC_NAME_MAX_NUM_BUCKETS, value);
		return settings;
	}
}
