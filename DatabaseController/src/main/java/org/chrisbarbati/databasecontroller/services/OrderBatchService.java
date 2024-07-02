package org.chrisbarbati.databasecontroller.services;

import org.chrisbarbati.databasecontroller.entities.OrderEntity;
import org.chrisbarbati.databasecontroller.repositories.OrderRepository;
import org.chrisbarbati.databasecontroller.utilities.ExcelWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderBatchService {

    @Autowired
    private ExcelWriter excelWriter;

    private final OrderRepository orderRepository;
    private final List<OrderEntity> orderBuffer = new ArrayList<>();
    private int batchSize = 1;
    private int recordCount = 0;
    private long startTime = 0;
    private long endTime = 0;
    private long lastOrderTime = System.currentTimeMillis();

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public OrderBatchService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

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

    @Transactional
    @Scheduled(fixedRate = 1000)
    public synchronized void saveBatch() {
        long currentTime = System.currentTimeMillis();
        // Save the batch if it is full or if it has been 1 second since the last order was added (to prevent waiting indefinitely for a full batch)
        if (!orderBuffer.isEmpty() && (orderBuffer.size() >= batchSize || currentTime - lastOrderTime > 1000)) {
            log.info("Scheduled event called, saving batch of " + orderBuffer.size() + " orders");
            recordCount += orderBuffer.size();
            orderRepository.saveAll(orderBuffer);
            orderBuffer.clear();
            if(recordCount == 10000){
                recordCount = 0;
                log.info("10000 orders have been processed");
                endTime = System.currentTimeMillis();
                log.info("Time to process 10000 orders: " + (endTime - startTime) + "ms");
                log.info("Test for batch size " + batchSize + " complete");
                excelWriter.writeData(batchSize, endTime - startTime);
            }
        }
    }


        @KafkaListener(topics = {"batch-size"}, groupId = "shop-group", autoStartup = "true")
        public void updateBatchSize(String message) {
            try {
                batchSize = Integer.parseInt(message);
                log.info("Batch size updated to: " + batchSize);
            } catch (NumberFormatException e) {
                log.error("Error occurred while parsing batch size", e);
            }
        }
}