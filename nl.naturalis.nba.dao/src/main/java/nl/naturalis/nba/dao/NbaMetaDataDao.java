package nl.naturalis.nba.dao;

import static nl.naturalis.nba.api.model.metadata.NbaSetting.OPERATOR_CONTAINS_MAX_TERM_LENGTH;
import static nl.naturalis.nba.api.model.metadata.NbaSetting.OPERATOR_CONTAINS_MIN_TERM_LENGTH;
import static nl.naturalis.nba.common.json.JsonUtil.deserialize;
import static nl.naturalis.nba.common.json.JsonUtil.readField;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import nl.naturalis.nba.api.INbaMetaData;
import nl.naturalis.nba.api.model.AreaClass;
import nl.naturalis.nba.api.model.License;
import nl.naturalis.nba.api.model.LicenseType;
import nl.naturalis.nba.api.model.Sex;
import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.api.model.SpatialDatum;
import nl.naturalis.nba.api.model.SpecimenTypeStatus;
import nl.naturalis.nba.api.model.TaxonRelationType;
import nl.naturalis.nba.api.model.TaxonomicStatus;
import nl.naturalis.nba.api.model.metadata.NbaSetting;
import nl.naturalis.nba.common.es.ESDateInput;

public class NbaMetaDataDao implements INbaMetaData {

	private static EnumMap<NbaSetting, Object> settings;

	@Override
	public Object getSetting(NbaSetting setting)
	{
		return getSettings().get(setting);
	}

	@Override
	public Map<NbaSetting, Object> getSettings()
	{
		if (settings == null) {
			settings = new EnumMap<>(NbaSetting.class);
			InputStream is = getClass().getResourceAsStream("/es-settings.json");
			Map<String, Object> esSettings = deserialize(is);
			String path = "analysis.tokenizer.like_tokenizer.min_gram";
			Object val = readField(esSettings, path);
			settings.put(OPERATOR_CONTAINS_MIN_TERM_LENGTH, val);
			path = "analysis.tokenizer.like_tokenizer.max_gram";
			val = readField(esSettings, path);
			settings.put(OPERATOR_CONTAINS_MAX_TERM_LENGTH, val);
		}
		return settings;
	}

	@Override
	public SourceSystem[] getSourceSystems()
	{
		return SourceSystem.getAllSourceSystems();
	}

	@Override
	public String[] getControlledLists()
	{
		return new String[] { "AreaClass", "License", "LicenseType", "RelationType", 
		                      "Sex", "SpatialDatum", "SpecimenTypeStatus", "TaxonomicStatus" };
	}

  @Override
  public AreaClass[] getControlledListAreaClass()
  {
    return AreaClass.values();
  }
  
	@Override
	public License[] getControlledListLicense()
	{
	  return License.values();
	}
	
	@Override
	public LicenseType[] getControlledListLicenseType()
	{
	  return LicenseType.values();
	}
		
	@Override
	public Sex[] getControlledListSex()
	{
	  return Sex.values();
	}
	
	@Override
	public SpatialDatum[] getControlledListSpatialDatum()
	{
	  return SpatialDatum.values();
	}
	
	@Override
	public SpecimenTypeStatus[] getControlledListSpecimenTypeStatus()
	{
	  return SpecimenTypeStatus.values();
	}
	
	@Override
	public TaxonRelationType[] getControlledListTaxonRelationType()
	{
	  return TaxonRelationType.values();
	}
	
	@Override
	public TaxonomicStatus[] getControlledListTaxonomicStatus()
	{
		return TaxonomicStatus.values();
	}
	
	@Override
	public String[] getAllowedDateFormats()
	{
		return ESDateInput.getAcceptedDateFormats();
	}

}
