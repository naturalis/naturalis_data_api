package nl.naturalis.nba.dao.es.format.dwca;

/**
 * Manages the assemblage and creation of DarwinCore archives.
 * 
 * @author Ayco Holleman
 *
 */
public class SpecimenArchiveWriter {

//	private static Logger logger = getLogger(SpecimenArchiveWriter.class);
//	private static TimeValue TIME_OUT = new TimeValue(5000);
//
//	private DataSet ds;
//	private ZipOutputStream zos;
//
//	/**
//	 * Creates a {code DwcaWriter} for the specified data set collection,
//	 * writing to the specified output stream.
//	 * 
//	 * @param dsc
//	 * @param out
//	 */
//	public SpecimenArchiveWriter(DataSet ds, ZipOutputStream zos)
//	{
//		this.ds = ds;
//		this.zos = zos;
//	}
//
//	private void writeCsv()
//			throws InvalidQueryException
//	{
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
