package com.arctro.processor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.arctro.database.DatabaseConnectionPool;
import com.ibatis.common.jdbc.ScriptRunner;

public class Main {

	public static String DB_NAME;
	public static String DB_USERNAME;
	public static String DB_PASSWORD;
	public static String DB_HOST;
	public static String DB_PORT;
	
	public static DatabaseConnectionPool dcp;
	
	public static void main(String[] args) {
		DB_NAME = args[0];
		DB_USERNAME = args[1];
		DB_PASSWORD = args[2];
		DB_HOST = args[3];
		DB_PORT = args[4];
		
		try{
			dcp = new DatabaseConnectionPool(DB_USERNAME, DB_PASSWORD, DB_HOST, DB_NAME, DB_PORT);
			setupDB();
			
			AnalyticsProcessor processor = new AnalyticsProcessor(dcp);
		}catch(SQLException | IOException | ClassNotFoundException e){
			e.printStackTrace();
			return;
		}
		
		return;
	}
	
	
	public static void setupDB() throws SQLException, IOException{
		Connection conn = null;
		try{
			conn = dcp.getConnection();
			
			ScriptRunner sr = new ScriptRunner(conn, false, false);
			
			Reader reader = new BufferedReader(new FileReader("res/create_db.mysql"));
			sr.runScript(reader);
			
			reader = new BufferedReader(new FileReader("res/config.mysql"));
			sr.runScript(reader);
		}finally{
			if(conn != null){
				conn.close();
			}
		}
	}
}
