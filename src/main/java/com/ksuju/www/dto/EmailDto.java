package com.ksuju.www.dto;

import java.io.InputStream;

public class EmailDto {
		private String from;
		private String receiver;
		private String text;
		private String subject;
		private InputStream resume; // 첨부 파일
		  
		public String getFrom() {
			return from;
		}
		public void setFrom(String from) {
			this.from = from;
		}
		public String getReceiver() {
			return receiver;
		}
		public void setReceiver(String receiver) {
			this.receiver = receiver;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public String getSubject() {
			return subject;
		}
		public void setSubject(String subject) {
			this.subject = subject;
		}
	    public InputStream getResume() {
	        return resume;
	    }
	
	    public void setResume(InputStream resume) {
	        this.resume = resume;
	    }
	
	}
