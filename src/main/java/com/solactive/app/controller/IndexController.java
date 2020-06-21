package com.solactive.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.solactive.app.model.Statistics;
import com.solactive.app.model.Tick;
import com.solactive.app.service.IndexService;
import com.solactive.app.validator.IndexRequestValidator;
import com.sun.net.httpserver.Authenticator.Success;


@RestController
public class IndexController {
	
	@Autowired
	private IndexRequestValidator indexRequestValidator;
	
	@Autowired
	private IndexService indexService;
	
	/**
	 * 
	 * @param tick
	 * @return HTTP status 201 when tick is inserted and HTTP status 204 when tick is invalid
	 */
	@PostMapping("/ticks")
	public ResponseEntity<Success> insertTicks(@RequestBody Tick tick) {
		final long currentTimeStamp = System.currentTimeMillis();
		
		indexRequestValidator.validate(tick, currentTimeStamp);
		
		indexService.insertTicks(tick);
		return new ResponseEntity<Success>(HttpStatus.CREATED);
		
	}
	
	/**
	 * 
	 * @return Statistics
	 */
	@GetMapping("/statistics")
	public Statistics getStatistics() {
		
		return indexService.getStatistics();
		
	}
	
	/**
	 * 
	 * @param instrument
	 * @return Statistics
	 */
	@GetMapping("/statistics/{instrument}")
	public Statistics getStatisticsOfInstrument(@PathVariable String instrument) {
		
		return indexService.getStatistics(instrument);
		
	}
	

}
