package com.grigg.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	public static Socket soc;
	public static BufferedReader reader;
	public static OutputStreamWriter writer;
	public static Boolean isRunning = true;
	public static String ip = "127.0.0.1";
	public static int port = 50005;

	public static void main(String[] args) {
		try {
			//Allocates the required resources for the client to function correctly
			soc = new Socket(ip ,port);
			reader = new BufferedReader(new InputStreamReader(soc.getInputStream(), "UTF-8"));
			writer = new OutputStreamWriter(soc.getOutputStream());
			Client client = new Client();
			
			//Starts the threads required for incoming and outgoing communications
			client.startReader();
			client.startWriter();
		}catch(Exception e) {
			System.out.print("Error: " + e.getMessage());
		}
	}
	
	//Starts client writer thread
	private void startWriter() {
		ClientWriter writer = new ClientWriter();
		Thread writerThread = new Thread(writer);
		writerThread.start();
	}
	
	//Starts client reader thread
	private void startReader() {
		ClientReader reader = new ClientReader();
		Thread readerThread = new Thread(reader);
		readerThread.start();
	}
}

class ClientReader implements Runnable{
	
	@Override
	public void run() {
		while(Client.isRunning) {
			try {
				if(Client.reader.ready() == true) {
					String line = Client.reader.readLine();
					System.out.println(line);
				}
			}catch(Exception e) {
				System.out.println("Error : " + e.getMessage());
			}
		}
	}
}

class ClientWriter implements Runnable{
	
	@Override
	public void run() {
		Scanner scanner = new Scanner(System.in);
		while(Client.isRunning){
			try {
				String input = scanner.nextLine() + System.getProperty("line.separator");
				
				Client.writer.write(input);
				Client.writer.flush();
			}catch(Exception e) {
				System.out.println("Error : " + e.getMessage());
			}
		}
		//Closes the scanner upon the connection terminating
		scanner.close();
	}
}