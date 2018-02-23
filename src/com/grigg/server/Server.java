package com.grigg.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	public final static int port = 50005;
	public static boolean isRunning = true;
	public static ServerSocket soc;
	private static ArrayList<Mail> mailList;
	
	public static void main(String[] args) {
		try {
			MailLoader.load("Email.xml");
			startServer();
		}catch(Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
	
	public static void startServer() {
		try {
			//Opens the specified server port
			soc = new ServerSocket(port);
			System.out.println("Server online");
			
			while(isRunning) {
				//Accepts incoming connections
				Socket client = soc.accept();
				
				//Creates thread to handle incoming connections
				ConnectionHandler connection = new ConnectionHandler(client);
				Thread connectionThread = new Thread(connection);
				connectionThread.start();
			}
		}catch(Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	public static void setMail(ArrayList<Mail> loadedMail) {
		mailList = loadedMail;
	}

	public static ArrayList<Mail> getMailList() {
		return mailList;
	}
	
	public static int tryParse(String s) {
		try {
			return Integer.parseInt(s);
		}catch(Exception e){
			return -1;
		}
	}
}

class ConnectionHandler implements Runnable{

	private BufferedReader reader;
	private boolean isRunning = true;
	private Socket socket;
	private MailManager mailList;
	
	
	public ConnectionHandler(Socket soc) {
		try {
			socket = soc;
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			mailList = new MailManager(Server.getMailList(), soc);
			
		}catch(Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
	
	@Override
	public void run() {
		try {
			while(isRunning) {
				if(reader.ready() == true) {
					String line = reader.readLine();
					ParseCommand(line);
				}
				Thread.sleep(10);
			}
		}catch(Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
	private void ParseCommand(String line) {
		String args[] = line.toUpperCase().split(" ");
		String cmd = args[0];
		switch(cmd) {
		case "LOGIN": 
			Login(args);
			break;
		case "RETRIEVE":
			Retrieve(args);
			break;
		case "DELETE":
			changeMailStatus(args, Mail.Status.DELETED);
			break;
		case "RESTORE":
			changeMailStatus(args, Mail.Status.INBOX);
			break;
		case "ARCHIVE":
			changeMailStatus(args, Mail.Status.ARCHIVED);
			break;
		case "SEARCH":
			Search(args);
			break;
		case "CHANGEDOMAIN":
			changeDomain(args);
			break;
		default:
			error(500);
			break;
		}
	}
	
	private void Login(String[] args) {
		}
		
	private void changeDomain(String[] args) {
		if(args.length == 2) {
			mailList.setDomain(Mail.Status.valueOf(args[1].toUpperCase()));
		}else {
			error(501);
		}
	}
	
	private void Search(String[] args) {
		switch(args.length) {
		case 2:
			if(args[1].equalsIgnoreCase("ALL")) {
				mailList.searchMail();
			}else{
				error(501);
			}
			break;
		case 3:
			switch(args[1]) {
			case "ID":
				int v = Server.tryParse(args[2]);
				if(v != -1) {
					mailList.addIDFilter(v);
					mailList.searchMail();
				}else {
					error(501);
				}
				break;
			case "RECIPIENT":
				mailList.addRecipientFilter(args[2]);
				mailList.searchMail();
				break;
			case "SENDER":
				mailList.addSenderFilter(args[2]);
				mailList.searchMail();
				break;
			case "KEYWORD":
				mailList.addKeywordFilter(args[2]);
				mailList.searchMail();
				break;
			case "SUBJECT":
				mailList.addSubjectFilter(args[2]);
				mailList.searchMail();
				break;
			default:
				error(501);
				break;
			}
			break;
		case 4:
			if(args[1].equalsIgnoreCase("DATE")) {
				mailList.addDateFilter(args[2], args[3]);
				mailList.searchMail();
			}else {
				error(501);
			}
			break;
		default:
			error(501);
			respond("Usage : "
					+ "RETRIEVE ALL | "
					+ "ID <ID> | "
					+ "RECIPIENT <Recipient> | "
					+ "DATE <Start-date> <end-date> | "
					+ "SENDER <Sender> | "
					+ "KEYWORD <Keyword> | "
					+ "SUBJECT <Subject>",
					socket);
			break;
		}
	}
		
	private void changeMailStatus(String[] args, Mail.Status status) {
		switch(args.length) {
		case 2:
			if(args[1].equalsIgnoreCase("ALL")) {
				mailList.setMailStatus(status);
			}else{
				error(501);
			}
			break;
		case 3:
			switch(args[1]) {
			case "ID":
				int v = Server.tryParse(args[2]);
				if(v != -1) {
					mailList.addIDFilter(v);
					mailList.setMailStatus(status);
				}else {
					error(501);
				}
				break;
			case "RECIPIENT":
				mailList.addRecipientFilter(args[2]);
				mailList.setMailStatus(status);
				break;
			case "SENDER":
				mailList.addSenderFilter(args[2]);
				mailList.setMailStatus(status);
				break;
			case "KEYWORD":
				mailList.addKeywordFilter(args[2]);
				mailList.setMailStatus(status);
				break;
			case "SUBJECT":
				mailList.addSubjectFilter(args[2]);
				mailList.setMailStatus(status);
				break;
			default:
				error(501);
				break;
			}
			break;
		case 4:
			if(args[1].equalsIgnoreCase("DATE")) {
				mailList.addDateFilter(args[2], args[3]);
				mailList.setMailStatus(status);
			}else {
				error(501);
			}
			break;
		default:
			break;
		}
	}
	
	private void Retrieve(String[] args) {
		switch(args.length) {
		case 2:
			if(args[1].equalsIgnoreCase("ALL")) {
				mailList.retrieveMail();
			}else {
				error(501);
			}
			break;
		case 3:
			switch(args[1]) {
			case "ID":
				int v = Server.tryParse(args[2]);
				if(v != -1) {
					mailList.addIDFilter(v);
					mailList.retrieveMail();
				}else {
					error(501);
				}
				break;
			case "RECIPIENT":
				mailList.addRecipientFilter(args[2]);
				mailList.retrieveMail();
				break;
			case "SENDER":
				mailList.addSenderFilter(args[2]);
				mailList.retrieveMail();
				break;
			case "KEYWORD":
				mailList.addKeywordFilter(args[2]);
				mailList.retrieveMail();
				break;
			case "SUBJECT":
				mailList.addSubjectFilter(args[2]);
				mailList.retrieveMail();
				break;
			default:
				error(501);
				break;
			}
			break;
		case 4:
			if(args[1].equalsIgnoreCase("DATE")) {
				mailList.addDateFilter(args[2], args[3]);
				mailList.retrieveMail();
			}else {
				error(501);
			}
			break;
		default:
			error(501);
			break;
		}
	}
	
	private void error(int i) {
		String s = "";
		switch(i) {
		case 500: s = "Error 500 : Syntax error, command unrecognised"; break;
		case 501: s = "Error 501 : Syntax error in parameters or arguments"; break;
		case 451:
		default: s = "Error 451 : Requested action aborted: local error in processing"; break;
		}
		respond(s,socket);
	}
	
	public static void respond (String string, Socket client) {
		try {
			OutputStreamWriter writer = new OutputStreamWriter(client.getOutputStream());
			writer.write(string + System.getProperty("line.separator"));
			writer.flush();
		}catch(Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
}