package com.springboot.app.dbsql.control;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.springboot.app.commons.models.entity.Metadates;


/**
 * 
 * @author Unai Pérez Sánchez
 *
 */
public interface IDataAccess {
	public void setConnectionToUse(String host, String alias, String user, String pass, Integer port);
	
	public List<String> getTablesNames() throws ClassNotFoundException, SQLException ;
	
	public void insertNewValues(String sql) throws ClassNotFoundException, SQLException;
	
	public TableInfo getAllOneTable(String table) throws ClassNotFoundException, SQLException;
	
	public ArrayList<String> getTablesNames(String alias) throws ClassNotFoundException, SQLException;
	
	public ArrayList<String> getColumnNamesFromTable(String table, String alias) throws ClassNotFoundException, SQLException;
	
	public Long insertMetadate(Metadates metadate) throws ClassNotFoundException, SQLException;
	
	public void insertConnectionMetadates(Long idConnection, Long idMetadate) throws ClassNotFoundException, SQLException;
}
