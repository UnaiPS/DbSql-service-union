package com.springboot.app.dbsql.control;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.springboot.app.commons.models.entity.Connections;

@Controller
@RequestMapping("/customResponse")
public class CustomResponseController {
	
	@Autowired
	private IDataAccess dataAccess;
	
	@GetMapping("/test")
	public ResponseEntity findAllTables(@RequestBody Connections connection) throws ClassNotFoundException, SQLException{
		if(connection.getHost() == null || connection.getAlias() == null || connection.getUser() == null || connection.getPass() == null) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body("Error: Faltan datos necesarios para continuar");
		}else {
			dataAccess.setConnectionToUse(connection);
			return new ResponseEntity<List<String>>(dataAccess.getTablesNames(),HttpStatus.OK);
		}
		
	}
}
