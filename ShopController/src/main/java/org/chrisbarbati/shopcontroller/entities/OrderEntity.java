package org.chrisbarbati.shopcontroller.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Entity class for orders in the database,
 * containing a list of LineItemEntity objects
 */

public class OrderEntity {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(OrderEntity.class);

    private int id;

    private int customerId;

    @JsonManagedReference
    private List<LineItemEntity> lineItems;

    /**
     * Default constructor
     */
    public OrderEntity() {
    }

    /**
     * Fully parameterized constructor
     * @param id Database ID for the order
     * @param customerId ID of the customer who placed the order
     * @param lineItems List of LineItemEntity objects that belong to the order
     */
    public OrderEntity(int id, int customerId, List<LineItemEntity> lineItems) {
        setId(id);
        setCustomerId(customerId);
        setLineItems(lineItems);
    }

    /**
     * Serialize the object to a JSON string
     *
     * @return JSON string representation of the object
     */
    public String toString() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (Exception e) {
            log.info("Error serializing OrderEntity to JSON: " + e.getMessage());
            return null;
        }
    }

    /*
     * Getters and setters
     */

    /**
     * @return The ID of the order
     */
    public int getId() {
        return id;
    }

    /**
     * @param id The ID of the order
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return The ID of the customer who placed the order
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * @param customerId The ID of the customer who placed the order
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /**
     * @return The list of LineItemEntity objects that belong to the order
     */
    public List<LineItemEntity> getLineItems() {
        return lineItems;
    }

    /**
     * @param lineItems The list of LineItemEntity objects that belong to the order
     */
    public void setLineItems(List<LineItemEntity> lineItems) {
        this.lineItems = lineItems;
    }
}
