package com.grigg.server;

import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MailManager {
	private ArrayList<Mail> mailList;
	private Socket client;
	private static int paddingWidth = 10;
	private Mail.Status domain = Mail.Status.INBOX;
	
	public MailManager(ArrayList<Mail> serverMail, Socket connectedClient) {
		mailList = serverMail;
		client = connectedClient;
		clearFilter();
	}
	
	public void setMailStatus(Mail.Status newStatus) {
		for(int i = 0; i < mailList.size(); i++) {
			mailList.get(i).updateStatus(newStatus);
		}
		Server.setMail(mailList);
		clearFilter();
	}

	public void retrieveMail() {
		for(int i = 0; i < mailList.size(); i++) {
			printMailFull(mailList.get(i));
		}
		clearFilter();
	}
	
	public void searchMail() {
		for(int i = 0; i < mailList.size(); i++) {
			printMailHeader(mailList.get(i));
		}
		clearFilter();
	}
	
	private void clearFilter() {
		mailList = Server.getMailList();
		
		ArrayList<Mail> filteredMail = new ArrayList<Mail>();
		
		for(int i = 0; i < mailList.size(); i++) {
			if(mailList.get(i).getStatus() == domain) {
				filteredMail.add(mailList.get(i));
			}
		}
		mailList = filteredMail;
	}
	
	public void setDomain(Mail.Status newDomain) {
		domain = newDomain;
		clearFilter();
	}
	
	public void addIDFilter(int filter) {
		ArrayList<Mail> filteredMail = new ArrayList<Mail>();
		
		for(int i = 0; i < mailList.size(); i++) {
			if(mailList.get(i).getid() == filter) {
				filteredMail.add(mailList.get(i));
			}
		}
		mailList = filteredMail;
	}

	public void addRecipientFilter(String filter) {
		ArrayList<Mail> filteredMail = new ArrayList<Mail>();
		
		for(int i = 0; i < mailList.size(); i++) {
			if(mailList.get(i).getMailTo().equalsIgnoreCase(filter)) {
				filteredMail.add(mailList.get(i));
			}
		}
		mailList = filteredMail;
	}
	
	public void addSenderFilter(String filter) {
		ArrayList<Mail> filteredMail = new ArrayList<Mail>();
		
		for(int i = 0; i < mailList.size(); i++) {
			if(mailList.get(i).getMailFrom().equalsIgnoreCase(filter)) {
				filteredMail.add(mailList.get(i));
			}
		}
		mailList = filteredMail;
	}
	
	public void addSubjectFilter(String filter) {
		ArrayList<Mail> filteredMail = new ArrayList<Mail>();
		
		for(int i = 0; i < mailList.size(); i++) {
			if(mailList.get(i).getSubject().equalsIgnoreCase(filter)) {
				filteredMail.add(mailList.get(i));
			}
		}
		mailList = filteredMail;
	}
	
	public void addKeywordFilter(String filter) {
		ArrayList<Mail> filteredMail = new ArrayList<Mail>();
		
		for(int i = 0; i < mailList.size(); i++) {
			if(mailList.get(i).getKeywords().toUpperCase().indexOf(filter.toUpperCase()) != -1) {
				filteredMail.add(mailList.get(i));
			}
		}
		mailList = filteredMail;
	}
	
	public void addDateFilter(String start, String end) {
		try {
			ArrayList<Mail> filteredMail = new ArrayList<Mail>();
			
			DateFormat format = new SimpleDateFormat("dd/mm/yyyy");
			Date startDate = format.parse(start);
			Date endDate = format.parse(end);
			
			for(int i = 0; i < mailList.size(); i++) {
				
				Calendar cal = mailList.get(i).getSent();
				String sDate = cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
				Date date = format.parse(sDate);
				
				if(startDate.before(date) && endDate.after(date)) {
					filteredMail.add(mailList.get(i));
				}
			}
			mailList = filteredMail;
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	private void printMailHeader(Mail mail) {
		ConnectionHandler.respond("", client);
		printField("ID",String.valueOf(mail.getid()));
		printField("Date Sent", mail.getSent().getTime().toString());
		printField("To", mail.getMailTo());
		printField("From", mail.getMailFrom());
		printField("Subject", mail.getSubject());
		printField("Keywords", mail.getKeywords());
		ConnectionHandler.respond("", client);
	}
	
	private void printMailBody(Mail mail) {
		printField("Message","");
		printField("",mail.getMessage());
		ConnectionHandler.respond("", client);
	}
	
	private void printMailFull(Mail mail) {
		printMailHeader(mail);
		printMailBody(mail);
	}
	
	private void printField(String field, String val) {
		String formattedField = String.format("%-" + paddingWidth + "s %-15s", field, " : " + val);
		ConnectionHandler.respond(formattedField, client);
	}
}