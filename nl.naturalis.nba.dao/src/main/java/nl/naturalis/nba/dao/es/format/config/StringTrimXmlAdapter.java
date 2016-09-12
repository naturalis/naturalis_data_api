package nl.naturalis.nba.dao.es.format.config;

import javax.xml.bind.annotation.adapters.XmlAdapter;

class StringTrimXmlAdapter extends XmlAdapter<String, String> {

	@Override
	public String unmarshal(String s) throws Exception
	{
		if (s == null || (s = s.trim()).isEmpty())
			return null;
		return s;
	}

	@Override
	public String marshal(String s) throws Exception
	{
		if (s == null || (s = s.trim()).isEmpty())
			return null;
		return s;
	}
}
