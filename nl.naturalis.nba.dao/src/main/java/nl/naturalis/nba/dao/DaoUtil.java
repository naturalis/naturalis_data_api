package nl.naturalis.nba.dao;

import org.apache.logging.log4j.Logger;

public class DaoUtil {

	@SuppressWarnings("unused")
	private static Logger logger = getLogger(DaoUtil.class);

	private DaoUtil()
	{
	}

	public static Logger getLogger(Class<?> cls)
	{
		return DaoRegistry.getInstance().getLogger(cls);
	}
}
