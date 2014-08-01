package nl.naturalis.nda.elasticsearch.load.nsr;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.naturalis.nda.domain.NsrTaxon;
import nl.naturalis.nda.domain.NsrTaxonDescription;
import nl.naturalis.nda.elasticsearch.client.Index;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.load.NDASchemaManager;

import org.domainobject.util.DOMUtil;
import org.domainobject.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class NsrTaxaImporter {

	public static void main(String[] args) throws Exception
	{

		IndexNative index = new IndexNative(NDASchemaManager.DEFAULT_NDA_INDEX_NAME);

		index.deleteType(LUCENE_TYPE_TAXON);
		index.deleteType(LUCENE_TYPE_SYNONYM);
		index.deleteType(LUCENE_TYPE_COMMON_NAME);
		index.deleteType(LUCENE_TYPE_DESCRIPTION);

		Thread.sleep(2000);

		String mapping = StringUtil.getResourceAsString("/es-mappings/NsrTaxon.json");
		index.addType(LUCENE_TYPE_TAXON, mapping);
		mapping = StringUtil.getResourceAsString("/es-mappings/NsrSynonym.json");
		index.addType(LUCENE_TYPE_SYNONYM, mapping);
		mapping = StringUtil.getResourceAsString("/es-mappings/NsrCommonName.json");
		index.addType(LUCENE_TYPE_COMMON_NAME, mapping);
		mapping = StringUtil.getResourceAsString("/es-mappings/NsrTaxonDescription.json");
		index.addType(LUCENE_TYPE_DESCRIPTION, mapping);

		NsrTaxaImporter importer = new NsrTaxaImporter(index);
		importer.importTaxa();
	}

	private static final Logger logger = LoggerFactory.getLogger(NsrTaxaImporter.class);
	private static final String LUCENE_TYPE_TAXON = "NsrTaxon";
	private static final String LUCENE_TYPE_SYNONYM = "NsrSynonym";
	private static final String LUCENE_TYPE_COMMON_NAME = "NsrCommonName";
	private static final String LUCENE_TYPE_DESCRIPTION = "NsrTaxonDescription";

	private final Index index;

	private int batchSize = 500;


	public NsrTaxaImporter(Index index)
	{
		this.index = index;
	}


	public void importTaxa() throws ParserConfigurationException, SAXException, IOException
	{
		InputStream is = NsrTaxaImporter.class.getResourceAsStream("/nsr/nsr_test_export.xml");
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document doc = builder.parse(is);
		is.close();
		List<Element> taxonElements = DOMUtil.getChildElements(doc.getDocumentElement());

		int processed = 0;

		NsrTaxon taxon;

		List<NsrTaxon> taxa = new ArrayList<NsrTaxon>(batchSize);
		List<String> ids = new ArrayList<String>(batchSize);

		for (Element taxonElement : taxonElements) {
			if (++processed % 1000 == 0) {
				logger.info("Records processed: " + processed);
			}
			taxon = transfer(taxonElement);
			taxa.add(taxon);
			ids.add(taxon.getNsrId());
			if (taxa.size() == batchSize) {
				index.saveObjects(LUCENE_TYPE_TAXON, taxa, ids);
				taxa.clear();
				ids.clear();
			}
		}
		if(!taxa.isEmpty()) {
			index.saveObjects(LUCENE_TYPE_TAXON, taxa, ids);			
		}
		
		

		logger.info("Records processed: " + processed);
		logger.info("Ready");
	}


	private static NsrTaxon transfer(Element taxonElement)
	{
		NsrTaxon taxon = new NsrTaxon();
		taxon.setId(DOMUtil.getIntValue(taxonElement, "id"));
		taxon.setParentId(DOMUtil.getIntValue(taxonElement, "parent_id"));
		taxon.setNsrId(DOMUtil.getValue(taxonElement, "nsr_id"));
		taxon.setUrl(DOMUtil.getValue(taxonElement, "url"));
		taxon.setRank(DOMUtil.getValue(taxonElement, "rank"));
		return taxon;
	}

}
