package com.springboot.app.dbsql.control;

import java.util.ArrayList;

import com.springboot.app.commons.models.entity.Connections;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreMetadate {
	private Connections connection;
	private ArrayList<MetaColumn> columns = new ArrayList<MetaColumn>();
}
