package com.arctro.log;

public class FileLogger implements LogBase{

	public void print(String m) {
		System.out.println(m);
		
	}

	public void error(Exception e) {
		e.printStackTrace();
		
	}
	
}
