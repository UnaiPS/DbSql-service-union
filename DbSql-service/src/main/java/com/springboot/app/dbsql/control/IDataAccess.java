package com.springboot.app.dbsql.control;

import java.sql.SQLException;
import java.util.List;

import com.springboot.app.commons.models.entity.Connections;

public interface IDataAccess {
	public void setConnectionToUse(Connections connection);
	
	public List<String> getTablesNames() throws ClassNotFoundException, SQLException ;
}
