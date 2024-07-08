package org.chrisbarbati.shopcontroller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TestRunner implements CommandLineRunner {

    private static Logger log = LoggerFactory.getLogger(TestRunner.class);

    private final PerformanceTest performanceTest;

    public TestRunner(PerformanceTest performanceTest) {
        this.performanceTest = performanceTest;
    }

    @Override
    public void run(String... args) throws Exception {
        // Adding a delay of 30 seconds before starting the performance test
        log.info("Waiting for 30 seconds before starting the test...");
        Thread.sleep(30000);

        performanceTest.testPerformance();
    }
}