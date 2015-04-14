package nl.naturalis.nda.elasticsearch.load.brahms;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

import nl.naturalis.nda.elasticsearch.load.LoadUtil;

import org.domainobject.util.FileUtil;
import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

public class BrahmsDumpUtil {

	public static void main(String[] args) throws Exception
	{
		convertFiles();
		//detectEncoding();
	}

	// Presumed extension for the original Brahms dump file
	static final String FILE_EXT_ORIGINAL = ".csv";

	// Extension for the backup of the original dump file
	// (before UTF-8 conversion)
	static final String FILE_EXT_BAK = ".csv.original";

	// Extension of the UTF-8 converted file; ready to be processed
	// by import programs
	static final String FILE_EXT_IMPORTABLE = ".utf8.csv";

	// Extension of the backup of the converted file. Will be
	// appended to FILE_EXT_IMPORTABLE:
	static final String FILE_EXT_IMPORTED = ".imported";

	private static final Logger logger = LoggerFactory.getLogger(BrahmsDumpUtil.class);


	/**
	 * Utility method to detect character set in Brahms dumps. Not part of the
	 * import program. Two different character set detector libraries are used:
	 * universalchardet (Mozilla) en ICU (IBM). Mozilla could not detect any
	 * character set at all. ICU settled on ISO-8859-1. Nonetheless, we assume
	 * the character set in the Brahms dumps is CP-1252, because that seemed to
	 * give the best results (least "funny" characters).
	 * 
	 * @throws IOException
	 */
	public static void detectEncoding() throws IOException
	{
		//String fileName = "C:\\test\\nda-import\\data\\brahms.orig\\LEIDEN1.CSV";
		//String fileName = "C:\\test\\nda-import\\data\\brahms.orig\\LEIDEN2.CSV";
		String fileName = "C:\\test\\nda-import\\data\\brahms.orig\\WAG.CSV";

		// Let's see what JUniversalCharDet thinks
		try (BufferedInputStream fis = new BufferedInputStream(new FileInputStream(fileName))) {
			byte[] buf = new byte[8192];
			System.out.println("Starting character set detection");
			UniversalDetector detector = new UniversalDetector(null);
			int nread;
			while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
				detector.handleData(buf, 0, nread);
			}
			detector.dataEnd();
			String encoding = detector.getDetectedCharset();
			if (encoding != null) {
				System.out.println("Character set detected by JUniversalCharDet: " + encoding);
			}
			else {
				System.out.println("JUniversalCharDet could not reliably detect a character set");
			}
			detector.reset();
		}

		System.out.println();

		// Let's see what JUniversalCharDet thinks
		try (BufferedInputStream fis = new BufferedInputStream(new FileInputStream(fileName))) {
			CharsetDetector cd = new CharsetDetector();
			cd.setText(fis);
			CharsetMatch[] matches = cd.detectAll();
			if (matches.length == 0) {
				System.out.println("ICU4J could not reliably detect a character set");
			}
			for (CharsetMatch match : matches) {
				System.out.println("Character set detected by ICU4J: " + match.getName() + " (confidence: " + match.getConfidence() + ")");
			}
		}
	}


	public static void convertFiles() throws Exception
	{
		logger.info("Checking file encoding for Brahms CSV files");
		String csvDir = LoadUtil.getConfig().required("brahms.csv_dir");
		File file = new File(csvDir);
		if (!file.isDirectory()) {
			throw new Exception(String.format("No such directory: \"%s\"", csvDir));
		}
		logger.info("Brahms data directory: " + file.getCanonicalPath());
		File[] newFiles = file.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name)
			{
				name = name.toLowerCase();

				return !name.endsWith(FILE_EXT_IMPORTABLE) && !name.endsWith(FILE_EXT_IMPORTED) && name.endsWith(FILE_EXT_ORIGINAL);
			}
		});
		if (newFiles.length == 0) {
			logger.info("No CSV files to convert");
			return;
		}
		String now = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		for (File newFile : newFiles) {

			String basename = newFile.getCanonicalPath();
			basename = basename.substring(0, basename.length() - FILE_EXT_ORIGINAL.length());

			File bakFile = new File(basename + "." + now + FILE_EXT_BAK);
			if (bakFile.isFile()) {
				throw new Exception("Error creating bak file (file already exists): " + bakFile.getAbsolutePath());
			}

			if (!newFile.renameTo(bakFile)) {
				throw new Exception(String.format("Error creating bak file for \"%s\"", newFile.getAbsolutePath()));
			}

			File processableFile = new File(basename + "." + now + FILE_EXT_IMPORTABLE);
			if (processableFile.isFile()) {
				throw new Exception("Error converting file (file already exists): " + processableFile.getAbsolutePath());
			}

			logger.info("Converting to UTF-8: " + newFile.getAbsolutePath());
			FileUtil.convertToUtf8(bakFile, processableFile, "Windows-1252");

		}
		logger.info("File encoding conversion complete");
	}


	public static void backup()
	{
		logger.info("Creating backups of imported files");
		try {
			for (File f : getImportableFiles()) {
				Path source = f.toPath();
				Path target = new File(f.getCanonicalPath() + FILE_EXT_IMPORTED).toPath();
				logger.info(String.format("Renaming %s => %s", source.getFileName().toString(), target.getFileName().toString()));
				Files.move(source, target);
			}
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	public static File[] getImportableFiles()
	{
		String csvDir = LoadUtil.getConfig().required("brahms.csv_dir");
		File file = new File(csvDir);
		if (!file.isDirectory()) {
			throw new RuntimeException(String.format("No such directory: \"%s\"", csvDir));
		}
		File[] files = file.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name)
			{
				return name.toLowerCase().endsWith(FILE_EXT_IMPORTABLE);
			}
		});
		return files;
	}

}
