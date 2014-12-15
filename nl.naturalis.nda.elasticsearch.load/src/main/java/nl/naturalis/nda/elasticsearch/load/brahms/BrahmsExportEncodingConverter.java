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
		logger.info("Checking file encoding for Brahms CSV files");
		String csvDir = LoadUtil.getConfig().required("brahms.csv_dir");
		File file = new File(csvDir);
		if (!file.isDirectory()) {
			throw new Exception(String.format("No such directory: \"%s\"", csvDir));
		}
		logger.info("Brahms data directory: " + file.getCanonicalPath());
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
			File original = new File(f.getCanonicalPath() + ".orig");
			if (original.isFile()) {
				logger.warn(String.format("A converted version of %1$s already exists. Remove %1$s.orig to force conversion", f.getName()));
				continue;
			}
			File converted = new File(f.getCanonicalPath());
			if (f.renameTo(original)) {
				logger.info("Converting to UTF-8: " + f.getCanonicalPath());
				FileUtil.convertToUtf8(original, converted, "Windows-1252");
			}
			else {
				throw new Exception(String.format("Failed to rename file \"%s\"", f.getAbsolutePath()));
			}
		}
		logger.info("File encoding conversion complete");
	}

}
