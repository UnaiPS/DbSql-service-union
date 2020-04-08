package com.springboot.app.dbsql.control;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Table {
	private String name;
	private ArrayList<Field> fields = new ArrayList<Field>();
}
