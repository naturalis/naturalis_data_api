package nl.naturalis.nba.dao;

import static nl.naturalis.nba.common.json.JsonUtil.deserialize;
import static nl.naturalis.nba.common.json.JsonUtil.readField;
import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.utils.debug.DebugUtil.printCall;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import org.apache.logging.log4j.Logger;
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
import nl.naturalis.nba.dao.format.dwca.DwcaDataSetType;
import nl.naturalis.nba.dao.translate.OperatorValidator;
import nl.naturalis.nba.dao.SpecimenMetaDataDao;
import nl.naturalis.nba.utils.FileUtil;
import nl.naturalis.nba.utils.IOUtil;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;


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
    Map<String, String> metadata = new HashMap<>();
    try {
      metadata = getMetaData();      
    } catch (IOException e) {
      metadata = null;
    }
    if (metadata == null) {
      return new MappingInfo<>(dt.getMapping()).getPathStrings(sorted);
    }
    
    String[] paths = new MappingInfo<>(dt.getMapping()).getPathStrings(sorted);
    List<String> result = new ArrayList<>();
    
    for (String path : paths) {
      if ( metadata.get(path) != null && metadata.get(path).length() > 0) {
        result.add(path + " - " + metadata.get(path));        
      } else {
        result.add(path);
      }
    }
    String[] resultArr = new String[result.size()];
    return result.toArray(resultArr);
  }

  public Map<String, String> getMetaData() throws IOException {
    
    Map<String, String> metadata = new HashMap<>();
    
    String fileName = "/" + dt.getName().concat("-metadata.csv").toLowerCase();
    File dir = getMetadataDir();
    File file = new File(dir, fileName);
    
    CSVParser parser =
        new CSVParserBuilder()
        .withSeparator(',')
        .withIgnoreQuotations(true)
        .build();

    InputStream is = new FileInputStream(file);
    
    if (is != null) {
      CSVReader reader =
          new CSVReaderBuilder(new InputStreamReader(is))
          .withSkipLines(1)
          .withCSVParser(parser)
          .build();
      
      String[] record = null;
      while ((record = reader.readNext()) != null) {
        if (record[0] != null)
          metadata.put(record[0], record[1]);
      }
      is.close();
    }
    
    return metadata;
  }
  
  private static File getMetadataDir()
  {
    File root = DaoRegistry.getInstance().getConfigurationDirectory();
    return FileUtil.newFile(root, "metadata/");
  }

}