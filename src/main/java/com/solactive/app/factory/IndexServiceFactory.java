package com.solactive.app.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.solactive.app.service.IndexService;
import com.solactive.app.service.impl.IndexNonBlockingServiceImpl;
import com.solactive.app.service.impl.IndexServiceImpl;

/**
 * factory class to give service impl on the basis of variable value
 * 
 * @author jay
 *
 */
@Component
public class IndexServiceFactory {
	
	@Autowired 
	IndexServiceImpl indexService;
	
	@Autowired 
	IndexNonBlockingServiceImpl indexNonBlockingService;
	
	public IndexService getInstance(String type){
	    switch(type){
	     case "BLOCKING" : // BLOCKING
	    	 return indexService;
	     case "NONBLOCKING" : 
	    	 return indexNonBlockingService;
	    
	     default : 
	    	 return null;
	   }

	  }
}
