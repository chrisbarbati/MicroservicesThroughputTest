package org.chrisbarbati.databasecontroller.services.producers;

import org.chrisbarbati.databasecontroller.entities.OrderEntity;
import org.chrisbarbati.databasecontroller.entities.ProductEntity;
import org.chrisbarbati.databasecontroller.repositories.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for Orders
 */
@Service
public class OrderProducerService {
    private static final Logger log = LoggerFactory.getLogger(OrderProducerService.class);

    /**
     * Autowired OrderRepository for database access
     */
    @Autowired
    private OrderRepository orderRepository;

    /**
     * Autowired KafkaTemplate for sending messages to Kafka
     */
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Publish all orders to the orders topic
     */
    public void publishOrders() {
        List<OrderEntity> orders = orderRepository.findAll();
        for (OrderEntity order : orders) {
            kafkaTemplate.send("orders", order.toString());
        }

        log.info("Sent " + orders.size() + " orders to Kafka");
    }
}
