package com.grigg.server;

import java.io.File;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.XMLReader;

public class MailLoader {
	public static void load(String file) {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
		    spf.setNamespaceAware(true);
		    SAXParser saxParser = spf.newSAXParser();
		    XMLReader xmlReader = saxParser.getXMLReader();
		    xmlReader.setContentHandler(new MailParser());
		    xmlReader.parse(convertToFileURL(file));
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String convertToFileURL(String fileName) {
		String path = new File(fileName).getAbsolutePath();
		if(File.separatorChar != '/') {
			path = path.replace(File.separatorChar, '/');
		}
		
		if(!path.startsWith("/")) {
			path = "/" + path;
		}
		
		return ("file:" + path);
	}
}