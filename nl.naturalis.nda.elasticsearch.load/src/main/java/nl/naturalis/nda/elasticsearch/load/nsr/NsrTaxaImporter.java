package nl.naturalis.nda.elasticsearch.load.nsr;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.naturalis.nda.domain.Monomial;
import nl.naturalis.nda.elasticsearch.client.Index;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESNsrTaxon;
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
		importer.importXml();
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

		ESNsrTaxon taxon;

		List<ESNsrTaxon> taxa = new ArrayList<ESNsrTaxon>(batchSize);
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
		if (!taxa.isEmpty()) {
			index.saveObjects(LUCENE_TYPE_TAXON, taxa, ids);
		}

		logger.info("Records processed: " + processed);
		logger.info("Ready");
	}


	private static ESNsrTaxon transfer(Element taxonElement)
	{
		ESNsrTaxon taxon = new ESNsrTaxon();
		taxon.setId(DOMUtil.getIntValue(taxonElement, "id"));
		taxon.setParentId(DOMUtil.getIntValue(taxonElement, "parent_id"));
		taxon.setNsrId(DOMUtil.getValue(taxonElement, "nsr_id"));
		taxon.setUrl(DOMUtil.getValue(taxonElement, "url"));
		taxon.setRank(DOMUtil.getValue(taxonElement, "rank"));
		setClassification(taxon, taxonElement);
		return taxon;
	}


	private static void setClassification(ESNsrTaxon taxon, Element taxonElement)
	{
		List<Monomial> monomials = getClassifiction(taxonElement);
		taxon.setNumMonomials(monomials.size());
		taxon.setMonomial00(monomials.size() > 0 ? monomials.get(0) : null);
		taxon.setMonomial01(monomials.size() > 1 ? monomials.get(1) : null);
		taxon.setMonomial02(monomials.size() > 2 ? monomials.get(2) : null);
		taxon.setMonomial03(monomials.size() > 3 ? monomials.get(3) : null);
		taxon.setMonomial04(monomials.size() > 4 ? monomials.get(4) : null);
		taxon.setMonomial05(monomials.size() > 5 ? monomials.get(5) : null);
		taxon.setMonomial06(monomials.size() > 6 ? monomials.get(6) : null);
		taxon.setMonomial07(monomials.size() > 7 ? monomials.get(7) : null);
		taxon.setMonomial08(monomials.size() > 8 ? monomials.get(8) : null);
		taxon.setMonomial09(monomials.size() > 9 ? monomials.get(9) : null);
		taxon.setMonomial10(monomials.size() > 10 ? monomials.get(10) : null);
		taxon.setMonomial11(monomials.size() > 11 ? monomials.get(11) : null);
	}


	private static List<Monomial> getClassifiction(Element taxonElement)
	{
		Element classificationElement = DOMUtil.getChild(taxonElement, "classification");
		List<Element> taxonElements = DOMUtil.getChildren(classificationElement);
		List<Monomial> monomials = new ArrayList<Monomial>(taxonElements.size());
		for (Element e : taxonElements) {
			monomials.add(new Monomial(DOMUtil.getValue(e, "rank"), DOMUtil.getValue(e, "name")));
		}
		return monomials;
	}

}
