package nl.naturalis.nba.etl.nsr;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.etl.nsr.model.NsrTaxon;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Arrays;

import static nl.naturalis.nba.etl.ETLUtil.getLogger;

public class ImportTest {

    private static final Logger logger = getLogger(ImportTest.class);

    public static void main(String[] args) {

        File[] files = getJsonFiles();
        Arrays.sort(files);
        if (files.length == 0) {
            logger.info("No XML files to process");
            System.exit(0);
        }


        int t = 0;
        for (File file : files) {
            int n = 0;
            logger.info("Processing file {}", file.getAbsolutePath());
            LineNumberReader lnr = null;
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                FileReader fr = new FileReader(file);
                lnr = new LineNumberReader(fr, 4096);
                String json ;
                int lineNumber = 0;
                while ((json = lnr.readLine()) != null) {
                    NsrTaxon nsrTaxon = objectMapper.readValue(json, NsrTaxon.class);
                    // logger.info("{} Processed nsr taxon: {}", lineNumber++, taxon.getNsr_id());
                    if ((n%1000) == 0) logger.info(JsonUtil.toPrettyJson(nsrTaxon));
                    t++;
                    n++;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            logger.info("{} > {} lines", file.getName(), n);
        }

        logger.info("Total: {} lines", t);


    }

    static File[] getJsonFiles()
    {
        File dir = getDataDir();
        logger.info("Searching for JSON files in " + dir.getAbsolutePath());
        return dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name)
            {
                return name.toLowerCase().endsWith(".jsonl");
            }
        });
    }

    private static File getDataDir()
    {
        return DaoRegistry.getInstance().getConfiguration().getDirectory("nsr.data.dir");
    }


}
