package nl.naturalis.nba.dao.es.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.domainobject.util.FileUtil;

import nl.naturalis.nba.dao.es.calc.ICalculator;
import nl.naturalis.nba.dao.es.exception.DaoException;

public class CsvColumnConfigurator {

	private static final String CALC_PACKAGE = ICalculator.class.getPackage().getName();

	private static CsvColumnConfigurator instance;

	public static CsvColumnConfigurator getInstance()
	{
		if (instance == null)
			instance = new CsvColumnConfigurator();
		return instance;
	}

	private final HashMap<File, IColumn[]> cache;

	private CsvColumnConfigurator()
	{
		cache = new HashMap<>();
	}

	public IColumn[] getColumns(File confDir)
	{
		IColumn[] columns = cache.get(confDir);
		if (columns == null) {
			columns = createColumns(confDir);
			cache.put(confDir, columns);
		}
		return columns;
	}

	@SuppressWarnings("unchecked")
	private static IColumn[] createColumns(File confDir)
	{
		File confFile = FileUtil.newFile(confDir, "fields.config");
		ArrayList<IColumn> columns = new ArrayList<>(60);
		try (LineNumberReader lnr = new LineNumberReader(new FileReader(confFile))) {
			for (String line = lnr.readLine(); line != null; line = lnr.readLine()) {
				line = line.trim();
				if (line.length() == 0 || line.charAt(0) == '#') {
					continue;
				}
				String[] chunks = line.split("=");
				if (chunks.length != 2) {
					throw missingDelimiter(confFile, lnr);
				}
				String key = chunks[0].trim();
				String val = chunks[1].trim();
				IColumn col;
				if (val.charAt(0) == '*') {
					col = new ConstantColumn(key, val.substring(1));
				}
				else if (val.charAt(0) == '%') {
					String className = CALC_PACKAGE + '.' + val.substring(1).trim();
					Class<? extends ICalculator> cls;
					try {
						cls = (Class<? extends ICalculator>) Class.forName(className);
					}
					catch (ClassNotFoundException e) {
						throw missingCalculator(confFile, lnr, key, val);
					}
					ICalculator calculator = cls.newInstance();
					col = new CalculatedColumn(key, calculator);
				}
				else {
					String[] path = val.split("\\.");
					col = new DataColumn(key, path);
				}
				columns.add(col);
			}
		}
		catch (FileNotFoundException e) {
			String msg = "Missing configuration file: " + confFile.getAbsolutePath();
			throw new DaoException(msg);
		}
		catch (IOException | InstantiationException | IllegalAccessException e) {
			throw new DaoException(e);
		}
		return columns.toArray(new IColumn[columns.size()]);
	}

	private static DaoException missingDelimiter(File confFile, LineNumberReader lnr)
	{
		int line = lnr.getLineNumber() + 1;
		String path = confFile.getAbsolutePath();
		String fmt = "Missing delimiter (%s, line %s)";
		String msg = String.format(fmt, path, line);
		return new DaoException(msg);
	}

	private static DaoException missingCalculator(File confFile, LineNumberReader lnr, String key,
			String val)
	{
		int line = lnr.getLineNumber() + 1;
		String path = confFile.getAbsolutePath();
		String fmt = "Invalid calculator specified for %s: \"%s\" (%s, line %s)";
		String msg = String.format(fmt, line, path);
		return new DaoException(msg);
	}

}
