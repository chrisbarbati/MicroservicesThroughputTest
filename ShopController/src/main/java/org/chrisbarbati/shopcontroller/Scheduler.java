package org.chrisbarbati.shopcontroller;

import org.chrisbarbati.shopcontroller.entities.OrderEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * Scheduler class to allow for scheduling of tasks
 *
 * Not currently in use
 */

@Component
public class Scheduler {

    private final API api;

    @Autowired
    public Scheduler(API api){
        this.api = api;
    }

    private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

    //@Scheduled(fixedRate = 10000)
    public void sendOrder() {
        OrderEntity order = new OrderEntity(); // Create an OrderEntity instance
        // Set the properties of the OrderEntity instance
        // order.set...

        log.info("Scheduled task called");

        api.sendOrderKafka(order);
        api.sendOrderHTTP(order);
        CountDownLatch latch = new CountDownLatch(1);

        api.sendOrderHTTP(order, latch);

        try {
            latch.await(); // Wait for all requests to complete
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}