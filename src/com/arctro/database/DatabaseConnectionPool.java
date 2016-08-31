package com.arctro.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.arctro.log.Log;

public class DatabaseConnectionPool {
	List<DatabaseConnection> cPool = new ArrayList<DatabaseConnection>();
	
	boolean running = true;
	long repeatWait = 10;
	
	int max = 65;
	int min = 5;
	
	long searchRepeatWait = 10;
	
	long timeSinceLastQuery = 1000;
	long openTimeout = 30000; 
	
	String jdbcUrl = null;
	
	public DatabaseConnectionPool(String username, String password, String host, String database, String port) throws ClassNotFoundException{
		Class.forName("com.mysql.jdbc.Driver");
		jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database + "?user=" + username + "&password=" + password;
		
		for(int i = 0; i < min; i++){
			try{
				requestNewConnection();
			}catch(SQLException e){
				Log.error(e);
			}
		}
		
		checker();
	}
	
	public synchronized Connection getConnection() throws SQLException{
		boolean found = false;
		while(!found){
			for(int c = 0; c < 10; c++){
				for(int i = 0; i < cPool.size(); i++){
					DatabaseConnection dc = cPool.get(i);
					if(dc.getClosed()){
						dc.setClosed(false);
						return cPool.get(i);
					}
				}
				
				try {
					Thread.sleep(searchRepeatWait);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			requestNewConnection();
		}
		
		return null;
	}
	
	private synchronized void requestNewConnection() throws SQLException{
		if(cPool.size() >= max){
			return;
		}
		
		cPool.add(new DatabaseConnection(DriverManager.getConnection(jdbcUrl)));
	}
	
	private synchronized void checker(){
		Thread t = new Thread(){
			public void run(){
				while(running){
					try {
						Thread.sleep(repeatWait);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					if(cPool.size() <= min){
						continue;
					}
					
					for(int i = 0; i < cPool.size(); i++){
						DatabaseConnection dc = cPool.get(i);
						if((dc.getClosed() && dc.timeSinceLastQuery() >= timeSinceLastQuery) || dc.timeSinceLastQuery() >= openTimeout){
							dc.setClosed(false);
							try {
								dc.getBaseConnection().close();
								cPool.remove(i);
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		};
		t.start();
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public long getRepeatWait() {
		return repeatWait;
	}

	public void setRepeatWait(long repeatWait) {
		this.repeatWait = repeatWait;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public long getSearchRepeatWait() {
		return searchRepeatWait;
	}

	public void setSearchRepeatWait(long searchRepeatWait) {
		this.searchRepeatWait = searchRepeatWait;
	}

	public long getTimeSinceLastQuery() {
		return timeSinceLastQuery;
	}

	public void setTimeSinceLastQuery(long timeSinceLastQuery) {
		this.timeSinceLastQuery = timeSinceLastQuery;
	}

	public long getOpenTimeout() {
		return openTimeout;
	}

	public void setOpenTimeout(long openTimeout) {
		this.openTimeout = openTimeout;
	}
	
	public int getActiveConnections(){
		return cPool.size();
	}
}
