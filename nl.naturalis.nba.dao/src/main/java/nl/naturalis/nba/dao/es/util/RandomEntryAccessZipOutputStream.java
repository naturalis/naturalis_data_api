package nl.naturalis.nba.dao.es.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class RandomEntryAccessZipOutputStream extends OutputStream {

	private HashMap<String, ZipOutputStream> streams;
	private ArrayList<SwapFileOutputStream> buckets;

	private String mainEntry;
	private ZipOutputStream main;

	private ZipOutputStream current;

	public RandomEntryAccessZipOutputStream(OutputStream out, String mainEntry) throws IOException
	{
		this.mainEntry = mainEntry;
		this.main = new ZipOutputStream(out);
		this.main.putNextEntry(new ZipEntry(mainEntry));
		streams = new HashMap<>();
		buckets = new ArrayList<>();
	}

	public void setActiveEntry(String name) throws IOException
	{
		if (name.equals(mainEntry)) {
			current = main;
		}
		else {
			ZipOutputStream zos = streams.get(name);
			if (zos == null) {
				SwapFileOutputStream sfos = new SwapFileOutputStream();
				buckets.add(sfos);
				zos = new ZipOutputStream(sfos);
				streams.put(name, zos);
				current = zos;
			}
		}
	}

	@Override
	public void write(int arg0) throws IOException
	{
		// TODO Auto-generated method stub
		
	}
	
	

}
