package com.solactive.app.jmh;

import org.openjdk.jmh.annotations.State;
import org.springframework.boot.test.context.SpringBootTest;

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
public class DemoApplicationTest {


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
				.jvmArgs("-server")
				.build();

		new Runner(opts).run();
	}

	@Benchmark
	public void dbInserts(Parameters parameters) {
		int size = Integer.parseInt(parameters.batchSize);

		for (int i = 0; i < size; i++) {
			System.out.println("temp");
		}
	}

	@State(value = Scope.Benchmark)
	public static class Parameters {

		@Param({"1", "1000"})
		String batchSize;
	}
	
	public static void disableWarning() {
	    System.err.close();
	    System.setErr(System.out);
	}

}