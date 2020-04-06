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

/**
 * 
 * @author Unai Pérez Sánchez
 * This class is to create custom methods to call them by using HTTP requests
 *
 */
@Controller
@RequestMapping("/dbsql")
public class CustomResponseController {
	
	private static Logger log = LoggerFactory.getLogger(CustomResponseController.class);
	
	@Autowired
	private IDataAccess dataAccess;
	
	/**
	 * This method gets the connection to use and with that information and tries
	 * to get the tables names of the database 
	 * @param host The host of the connection to the database
	 * @param alias The name of the schema of the database
	 * @param user The username of the database
	 * @param pass The password of the database
	 * @param port The port where is located the database
	 * @return Returns the list of the names of the tables
	 * @throws Exception
	 */
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
	
	/**
	 * This method only is called if findAllTables method fails in some way
	 * @param host The host name of the connection
	 * @param alias The name of the schema of the connection to the database
	 * @param user The username of the connection to the database
	 * @param pass The password of the user to connect to the database
	 * @param port The port where is located the database
	 * @return Returns a bad request via HTTP
	 */
	public ResponseEntity<?> findAllTablesFail(String host, String alias, String user, String pass, Integer port){
		List<String> nullResponse = new ArrayList<>();
		return new ResponseEntity<List<String>>(nullResponse, HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * This method checks if the connection was sucessful or not
	 * @return Returns a boolean, true if the connection was sucessful or false if it wasn't
	 */
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
	
	/**
	 * This method checks if some value of the given connection is null
	 * @param host The host name of the connection
	 * @param alias The name of the schema of the connection to the database
	 * @param user The username of the connection to the database
	 * @param pass The password of the user to connect to the database
	 * @param port The port where is located the database
	 * @return Returns a boolean, true if there was any data null, or false if there wasn't
	 */
	private Boolean isDataNull(String host, String alias, String user, String pass, Integer port) {
		Boolean response = false;
		if(host == null || port == 0 || port == null || alias == null || user == null || pass == null) {
			response = true;
		}
		return response;
	}
}
