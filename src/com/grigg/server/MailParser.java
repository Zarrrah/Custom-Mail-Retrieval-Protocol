package com.grigg.server;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MailParser extends DefaultHandler {

	private ArrayList<Mail> loadedMail;
	private String reading;
	
	private int id;
	private String dateTime;
	private String mailTo;
	private String mailFrom;
	private String subject;
	private String keywords;
	private String message;
	private String status;
	private GregorianCalendar date;

	public void startDocument() throws SAXException {
		loadedMail = new ArrayList<Mail>();
	}

	public void endDocument() throws SAXException{
		System.out.println("Mails loaded!");
		Server.setMail(loadedMail);
	}
	
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
		if(qName.equalsIgnoreCase("EMAIL")) {
			id = -2;
			dateTime = "";
			mailTo = "";
			mailFrom = "";
			subject = "";
			keywords = "";
			message = "";
			status = "";
		}
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException{
		switch(qName.toUpperCase()) {
		case "ID":
			id = Server.tryParse(reading);
			break;
		case "DATE":
			dateTime = reading;
			int times[] = new int[6];
			
			for(int i = 0; i < 6; i++) {
				times[i] = Server.tryParse(dateTime.split("-")[i]);
			}
			
			date = new GregorianCalendar(times[0], times[1], times[2], times[3], times[4], times[5]);
			break;
		case "MAILTO":
			mailTo = reading;
			break;
		case "MAILFROM":
			mailFrom = reading;
			break;
		case "SUBJECT":
			subject = reading;
			break;
		case "KEYWORDS":
			keywords = reading;
			break;
		case "MESSAGE":
			message = reading;
			break;
		case "STATUS":
			status = reading;
			break;
		case "EMAIL":
			Mail mail = new Mail(id, date, mailTo, mailFrom, subject, keywords, message, status);
			loadedMail.add(mail);
			break;
		default: break;
		}
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException{
		reading = new String(ch,start,length);
	}
}
