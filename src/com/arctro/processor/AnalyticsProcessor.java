package com.arctro.processor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.arctro.database.DatabaseConnectionPool;

public class AnalyticsProcessor {
	public int CURRENT_ARCHIVE;
	public int INDEX_INCREMENT;
	public int INDEX_INCREMENT_GS;
	public String INDEX_FILENAME;
	public String[] INDEX_FORMATS;
	
	DatabaseConnectionPool pool;
	
	public AnalyticsProcessor(DatabaseConnectionPool pool) throws SQLException{
		this.pool = pool;
		
		CURRENT_ARCHIVE = (int)(Timestamp.valueOf(getConfig("CURRENT_ARCHIVE")).getTime()/1000);
		INDEX_FILENAME = getConfig("INDEX_FILENAME");
		INDEX_FORMATS = getConfig("INDEX_FORMATS").split(",");
		INDEX_INCREMENT = Integer.parseInt(getConfig("INDEX_INCREMENT"));
		INDEX_INCREMENT_GS = Integer.parseInt(getConfig("INDEX_INCREMENT_GS"));
	}
	
	/**
	 * Generates the filename for an archive/index
	 * @param cat The category of the archive. Optional (only required when used in the INDEX_FILENAME)
	 * @param date The start date of the archive
	 * @param length The time length of the archive (seconds)
	 * @return The filename, excluding extension
	 * @throws SQLException
	 */
	public String getFilename(String cat, Timestamp date, int length) throws SQLException{		
		return INDEX_FILENAME
				.replaceAll("%l", length+"")
				.replaceAll("%c", cat)
				.replaceAll("%y", date.getYear()+"")
				.replaceAll("%m", date.getMonth()+"")
				.replaceAll("%d", date.getDay()+"")
				.replaceAll("%t", (date.getTime()/1000)+"");
	}
	
	public String getConfig(String key) throws SQLException{
		Connection conn = null;
		
		try{
			conn = pool.getConnection();
			
			PreparedStatement prepared = conn.prepareStatement("SELECT `value` FROM `config` WHERE `key` = ?");
			prepared.setString(1,key);
			
			ResultSet rs = prepared.executeQuery();
			
			if(!rs.next()){
				return null;
			}
			
			return rs.getString("value");
		}finally{
			if(conn != null){
				conn.close();
			}
		}
	}
}
