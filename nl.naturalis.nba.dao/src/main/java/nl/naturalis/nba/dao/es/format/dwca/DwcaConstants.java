package nl.naturalis.nba.dao.es.format.dwca;

class DwcaConstants {

	static final String ENCODING = "UTF-8";
	static final String FIELDS_ENCLOSED_BY = "\"";
	static final String FIELDS_TERMINATED_BY = ",";
	static final String LINES_TERMINATED_BY = "\n";
	static final String IGNORE_HEADER_LINES = "1";
	static final String METADATA = "eml.xml";
	static final String XMLNS = "http://rs.tdwg.org/dwc/text/";
	static final String XMLNS_XSI = "http://www.w3.org/2001/XMLSchema-instance";
	static final String XSI_SCHEMA_LOCATION = "http://rs.tdwg.org/dwc/text/ http://rs.tdwg.org/dwc/text/tdwg_dwc_text.xsd";

	private DwcaConstants()
	{
	}

}
