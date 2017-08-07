package nl.naturalis.nba.dao;

import static nl.naturalis.nba.api.model.metadata.NbaSetting.OPERATOR_CONTAINS_MAX_TERM_LENGTH;
import static nl.naturalis.nba.api.model.metadata.NbaSetting.OPERATOR_CONTAINS_MIN_TERM_LENGTH;
import static nl.naturalis.nba.common.json.JsonUtil.deserialize;
import static nl.naturalis.nba.common.json.JsonUtil.readField;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.EnumMap;
import java.util.Map;

import nl.naturalis.nba.api.INbaMetaData;
import nl.naturalis.nba.api.model.PhaseOrStage;
import nl.naturalis.nba.api.model.Sex;
import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.api.model.SpecimenTypeStatus;
import nl.naturalis.nba.api.model.TaxonomicStatus;
import nl.naturalis.nba.api.model.metadata.NbaSetting;

public class NbaMetaDataDao implements INbaMetaData {

	public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat(
			DEFAULT_DATE_PATTERN);
	private static final SimpleDateFormat SDF1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final SimpleDateFormat SDF2 = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat SDF3 = new SimpleDateFormat("yyyy-MM");
	private static final SimpleDateFormat SDF4 = new SimpleDateFormat("yyyy");

	public static final SimpleDateFormat[] ACCEPTED_DATE_FORMATS = new SimpleDateFormat[] {
			DEFAULT_DATE_FORMAT, SDF1, SDF2, SDF3, SDF4 };

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
		return new String[] { "PhaseOrStage", "Sex", "SpecimenTypeStatus", "TaxonomicStatus" };
	}

	@Override
	public Sex[] getControlledListSex()
	{
		return Sex.values();
	}

	@Override
	public PhaseOrStage[] getControlledListPhaseOrStage()
	{
		return PhaseOrStage.values();
	}

	@Override
	public TaxonomicStatus[] getControlledListTaxonomicStatus()
	{
		return TaxonomicStatus.values();
	}

	@Override
	public SpecimenTypeStatus[] getControlledListSpecimenTypeStatus()
	{
		return SpecimenTypeStatus.values();
	}

	@Override
	public String[] getAllowedDateFormats()
	{
		String[] formats = new String[ACCEPTED_DATE_FORMATS.length];
		for (int i = 0; i < formats.length; i++) {
			formats[i] = ACCEPTED_DATE_FORMATS[i].toPattern();
		}
		return formats;
	}

}
