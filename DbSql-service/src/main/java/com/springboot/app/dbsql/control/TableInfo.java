package com.springboot.app.dbsql.control;

import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor
public class TableInfo {
	private String name;
	private ArrayList<Column> columns = new ArrayList<Column>();
}