package com.springboot.app.dbsql.control;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/customResponse")
public class CustomResponseController {
	
	@Autowired
	private IDataAccess dataAccess;
	
	@CrossOrigin
	@GetMapping("/test/{host}/{alias}/{user}/{pass}/{port}")
	public ResponseEntity<?> findAllTables(@PathVariable String host, @PathVariable String alias, @PathVariable String user, @PathVariable String pass, @PathVariable Integer port) throws ClassNotFoundException, SQLException{
		if(host == null || alias == null || user == null || pass == null) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body("Error: Faltan datos necesarios para continuar");
		}else {
			dataAccess.setConnectionToUse(host, alias, user, pass, port);
			return new ResponseEntity<List<String>>(dataAccess.getTablesNames(),HttpStatus.OK);
		}
		
	}
}
