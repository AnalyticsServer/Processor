package com.arctro.log;


public class Log {
	public static Log l = new Log();
	
	public LogBase lB = null;
	
	private Log(){
		lB = new FileLogger();
	}
	
	public static void print(String m){
		l.lB.print(m);
	}
	
	public static void error(Exception e){
		l.lB.error(e);
	}
}
