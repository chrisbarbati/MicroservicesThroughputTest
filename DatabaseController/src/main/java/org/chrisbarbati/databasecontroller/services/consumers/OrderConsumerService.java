package org.chrisbarbati.databasecontroller.services.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.chrisbarbati.databasecontroller.entities.OrderEntity;
import org.chrisbarbati.databasecontroller.repositories.OrderRepository;
import org.chrisbarbati.databasecontroller.services.OrderBatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@Service
@CrossOrigin
@RestController
@RequestMapping("/order")
public class OrderConsumerService {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderBatchService orderBatchService;

    /**
     * Method to consume a message from addOrder topic
     *
     * Adds a new order to the database
     *
     * @param message The message received from the Kafka topic
     */
    @KafkaListener(topics = {"addOrder"}, groupId = "shop-group", autoStartup = "true")
    public void addOrder(String message) {
        try {
            OrderEntity order = objectMapper.readValue(message, OrderEntity.class);
            log.info("New Order Received via Kafka: " + order);
            orderBatchService.addOrderToBatch(order);
        } catch (Exception e) {
            log.error("Error occurred while deserializing order message", e);
        }
    }

    /**
     * Method to consume a receive an OrderEntity serialized as JSON
     * in the request parameters and add it to the database
     *
     *
     */
    @PostMapping("/addOrder")
    public void addOrderHttp(@RequestBody OrderEntity order) {
        log.info("New Order Received via POST: " + order);
        orderBatchService.addOrderToBatch(order);
    }

    /**
     * Method to consume a message from deleteOrder topic
     *
     * Deletes an order from the database
     *
     * @param message The message received from the Kafka topic
     */
    @KafkaListener(topics = {"deleteOrder"}, groupId = "shop-group", autoStartup = "true")
    public void deleteOrder(String message) {
        try {
            OrderEntity order = objectMapper.readValue(message, OrderEntity.class);
            log.info("Order to delete: " + order);
            orderRepository.delete(order);
        } catch (Exception e) {
            log.error("Error occurred while deserializing order message", e);
        }
    }

    /**
     * Method to consume a message from updateOrder topic
     *
     * Updates an order in the database
     *
     * @param message The message received from the Kafka topic
     */
    @KafkaListener(topics = {"updateOrder"}, groupId = "shop-group", autoStartup = "true")
    public void updateOrder(String message) {
        try {
            OrderEntity order = objectMapper.readValue(message, OrderEntity.class);
            log.info("Order to update: " + order);
            orderRepository.save(order);
        } catch (Exception e) {
            log.error("Error occurred while deserializing order message", e);
        }
    }
}
