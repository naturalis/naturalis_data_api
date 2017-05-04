package nl.naturalis.nba.etl;

import static nl.naturalis.nba.api.model.TaxonomicRank.GENUS;
import static nl.naturalis.nba.api.model.TaxonomicRank.SPECIES;
import static nl.naturalis.nba.api.model.TaxonomicRank.SUBGENUS;
import static nl.naturalis.nba.api.model.TaxonomicRank.SUBSPECIES;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.DefaultClassification;
import nl.naturalis.nba.api.model.Monomial;
import nl.naturalis.nba.api.model.MultiMediaContentIdentification;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.TaxonomicIdentification;
import nl.naturalis.nba.utils.StringUtil;

/**
 * Provides common functionality for the various {@link Transformer}
 * implementations in this library.
 * 
 * @author Ayco Holleman
 *
 */
public class TransformUtil {

	private static final Logger logger = ETLRegistry.getInstance().getLogger(TransformUtil.class);

	private static final SimpleDateFormat DATE_FORMAT0 = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat DATE_FORMAT1 = new SimpleDateFormat("yyyy/MM/dd");
	private static final SimpleDateFormat DATE_FORMAT2 = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat DATE_FORMAT3 = new SimpleDateFormat("dd MMMM yyyy");
	private static final SimpleDateFormat DATE_FORMAT4 = new SimpleDateFormat("MM/yyyy");
	private static final SimpleDateFormat DATE_FORMAT5 = new SimpleDateFormat("yyyy");
	private static final SimpleDateFormat DATE_FORMAT6 = new SimpleDateFormat("yy");

	public static final List<SimpleDateFormat> DATE_FORMATS = Arrays.asList(DATE_FORMAT0,
			DATE_FORMAT1, DATE_FORMAT2, DATE_FORMAT3, DATE_FORMAT4, DATE_FORMAT5, DATE_FORMAT6);

	/**
	 * Attempts to parse the specified string into a date using the following
	 * date patterns:
	 * <ul>
	 * <li>yyyyMMdd</li>
	 * <li>yyyy/MM/dd</li>
	 * <li>yyyy-MM-dd</li>
	 * <li>dd MMMM yyyy</li>
	 * <li>yyyy</li>
	 * <li>yy</li>
	 * </ul>
	 * If none of the patterns work, a warning is issued and {@code null} is
	 * returned.
	 * 
	 * @param s
	 * @return
	 */
	public static Date parseDate(String s)
	{
		if (s == null)
			return null;
		if ((s = s.trim()).length() == 0)
			return null;
		for (SimpleDateFormat df : DATE_FORMATS) {
			try {
				return df.parse(s);
			}
			catch (ParseException e) {}
		}
		logger.warn(String.format("Invalid date: \"%s\"", s));
		return null;
	}

	private static final String EQUALIZE = "Equalizing value of %s (copy from %s to %s: \"%s\")";
	private static final String NAME = "scientific name";
	private static final String CLASSIFICATION = "classification ";

	public static void setScientificNameGroup(ScientificName sn)
	{
		String s0 = sn.getGenusOrMonomial();
		s0 = s0 == null ? "?" : s0.toLowerCase();
		String s1 = sn.getSpecificEpithet();
		s1 = s1 == null ? "?" : s1.toLowerCase();
		String s2 = sn.getInfraspecificEpithet();
		if (s2 == null) {
			sn.setScientificNameGroup(s0 + " " + s1);
		}
		else {
			sn.setScientificNameGroup(s0 + " " + s1 + " " + s2.toLowerCase());
		}
	}

	/**
	 * Constructs a {@code DefaultClassification} object from the name epithets
	 * in the specified scientific name.
	 * 
	 * @param sn
	 * @return
	 */
	public static DefaultClassification extractClassificiationFromName(ScientificName sn)
	{
		DefaultClassification dc = new DefaultClassification();
		dc.setGenus(sn.getGenusOrMonomial());
		dc.setSubgenus(sn.getSubgenus());
		dc.setSpecificEpithet(sn.getSpecificEpithet());
		dc.setInfraspecificEpithet(sn.getInfraspecificEpithet());
		return dc;
	}

