package com.solactive.app.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

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
public class IndexControllerTest {
	
	@Autowired
	private IndexController indexController;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void contexLoads() throws Exception {
		assertThat(indexController).isNotNull();
		assertThat(mockMvc).isNotNull();
		assertThat(objectMapper).isNotNull();
	}
	
	
	@Test
	public void shouldReturnTickCreatedStatus() throws Exception {
		Tick tick = new Tick();
		tick.setInstrument("IBM");
		tick.setPrice(100);
		tick.setTimestamp(System.currentTimeMillis());
		
		System.out.println(objectMapper.writeValueAsString(tick));
		
		this.mockMvc.perform(post("/ticks")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(tick)))
				.andDo(print()).andExpect(status().isCreated());
	}
	
	@Test
	public void shouldReturnTickNoContentStatus() throws Exception {
		Tick tick = new Tick();
		tick.setInstrument("IBM");
		tick.setPrice(100);
		tick.setTimestamp(System.currentTimeMillis() - 60001);
		
		this.mockMvc.perform(post("/ticks")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(tick)))
				.andDo(print()).andExpect(status().isNoContent());
	}
	
	
	@Test
	public void testSingleTick() throws Exception {
		
		List<Tick> tickList = new ArrayList<>();
		
		long currentTime = System.currentTimeMillis();
		System.out.println("currentTime: "+currentTime);
		for(int i=0; i< 100; i++) {
			Tick tick = new Tick();
			tick.setInstrument("MS");
			tick.setPrice(100+i);
			tick.setTimestamp(currentTime - i*1000);
			tickList.add(tick);
		
			this.mockMvc.perform(post("/ticks")
					.contentType("application/json")
					.content(objectMapper.writeValueAsString(tick)));
					
		}
		
		this.mockMvc.perform(get("/statistics/MS"))
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.avg").value(129.5d))
			.andExpect(MockMvcResultMatchers.jsonPath("$.max").value(159d))
		    .andExpect(MockMvcResultMatchers.jsonPath("$.min").value(100d))
		    .andExpect(MockMvcResultMatchers.jsonPath("$.count").value(60l));
		
			
	
	}
	

	@Test
	public void testSingleTickRequest() throws Exception {
		
		List<Tick> tickList = new ArrayList<>();
		
		String ticker = "BRK.A";
		
		long currentTime = System.currentTimeMillis();
		System.out.println("currentTime: "+currentTime);
		for(int i=0; i< 1000; i++) {
			Tick tick = new Tick();
			tick.setInstrument(ticker);
			tick.setPrice(100+i);
			tick.setTimestamp(currentTime - i*100);
			tickList.add(tick);
			
			this.mockMvc.perform(post("/ticks")
					.contentType("application/json")
					.content(objectMapper.writeValueAsString(tick)));
					
		}
		
		System.out.println("Start get call ("+ticker+"): "+System.currentTimeMillis());
		
		MvcResult result  = this.mockMvc.perform(get("/statistics/"+ticker))
			.andExpect(status().isOk()).andReturn();
			
		System.out.println(result.getResponse().getContentAsString());
	
		System.out.println("End get call ("+ticker+"): "+System.currentTimeMillis());
		
	
	}
	
	@Test
	public void testMultipleTickerRequest() throws Exception {
		
		List<Tick> tickList = new ArrayList<>();
		
		String[] tickerArr = {"MU", "GOOGL", "AAPL"};
		
		ExecutorService service = Executors.newFixedThreadPool(100);
		List<Future> futureList = new ArrayList<>();
		
		long currentTime = System.currentTimeMillis();
		System.out.println("currentTime: "+currentTime);
		for( String ticker: tickerArr) {
			for(int i=0; i< 1000; i++) {
				Tick tick = new Tick();
				tick.setInstrument(ticker);
				tick.setPrice(100+i);
				tick.setTimestamp(currentTime - i*100);
				tickList.add(tick);
				
				futureList.add(service.submit(() -> {
					try {
						this.mockMvc.perform(post("/ticks")
							.contentType("application/json")
							.content(objectMapper.writeValueAsString(tick)));
					} catch (Exception e) {
						
					}
				}));
						
			}
			
			
		}
		
		futureList.forEach(f -> {
			try {
				f.get();
			} catch (Exception e) {
				
			}
		});
		
		for( String ticker: tickerArr) {
			System.out.println("Start get call ("+ticker+"): "+System.currentTimeMillis());
			
			MvcResult result  = this.mockMvc.perform(get("/statistics/"+ticker))
				.andExpect(status().isOk()).andReturn();
				
			System.out.println(result.getResponse().getContentAsString());
		
			System.out.println("End get call ("+ticker+"): "+System.currentTimeMillis());
		}
		
		System.out.println("----------------------------------------------------");
		
		System.out.println("Start get call (Aggregation): "+System.currentTimeMillis());
		
		MvcResult result  = this.mockMvc.perform(get("/statistics"))
			.andExpect(status().isOk()).andReturn();
			
		System.out.println(result.getResponse().getContentAsString());
	
		System.out.println("End get call (Aggregation): "+System.currentTimeMillis());
	
	}
	
	
	

}
