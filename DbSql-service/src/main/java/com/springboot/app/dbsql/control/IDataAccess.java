package com.springboot.app.dbsql.control;

import java.sql.SQLException;
import java.util.List;

import com.springboot.app.commons.models.entity.Connections;

/**
 * 
 * @author Unai Pérez Sánchez
 *
 */
public interface IDataAccess {
	public void setConnectionToUse(String host, String alias, String user, String pass, Integer port);
	
	public List<String> getTablesNames() throws ClassNotFoundException, SQLException ;
	
	public void insertNewValues(String sql) throws ClassNotFoundException, SQLException;
}
