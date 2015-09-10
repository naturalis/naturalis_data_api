package nl.naturalis.nda.elasticsearch.load;

import static nl.naturalis.nda.domain.TaxonomicRank.GENUS;
import static nl.naturalis.nda.domain.TaxonomicRank.SPECIES;
import static nl.naturalis.nda.domain.TaxonomicRank.SUBGENUS;
import static nl.naturalis.nda.domain.TaxonomicRank.SUBSPECIES;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nl.naturalis.nda.domain.DefaultClassification;
import nl.naturalis.nda.domain.Monomial;
import nl.naturalis.nda.domain.MultiMediaContentIdentification;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.SpecimenIdentification;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;

import org.domainobject.util.StringUtil;
import org.slf4j.Logger;

/**
 * Provides common functionality for the various {@link Transformer}
 * implementations in this library.
 * 
 * @author Ayco Holleman
 *
 */
public class TransformUtil {

	private static final Logger logger = Registry.getInstance().getLogger(TransformUtil.class);

	private static final SimpleDateFormat DATE_FORMAT0 = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat DATE_FORMAT1 = new SimpleDateFormat("yyyy/MM/dd");
	private static final SimpleDateFormat DATE_FORMAT2 = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat DATE_FORMAT3 = new SimpleDateFormat("dd MMMM yyyy");
	private static final SimpleDateFormat DATE_FORMAT4 = new SimpleDateFormat("yyyy");
	private static final SimpleDateFormat DATE_FORMAT5 = new SimpleDateFormat("yy");

	//@formatter:off
	public static final List<SimpleDateFormat> DATE_FORMATS = Arrays.asList(
		DATE_FORMAT0,
		DATE_FORMAT1,
		DATE_FORMAT2,
		DATE_FORMAT3,
		DATE_FORMAT4,
		DATE_FORMAT5
	);
	//@formatter:on

	public static Date parseDate(String s)
	{
		if (s == null) {
			return null;
		}
		s = s.trim();
		if (s.length() == 0) {
			return null;
		}
		for (SimpleDateFormat df : DATE_FORMATS) {
			try {
				return df.parse(s);
			}
			catch (ParseException e) {
			}
		}
		logger.warn(String.format("Invalid date: \"%s\"", s));
		return null;
	}

	private static final String MISMATCH = "Mismatch between %s in classification and scientific name: \"%s\", \"%s\"";
	private static final String EQUALIZE = "Equalizing value of %s (copy from %s to %s: \"%s\")";
	private static final String NAME = "scientific name";
	private static final String CLASSIFICATION = "classification ";

	public static DefaultClassification extractClassificiationFromName(ScientificName sn)
	{
		DefaultClassification dc = new DefaultClassification();
		dc.setGenus(sn.getGenusOrMonomial());
		dc.setSubgenus(sn.getSubgenus());
		dc.setSpecificEpithet(sn.getSpecificEpithet());
		dc.setInfraspecificEpithet(sn.getInfraspecificEpithet());
		return dc;
	}

	public static List<Monomial> getMonomialsInName(ScientificName sn)
	{
		List<Monomial> monomials = new ArrayList<>(3);
		if (sn.getGenusOrMonomial() != null) {
			monomials.add(new Monomial(GENUS, sn.getGenusOrMonomial()));
		}
		if (sn.getSubgenus() != null) {
			monomials.add(new Monomial(SUBGENUS, sn.getSubgenus()));
		}
		if (sn.getSpecificEpithet() != null) {
			monomials.add(new Monomial(SPECIES, sn.getSpecificEpithet()));
		}
		if (sn.getInfraspecificEpithet() != null) {
			monomials.add(new Monomial(SUBSPECIES, sn.getInfraspecificEpithet()));
		}
		return monomials;
	}

	public static ScientificName extractNameFromClassification(DefaultClassification dc)
	{
		ScientificName sn = new ScientificName();
		sn.setGenusOrMonomial(dc.getGenus());
		sn.setSubgenus(dc.getSubgenus());
		sn.setSpecificEpithet(dc.getSpecificEpithet());
		sn.setInfraspecificEpithet(dc.getInfraspecificEpithet());
		return sn;
	}

	public static void equalizeNameComponents(ESTaxon taxon)
	{
		equalizeNameComponents(taxon.getDefaultClassification(), taxon.getAcceptedName());
	}

	public static void equalizeNameComponents(ESSpecimen specimen)
	{
		for (SpecimenIdentification i : specimen.getIdentifications()) {
			equalizeNameComponents(i.getDefaultClassification(), i.getScientificName());
		}
	}

