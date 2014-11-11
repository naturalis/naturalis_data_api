package nl.naturalis.nda.elasticsearch.load.brahms;

import java.io.File;
import java.io.FilenameFilter;

import nl.naturalis.nda.elasticsearch.load.LoadUtil;

import org.domainobject.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrahmsExportEncodingConverter {

	public static void main(String[] args) throws Exception
	{
		BrahmsExportEncodingConverter.convertFiles();
	}

	static final Logger logger = LoggerFactory.getLogger(BrahmsExportEncodingConverter.class);


	public static void convertFiles() throws Exception
	{
		logger.info("Preparing file encoding conversion");
		String csvDir = LoadUtil.getConfig().required("brahms.csv_dir");
		File file = new File(csvDir);
		if (!file.isDirectory()) {
			throw new Exception(String.format("No such directory: \"%s\"", csvDir));
		}
		logger.info("Converting CSV files in directory " + file.getCanonicalPath());
		File[] csvFiles = file.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name)
			{
				return name.toLowerCase().endsWith(".csv");
			}
		});
		if (csvFiles.length == 0) {
			logger.info("No CSV files to convert");
			return;
		}
		for (File f : csvFiles) {
			File input = new File(f.getCanonicalPath() + ".orig");
			if (input.isFile()) {
				logger.warn(String.format("A converted version of %1$s already exists. Remove %1$s.orig to force conversion", f.getName()));
				continue;
			}
			File output = new File(f.getCanonicalPath());
			f.renameTo(input);
			FileUtil.convertToUtf8(input, output, "Windows-1252");
			logger.info("Converting to UTF-8: " + f.getCanonicalPath());
		}
		logger.info("File encoding conversion complete");
	}

}
