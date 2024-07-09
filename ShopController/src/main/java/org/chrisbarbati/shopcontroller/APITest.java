package org.chrisbarbati.shopcontroller;

import org.chrisbarbati.shopcontroller.entities.OrderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.CountDownLatch;

/**
 *
 */

@Service
public class APITest {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final WebClient.Builder webClientBuilder;

    @Autowired
    public APITest(KafkaTemplate<String, String> kafkaTemplate, WebClient.Builder webClientBuilder) {
        this.kafkaTemplate = kafkaTemplate;
        this.webClientBuilder = webClientBuilder;
    }

    private RestTemplate restTemplate = new RestTemplate();

    /**
     * Method to send an OrderEntity via Kafka
     * @param order OrderEntity to send
     */
    public void sendOrderKafka(OrderEntity order){
        kafkaTemplate.send("addOrder", order.toString());
    }

    /**
     * Method to send an OrderEntity via HTTP GET
     * @param order OrderEntity to send
     */
    public void sendOrderHTTP(OrderEntity order){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OrderEntity> request = new HttpEntity<>(order, headers);
        restTemplate.postForObject("http://database-controller:8080/order/addOrder", request, String.class);
    }

    /**
     * Method to send an OrderEntity via asynchronous HTTP POST using WebClient
     * @param order OrderEntity to send
     * @param latch CountDownLatch to track completion of all requests
     */
    public void sendOrderHTTP(OrderEntity order, CountDownLatch latch) {
        try {
            WebClient webClient = webClientBuilder.build();
            webClient.post()
                    .uri("http://database-controller:8080/order/addOrder")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(order), OrderEntity.class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnTerminate(latch::countDown)
                    .subscribe();
        } catch (Exception e) {
            e.printStackTrace();
            latch.countDown();
        }
    }

    /**
     * Method to set the batch size for the SQL insert statements
     * @param batchSize The new batch size to set
     */
    public void setBatchSize(int batchSize){
        kafkaTemplate.send("batch-size", Integer.toString(batchSize));
    }

    /**
     * Method to set the test quantity
     * @param testQuantity The new test quantity to set
     */
    public void setTestQuantity(int testQuantity) {
        kafkaTemplate.send("test-quantity", Integer.toString(testQuantity));
    }
}
