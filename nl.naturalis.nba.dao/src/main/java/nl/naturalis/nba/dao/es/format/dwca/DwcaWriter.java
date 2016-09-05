package nl.naturalis.nba.dao.es.format.dwca;

/**
 * Manages the assemblage and creation of DarwinCore archives.
 * 
 * @author Ayco Holleman
 *
 */
public class DwcaWriter {

//	private static Logger logger = getLogger(DwcaWriter.class);
//	private static TimeValue TIME_OUT = new TimeValue(5000);
//
//	private ZipOutputStream zos;
//
//	/**
//	 * Writes a DarwinCore archive for the specified data set. The Elasticsearch
//	 * query to be executed is specified in a file called "queryspec.json"
//	 * residing in the
//	 * {@link DwcaUtil#getDatasetDirectory(DataSetCollectionConfiguration, String) directory}
//	 * created for the data set.
//	 * 
//	 * @param dataSet
//	 * @throws InvalidQueryException
//	 */
//	public void processPredefinedQuery(String dataSet) throws InvalidQueryException
//	{
////		logger.debug("Configuring DwCA writer for data set \"{}\"", dataSet);
////		IField[] fields = getFields(dsc);
////		logger.debug("Writing meta.xml");
////		writeMetaXml(zos, fields);
////		logger.debug("Writing eml.xml");
////		writeEmlXml(zos, dataSet);
////		logger.debug("Loading query specification for data set \"{}\"", dataSet);
////		QuerySpec querySpec = getQuerySpec(dsc, dataSet);
////		logger.debug("Writing CSV payload");
////		writeCsv(querySpec, fields, zos);
//	}
//
//	/**
//	 * Writes a DarwinCore archive containing the data retrieved using the
//	 * specified {@link QuerySpec}.
//	 * 
//	 * @param querySpec
//	 * @throws InvalidQueryException
//	 */
//	public void processDynamicQuery(QuerySpec querySpec) throws InvalidQueryException
//	{
////		if (logger.isDebugEnabled()) {
////			String json = JsonUtil.toPrettyJson(querySpec, true);
////			logger.debug("Configuring DwCA writer for query:\n{}", json);
////		}
////		IField[] fields = getFields(dsc);
////		logger.debug("Writing meta.xml");
////		writeMetaXml(zos, fields);
////		logger.debug("Writing eml.xml");
////		writeEmlXml(zos, null);
////		logger.debug("Writing CSV payload");
////		writeCsv(querySpec, fields, zos);
//	}
//
//	private void writeMetaXml(ZipOutputStream zos, IField[] fields)
//	{
////		newZipEntry(zos, "meta.xml");
////		MetaXmlGenerator metaXmlGenerator = getMetaXmlGenerator(dsc, fields);
////		metaXmlGenerator.generateMetaXml(zos);
//	}
//
//	private void writeEmlXml(ZipOutputStream zos, String dataSet)
//	{
//		newZipEntry(zos, "eml.xml");
//		File emlFile = getEmlFile(dsc, dataSet);
//		try (InputStream is = new FileInputStream(emlFile)) {
//			IOUtil.pipe(is, zos, 2048);
//		}
//		catch (IOException e) {
//			throw new DwcaCreationException(e);
//		}
//	}
//
//	private void writeCsv(QuerySpec spec, IField[] fields, ZipOutputStream zos)
//			throws InvalidQueryException
//	{
////		newZipEntry(zos, getCsvFileName(dsc));
////		CsvPrinter csvPrinter = new CsvPrinter(fields, zos);
////		SearchResponse response = executeQuery(spec);
////		csvPrinter.printHeader();
////		int processed = 0;
////		while (true) {
////			for (SearchHit hit : response.getHits().getHits()) {
////				if (++processed % 50000 == 0) {
////					logger.debug("Records processed: " + processed);
////					csvPrinter.flush();
////				}
////				csvPrinter.printRecord(hit.getSource());
////			}
////			String scrollId = response.getScrollId();
////			Client client = ESClientManager.getInstance().getClient();
////			SearchScrollRequestBuilder ssrb = client.prepareSearchScroll(scrollId);
////			response = ssrb.setScroll(TIME_OUT).execute().actionGet();
////			if (response.getHits().getHits().length == 0) {
////				break;
////			}
////		}
////		finish(zos);
//	}
//
//	private static SearchResponse executeQuery(QuerySpec spec) throws InvalidQueryException
//	{
//		QuerySpecTranslator qst = new QuerySpecTranslator(spec, SPECIMEN);
//		SearchRequestBuilder request = qst.translate();
//		request.addSort(DOC_FIELD_NAME, SortOrder.ASC);
//		request.setScroll(TIME_OUT);
//		request.setSize(1000);
//		SearchResponse response = request.execute().actionGet();
//		return response;
//	}
//
//	private static ZipEntry newZipEntry(ZipOutputStream zos, String name)
//	{
//		ZipEntry entry = new ZipEntry(name);
//		try {
//			zos.putNextEntry(entry);
//		}
//		catch (IOException e) {
//			throw new DwcaCreationException(e);
//		}
//		return entry;
//	}
//
//	private static void finish(ZipOutputStream zos)
//	{
//		try {
//			zos.finish();
//		}
//		catch (IOException e) {
//			throw new DwcaCreationException(e);
//		}
//	}
}
