package com.springboot.app.dbsql.control;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.springboot.app.commons.models.entity.Connections;

/**
 * 
 * @author Unai Pérez Sánchez
 * 
 * This class is the implementation of the interface
 * IDataAccess. This implementation is meant to be the class
 * that can connect to the database and get all the data
 *
 */
@Service
public class DataAccessImpl implements IDataAccess{
	private Connection conn;
	private PreparedStatement preparedStatement;
	private Connections connectionToUse = new Connections();
	private static Logger log = LoggerFactory.getLogger(DataAccessImpl.class);
	
	/**
	 * This method gets all the information about the connection and stores in a variable to use later
	 */
	@Override
	public void setConnectionToUse(String host, String alias, String user, String pass, Integer port) {
		this.connectionToUse.setAlias(alias);
		this.connectionToUse.setHost(host);
		this.connectionToUse.setUser(user);
		this.connectionToUse.setPass(pass);
	}
	
	/**
	 * This method is going to get the connection and try to connect to the database
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private void connect() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		String url = "jdbc:mysql://" + connectionToUse.getHost() + "/"
				+ connectionToUse.getAlias() + "?serverTimezone=Europe/Madrid";
		conn = DriverManager.getConnection(url, connectionToUse.getUser(), connectionToUse.getPass());
	}
	
	/**
	 * This method is to disconnect the connection from the database
	 * @throws SQLException
	 */
	private void disconnect() throws SQLException {
		if(preparedStatement != null) {
			preparedStatement.close();
		}
		if(conn != null) {
			conn.close();
		}
	}
	
	/**
	 * When a connection is active, this method can return the name of the tables that
	 * are stored in the database
	 */
	@Override
	public List<String> getTablesNames() throws ClassNotFoundException, SQLException {
		ResultSet rs = null;
		List<String> tables = new ArrayList<String>();
		try {
			connect();
			String sql = "SELECT table_name FROM information_schema.tables WHERE table_schema = ?";
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, connectionToUse.getAlias());
			rs = preparedStatement.executeQuery();
			while(rs.next()) {
				tables.add(rs.getString("table_name"));
			}
		}catch(Exception e) {
			log.error("Error: Cannot connect to the database");
		}finally {
			disconnect();
		}
		return tables;
	}
	
	
	public void insertNewValues(String sql) throws ClassNotFoundException, SQLException {
		try {
			connect();
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.executeUpdate();
		} finally {
			disconnect();
		}
	}
	
	@Override
	public TableInfo getAllOneTable(String table) throws ClassNotFoundException, SQLException {
		ResultSet rs = null;
		List<String> tableList = new ArrayList<String>();
		ArrayList<Column> columnList = new ArrayList<Column>();
		TableInfo objTable = new TableInfo();
		objTable.setName(table);

		try {
			connect();
			String query = "SELECT table_name FROM information_schema.tables WHERE table_schema = ?";
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setString(1, connectionToUse.getAlias());
			rs = preparedStatement.executeQuery();
			while (rs.next()) {
				tableList.add(rs.getString("table_name"));
			}
			if (tableExist(tableList, table) == true) {
				String getInfo = "SELECT * FROM " + table;
				rs = null;
				preparedStatement = conn.prepareStatement(getInfo);
				rs = preparedStatement.executeQuery();
				while (rs.next()) {

					for (int i = 1; i < rs.getMetaData().getColumnCount() + 1; i++) {
						System.out.print(" " + rs.getMetaData().getColumnName(i) + "=" + rs.getObject(i));
						Column column = new Column(rs.getMetaData().getColumnName(i),rs.getObject(i));
						columnList.add(column);
					}
				}
			
			objTable.setColumns(columnList);
			}
		} finally {

			disconnect();

		}
		return objTable;
	}

	public boolean tableExist(List<String> tableList, String table) {
		Boolean exist = false;
		for (int i = 0; i < tableList.size(); i++) {
			if (tableList.get(i).contentEquals(table) == true) {
				exist = true;
			}
		}
		return exist;

	}
}
