package com.tcs.reporter.dao;
import com.tcs.reporter.bean.Config;
import com.tcs.reporter.bean.Column;
import com.tcs.reporter.exception.ReporterException;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
public class ReporterDAO {
	private Config config;
	private Connection connection;
	public ReporterDAO(Config config) {
		this.config=config;
	}
	public void openConnection() throws ReporterException
	{
		try
		{
			closeConnection();
			Class.forName(config.getDriver());
			this.connection=DriverManager.getConnection(config.getDbUrl(), config.getUsername(), config.getPassword());
			
		}catch(ClassNotFoundException cnfe)
		{
			throw new ReporterException(" ClassNotFoundException in openConnection():ReporterDAO "+cnfe.getMessage());
		}catch(SQLException se)
		{
			throw new ReporterException(" SQLException in openConnection():ReporterDAO "+se.getMessage());
		}
		
	}
	public void closeConnection() throws ReporterException
	{
		try
		{
			if(this.connection!=null && this.connection.isClosed()==false)
			{
				this.connection.close();
				this.connection=null;
			}
		}catch(SQLException se)
		{
			throw new ReporterException(" SQLException in closeConnection():ReporterDAO "+se.getMessage());
		}
	}
	public ArrayList<ArrayList<String>> getRecords(String sql, ArrayList<Column> colList) throws ReporterException
	{
		ArrayList<ArrayList<String>> records= new ArrayList<ArrayList<String>>();
		try
		{
			openConnection();
			PreparedStatement ps=connection.prepareStatement(sql);
			ResultSet rs=ps.executeQuery();
			ArrayList<String> record;
			while(rs.next())
			{
				record=new ArrayList<String>();
				for(Column c:colList)
				{
					record.add(rs.getString(c.getSqlName()));
				}
				records.add(record);
			}
			rs.close();
			ps.close();
		}catch(SQLException se)
		{
			throw new ReporterException(" SQLException in getRecords():ReporterDAO "+se.getMessage());
		}
		finally
		{
			closeConnection();
		}
		return records;
	}
}
