package nl.naturalis.nda.elasticsearch.load.nsr;

import java.net.URL;

public class DOMNsrHarvester {

	public static void main(String[] args)
	{
		URL url = DOMNsrHarvester.class.getResource("/nsr/nsr_test_export.xml");
		System.out.println("URL: " + url);
	}


	public DOMNsrHarvester()
	{
		// TODO Auto-generated constructor stub
	}

}
