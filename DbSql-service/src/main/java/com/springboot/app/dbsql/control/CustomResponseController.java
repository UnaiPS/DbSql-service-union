package com.springboot.app.dbsql.control;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/customResponse")
public class CustomResponseController {
	
	@Autowired
	private IDataAccess dataAccess;
	
	@GetMapping("/test")
	public ResponseEntity<List<String>> findAllTables() throws ClassNotFoundException, SQLException{
		
		return new ResponseEntity<List<String>>(dataAccess.getTablesNames(),HttpStatus.OK);
	}
}
