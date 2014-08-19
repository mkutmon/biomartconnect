package org.pathvisio.biomartconnect.impl;

import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class AttributesImporter {
	
	String organism;
	String identifier;
	
	public AttributesImporter(String organism,String identifier){
		this.organism = organism;
		this.identifier=identifier;
	}
	
	public Map<String,String> getAttributes() {	
		String url = "http://www.biomart.org/biomart/martservice?type=attributes&dataset=" + organism;
		URL attribute_file = null;
		BufferedReader in = null;
		Map<String,String> attr_map = new HashMap<String,String>();
		
		try {
			attribute_file = new URL(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			System.err.println("Url for getting attributes not formed properly.");
			e.printStackTrace();
		}
		if(attribute_file != null){
			try {
				in = new BufferedReader(new InputStreamReader(attribute_file.openStream()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println("Cannot open attribute file");
				e.printStackTrace();
			}
			
	        String inputLine;
	        try {
				while ((inputLine = in.readLine()) != null && inputLine.length() > 0){
				    //System.err.println(inputLine);
				    //System.err.println(inputLine.length());
				    String [] temp = inputLine.split("\t");
				    System.err.println(organism + identifier);
				    if(temp.length > 5 && temp[5].equals(organism + identifier)){
				    	attr_map.put(temp[1], temp[0]);
				    }
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println("cannot read attribute file input stream");
				e.printStackTrace();
			}
	        try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println("Cannot close attribute file input stream");
				e.printStackTrace();
			}
		}
		return attr_map;
	}
}