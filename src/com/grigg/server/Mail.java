package com.grigg.server;

import java.util.Calendar;

public class Mail {
	private int 	_id;
	private Calendar _sent;
	private String 	_mailTo;
	private String 	_mailFrom;
	private String 	_subject;
	private String 	_keywords;
	private String 	_message;
	private Status _status;
	
	public Mail(int id, Calendar sent, String mailTo, String mailFrom, String subject, String keywords, String message, String status){
		_id = id;
		_sent = sent;
		_mailTo = mailTo;
		_mailFrom = mailFrom;
		_subject = subject;
		_keywords = keywords;
		_message = message;
		_status = Status.valueOf(status);
	}
	
	public enum Status{
		INBOX,
		DELETED,
		ARCHIVED
	}
	
	public int getid() {
		return _id;
	}
	
	public Calendar getSent() {
		return _sent;
	}
	
	public String getMailTo() {
		return _mailTo;
	}
	
	public String getMailFrom() {
		return _mailFrom;
	}
	
	public String getSubject() {
		return _subject;
	}
	
	public String getKeywords() {
		return _keywords;
	}
	
	public String getMessage() {
		return _message;
	}
	
	public Status getStatus() {
		return _status;
	}
	
	public void updateStatus(Status newStatus) {
		_status = newStatus;
	}
}