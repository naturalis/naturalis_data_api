package nl.naturalis.nba.etl.brahms;

import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.AUTHOR1;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.AUTHOR2;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.AUTHOR3;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.FAMCLASS;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.FAMILY;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.GENUS;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.ORDER;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.RANK1;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.RANK2;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.SP1;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.SP2;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.SP3;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.SPECIES;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.DefaultClassification;
import nl.naturalis.nba.api.model.Monomial;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.TaxonomicRank;
import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.ETLRegistry;
import nl.naturalis.nba.etl.TransformUtil;

/**
 * Provides common functionality related to the Brahms ETL cycle.
 * 
 * @author Ayco Holleman
 *
 */
class BrahmsImportUtil {

	private static final Logger logger = ETLRegistry.getInstance()
			.getLogger(BrahmsImportUtil.class);
	private static final SimpleDateFormat fileNameDateFormatter = new SimpleDateFormat("yyyyMMdd");

	private BrahmsImportUtil()
	{
	}

	/**
	 * Provides a list of CSV files to process. Only files whose name end with
	 * {@code .csv} (case-insensitive) will be processed.
	 * 
	 * @return
	 */
	static File[] getCsvFiles()
	{
		File[] files = getDataDir().listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name)
			{
				return name.toLowerCase().endsWith(".csv");
			}
		});
		return files;
	}

	/**
	 * Creates a backup of successfully processed CSV files by appending a
	 * datetime stamp and a {@code .imported} file extension to their name.
	 */
	static void backup()
	{
		String ext = "." + fileNameDateFormatter.format(new Date()) + ".imported";
		for (File f : getCsvFiles()) {
			f.renameTo(new File(f.getAbsolutePath() + ext));
		}
	}

	/**
	 * Removes the {@code .imported} file extension from files that have it,
	 * causing them to be re-processed the next time an import is started.
	 */
	static void removeBackupExtension()
	{
		File dir = getDataDir();
		File[] files = dir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name)
			{
				return name.toLowerCase().endsWith(".imported");
			}
		});
		for (File file : files) {
			int pos = file.getName().toLowerCase().indexOf(".csv");
			String chopped = file.getName().substring(0, pos + 4);
			logger.info(file.getName() + " ---> " + chopped);
			chopped = dir.getAbsolutePath() + "/" + chopped;
			file.renameTo(new File(chopped));
		}
	}

	/**
	 * Extracts a {@code ScientificName} instance from a raw CSV record.
	 * 
	 * @param record
	 * @return
	 */
	static ScientificName getScientificName(CSVRecordInfo<BrahmsCsvField> record)
	{
		ScientificName sn = new ScientificName();
		sn.setFullScientificName(record.get(SPECIES));
		sn.setAuthorshipVerbatim(getAuthor(record));
		sn.setGenusOrMonomial(record.get(GENUS));
		sn.setSpecificEpithet(record.get(SP1));
		sn.setInfraspecificMarker(getInfraspecificMarker(record));
		sn.setInfraspecificEpithet(getInfraspecificEpithet(record));
		if (sn.getFullScientificName() == null) {
			StringBuilder sb = new StringBuilder();
			if (sn.getGenusOrMonomial() != null) {
				sb.append(sn.getGenusOrMonomial()).append(' ');
			}
			if (sn.getSubgenus() != null) {
				sb.append(sn.getSubgenus()).append(' ');
			}
			if (sn.getSpecificEpithet() != null) {
				sb.append(sn.getSpecificEpithet()).append(' ');
			}
			if (sn.getInfraspecificMarker() != null) {
				sb.append(sn.getInfraspecificMarker()).append(' ');
			}
			if (sn.getInfraspecificEpithet() != null) {
				sb.append(sn.getInfraspecificEpithet()).append(' ');
			}
			if (sn.getAuthorshipVerbatim() != null) {
				if (sn.getAuthorshipVerbatim().charAt(0) != '(') {
					sb.append('(');
				}
				sb.append(sn.getAuthorshipVerbatim());
				if (sn.getAuthorshipVerbatim()
						.charAt(sn.getAuthorshipVerbatim().length() - 1) != ')') {
					sb.append(')');
				}
			}
			if (sb.length() != 0) {
				sn.setFullScientificName(sb.toString().trim());
			}
		}
		TransformUtil.setScientificNameGroup(sn);
		return sn;
	}

	/**
	 * Constructs a {@code DefaultClassification} from a raw CSV record and a
	 * {@code ScientificName} instance (presumably extracted via
	 * {@link #getScientificName(CSVRecord) getScientificName}).
	 * 
	 * @param record
	 * @param sn
	 * @return
	 */
	static DefaultClassification getDefaultClassification(CSVRecordInfo<BrahmsCsvField> record,
			ScientificName sn)
	{
		DefaultClassification dc = TransformUtil.extractClassificiationFromName(sn);
		// There is no field for Kingdom, so all specimens are asigned to Plantae,
		// except for those that have className Fungi (fieldname FAMCLASS).
		boolean fungus = false;
		if (record.get(FAMCLASS).toLowerCase().equals("fungi")) {
			fungus = true;
		}
		dc.setKingdom("Plantae");
		if (fungus) {
			dc.setKingdom("Fungi");
		}
		// Phylum deliberately not set

		// Note: fungi specimen import records are identified by FAMCLASS == "fungi"
		// This is NOT the value to be here. Hence, null for all fungi specimens.
		if (!fungus)
			dc.setClassName(record.get(FAMCLASS));

		dc.setOrder(record.get(ORDER));
		
		// Note: the field family names of fungi specimens can --but most often does 
		// not-- contain a family name. This is then prefixed with "Fungi-". 
		// E.g.: "Fungi-Boletaceae"
		// This prefix needs to be removed.
		if (!fungus) { 
			dc.setFamily(record.get(FAMILY));
		}
		else {
			String prefix = "(?i)(fungi-)";
			String familyName = record.get(FAMILY);
			if (familyName.matches(prefix))
				dc.setFamily(familyName.replaceFirst(prefix, ""));
		}
		return dc;
	}

	/**
	 * Converts a {@code DefaultClassification} instance to a system
	 * classification (which is just a list of {@code Monomial}s).
	 * 
	 * @param dc
	 * @return
	 */
	static List<Monomial> getSystemClassification(DefaultClassification dc)
	{
		List<Monomial> sc = new ArrayList<>(8);
		if (dc.getKingdom() != null) {
			sc.add(new Monomial(TaxonomicRank.KINGDOM, dc.getKingdom()));
		}
		if (dc.getOrder() != null) {
			sc.add(new Monomial(TaxonomicRank.ORDER, dc.getOrder()));
		}
		if (dc.getFamily() != null) {
			sc.add(new Monomial(TaxonomicRank.FAMILY, dc.getFamily()));
		}
		if (dc.getGenus() != null) {
			sc.add(new Monomial(TaxonomicRank.GENUS, dc.getGenus()));
		}
		if (dc.getSpecificEpithet() != null) {
			sc.add(new Monomial(TaxonomicRank.SPECIES, dc.getSpecificEpithet()));
		}
		if (dc.getInfraspecificEpithet() != null) {
			sc.add(new Monomial(TaxonomicRank.SUBSPECIES, dc.getInfraspecificEpithet()));
		}
		return sc;
	}

	private static String getAuthor(CSVRecordInfo<BrahmsCsvField> record)
	{
		if (record.get(SP3) == null) {
			if (record.get(SP2) == null) {
				return record.get(AUTHOR1);
			}
			return record.get(AUTHOR2);
		}
		return record.get(AUTHOR3);
	}

	private static String getInfraspecificMarker(CSVRecordInfo<BrahmsCsvField> record)
	{
		String s = record.get(RANK2);
		return s == null ? record.get(RANK1) : s;
	}

	private static String getInfraspecificEpithet(CSVRecordInfo<BrahmsCsvField> record)
	{
		String s = record.get(SP3);
		return s == null ? record.get(SP2) : s;
	}

	static String getTaxonRank(CSVRecordInfo<BrahmsCsvField> record)
	{
		if (record.get(SP3) == null) {
			if (record.get(SP2) == null) {
				if (record.get(SP1) == null) {
					// TODO: replace literal with DefaultClassification.Rank
					return "genus";
				}
				return "species";
			}
			return record.get(RANK1);
		}
		return record.get(RANK2);
	}

	private static File getDataDir()
	{
		return DaoRegistry.getInstance().getConfiguration().getDirectory("brahms.data.dir");
	}

}
