package com.solactive.app.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solactive.app.model.Tick;

/**
 * 
 * @author jay
 * test IndexCOntroller rest endpoints
 * 
 */
@SpringBootTest
@AutoConfigureMockMvc
public class SingleTickerTest {
	
	@Autowired
	private IndexController indexController;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void contexLoads() throws Exception {
		assertThat(indexController).isNotNull();
	}

	
	
	@Test
	public void testSingleTick() throws Exception {
		
		List<Tick> tickList = new ArrayList<>();
		
		long currentTime = System.currentTimeMillis();
		System.out.println("currentTime: "+currentTime);
		for(int i=0; i <100; i++) {
			Tick tick = new Tick();
			tick.setInstrument("MSFT");
			tick.setPrice(100+i);
			tick.setTimestamp(System.currentTimeMillis());
			Thread.sleep(10);
			tickList.add(tick);
			
			this.mockMvc.perform(post("/ticks")
					.contentType("application/json")
					.content(objectMapper.writeValueAsString(tick)));
					
		}
		
		this.mockMvc.perform(get("/statistics/MSFT"))
			.andExpect(status().isOk());
			
	
	}
	

}
