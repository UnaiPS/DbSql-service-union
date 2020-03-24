package com.springboot.app.dbsql.control;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.springboot.app.commons.models.entity.Connections;

@Service
public class DataAccessImpl implements IDataAccess{
	private Connection conn;
	private PreparedStatement preparedStatement;
	private Connections connectionToUse = new Connections();
	
	@Override
	public void setConnectionToUse(String host, String alias, String user, String pass, Integer port) {
		this.connectionToUse.setAlias(alias);
		this.connectionToUse.setHost(host);
		this.connectionToUse.setUser(user);
		this.connectionToUse.setPass(pass);
	}
	
	private void connect() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		String url = "jdbc:mysql://" + connectionToUse.getHost() + "/"
				+ connectionToUse.getAlias() + "?serverTimezone=Europe/Madrid";
		conn = DriverManager.getConnection(url, connectionToUse.getUser(), connectionToUse.getPass());
	}
	
	private void disconnect() throws SQLException {
		if(preparedStatement != null) {
			preparedStatement.close();
		}
		if(conn != null) {
			conn.close();
		}
	}

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
		} finally {
			disconnect();
		}
		return tables;
	}
	
}