	/**
	 * Extracts a list of monomials from the specified scientific name.
	 * 
	 * @param sn
	 * @return
	 */
	public static List<Monomial> getMonomialsInName(ScientificName sn)
	{
		List<Monomial> monomials = new ArrayList<>(3);
		if (sn.getGenusOrMonomial() != null)
			monomials.add(new Monomial(GENUS, sn.getGenusOrMonomial()));
		if (sn.getSubgenus() != null)
			monomials.add(new Monomial(SUBGENUS, sn.getSubgenus()));
		if (sn.getSpecificEpithet() != null)
			monomials.add(new Monomial(SPECIES, sn.getSpecificEpithet()));
		if (sn.getInfraspecificEpithet() != null)
			monomials.add(new Monomial(SUBSPECIES, sn.getInfraspecificEpithet()));
		return monomials;
	}

	/**
	 * Constructs a {@code ScientificName} object from the specified
	 * classification object (using its lower ranks).
	 * 
	 * @param dc
	 * @return
	 */
	public static ScientificName extractNameFromClassification(DefaultClassification dc)
	{
		ScientificName sn = new ScientificName();
		sn.setGenusOrMonomial(dc.getGenus());
		sn.setSubgenus(dc.getSubgenus());
		sn.setSpecificEpithet(dc.getSpecificEpithet());
		sn.setInfraspecificEpithet(dc.getInfraspecificEpithet());
		return sn;
	}

	public static void equalizeNameComponents(Taxon taxon) throws NameMismatchException
	{
		equalizeNameComponents(taxon.getDefaultClassification(), taxon.getAcceptedName());
	}

	public static void equalizeNameComponents(Specimen specimen) throws NameMismatchException
	{
		for (TaxonomicIdentification i : specimen.getIdentifications()) {
			equalizeNameComponents(i.getDefaultClassification(), i.getScientificName());
		}
	}

	public static void equalizeNameComponents(MultiMediaObject mmo) throws NameMismatchException
	{
		for (MultiMediaContentIdentification i : mmo.getIdentifications()) {
			equalizeNameComponents(i.getDefaultClassification(), i.getScientificName());
		}
	}

