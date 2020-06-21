package com.solactive.app.jmh;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.solactive.app.model.Tick;
import com.solactive.app.service.IndexService;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@SpringBootTest
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class JmhTest {
	
	@Autowired
	private IndexService indexService;
	
	@Param({ "100", "1000", "1000", "10000", "100000" })
    private int iterations;
	
	private List<Tick> DATA_FOR_TESTING = prepareTicks();
	
	private String[] tickers = new String[]{"AAPL", "GOOGL", "MSFT", "FB", "BRK.A", "JNJ", "MA", "JPM", "PG", "T"};
    
	
	@Test
	public void runBenchmarks() throws Exception {
		Options opts = new OptionsBuilder()
				// set the class name regex for benchmarks to search for to the current class
				.include("\\." + this.getClass().getSimpleName() + "\\.")
				.warmupIterations(3)
				.measurementIterations(3)
				// do not use forking or the benchmark methods will not see references stored within its class
				.forks(0)
				// do not use multiple threads
				.threads(1)
				.shouldDoGC(true)
				.shouldFailOnError(true)
				.build();

		new Runner(opts).run();
	}
	
	@State(value = Scope.Benchmark)
	public static class Parameters {

		@Param({ "100", "1000", "1000", "10000", "100000" })
		String batchSize;
	}
	

	@Benchmark
	public void tickInserts(Parameters parameters) {
	 
		int size = Integer.parseInt(parameters.batchSize);

		
	    for (int i = 0; i < DATA_FOR_TESTING.size(); i++) {
	    	Tick tick = DATA_FOR_TESTING.get(i);
	    	indexService.insertTicks(tick);
	    }
	 
	}
	
	@Benchmark
	public void getTickStats() {
	 
		indexService.getStatistics(System.currentTimeMillis(), tickers[0]);
	    
	}
	
	@Benchmark
	public void getAllTickStats() {
	 
		indexService.getStatistics(System.currentTimeMillis());
	    
	}

	private List<Tick> prepareTicks() {
		
		List<Tick> ticks = new ArrayList<>();
		
		for (int i = 0; i < iterations; i++) {
			Tick tick = new Tick();
			tick.setInstrument(tickers[i%10]);
			tick.setPrice(Math.random()*1000);
			tick.setTimestamp((long) (System.currentTimeMillis() - Math.random()*100));
			ticks.add(tick);
		}
		
		return ticks;
	}

}
