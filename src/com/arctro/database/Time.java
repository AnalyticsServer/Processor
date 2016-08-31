package com.arctro.database;

public class Time {
	static int hours = 0;
	static int minutes = 0;
	
	public static long current(){
		return System.currentTimeMillis() + (hours * 3600000l) + (minutes * 60000l);
	}
	
	public static void setTimezone(int hours, int minutes){
		Time.hours = hours;
		Time.minutes = minutes;
	}
	public static double getTimezone(){
		return (double)(hours) + ((double)(minutes) / (double)(60));
	}
}
