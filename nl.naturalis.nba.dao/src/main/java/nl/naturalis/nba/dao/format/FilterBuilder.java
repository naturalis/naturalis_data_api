package nl.naturalis.nba.dao.format;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.naturalis.nba.dao.format.config.PluginParamXmlConfig;
import nl.naturalis.nba.dao.format.config.PluginXmlConfig;
import nl.naturalis.nba.dao.format.filter.SimpleEntityFilter;

class FilterBuilder {

	/* Pick an arbitrary class from the package containing the entity filters */
	private static String FILTER_PACKAGE = SimpleEntityFilter.class.getPackage().getName();

	private static String ERR_NO_SUCH_FILTER = "No such entity filter: \"%s\". Please specify a valid Java class";
	private static String ERR_NOT_A_FILTER = "Class %s does not implement IEntityFilter";

	private PluginXmlConfig filterConfig;

	FilterBuilder(PluginXmlConfig filterConfig)
	{
		this.filterConfig = filterConfig;
	}

	IEntityFilter build() throws DataSetConfigurationException
	{
		String javaClass = filterConfig.getJavaClass();
		IEntityFilter filter = getFilter(javaClass);
		Map<String, String> args = null;
		List<PluginParamXmlConfig> argConfigs = filterConfig.getArg();
		if (argConfigs != null) {
			args = new HashMap<>(4);
			for (PluginParamXmlConfig argConfig : argConfigs) {
				args.put(argConfig.getName(), argConfig.getValue());
			}
			try {
				filter.initialize(args);
			}
			catch (EntityFilterInitializationException e) {
				throw new DataSetConfigurationException(e.getMessage());
			}
		}
		return filter;
	}

	private static IEntityFilter getFilter(String className) throws DataSetConfigurationException
	{
		Class<?> cls;
		try {
			cls = Class.forName(FILTER_PACKAGE + '.' + className);
		}
		catch (ClassNotFoundException e) {
			try {
				cls = Class.forName(className);
			}
			catch (ClassNotFoundException e2) {
				String msg = String.format(ERR_NO_SUCH_FILTER, className);
				throw new DataSetConfigurationException(msg);
			}
		}
		try {
			return (IEntityFilter) cls.getDeclaredConstructor().newInstance();
		}
		catch (ClassCastException e) {
			String msg = String.format(ERR_NOT_A_FILTER, className);
			throw new DataSetConfigurationException(msg);
		}
		catch (InstantiationException | IllegalAccessException e) {
			throw new DataSetConfigurationException(e.getMessage());
		}
    catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
      throw new RuntimeException(e);
    }
	}
}
