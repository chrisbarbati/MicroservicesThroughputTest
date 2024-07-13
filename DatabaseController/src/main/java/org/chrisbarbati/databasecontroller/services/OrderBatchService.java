package org.chrisbarbati.databasecontroller.services;

import org.chrisbarbati.databasecontroller.entities.OrderEntity;
import org.chrisbarbati.databasecontroller.repositories.OrderRepository;
import org.chrisbarbati.databasecontroller.utilities.ExcelWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for testing various batch sizes and their effect on performance
 */
@Service
public class OrderBatchService {

    private final ExcelWriter excelWriter;
    private final OrderRepository orderRepository;
    private final KafkaTemplate kafkaTemplate;
    private final List<OrderEntity> orderBuffer = new ArrayList<>();
    private int testQuantity = 30000;
    private final long maxWaitTime = 100;
    private int batchSize = 1;
    private int recordCount = 0;
    private long startTime = 0;
    private long endTime = 0;
    private long lastOrderTime = System.currentTimeMillis();

    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Constructor for OrderBatchService
     *
     * @param orderRepository
     */
    @Autowired
    public OrderBatchService(OrderRepository orderRepository, ExcelWriter excelWriter, KafkaTemplate kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.excelWriter = excelWriter;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Thread-safe method to add an order to the batch
     *
     * @param order Order to add
     */
    public synchronized void addOrderToBatch(OrderEntity order) {

        if(recordCount == 0){
            startTime = System.currentTimeMillis();
        }
        orderBuffer.add(order);
        lastOrderTime = System.currentTimeMillis();
        if (orderBuffer.size() >= batchSize) {
            saveBatch();
        }
    }

    /**
     * Save the current batch of orders to the database.
     *
     * Scheduled to run every second to prevent waiting indefinitely for a full batch,
     * but to only save the batch if it is full or if it has been 1 second since the last order was added.
     */
    @Transactional
    @Scheduled(fixedRate = 1000)
    public synchronized void saveBatch() {
        long currentTime = System.currentTimeMillis();
        // Save the batch if it is full or if it has been more than maxWaitTime since the last order was added (to prevent waiting indefinitely for a full batch)
        if (!orderBuffer.isEmpty() && (orderBuffer.size() >= batchSize || currentTime - lastOrderTime > maxWaitTime)) {
            log.info("Scheduled event called, saving batch of " + orderBuffer.size() + " orders");
            recordCount += orderBuffer.size();
            orderRepository.saveAll(orderBuffer);
            orderBuffer.clear();

            if(recordCount == testQuantity){
                recordCount = 0;
                log.info(testQuantity + " orders have been processed");
                endTime = System.currentTimeMillis();
                log.info("Time to process " + testQuantity +  " orders: " + (endTime - startTime) + "ms");
                log.info("Test for batch size " + batchSize + " complete");
                excelWriter.writeData(batchSize, endTime - startTime);

                // Send a Kafka message to the batch-complete topic if the test is complete
                kafkaTemplate.send("batch-complete", "Batch complete");
            }
        }
    }

    /**
     * Listens for messages from the shop-controller application to update the batch size
     *
     * @param message
     */
        @KafkaListener(topics = {"batch-size"}, groupId = "shop-group", autoStartup = "true")
        public void updateBatchSize(String message) {
            try {
                batchSize = Integer.parseInt(message);
                log.info("Batch size updated to: " + batchSize);
            } catch (NumberFormatException e) {
                log.error("Error occurred while parsing batch size", e);
            }
        }

    /**
     * Listens for messages from the shop-controller application to update the test quantity
     *
     * @param message
     */
    @KafkaListener(topics = {"test-quantity"}, groupId = "shop-group", autoStartup = "true")
    public void updateTestQuantity(String message) {
        try {
            testQuantity = Integer.parseInt(message);
            log.info("Test quantity updated to: " + testQuantity);
        } catch (NumberFormatException e) {
            log.error("Error occurred while parsing test quantity", e);
        }
    }
}
