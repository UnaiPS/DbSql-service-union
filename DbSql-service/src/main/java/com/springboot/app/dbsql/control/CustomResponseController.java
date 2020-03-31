package com.springboot.app.dbsql.control;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Controller
@RequestMapping("/dbsql")
public class CustomResponseController {
	
	private static Logger log = LoggerFactory.getLogger(CustomResponseController.class);
	
	@Autowired
	private IDataAccess dataAccess;
	@HystrixCommand(fallbackMethod = "findAllTablesFail")
	@CrossOrigin
	@GetMapping("/findAllTables/{host}/{alias}/{user}/{pass}/{port}")
	public ResponseEntity<?> findAllTables(@PathVariable String host, @PathVariable String alias, @PathVariable String user, @PathVariable String pass, @PathVariable Integer port) throws Exception{
		HttpStatus statusToSend;
		if(isDataNull(host, alias, user, pass, port)) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body("Error: Faltan datos necesarios para continuar");
		}else {
			dataAccess.setConnectionToUse(host, alias, user, pass, port);
			if(connectionSuccessful()) {
				statusToSend = HttpStatus.OK;
			}else {
				throw new Exception("No se pudo establecer la conexión");
			}
			return new ResponseEntity<List<String>>(dataAccess.getTablesNames(), statusToSend);
		}
		
	}
	
	public ResponseEntity<?> findAllTablesFail(String host, String alias, String user, String pass, Integer port){
		List<String> nullResponse = new ArrayList<>();
		return new ResponseEntity<List<String>>(nullResponse, HttpStatus.BAD_REQUEST);
	}
	
	private Boolean connectionSuccessful(){
		Boolean response = true;
		try {
			if(dataAccess.getTablesNames().isEmpty()) {
				response = false;
			}
		} catch (ClassNotFoundException | SQLException e) {
			log.error("Error: Something went wrong with the request");
		}
		return response;
	}
	
	private Boolean isDataNull(String host, String alias, String user, String pass, Integer port) {
		Boolean response = false;
		if(host == null || port == 0 || port == null || alias == null || user == null || pass == null) {
			response = true;
		}
		return response;
	}
}
