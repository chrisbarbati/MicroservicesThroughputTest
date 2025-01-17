package org.chrisbarbati.shopcontroller;

import org.chrisbarbati.shopcontroller.entities.OrderEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class PerformanceTest {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceTest.class);
    private final API api;
    private final int BATCH_STEP;
    private final int TEST_QUANTITY;
    private static boolean batchComplete = false;
    private boolean factorsOnly = true;

    @Autowired
    public PerformanceTest(API api, Environment env) {
        this.api = api;
        this.BATCH_STEP = Integer.parseInt(env.getProperty("batch.step"));
        this.TEST_QUANTITY = Integer.parseInt(env.getProperty("test.quantity"));
        this.factorsOnly = Boolean.parseBoolean(env.getProperty("factors.only"));
    }

    /**
     * Method to send the orders to the database controller and test the performance with varying batch sizes
     * and different methods of sending the orders
     */
    public void testPerformance() {
        // Generate 10,000 order entities
        List<OrderEntity> orders = IntStream.range(0, TEST_QUANTITY)
                .mapToObj(i -> new OrderEntity(/* populate order fields */))
                .toList();

        api.setTestQuantity(orders.size());

        //Sleep for 5 seconds
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }

        // Test Kafka performance with varying batch sizes
        for(int i = 1; i <= (TEST_QUANTITY/BATCH_STEP); i++){
            //Reset batchComplete to false
            setBatchComplete(false);

            if(factorsOnly && ( TEST_QUANTITY % (i * BATCH_STEP) )!= 0){
                logger.info("Skipping batch size: " + i * BATCH_STEP + " because it is not a factor of " + TEST_QUANTITY);
                continue;
            }

            //Set the batch size
            api.setBatchSize(i * BATCH_STEP);
            long kafkaStartTime = System.currentTimeMillis();
            orders.forEach(o -> api.sendOrderKafka(o));
            long kafkaEndTime = System.currentTimeMillis();
            long kafkaDuration = kafkaEndTime - kafkaStartTime;
            System.out.println("Kafka Time: " + kafkaDuration + "ms");

            //Wait for the batch to complete before starting the next test
            while(!isBatchComplete()){
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // Test HTTP synchronous performance
//        for(int i = 1; i <= (TEST_QUANTITY/BATCH_STEP); i++){
//            //Reset batchComplete to false
//            setBatchComplete(false);

//            if(factorsOnly && ( TEST_QUANTITY % (i * BATCH_STEP) )!= 0){
//                logger.info("Skipping batch size: " + i * BATCH_STEP + " because it is not a factor of " + TEST_QUANTITY);
//                continue;
//            }
//            //Set the batch size
//            api.setBatchSize(i * BATCH_STEP);
//            long httpSyncStartTime = System.currentTimeMillis();
//            orders.forEach(o -> api.sendOrderHTTP(o));
//            long httpSyncEndTime = System.currentTimeMillis();
//            long httpSyncDuration = httpSyncEndTime - httpSyncStartTime;
//            System.out.println("HTTP Sync Time: " + httpSyncDuration + "ms");
//            //Wait for the batch to complete before starting the next test
//            while(!isBatchComplete()){
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }


        // Test HTTP performance
//        for(int i = 1; i <= (TEST_QUANTITY/BATCH_STEP); i++){
//                //Reset batchComplete to false
//                setBatchComplete(false);


    //        if(factorsOnly && ( TEST_QUANTITY % (i * BATCH_STEP) )!= 0){
    //            logger.info("Skipping batch size: " + i * BATCH_STEP + " because it is not a factor of " + TEST_QUANTITY);
    //            continue;
    //        }
//            api.setBatchSize(i * BATCH_STEP);
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
//            //Wait for the batch to complete before starting the next test
//            while(!isBatchComplete()){
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }

    }

    public static boolean isBatchComplete() {
        return batchComplete;
    }

    public static void setBatchComplete(boolean batchComplete) {
        PerformanceTest.batchComplete = batchComplete;
    }
}