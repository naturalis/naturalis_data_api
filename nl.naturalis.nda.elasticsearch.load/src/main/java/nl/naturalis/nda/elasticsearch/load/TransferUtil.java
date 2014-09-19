package nl.naturalis.nda.elasticsearch.load;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransferUtil {

	private static final Logger logger = LoggerFactory.getLogger(TransferUtil.class);

	private static final SimpleDateFormat DATE_FORMAT0 = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat DATE_FORMAT1 = new SimpleDateFormat("yyyy/MM/dd");
	private static final SimpleDateFormat DATE_FORMAT2 = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat DATE_FORMAT3 = new SimpleDateFormat("yyyy");
	private static final SimpleDateFormat DATE_FORMAT4 = new SimpleDateFormat("yy");

	//@formatter:off
	public static final List<SimpleDateFormat> DATE_FORMATS = Arrays.asList(
		DATE_FORMAT0,
		DATE_FORMAT1,
		DATE_FORMAT2,
		DATE_FORMAT3,
		DATE_FORMAT4
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

}
