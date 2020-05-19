package com.springboot.app.dbsql.control;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.springboot.app.commons.models.entity.Connections;
import com.springboot.app.commons.models.entity.Metadates;

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

	@Override
	public ArrayList<String> getTablesNames(String alias) throws ClassNotFoundException, SQLException {
		ArrayList<String> tablesNames = new ArrayList<String>();
		ResultSet rs = null;
		try {
			connect();
			String query = "SHOW TABLES FROM " + alias;
			log.info(query);
			preparedStatement = conn.prepareStatement(query);
			rs = preparedStatement.executeQuery();
			while (rs.next()) {
				tablesNames.add(rs.getString("Tables_in_" + alias));
			}
		} finally {
			disconnect();
		}
		return tablesNames;
	}

	@Override
	public ArrayList<String> getColumnNamesFromTable(String table, String alias) throws ClassNotFoundException, SQLException {
		ResultSet rs = null;
		ArrayList<String> columnNames = new ArrayList<String>();
		
		try {
			connect();
			String query = "SHOW COLUMNS FROM " + alias + "." + table;
			preparedStatement = conn.prepareStatement(query);
			rs = preparedStatement.executeQuery();
			while (rs.next()) {
				columnNames.add(rs.getString("field"));			
			}
		} finally {
			disconnect();
		}
		
		return columnNames;
	}

	@Override
	public Long insertMetadate(Metadates metadate) throws ClassNotFoundException, SQLException {
		Long createdId = 0L;
		String sql = "";
		ResultSet rs = null;
		setConnectionToUse("localhost", "db_connections_service", "root", "abcd*1234", 3306);
		try {
			connect();
			if(metadate.getLevel() == 1) {
				sql = "INSERT INTO metadates (active,description,level,meta) VALUES(" + metadate.getActive() + ",'"+
						metadate.getDescription() + "','" + metadate.getLevel() + "','" + metadate.getMeta() + "')";
			}else {
				sql = "INSERT INTO metadates (active,description,id_parent,level,meta) VALUES(" + metadate.getActive() + ",'"+
						metadate.getDescription() + "','" + metadate.getIdParent() + "','" + metadate.getLevel() + "','" + metadate.getMeta() + "')";
			}
			preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.execute();
			rs = preparedStatement.getGeneratedKeys();
			if(rs.next()) {
				createdId = rs.getLong(1);
			}
			
		} finally {
			disconnect();
		}
		return createdId;
	}

	@Override
	public void insertConnectionMetadates(Long idConnection, Long idMetadate)
			throws ClassNotFoundException, SQLException {
		setConnectionToUse("localhost", "db_connections_service", "root", "abcd*1234", 3306);
		String sql = "";
		try {
			connect();
			sql = "INSERT INTO connections_metadates (id_connection,id_metadate) VALUES (" + idConnection +"," + idMetadate + ")";
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.executeUpdate();
		} finally {
			disconnect();
		}
	}

	@Override
	public void deleteFromConnectionMetadates(Long idMeta) throws ClassNotFoundException, SQLException {
		String sql = "";
		try {
			connect();
			sql = "DELETE FROM connections_metadates WHERE id_metadate = "+idMeta;
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.executeUpdate();
		} finally {
			disconnect();
		}
		
	}
}
