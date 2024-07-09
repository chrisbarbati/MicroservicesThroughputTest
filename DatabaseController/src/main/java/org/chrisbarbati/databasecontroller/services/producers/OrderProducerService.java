package org.chrisbarbati.databasecontroller.services.producers;

import org.chrisbarbati.databasecontroller.entities.OrderEntity;
import org.chrisbarbati.databasecontroller.repositories.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for Orders
 */
@Service
public class OrderProducerService {

    private static final Logger log = LoggerFactory.getLogger(OrderProducerService.class);
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public OrderProducerService(OrderRepository orderRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

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
