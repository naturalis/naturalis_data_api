package nl.naturalis.nba.dao;

import static nl.naturalis.nba.common.json.JsonUtil.deserialize;
import static nl.naturalis.nba.common.json.JsonUtil.readField;
import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.utils.debug.DebugUtil.printCall;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import com.univocity.parsers.common.TextParsingException;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import nl.naturalis.nba.api.ComparisonOperator;
import nl.naturalis.nba.api.INbaDocumentMetaData;
import nl.naturalis.nba.api.NoSuchFieldException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.metadata.FieldInfo;
import nl.naturalis.nba.api.model.metadata.NbaSetting;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.SimpleField;
import nl.naturalis.nba.dao.exception.DaoException;
import nl.naturalis.nba.dao.translate.OperatorValidator;
import nl.naturalis.nba.utils.FileUtil;
import nl.naturalis.nba.utils.IOUtil;

public abstract class NbaDocumentMetaDataDao<T extends IDocumentObject>
    implements INbaDocumentMetaData<T> {

  private static final Logger logger = getLogger(NbaDocumentMetaDataDao.class);

  private final DocumentType<T> dt;

  NbaDocumentMetaDataDao(DocumentType<T> dt) {
    this.dt = dt;
  }

  @Override
  public Object getSetting(NbaSetting setting) {
    return getSettings().get(setting);
  }

  @Override
  public Map<NbaSetting, Object> getSettings() {
    EnumMap<NbaSetting, Object> settings = new EnumMap<>(NbaSetting.class);
    InputStream is = null;
    try {
      is = getClass().getResourceAsStream("/es-settings.json");
      Map<String, Object> esSettings = deserialize(is);
      String path = "index.max_result_window";
      Object val = readField(esSettings, path);
      settings.put(NbaSetting.INDEX_MAX_RESULT_WINDOW, val);
    } finally {
      IOUtil.close(is);
    }
    return settings;
  }

  @Override
  public Map<String, FieldInfo> getFieldInfo(String... fields) throws NoSuchFieldException {
    if (logger.isDebugEnabled()) {
      logger.debug(printCall("getAllowedOperators", fields));
    }
    if (fields == null || (fields.length == 1 && fields[0].equals("*"))) {
      fields = getPaths(true);
    }

    Map<String, String> metadata = new HashMap<>();
    try {
      metadata = getMetaData();
    } catch (IOException e) {
      metadata = null;
    }

    int mapSize = ((int) (fields.length / .75) + 1);
    Map<String, FieldInfo> result = new LinkedHashMap<>(mapSize);
    MappingInfo<T> mappingInfo = new MappingInfo<>(dt.getMapping());
    for (String field : fields) {
      Path path = new Path(field);
      ESField esField = mappingInfo.getField(path);
      if (!(esField instanceof SimpleField)) {
        throw new NoSuchFieldException(path);
      }
      SimpleField sf = (SimpleField) esField;
      FieldInfo info = new FieldInfo();
      info.setIndexed(sf.getIndex() != Boolean.FALSE);
      info.setType(esField.getType().toString());
      EnumSet<ComparisonOperator> allowed = EnumSet.noneOf(ComparisonOperator.class);
      for (ComparisonOperator op : ComparisonOperator.values()) {
        if (OperatorValidator.isOperatorAllowed(sf, op)) {
          allowed.add(op);
        }
      }
      info.setAllowedOperators(allowed);
      String description = (metadata != null) ? metadata.get(field) : null;
      if (description != null && description.length() > 0) {
        info.setDescription(description);
      }
      result.put(field, info);
    }
    return result;
  }

  @Override
  public boolean isOperatorAllowed(String field, ComparisonOperator operator) {
    if (logger.isDebugEnabled()) {
      logger.debug(printCall("isOperatorAllowed", field, operator));
    }
    try {
      return OperatorValidator.isOperatorAllowed(field, operator, dt);
    } catch (NoSuchFieldException e) {
      throw new DaoException(e);
    }
  }

  @Override
  public String[] getPaths(boolean sorted) {
    return new MappingInfo<>(dt.getMapping()).getPathStrings(sorted);
  }

  private Map<String, String> getMetaData() throws IOException {
    Map<String, String> metadata = new HashMap<>();
    String fileName = "/" + dt.getName().concat("-metadata.csv").toLowerCase();
    File dir = getMetadataDir();
    File file = new File(dir, fileName);
    InputStream is = new FileInputStream(file);
    if (is != null) {
      CsvParserSettings settings = new CsvParserSettings();
      settings.getFormat().setLineSeparator("\n");
      settings.setHeaderExtractionEnabled(true);
      CsvParser parser = new CsvParser(settings);      
      try {
        List<Record> allRecords = parser.parseAllRecords(new InputStreamReader(is, "UTF-8"));
        for (Record record : allRecords) {
          metadata.put(record.getString("field"), record.getString("reference"));
        }        
      } catch (UnsupportedEncodingException | TextParsingException e) {
        logger.debug("Error in csv file: " + file.toString() + " File will be ignored.");
        metadata = null;
      }
    }    
    return metadata;
  }

  private static File getMetadataDir() {
    File root = DaoRegistry.getInstance().getConfiguration().getDirectory("nba.api.install.dir");
    return FileUtil.newFile(root, "metadata/");
  }

}
