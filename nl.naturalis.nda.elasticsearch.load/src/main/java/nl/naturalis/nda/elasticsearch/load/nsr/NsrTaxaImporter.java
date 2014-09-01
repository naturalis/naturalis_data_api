package nl.naturalis.nda.elasticsearch.load.nsr;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.naturalis.nda.elasticsearch.client.Index;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.load.NDASchemaManager;

import org.domainobject.util.DOMUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class NsrTaxaImporter {

	public static void main(String[] args) throws Exception
	{
		IndexNative index = new IndexNative(NDASchemaManager.DEFAULT_NDA_INDEX_NAME);
		//index.deleteType(LUCENE_TYPE);
		//Thread.sleep(2000);
		NsrTaxaImporter importer = new NsrTaxaImporter(index);
		importer.importXml();
	}

	static final Logger logger = LoggerFactory.getLogger(NsrTaxaImporter.class);
	static final String ID_PREFIX = "NSR-";
	static final String LUCENE_TYPE = "Taxon";

	private final Index index;

	private int batchSize = 500;


	public NsrTaxaImporter(Index index)
	{
		this.index = index;
	}


	public void importXml() throws ParserConfigurationException, SAXException, IOException
	{
		InputStream is = new FileInputStream("C:/test/nsr-exports/nsr-export--2014-08-01_1536-000.xml");
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document doc = builder.parse(is);
		is.close();
		Element taxaElement = DOMUtil.getChild(doc.getDocumentElement());
		List<Element> taxonElements = DOMUtil.getChildren(taxaElement);

		int processed = 0;

		ESTaxon taxon;

		List<ESTaxon> taxa = new ArrayList<ESTaxon>(batchSize);
		List<String> ids = new ArrayList<String>(batchSize);

		//BeanPrinter bp = new BeanPrinter("C:/test/taxa.tst");

		for (Element taxonElement : taxonElements) {
			if (++processed % 1000 == 0) {
				logger.info("Records processed: " + processed);
			}
			taxon = NsrTaxonTransfer.transfer(taxonElement);
			// bp.dump(taxon);
			taxa.add(taxon);
			ids.add(taxon.getSourceSystemId());
			if (taxa.size() == batchSize) {
				index.saveObjects(LUCENE_TYPE, taxa, ids);
				taxa.clear();
				ids.clear();
			}
		}
		if (!taxa.isEmpty()) {
			index.saveObjects(LUCENE_TYPE, taxa, ids);
		}

		logger.info("Records processed: " + processed);
		logger.info("Ready");
	}

}