	private static void equalizeNameComponents(DefaultClassification dc, ScientificName sn)
			throws NameMismatchException
	{
		if (dc.getGenus() != null && sn.getGenusOrMonomial() != null) {
			if (!dc.getGenus().equals(sn.getGenusOrMonomial()))
				throw new NameMismatchException(GENUS, dc, sn);
		}
		else if (dc.getGenus() == null && sn.getGenusOrMonomial() != null) {
			dc.setGenus(sn.getGenusOrMonomial());
			if (logger.isDebugEnabled())
				logger.debug(String.format(EQUALIZE, GENUS, NAME, CLASSIFICATION,
						sn.getGenusOrMonomial()));
		}
		else if (dc.getGenus() != null && sn.getGenusOrMonomial() == null) {
			sn.setGenusOrMonomial(dc.getGenus());
			if (logger.isDebugEnabled())
				logger.debug(String.format(EQUALIZE, GENUS, CLASSIFICATION, NAME,
						sn.getGenusOrMonomial()));
		}

		if (dc.getSubgenus() != null && sn.getSubgenus() != null) {
			if (!dc.getSubgenus().equals(sn.getSubgenus()))
				throw new NameMismatchException(SUBGENUS, dc, sn);
		}
		else if (dc.getSubgenus() == null && sn.getSubgenus() != null) {
			dc.setSubgenus(sn.getSubgenus());
			if (logger.isDebugEnabled())
				logger.debug(
						String.format(EQUALIZE, SUBGENUS, NAME, CLASSIFICATION, sn.getSubgenus()));
		}
		else if (dc.getSubgenus() != null && sn.getSubgenus() == null) {
			sn.setSubgenus(dc.getSubgenus());
			if (logger.isDebugEnabled())
				logger.debug(
						String.format(EQUALIZE, SUBGENUS, CLASSIFICATION, NAME, sn.getSubgenus()));
		}

		if (dc.getSpecificEpithet() != null && sn.getSpecificEpithet() != null) {
			if (!dc.getSpecificEpithet().equals(sn.getSpecificEpithet()))
				throw new NameMismatchException(SPECIES, dc, sn);
		}
		else if (dc.getSpecificEpithet() == null && sn.getSpecificEpithet() != null) {
			dc.setSpecificEpithet(sn.getSpecificEpithet());
			logger.debug(String.format(EQUALIZE, SPECIES, NAME, CLASSIFICATION,
					sn.getSpecificEpithet()));
		}
		else if (dc.getSpecificEpithet() != null && sn.getSpecificEpithet() == null) {
			sn.setSpecificEpithet(dc.getSpecificEpithet());
			if (logger.isDebugEnabled())
				logger.debug(String.format(EQUALIZE, SPECIES, CLASSIFICATION, NAME,
						sn.getSpecificEpithet()));
		}

		if (dc.getInfraspecificEpithet() != null && sn.getInfraspecificEpithet() != null) {
			if (!dc.getInfraspecificEpithet().equals(sn.getInfraspecificEpithet()))
				throw new NameMismatchException(SUBSPECIES, dc, sn);
		}
		else if (dc.getInfraspecificEpithet() == null && sn.getInfraspecificEpithet() != null) {
			dc.setInfraspecificEpithet(sn.getInfraspecificEpithet());
			if (logger.isDebugEnabled())
				logger.debug(String.format(EQUALIZE, SUBSPECIES, NAME, CLASSIFICATION,
						sn.getInfraspecificEpithet()));
		}
		else if (dc.getInfraspecificEpithet() != null && sn.getInfraspecificEpithet() == null) {
			sn.setInfraspecificEpithet(dc.getInfraspecificEpithet());
			if (logger.isDebugEnabled())
				logger.debug(String.format(EQUALIZE, SUBSPECIES, CLASSIFICATION, NAME,
						sn.getInfraspecificEpithet()));
		}
	}

	private static final String jpeg = "image/jpeg";

	/**
	 * Guesses the mime type of an URL based on the file extension (assuming the
	 * last part of the URL <i>is</i> a file name).
	 * 
	 * @param imageUrl
	 * @return
	 */
	public static String guessMimeType(String imageUrl)
	{
		String ext = StringUtil.substr(imageUrl, -4).toLowerCase();
		String mimetype;
		if (ext.equals(".jpg"))
			mimetype = jpeg;
		else if (ext.equals(".png"))
			mimetype = "image/png";
		else if (ext.equals(".gif"))
			mimetype = "image/gif";
		else if (ext.equals(".tif"))
			mimetype = "image/tiff";
		else if (ext.equals(".bmp"))
			mimetype = "image/bmp";
		else if (ext.equals(".mp3"))
			mimetype = "audio/mpeg"; // according to
									// http://tools.ietf.org/html/rfc3003
		else if (ext.equals(".mp4"))
			mimetype = "video/mp4"; // according to
		// http://www.rfc-editor.org/rfc/rfc4337.txt
		else if (ext.equals(".pdf"))
			mimetype = "application/pdf";
		else {
			ext = StringUtil.substr(imageUrl, -5).toLowerCase();
			if (ext.equals("jpeg"))
				mimetype = jpeg;
			else if (ext.equals(".tiff"))
				mimetype = "image/tiff";
			else
				// Whatever ...
				mimetype = jpeg;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Mime type guessed for " + imageUrl + ": " + mimetype);
		}
		return mimetype;
	}

}
