package com.springboot.app.dbsql.control;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response {
	private String host;
	private String alias;
	private String user;
	private String pass;
	private Integer port;
	private ArrayList<Table> tables = new ArrayList<Table>();
}