	public static void equalizeNameComponents(ESMultiMediaObject mmo)
	{
		for (MultiMediaContentIdentification i : mmo.getIdentifications()) {
			equalizeNameComponents(i.getDefaultClassification(), i.getScientificName());
		}
	}

	private static void equalizeNameComponents(DefaultClassification dc, ScientificName sn)
	{
		if (dc.getGenus() != null && sn.getGenusOrMonomial() != null) {
			if (!dc.getGenus().equals(sn.getGenusOrMonomial())) {
				String msg = String.format(MISMATCH, GENUS, dc.getGenus(), sn.getGenusOrMonomial());
				throw new RuntimeException(msg);
			}
		}
		else if (dc.getGenus() == null && sn.getGenusOrMonomial() != null) {
			dc.setGenus(sn.getGenusOrMonomial());
			if (logger.isDebugEnabled())
				logger.debug(String.format(EQUALIZE, GENUS, NAME, CLASSIFICATION, sn.getGenusOrMonomial()));
		}
		else if (dc.getGenus() != null && sn.getGenusOrMonomial() == null) {
			sn.setGenusOrMonomial(dc.getGenus());
			if (logger.isDebugEnabled())
				logger.debug(String.format(EQUALIZE, GENUS, CLASSIFICATION, NAME, sn.getGenusOrMonomial()));
		}

		if (dc.getSubgenus() != null && sn.getSubgenus() != null) {
			if (!dc.getSubgenus().equals(sn.getSubgenus())) {
				String msg = String.format(MISMATCH, SUBGENUS, dc.getSubgenus(), sn.getSubgenus());
				throw new RuntimeException(msg);
			}
		}
		else if (dc.getSubgenus() == null && sn.getSubgenus() != null) {
			dc.setSubgenus(sn.getSubgenus());
			if (logger.isDebugEnabled())
				logger.debug(String.format(EQUALIZE, SUBGENUS, NAME, CLASSIFICATION, sn.getSubgenus()));
		}
		else if (dc.getSubgenus() != null && sn.getSubgenus() == null) {
			sn.setSubgenus(dc.getSubgenus());
			if (logger.isDebugEnabled())
				logger.debug(String.format(EQUALIZE, SUBGENUS, CLASSIFICATION, NAME, sn.getSubgenus()));
		}

		if (dc.getSpecificEpithet() != null && sn.getSpecificEpithet() != null) {
			if (!dc.getSpecificEpithet().equals(sn.getSpecificEpithet())) {
				String msg = String.format(MISMATCH, SPECIES, dc.getSpecificEpithet(), sn.getSpecificEpithet());
				throw new RuntimeException(msg);
			}
		}
		else if (dc.getSpecificEpithet() == null && sn.getSpecificEpithet() != null) {
			dc.setSpecificEpithet(sn.getSpecificEpithet());
			logger.debug(String.format(EQUALIZE, SPECIES, NAME, CLASSIFICATION, sn.getSpecificEpithet()));
		}
		else if (dc.getSpecificEpithet() != null && sn.getSpecificEpithet() == null) {
			sn.setSpecificEpithet(dc.getSpecificEpithet());
			if (logger.isDebugEnabled())
				logger.debug(String.format(EQUALIZE, SPECIES, CLASSIFICATION, NAME, sn.getSpecificEpithet()));
		}

		if (dc.getInfraspecificEpithet() != null && sn.getInfraspecificEpithet() != null) {
			if (!dc.getInfraspecificEpithet().equals(sn.getInfraspecificEpithet())) {
				String msg = String.format(MISMATCH, SUBSPECIES, dc.getInfraspecificEpithet(), sn.getInfraspecificEpithet());
				throw new RuntimeException(msg);
			}
		}
		else if (dc.getInfraspecificEpithet() == null && sn.getInfraspecificEpithet() != null) {
			dc.setInfraspecificEpithet(sn.getInfraspecificEpithet());
			if (logger.isDebugEnabled())
				logger.debug(String.format(EQUALIZE, SUBSPECIES, NAME, CLASSIFICATION, sn.getInfraspecificEpithet()));
		}
		else if (dc.getInfraspecificEpithet() != null && sn.getInfraspecificEpithet() == null) {
			sn.setInfraspecificEpithet(dc.getInfraspecificEpithet());
			if (logger.isDebugEnabled())
				logger.debug(String.format(EQUALIZE, SUBSPECIES, CLASSIFICATION, NAME, sn.getInfraspecificEpithet()));
		}
	}

	private static final String jpeg = "image/jpeg";

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
