package org.chrisbarbati.shopcontroller;

import org.chrisbarbati.shopcontroller.entities.OrderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class PerformanceTest {

    private final APITest api;

    @Autowired
    public PerformanceTest(APITest apiTest) {
        this.api = apiTest;
    }

    public void testPerformance() {

        // Generate 10,000 order entities
        List<OrderEntity> orders = IntStream.range(0, 10000)
                .mapToObj(i -> new OrderEntity(/* populate order fields */))
                .collect(Collectors.toList());

        // Test Kafka performance with varying batch sizes
        for(int i = 1; i <= 10; i++){
            //Set the batch size
            api.setBatchSize(i * 1000);
            long kafkaStartTime = System.currentTimeMillis();
            orders.forEach(o -> api.sendOrderKafka(o));
            long kafkaEndTime = System.currentTimeMillis();
            long kafkaDuration = kafkaEndTime - kafkaStartTime;
            System.out.println("Kafka Time: " + kafkaDuration + "ms");

            //Wait 1 minute before starting the next test
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Test HTTP synchronous performance
//        for(int i = 1; i <= 10; i++){
//            //Set the batch size
//            api.setBatchSize(i * 1000);
//            long httpSyncStartTime = System.currentTimeMillis();
//            orders.forEach(o -> api.sendOrderHTTP(o));
//            long httpSyncEndTime = System.currentTimeMillis();
//            long httpSyncDuration = httpSyncEndTime - httpSyncStartTime;
//            System.out.println("HTTP Sync Time: " + httpSyncDuration + "ms");
//
//            //Wait 1 minute before starting the next test
//            try {
//                Thread.sleep(60000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }


        // Test HTTP performance
//        for(int i = 1; i <= 10; i++){
//            long httpStartTime = System.currentTimeMillis();
//            CountDownLatch latch = new CountDownLatch(orders.size());
//            orders.forEach(o -> api.sendOrderHTTP(o, latch));
//
//            try {
//                latch.await(); // Wait for all requests to complete
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            long httpEndTime = System.currentTimeMillis();
//            long httpDuration = httpEndTime - httpStartTime;
//            System.out.println("HTTP Time: " + httpDuration + "ms");
//
//            //Wait 1 minute before starting the next test
//            try {
//                Thread.sleep(60000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

    }
}