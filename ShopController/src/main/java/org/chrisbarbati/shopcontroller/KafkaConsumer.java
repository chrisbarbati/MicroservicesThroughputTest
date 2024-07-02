package org.chrisbarbati.shopcontroller;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.event.ListenerContainerIdleEvent;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@Service
public class KafkaConsumer {

    private Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    @PostConstruct
    public void init() {
        log.info("Kafka consumer started");
    }

    @EventListener
    public void eventHandler(ListenerContainerIdleEvent event) {
        log.info("No messages received for " + event.getIdleTime() + " milliseconds");
    }

    @KafkaListener(topics = {"products"}, groupId = "shop-group", autoStartup = "true")
    public void consumeProduct(String message) {
        log.info("Consumed message from 'product' topic: " + message);
    }

    @KafkaListener(topics = {"departments"}, groupId = "shop-group", autoStartup = "true")
    public void consumeDepartment(String message) {
        log.info("Consumed message from 'department' topic: " + message);
    }
}