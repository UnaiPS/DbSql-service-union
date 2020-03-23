package com.springboot.app.dbsql.control;

import java.sql.SQLException;
import java.util.List;

public interface IDataAccess {
	public List<String> getTablesNames() throws ClassNotFoundException, SQLException ;
}
